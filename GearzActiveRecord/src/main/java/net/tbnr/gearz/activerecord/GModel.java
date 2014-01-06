package net.tbnr.gearz.activerecord;

import com.mongodb.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Models you create should extend this class
 * All fields annotated with {@link BasicField}
 *
 * @author Joey Sacchini
 * @version 1.0
 */
@SuppressWarnings({"UnusedDeclaration", "unchecked"})
@EqualsAndHashCode(of = {"objectId"}, doNotUseGetters = true)
@ToString(includeFieldNames = false)
public abstract class GModel {
    /**
     * This is the database that the GearzModel will operate in
     */
    @Setter private static DB defaultDatabase;

    /**
     * The ID of this object. Is null by default
     */
    @Getter private ObjectId objectId;

    /**
     * Database
     */
    private DB database;

    /**
     * The collection where this data will be saved
     */
    private DBCollection collection;

    /**
     * Used for building an object.
     */
    private BasicDBObjectBuilder basicDBObjectBuilder;

    /**
     * Creates a {@link GModel} with default values
     */
    public GModel() {
        this.database = GModel.defaultDatabase;
        this.objectId = null;
        setupAllEmptys();
        loadCollection();
    }

    /**
     * new GModel with overridden database
     *
     * @param database database
     */
    public GModel(DB database) {
        this.objectId = null;
        this.database = database;
        setupAllEmptys();
        loadCollection();
    }

    /**
     * Loads a GModel from an existing {@link DBObject} in a Database
     *
     * @param database The database to reference for linked objects.
     * @param dBobject The {@link DBObject} to load data from.
     */
    public GModel(DB database, DBObject dBobject) {
        this.database = database;
        this.objectId = (ObjectId) dBobject.get("_id");
        loadCollection();
        List<BasicAnalyzedField> allFields = getAllFields();
        for (BasicAnalyzedField analyzedField : allFields) {
            if (!dBobject.containsField(analyzedField.getKey())) {
                setupEmptyField(analyzedField.getField());
            }
            Object o = dBobject.get(analyzedField.getKey());
            o = readObjectFromDB(o);
            try {
                analyzedField.getField().set(this, o);
            } catch (IllegalAccessException e) {
                e.printStackTrace(); //TODO remove this
            }
        }
    }

