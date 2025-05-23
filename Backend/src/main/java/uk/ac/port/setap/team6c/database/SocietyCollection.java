package uk.ac.port.setap.team6c.database;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A lazy-loaded collection of {@link Society} objects
 */
@AllArgsConstructor
public class SocietyCollection implements Iterable<Society> {

    private final List<Integer> societyIds;

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
                societies.add(Society.get(societyId));
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

    /**
     * Filter societies by a predicate
     * @param predicate The predicate to test societies against. The parameter is the societyId, to make this a
     *                  {@link Society} object use {@link Society#get(int)}
     * @return A new SocietyCollection
     */
    public SocietyCollection filter(Function<Integer, Boolean> predicate) {
        List<Integer> passedTest = new ArrayList<>();
        for (int id : societyIds) {
            if (predicate.apply(id))
                passedTest.add(id);
        }
        return new SocietyCollection(passedTest);
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
                return Society.get(societyIdIterator.next());
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
