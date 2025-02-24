package uk.ac.port.setap.team6c.database;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class UserCollection implements Iterable<User> {

    private final List<Integer> userIds;

    public UserCollection(List<Integer> userIds) {
        this.userIds = userIds;
    }

    /**
     * Convert this {@link UserCollection} to a list of users
     * <p>
     * Note that this method will access the database for each user in the collection and will create an instance of
     * {@link User} for each user. This can be an expensive operation if the collection is large. The size of the
     * collection should be checked before calling this method using {@link UserCollection#size()}. Prefer using the
     * iterator returned by {@link UserCollection#iterator()} if you only need to iterate over the collection as this
     * will lazy-load the users from the database as they are encountered.
     * <p>
     * If the user does not exist in the database, the user will not be included in the list. This may result in the
     * list being smaller than the collection in rare cases.
     * @return A list of users
     */
    public List<User> toList() {
        List<User> users = new ArrayList<>();
        for (int userId : userIds) {
            try {
                users.add(new User(userId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return users;
    }

    /**
     * Get the number of users in this collection
     * @return The number of users in this collection
     */
    public int size() {
        return userIds.size();
    }

    /**
     * Check if this collection contains a user
     * @param user The user to check for
     * @return True if the user is in this collection, false otherwise
     */
    public boolean contains(@NotNull User user) {
        return userIds.contains(user.getUserId());
    }

    public class UserCollectionIterator implements Iterator<User> {
        private final Iterator<Integer> userIdIterator = userIds.iterator();

        @Override
        public boolean hasNext() {
            return userIdIterator.hasNext();
        }

        @Override
        public User next() {
            try {
                return new User(userIdIterator.next());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public @NotNull Iterator<User> iterator() {
        return new UserCollectionIterator();
    }

    @Override
    public void forEach(Consumer<? super User> action) {
        Iterable.super.forEach(action);
    }

}