    private void setupEmptyField(Field f) {
        Type type = f.getGenericType();
        Object setTo = null;
        if (List.class.isAssignableFrom(f.getType())) {
            setTo = new ArrayList<>();
        }
        if (Map.class.isAssignableFrom(f.getType())) {
            setTo = new HashMap<>();
        }
        if (setTo == null) return;
        try {
            f.set(this, setTo);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void setupAllEmptys() {
        for (Field f : this.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            if (!f.isAnnotationPresent(BasicField.class)) continue;
            try {
                if (f.get(this) != null) continue;
            } catch (IllegalAccessException e) {
                e.printStackTrace(); //TODO remove
                continue;
            }
            setupEmptyField(f);
        }
    }

    /**
     * Updates the internal variables to be consistent.
     */
    private void updateObjects() {
        this.basicDBObjectBuilder = new BasicDBObjectBuilder();
        for (BasicAnalyzedField analyzedField : getAllFields()) {
            if (!isValidValue(analyzedField.getValue())) continue;
            this.basicDBObjectBuilder.append(analyzedField.getKey(), analyzedField.getValue());
        }
        if (this.objectId != null) {
            this.basicDBObjectBuilder.append("_id", this.objectId);
        }
        this.basicDBObjectBuilder.append("_class", this.getClass().getName());
        this.basicDBObjectBuilder.append("_schema_v", "1.0");
    }

    /**
     * Saves the data to the database. Possibly updates it.
     */
    public void save() {
        DBObject objectValue = this.getObjectValue();
        WriteResult save = this.collection.save(objectValue);
        this.objectId = (ObjectId) objectValue.get("_id");
    }

    /**
     * Process each field
     *
     * @return All processed fields.
     */
    private List<BasicAnalyzedField> getAllFields() {
        ArrayList<BasicAnalyzedField> fields = new ArrayList<>();
        for (Field f : this.getClass().getDeclaredFields()) {
            if (!f.isAnnotationPresent(BasicField.class)) continue;
            BasicField annotation = f.getAnnotation(BasicField.class);
            BasicAnalyzedField analyzedField = new BasicAnalyzedField((annotation.key().equals("") ? f.getName().toLowerCase() : annotation.key()), f);
            if (analyzedField.getKey().equals("_id") ||
                    analyzedField.getKey().equals("_link_flag") ||
                    analyzedField.getKey().equals("_class") ||
                    analyzedField.getKey().equals("_schema_v")) continue;
            f.setAccessible(true);
            Object o;
            try {
                o = f.get(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace(); //TODO remove this
                return null;
            }
            o = processField(o, f, analyzedField.getKey());
            analyzedField.setValue(o);
            fields.add(analyzedField);
        }
        return fields;
    }

    /**
     * Will turn an object within the code into something for the database (that can be read by method below)
     *
     * @param f     The field
     * @param dbKey The key for the field in the database (used for auto-increments on fields)
     * @return The processed field value.
     */
    private Object processField(Object o, Field f, String dbKey) {
        if (o == null) {
            if (!f.isAnnotationPresent(AutoIncrement.class)) return null;
            if (!f.getType().equals(Integer.class)) return null;
            Integer i = 1;
            DBObject obj = null;
            while (obj == null || this.collection.findOne(obj) == null) {
                i++;
                obj = new BasicDBObject(dbKey, i);
            }
            o = i;
            try {
                f.set(this, o);
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //TODO remove this
                return null;
            }
        }
        if (o instanceof GModel) {
            if (f.isAnnotationPresent(LinkedObject.class)) {
                ObjectId objectId1 = ((GModel) o).getObjectId();
                if (objectId1 == null) {
                    ((GModel) o).save();
                    objectId1 = ((GModel) o).getObjectId();
                }
                BasicDBObject object = new BasicDBObject();
                object.put("_class", o.getClass().getName());
                object.put("_id", objectId1);
                object.put("_link_flag", true);
                o = object;
            } else if (f.isAnnotationPresent(EmbeddedObject.class)) {
                o = ((GModel) o).getObjectValue();
            } else {
                return null;
            }
        }
        if (o instanceof Map) {
            o = processMap(f, dbKey, (Map) o);
        }
        if (o instanceof List) {
            o = processList(f, dbKey, (List) o);
        }
        return o;
    }

    private DBObject processMap(Field f, String dbKey, Map o) {
        DBObject object = new BasicDBObject();
        for (Object o1 : o.keySet()) {
            if (!(o1 instanceof String)) continue;
            String key = (String) o1;
            Object value = o.get(key);
            value = processField(value, f, dbKey);
            object.put(key, value);
        }
        return object;
    }

    /**
     * This is used to take a DB object, and read it. It will convert linked objets, embedded objects, and lists of anything.
     *
     * @param o The object from the database
     * @return Processed data.
     */
    private Object readObjectFromDB(Object o) {
        if (o == null) return null;
        if (o instanceof DBObject) {
            if (o instanceof BasicDBList) {
                BasicDBList l = (BasicDBList) o;
                List list = new ArrayList();
                for (Object next : l) {
                    list.add(readObjectFromDB(next));
                }
                o = list;
            } else {
                Object aClass = ((DBObject) o).get("_class");
                if (aClass == null) {
                    DBObject dBobject = (DBObject) o;
                    HashMap<String, Object> m = new HashMap<>();
                    for (String s : dBobject.keySet()) {
                        m.put(s, dBobject.get(s));
                    }
                    return m;
                }
                if (!(aClass instanceof String)) return null;
                Class c;
                try {
                    c = Class.forName((String) aClass);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace(); //TODO remove this
                    return null;
                }
                if (!c.isInstance(GModel.class)) return null;
                GModel m = modelFromOne(c, (DBObject) o, this.database);
                if (((DBObject) o).containsField("_link_flag")) {
                    m = m.findOne();
                }
                o = m;
            }
        }
        return o;
    }

    /**
     * Turns this into a DBObject
     *
     * @return DBObject
     */
    DBObject getObjectValue() {
        updateObjects();
        return basicDBObjectBuilder.get();
    }

    /**
     * Validates the type.
     *
     * @param o Object to test.
     * @return If we can store this object.
     */
    static boolean isValidValue(Object o) {
        return (o instanceof String) ||
                (o instanceof Integer) ||
                (o instanceof Float) ||
                (o instanceof Long) ||
                (o instanceof DBObject) ||
                (o instanceof Boolean) ||
                (o instanceof ObjectId) ||
                (o instanceof Double) ||
                (o instanceof Character) ||
                (o instanceof Short);
    }

    /**
     * Processes a list into a {@link BasicDBList}
     *
     * @param l The {@link List} object
     * @return The {@link BasicDBList} object.
     */
    private BasicDBList processList(Field f, String dbKey, List l) {
        BasicDBList list = new BasicDBList();
        for (Object o : l) {
            Object o1 = processField(o, f, dbKey);
            if (!isValidValue(o1)) continue;
            list.add(o1);
        }
        return list;
    }

    /**
     * Finds a single object from the database based upon the values of this object.
     *
     * @return one.
     */
    public GModel findOne() {
        DBObject objectValue = this.getObjectValue();
        DBObject one = this.collection.findOne(objectValue);
        if (one == null) return null;
        GModel gModel = modelFromOne(this.getClass(), one, this.database);
        gModel.database = this.database;
        gModel.updateObjects();
        return gModel;
    }

    /**
     * Finds many from the params supplied
     *
     * @return All objects.
     */
    public List<GModel> findMany() {
        ArrayList<GModel> models = new ArrayList<>();
        DBCursor dbObjects = this.collection.find(this.getObjectValue());
        for (DBObject o : dbObjects) {
            GModel m = modelFromOne(this.getClass(), o, this.database);
            models.add(m);
        }
        return models;
    }

    public List<GModel> findAll() {
        ArrayList<GModel> models = new ArrayList<>();
        DBCursor dbObjects = this.collection.find();
        for (DBObject o : dbObjects) {
            GModel m = modelFromOne(this.getClass(), o, this.database);
            models.add(m);
        }
        return models;
    }

    /**
     * Generates a GModel class from the database (read)
     *
     * @param clazz    GModel
     * @param one      The object
     * @param database The database
     * @return A {@link GModel} or null if there is an error getting the value from the database
     */
    static GModel modelFromOne(Class<? extends GModel> clazz, DBObject one, DB database) {
        Constructor<? extends GModel> constructor;
        try {
            constructor = clazz.getConstructor(DB.class, DBObject.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace(); //TODO remove this
            return null;
        }
        GModel gModel;
        try {
            gModel = constructor.newInstance(database, one);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace(); //TODO remove this
            return null;
        }
        return gModel;
    }

    /**
     * Loads the collection for the constructors.
     */
    private void loadCollection() {
        String name;
        if (this.getClass().isAnnotationPresent(Collection.class)) {
            Collection annotation = this.getClass().getAnnotation(Collection.class);
            name = annotation.name();
        } else {
            name = this.getClass().getSimpleName().toLowerCase();
            name = name + "s";
        }
        this.collection = this.database.getCollection(name);
    }

    public void remove() {
        if (this.objectId == null) return;
        this.collection.remove(new BasicDBObject("_id", this.objectId));
    }
}