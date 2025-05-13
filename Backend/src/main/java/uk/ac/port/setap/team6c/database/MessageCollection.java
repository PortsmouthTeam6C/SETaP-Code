package uk.ac.port.setap.team6c.database;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

@AllArgsConstructor
public class MessageCollection implements Iterable<Message> {

    private final List<Integer> messageIds;

    /**
     * Convert this {@link MessageCollection} to a list of messages
     * <p>
     * Note that this method will access the database for each message in the collection and will create an instance of
     * {@link Message} for each message. This can be an expensive operation if the collection is large. The size of the
     * collection should be checked before calling this method using {@link MessageCollection#size()}. Prefer using the
     * iterator returned by {@link MessageCollection#iterator()} if you only need to iterate over the collection as this
     * will lazy-load the messages from the database as they are encountered.
     * <p>
     * If the message does not exist in the database, the message will not be included in the list. This may result in
     * the list being smaller than the collection in rare cases.
     * @return A list of messages
     */
    public List<Message> toList() {
        List<Message> messages = new ArrayList<>();
        for (int messageId : messageIds) {
            try {
                messages.add(Message.get(messageId));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return messages;
    }

    /**
     * Get the number of messages in the collection
     * @return The number of messages in the collection
     */
    public int size() {
        return messageIds.size();
    }

    public class MessageCollectionIterator implements Iterator<Message> {
        private final Iterator<Integer> messageIdIterator = messageIds.iterator();

        @Override
        public boolean hasNext() {
            return messageIdIterator.hasNext();
        }

        @Override
        public Message next() {
            try {
                return Message.get(messageIdIterator.next());
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public @NotNull Iterator<Message> iterator() {
        return new MessageCollectionIterator();
    }

    @Override
    public void forEach(Consumer<? super Message> action) {
        Iterable.super.forEach(action);
    }

}
