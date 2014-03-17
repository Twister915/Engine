package net.cogz.friends;

import com.mongodb.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Gearz Friends API
 */
public abstract class GearzFriends {

    public abstract DBCollection getCollection();

    public abstract DBObject getPlayerDocument(String player);

    public List<String> getPlayerFriends(String player) {
        List<String> friends = new ArrayList<>();
        Object friendsObj = getPlayerDocument(player).get("friends");
        if (friendsObj == null || !(friendsObj instanceof BasicDBList)) {
            return friends;
        }
        BasicDBList friendsList = (BasicDBList) friendsObj;
        for (Object o : friendsList) {
            if (!(o instanceof BasicDBObject)) continue;
            BasicDBObject friendObject = (BasicDBObject) o;
            String friend = friendObject.getString("name");
            friends.add(friend);
        }
        return friends;
    }

    public void addFriend(String toUpdate, String toAdd, boolean primary) throws FriendRequestException {
        if (isFriend(toUpdate, toAdd)) throw new IllegalStateException("already added");
        DBObject playerDocument = getPlayerDocument(toUpdate);
        Object friendsObj = playerDocument.get("friends");
        if (friendsObj == null || !(friendsObj instanceof BasicDBList)) {
            friendsObj = new BasicDBList();
        }
        BasicDBList friendsList = (BasicDBList) friendsObj;
        if (!hasRequest(toUpdate, toAdd)) {
            throw new FriendRequestException("no request found");
        } else {
            DBObject newFriend = new BasicDBObjectBuilder()
                    .add("name", toAdd)
                    .add("added", new Date()).get();
            friendsList.add(newFriend);
            if (primary) {
                addFriendRequest(toAdd, toUpdate);
                addFriend(toAdd, toUpdate, false);
            }
            denyFriendRequest(toUpdate, toAdd);
        }
        playerDocument.put("friends", friendsList);
        getCollection().save(playerDocument);
    }

    public void removeFriend(String toUpdate, String toRemove, boolean primary) {
        DBObject playerDocument = getPlayerDocument(toUpdate);
        Object friendsObj = playerDocument.get("friends");
        if (friendsObj == null || !(friendsObj instanceof BasicDBList)) {
            return;
        }
        BasicDBList friendsList = (BasicDBList) friendsObj;
        for (Object object : friendsList) {
            if (!(object instanceof BasicDBObject)) continue;
            BasicDBObject friend = (BasicDBObject) object;
            String s = friend.getString("name");
            if (s.equals(toRemove)) {
                friendsList.remove(friend);
                if (primary) {
                    removeFriend(toRemove, toUpdate, false);
                }
                playerDocument.put("friends", friendsList);
                getCollection().save(playerDocument);
                return;
            }
        }
        throw new IllegalStateException("That player is not your friend");
    }

    /**
     * Checks if a player is friends
     * with another player
     *
     * @param friendOf Player to check friends list of
     * @param toCheck  Player to search list for
     * @return whether or not players are friends
     */
    public boolean isFriend(String friendOf, String toCheck) {
        Object friendsObj = getPlayerDocument(friendOf).get("friends");
        if (friendsObj == null || !(friendsObj instanceof BasicDBList)) return false;
        BasicDBList friendsList = (BasicDBList) friendsObj;
        for (Object object : friendsList) {
            if (!(object instanceof BasicDBObject)) continue;
            BasicDBObject friend = (BasicDBObject) object;
            String name = friend.getString("name");
            if (name.equals(toCheck)) return true;
        }
        return false;
    }


    public List<String> getPendingRequests(String player) {
        List<String> friends = new ArrayList<>();
        Object friendsObj = getPlayerDocument(player).get("friend_requests");
        if (friendsObj == null || !(friendsObj instanceof BasicDBList)) {
            return friends;
        }
        BasicDBList friendsList = (BasicDBList) friendsObj;
        for (Object o : friendsList) {
            if (!(o instanceof BasicDBObject)) continue;
            BasicDBObject friend = (BasicDBObject) o;
            String name = friend.getString("name");
            friends.add(name);
        }
        return friends;
    }

    public void addFriendRequest(String receiver, String from) {
        DBObject playerDocument = getPlayerDocument(receiver);
        Object friendsObj = playerDocument.get("friend_requests");
        if (friendsObj == null || !(friendsObj instanceof BasicDBList)) {
            friendsObj = new BasicDBList();
        }
        BasicDBList friendsList = (BasicDBList) friendsObj;
        if (hasRequest(receiver, from)) {
            throw new IllegalStateException("Friend request exists for that player");
        } else {
            DBObject newRequest = new BasicDBObjectBuilder()
                    .add("name", from)
                    .add("sent", new Date()).get();
            friendsList.add(newRequest);
        }
        playerDocument.put("friend_requests", friendsList);
        getCollection().save(playerDocument);
    }

    public void denyFriendRequest(String player, String toDeny) {
        DBObject playerDocument = getPlayerDocument(player);
        Object friendsObj = playerDocument.get("friend_requests");
        if (friendsObj == null || !(friendsObj instanceof BasicDBList)) {
            friendsObj = new BasicDBList();
        }
        BasicDBList friendsList = (BasicDBList) friendsObj;
        if (hasRequest(player, toDeny)) {
            for (Object object : friendsList) {
                if (!(object instanceof BasicDBObject)) continue;
                BasicDBObject friend = (BasicDBObject) object;
                String s = friend.getString("name");
                if (s.equals(toDeny)) {
                    friendsList.remove(friend);
                    playerDocument.put("friend_requests", friendsList);
                    getCollection().save(playerDocument);
                    return;
                }
            }
        } else {
            throw new IllegalStateException("No friend request from that player");
        }
        playerDocument.put("friend_requests", friendsList);
        getCollection().save(playerDocument);
    }

    public boolean hasRequest(String player, String fromPlayer) {
        Object friendsObj = getPlayerDocument(player).get("friend_requests");
        if (friendsObj == null || !(friendsObj instanceof BasicDBList)) {
            return false;
        }
        BasicDBList friendsList = (BasicDBList) friendsObj;
        for (Object object : friendsList) {
            if (!(object instanceof BasicDBObject)) continue;
            BasicDBObject friend = (BasicDBObject) object;
            String s = friend.getString("name");
            if (s.equals(fromPlayer)) {
                return true;
            }
        }
        return false;
    }
}
