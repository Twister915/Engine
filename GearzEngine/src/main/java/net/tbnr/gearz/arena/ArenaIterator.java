package net.tbnr.gearz.arena;

import net.tbnr.gearz.Gearz;
import net.tbnr.gearz.GearzException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Never create an instance directly of this, it will be managed by the setup or Gearz plugin.
 * Simply annotate this as a field of an Arena using @ArenaField :D
 *
 * @param <T> The type, normally Point.
 */
public class ArenaIterator<T> implements Iterator<T> {
    /**
     * This stores the data
     */
    private List<T> arrayList = new ArrayList<>();
    /**
     * This is the current index
     */
    private int index;
    /**
     * Stores if this Iterator is in looping mode.
     */
    private boolean loop;

    /**
     * Creates an ArenaIterator with the list of "stuff" passed in the first paramater.
     *
     * @param stuff Is the list added to the iterator.
     * @throws GearzException Thrown when there is invalid data. All data must be of type "Integer, Point, Double, Boolean, or String"
     */
    public ArenaIterator(List<T> stuff) throws GearzException {
        this.arrayList = stuff;
        this.index = 0;
    }

    /**
     * Creates an empty Iterator of type.
     *
     * @throws GearzException Thrown when you specify an invalid data type.
     */
    public ArenaIterator() throws GearzException {
        this(new ArrayList<T>());
    }

    /**
     * Has next?
     *
     * @return Has next.
     */
    @Override
    public boolean hasNext() {
        return (loop || this.arrayList.size() > index);  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Next.
     *
     * @return The next item in the iterator.
     */
    @Override
    public T next() {
        if (this.arrayList.size() == 0) {
            return null;
        }
        if (this.arrayList.size() - 1 == index && this.loop) {
            this.index = 0;
        }
        T t = this.arrayList.get(this.index);
        index++;
        return t;
    }

    /**
     * Removes the last item called from the array. Shouldn't be used that often, since it can cause a real problem.
     * Also it doesn't save.
     */
    @Override
    public void remove() {
        this.arrayList.remove(index - 1);
    }

    /**
     * Add the item to the array.
     *
     * @param thing The thing to add.
     */
    public void add(T thing) {
        this.arrayList.add(thing);
    }

    /**
     * Sets if this iterator is looping through the object infinitely.
     *
     * @param loop If it's looping or not.
     */
    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    /**
     * Checks if we are looping.
     *
     * @return If we are looping.
     */
    public boolean isLooping() {
        return this.loop;
    }

    /**
     * Gets the objects in the array.
     *
     * @return A List of type T containing all objects in the iterator.
     */
    public List<T> getArrayList() {
        return this.arrayList;
    }

    /**
     * Reset the index back to zero.
     */
    public void reset() {
        this.index = 0;
    }

    /**
     * Gets the current index of the iterator
     *
     * @return The index of the iterator in the Array.
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Checks if we already an object in the array list.
     *
     * @param object The object to check.
     * @return If the array already has this object.
     */
    public boolean contains(T object) {
        return this.arrayList.contains(object);
    }

    /**
     * Gives you a random item
     *
     * @return Random
     */
    public T random() {
        if (!this.loop) {
            return null;
        }
        return this.arrayList.get(Gearz.getRandom().nextInt(this.arrayList.size()));
    }

}
