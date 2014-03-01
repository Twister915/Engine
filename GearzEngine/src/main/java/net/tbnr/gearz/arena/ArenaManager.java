/*
 * Copyright (c) 2014.
 * Cogz Development LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.gearz.arena;

import com.mongodb.*;
import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.GearzException;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 9/5/13
 * Time: 11:26 PM
 * To change this template use File | Settings | File Templates.
 */
public final class ArenaManager {
    /**
     * Stores all the Arenas for this game
     */
    private List<Arena> arenas;
    /**
     * The collection where the arenas are stored.
     */
    private final DBCollection collection;

    /**
     * The arena class
     */
    private final Class<? extends Arena> arenaClass;

    /**
     * Creates an ArenaManager based off a game identifier
     *
     * @param gameId The game identifier
     */
    public ArenaManager(String gameId, Class<? extends Arena> arenaClass) throws GearzException {
        try {
            this.collection = Gearz.getInstance().getMongoDB().getCollection("arena_v2_" + gameId);
        } catch (NullPointerException ex) {
            Bukkit.shutdown();
            throw new GearzException("Cannot start ArenaManager! Not connected to Mongo!");
        }
        this.arenaClass = arenaClass;
        try {
            reloadArenas();
        } catch (GearzException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Loads the arenas into the List.
     */
    private void loadArenas() throws GearzException {
        DBCursor dbCursor = this.collection.find();
        if (dbCursor.size() == 0) {
            throw new GearzException("No arenas found!");
        }
        while (dbCursor.hasNext()) {
            DBObject next = dbCursor.next();
            Arena attempt;
            try {
                attempt = ArenaManager.arenaFromDBObject(this.arenaClass, next);
            } catch (GearzException e) {
                e.printStackTrace();
                continue;
            }
            this.arenas.add(attempt);
            Gearz.getInstance().getLogger().info("Loaded Arena - " + attempt.getName() + " by - " + attempt.getAuthors());
        }
    }

    /**
     * Clears the list, and loads the Arenas
     */
    private void reloadArenas() throws GearzException {
        this.arenas = new ArrayList<>();
        loadArenas();
    }

    /**
     * CRUD - C : Creates an arena in the database. Checks for existing data and falls back to CRUD - U
     *
     * @param arena The arena to check.
     * @throws GearzException This is when there is an exception parsing this arena.
     */
    public void addArena(Arena arena) throws GearzException {
        this.collection.insert(ArenaManager.objectFromArena(arena));
    }

    /**
     * CRUD - D : Deletes an entry from the database (removes the Arena from circulation)
     *
     * @param arena The arena to remove from the database.
     * @throws GearzException
     */
    public void removeArena(Arena arena) throws GearzException {
        this.collection.remove(new BasicDBObject("_id", new ObjectId(arena.getId())));
    }

    /**
     * CRUD - U : Updates a current arena using the ID from the Arena.
     *
     * @param arena The arena to update.
     * @throws GearzException
     */
    public void updateArena(Arena arena) throws GearzException {
        if (arena.getId() == null) {
            throw new GearzException("Cannot update a new arena!");
        }
        this.collection.save(ArenaManager.objectFromArena(arena));
    }

    /**
     * Gets all loaded arenas.
     *
     * @return The loaded arenas.
     */
    public List<Arena> getArenas() {
        return this.arenas;
    }

    /**
     * Converts an Arena object into a DB Object that can be reversed using the other method (which is not static)
     *
     * @param arena The arena to convert into a DBObject
     * @return The DBObject version of this Arena.
     */
    public static DBObject objectFromArena(Arena arena) {
        BasicDBObjectBuilder objectBuilder = new BasicDBObjectBuilder(); //Start building the database object for this arena
        for (Field field : arena.getClass().getFields()) { //Get all the fields ...
            if (!field.isAnnotationPresent(ArenaField.class)) {
                continue; //... that we can access, and are annotated by ArenaFeild ...
            }
            if (!field.getType().equals(ArenaIterator.class)) {
                continue; //... and are ArenaIterators.
            }
            ArenaField annotation = field.getAnnotation(ArenaField.class); //Get the Annotation from the field as an object
            ArenaIterator iterator; //Setup an object to put the iterator in
            try {
                iterator = (ArenaIterator) field.get(arena); //Try to get the arena iterator
            } catch (IllegalAccessException e) {
                continue; //Didn't work :o
            }
            BasicDBList list = new BasicDBList(); //Pour our list into the DB List object
            while (iterator.hasNext()) {
                Object next = iterator.next();
                if (next instanceof Point) { //Do some magic to store a point in the database
                    DBObject embeddedObject = new BasicDBObject();
                    Point p = (Point) next;
                    embeddedObject.put("x", p.getX());
                    embeddedObject.put("y", p.getY());
                    embeddedObject.put("z", p.getZ());
                    embeddedObject.put("pitch", p.getPitch());
                    embeddedObject.put("yaw", p.getYaw());
                    next = embeddedObject;
                }
                list.add(next); //Add whatever "next" is now. Depending on code above, it could be a DBObject, or whatever the iterator has in store.
            }
            objectBuilder.append(annotation.key(), list); //Put that in the database
        }
        objectBuilder.append("name", arena.getName()); //Meta for a bit.
        objectBuilder.append("description", arena.getDescription());
        objectBuilder.append("worldId", arena.getWorldId());
        objectBuilder.append("last-updated", Calendar.getInstance().getTimeInMillis());
        objectBuilder.append("author", arena.getAuthors());
        if (arena.getId() != null) {
            objectBuilder.append("_id", new ObjectId(arena.getId())); //Good for replacing/updating
        }
        return objectBuilder.get(); //Finish the object off! :D
    }

    public void logVotes(Arena arena, Integer votes) {
        DBObject object = this.collection.findOne(new BasicDBObject("_id", new ObjectId(arena.getId())));
        Integer votes1 = getInt(object.get("votes"));
        if (votes1 == null) {
            votes1 = 0;
        }
        votes1 += votes;
        object.put("votes", votes1);
        this.collection.save(object);
    }

    /**
     * Loads an arena
     *
     * @param object The DBObject that represents the arena
     * @return The arena object, fully loaded.
     * @throws GearzException When something goes wrong
     */
    public static Arena arenaFromDBObject(Class<? extends Arena> arenaClass, DBObject object) throws GearzException {
        Arena arena;
        try {
            Constructor constructor = arenaClass.getConstructor(String.class, String.class, String.class, String.class, String.class);
            if (constructor == null) {
                return null;
            }
            arena = (Arena) constructor.newInstance(object.get("name"), object.get("author"), object.get("description"), object.get("worldId"), object.get("_id").toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        for (Field field : arenaClass.getFields()) {
            if (!(field.isAnnotationPresent(ArenaField.class))) {
                continue;
            }
            if (!field.getType().equals(ArenaIterator.class)) {
                continue;
            }
            ArenaField annotation = field.getAnnotation(ArenaField.class);
            if (!object.containsField(annotation.key())) {
                continue;
            }
            Object o = object.get(annotation.key());
            if (!(o instanceof BasicDBList)) {
                continue;
            }
            BasicDBList list = (BasicDBList) o;
            List<Object> list2 = new ArrayList<>();
            for (Object lObject : list) {
                if (lObject instanceof DBObject) {
                    DBObject lObject1 = (DBObject) lObject;
                    if (!lObject1.containsField("x")) {
                        continue;
                    }
                    Point point = new Point(getDouble(lObject1.get("x")), getDouble(lObject1.get("y")), getDouble(lObject1.get("z")), getFloat(lObject1.get("pitch")), getFloat(lObject1.get("yaw")));
                    list2.add(point);
                    continue;
                }
                list2.add(lObject);
            }
            ArenaIterator iterator = new ArenaIterator<>(list2);
            iterator.setLoop(annotation.loop()); //Checks the annotation for this new looping thing. :D
            try {
                field.set(arena, field.getType().cast(iterator));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return arena;
    }

    private static Double getDouble(Object o) {
        if (o instanceof Double) {
            return (Double) o;
        }
        if (o instanceof Integer) {
            return ((Integer) o).doubleValue();
        }
        if (o instanceof Float) {
            return ((Float) o).doubleValue();
        }
        return null;
    }

    private static Float getFloat(Object o) {
        if (o instanceof Float) {
            return (Float) o;
        }
        if (o instanceof Double) {
            return ((Double) o).floatValue();
        }
        if (o instanceof Integer) {
            return ((Integer) o).floatValue();
        }
        return null;
    }

    private static Integer getInt(Object o) {
        if (o instanceof Integer) {
            return (Integer) o;
        }
        if (o instanceof Double) {
            return ((Double) o).intValue();
        }
        if (o instanceof Float) {
            return ((Float) o).intValue();
        }
        return null;
    }

    public List<Arena> getRandomArenas(int count) {
        List<Arena> arenas = new ArrayList<>();
        while (arenas.size() < count) {
            Arena a = null;
            while (a == null || arenas.contains(a)) {
                a = getArenas().get(Gearz.getRandom().nextInt(getArenas().size()));
            }
            arenas.add(a);
            if (arenas.containsAll(getArenas())) {
                break;
            }
        }
        return arenas;
    }
}
