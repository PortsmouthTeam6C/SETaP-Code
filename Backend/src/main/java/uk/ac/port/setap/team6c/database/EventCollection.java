package uk.ac.port.setap.team6c.database;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

@AllArgsConstructor
public class EventCollection implements Iterable<Event> {

    private final List<Integer> eventIds;

    /**
     * Convert this {@link EventCollection} to a list of Events
     * <p>
     * Note that this method will access the database for each event in the collection and will create an instance of
     * {@link Event} for each event. This can be an expensive operation if the collection is large. The size of the
     * collection should be checked before calling this method using {@link EventCollection#size()}. Prefer using the
     * iterator returned by {@link EventCollection#iterator()} if you only need to iterate over the collection as this
     * will lazy-load the events from the database as they are encountered.
     * <p>
     * If the event does not exist in the database, the event will not be included in the list. This may result in the
     * list being smaller than the collection in rare cases.
     * @return A list of events
     */
    public List<Event> toList() {
        List<Event> events = new ArrayList<>();
        for (int eventId : eventIds) {
            try {
                events.add(Event.get(eventId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return events;
    }

    /**
     * Get the number of eventss in this collection
     * @return The number of events in this collection
     */
    public int size() {
        return eventIds.size();
    }

    /**
     * Check if this collection contains a event
     * @param event The event to check for
     * @return True if the event is in this collection, false otherwise
     */
    public boolean contains(@NotNull Event event) {
        return eventIds.contains(event.getEventId());
    }

    public class EventCollectionIterator implements Iterator<Event> {
        private final Iterator<Integer> eventIdIterator = eventIds.iterator();

        @Override
        public boolean hasNext() {
            return eventIdIterator.hasNext();
        }

        @Override
        public Event next() {
            try {
                return Event.get(eventIdIterator.next());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public @NotNull Iterator<Event> iterator() {
        return new EventCollectionIterator();
    }

    @Override
    public void forEach(Consumer<? super Event> action) {
        Iterable.super.forEach(action);
    }

}
