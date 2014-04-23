/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.cogz.friends;

import com.mongodb.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Gearz Friends API
 */
public abstract class GearzFriends {

    public abstract DBCollection getCollection();

    public abstract DBObject getPlayerDocument(String player);

    public DBObject getById(ObjectId id) {
        DBObject query = new BasicDBObject("_id", id);
        return getCollection().findOne(query);
    }

    public ObjectId getObjectId(DBObject object) {
        return (ObjectId) object.get("_id");
    }

    public List<String> getPlayerFriends(String player) {
        List<String> friends = new ArrayList<>();
        Object friendsObj = getPlayerDocument(player).get("friends");
        if (friendsObj == null || !(friendsObj instanceof BasicDBList)) {
            return friends;
        }
        BasicDBList friendsList = (BasicDBList) friendsObj;
        System.out.println("SIZE: " + friendsList.size());
        for (Object o : friendsList) {
            if (!(o instanceof BasicDBObject)) continue;
            BasicDBObject friend = (BasicDBObject) o;
            ObjectId id = friend.getObjectId("name");
            System.out.println("ID found: " + id);
            DBObject friendObject = getById(id);
            System.out.println("FOUND DBOBJECT: " + friendObject.get("current_username"));
            friends.add((String) friendObject.get("current_username"));
        }
        for (String friend : friends ) {
            System.out.println("FOUND FRIEND: " +friend);
        }
        return friends;
    }

    public void addFriend(String toUpdate, String toAdd, boolean primary) throws FriendRequestException {
        if (isFriend(toUpdate, toAdd)) throw new IllegalStateException("Already added");
        DBObject playerDocument = getPlayerDocument(toUpdate);
        Object friendsObj = playerDocument.get("friends");
        if (friendsObj == null || !(friendsObj instanceof BasicDBList)) {
            friendsObj = new BasicDBList();
        }
        BasicDBList friendsList = (BasicDBList) friendsObj;
        if (!hasRequest(toUpdate, toAdd)) {
            throw new FriendRequestException("No request found");
        } else {
            DBObject newFriend = new BasicDBObjectBuilder()
                    .add("name", getObjectId(getPlayerDocument(toAdd)))
                    .add("added", new Date())
                    .get();
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
            throw new IllegalStateException("That player is not your friend");
        }
        BasicDBList friendsList = (BasicDBList) friendsObj;
        for (Object object : friendsList) {
            if (!(object instanceof BasicDBObject)) continue;
            BasicDBObject friend = (BasicDBObject) object;
            ObjectId s = friend.getObjectId("name");
            DBObject toRemoveDocument = getPlayerDocument(toRemove);
            if (s.equals(getObjectId(toRemoveDocument))) {
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
            DBObject toCheckDocument = getPlayerDocument(toCheck);
            ObjectId name = friend.getObjectId("name");
            if (name.equals(getObjectId(toCheckDocument))) {
                return true;
            }
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
            ObjectId id = friend.getObjectId("name");
            DBObject friendObject = getById(id);
            friends.add((String) friendObject.get("current_username"));
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
                    .add("name", getObjectId(getPlayerDocument(from)))
                    .add("sent", new Date())
                    .get();
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
                DBObject toRemoveDocument = getPlayerDocument(toDeny);
                ObjectId s = friend.getObjectId("name");
                if (s.equals(getObjectId(toRemoveDocument))) {
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
            DBObject toCheckDocument = getPlayerDocument(fromPlayer);
            ObjectId name = friend.getObjectId("name");
            if (name.equals(getObjectId(toCheckDocument))) {
                return true;
            }
        }
        return false;
    }
}
