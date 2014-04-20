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

package net.tbnr.gearz.arena;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import lombok.Getter;
import net.tbnr.gearz.GearzException;

import java.util.List;

@SuppressWarnings("unchecked")
public enum ArenaFieldSerializer {
    POINT(new SerializationDelegate<Point>() {
        @Override
        protected DBObject getObjectForInternal(Point p) {
            DBObject embeddedObject = new BasicDBObject();
            embeddedObject.put("x", p.getX());
            embeddedObject.put("y", p.getY());
            embeddedObject.put("z", p.getZ());
            embeddedObject.put("pitch", p.getPitch());
            embeddedObject.put("yaw", p.getYaw());
            return embeddedObject;
        }

        @Override
        protected Point getObjectForInternal(DBObject object) {
            try {
                double x = ((Number) object.get("x")).doubleValue();
                double y = ((Number) object.get("y")).doubleValue();
                double z = ((Number) object.get("z")).doubleValue();
                float pitch = ((Number) object.get("pitch")).floatValue();
                float yaw = ((Number) object.get("yaw")).floatValue();
                return new Point(x, y, z, pitch, yaw);
            }
            catch (ClassCastException | NullPointerException ex) {
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected ArenaIterator<Point> getNewIterator(List<Point> values) throws GearzException {
            return new PointIterator(values);
        }

        @Override
        protected String getContainerName() {
            return "POINT";
        }
    }, Point.class),
    REGION(new SerializationDelegate<Region>() {
        @Override
        protected DBObject getObjectForInternal(Region object) {
            SerializationDelegate delegate1 = ArenaFieldSerializer.POINT.getDelegate();
            DBObject maximum = delegate1.getObjectFor(object.getMaximum());
            DBObject minimum = delegate1.getObjectFor(object.getMinimum());
            BasicDBObject rValue = new BasicDBObject();
            rValue.put("minimum", minimum);
            rValue.put("maximum", maximum);
            return rValue;
        }

        @Override
        protected Region getObjectForInternal(DBObject object) {
            DBObject minimum = (DBObject) object.get("minimum");
            DBObject maximum = (DBObject) object.get("maximum");
            return new Region((Point)POINT.getDelegate().getObjectFor(minimum), (Point)POINT.getDelegate().getObjectFor(maximum));
        }

        @Override
        protected ArenaIterator<Region> getNewIterator(List<Region> values) throws GearzException {
            return new RegionIterator(values);
        }

        @Override
        protected String getContainerName() {
            return "REGION";
        }
    }, Region.class);

    @Getter private final SerializationDelegate delegate;
    @Getter private final Class type;

    <T> ArenaFieldSerializer(SerializationDelegate<T> delegate, Class<T> type) {
        this.delegate = delegate;
        this.type = type;
    }

    static abstract class SerializationDelegate<T> {
        protected abstract DBObject getObjectForInternal(T object);
        protected abstract T getObjectForInternal(DBObject object);
        protected abstract ArenaIterator<T> getNewIterator(List<T> values) throws GearzException;
        public DBObject getObjectFor(Object object) {
            try {
                DBObject objectForInternal = getObjectForInternal((T) object);
                objectForInternal.put("_TYPE", getContainerName());
                return objectForInternal;
            } catch (ClassCastException ex) {
                return null;
            }
        }

        protected abstract String getContainerName();

        public Object getObjectFor(DBObject object) {
            return getObjectForInternal(object);
        }
    }

    public static <T> SerializationDelegate<T> getSerializerFor(Class<T> clazz) {
        for (ArenaFieldSerializer arenaFieldSerializer : values()) {
            if (arenaFieldSerializer.getType().equals(clazz)) return arenaFieldSerializer.getDelegate();
        }
        return null;
    }

    public static SerializationDelegate getSerializerFor(DBObject object) {
        //HOTFIX no type = point
        Object type1 = object.get("_TYPE");
        if (type1 == null || !(type1 instanceof String)) return POINT.getDelegate();
        String t = (String)type1;
        for (ArenaFieldSerializer serializer : values()) {
            if (serializer.getClass().getSimpleName().equals(t)) return serializer.getDelegate();
        }
        return POINT.getDelegate();
    }
}
