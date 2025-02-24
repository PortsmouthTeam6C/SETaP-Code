package uk.ac.port.setap.team6c.database;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class SocietyCollection implements Iterable<Society> {

    private final List<Integer> societyIds;

    public SocietyCollection(List<Integer> societyIds) {
        this.societyIds = societyIds;
    }

    /**
     * Convert this {@link SocietyCollection} to a list of societies
     * <p>
     * Note that this method will access the database for each society in the collection and will create an instance of
     * {@link Society} for each society. This can be an expensive operation if the collection is large. The size of the
     * collection should be checked before calling this method using {@link SocietyCollection#size()}. Prefer using the
     * iterator returned by {@link SocietyCollection#iterator()} if you only need to iterate over the collection as this
     * will lazy-load the societies from the database as they are encountered.
     * <p>
     * If the society does not exist in the database, the society will not be included in the list. This may result in
     * the list being smaller than the collection in rare cases.
     * @return A list of societies
     */
    public List<Society> toList() {
        List<Society> societies = new ArrayList<>();
        for (int societyId : societyIds) {
            try {
                societies.add(new Society(societyId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return societies;
    }

    /**
     * Get the number of societies in this collection
     * @return The number of societies in this collection
     */
    public int size() {
        return societyIds.size();
    }

    /**
     * Check if this collection contains a society
     * @param society The society to check for
     * @return True if the society is in this collection, false otherwise
     */
    public boolean contains(@NotNull Society society) {
        return societyIds.contains(society.getSocietyId());
    }

    public class SocietyCollectionIterator implements Iterator<Society> {
        private final Iterator<Integer> societyIdIterator = societyIds.iterator();

        @Override
        public boolean hasNext() {
            return societyIdIterator.hasNext();
        }

        @Override
        public Society next() {
            try {
                return new Society(societyIdIterator.next());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public @NotNull Iterator<Society> iterator() {
        return new SocietyCollectionIterator();
    }

    @Override
    public void forEach(Consumer<? super Society> action) {
        Iterable.super.forEach(action);
    }

}
