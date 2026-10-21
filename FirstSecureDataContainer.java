// GIUSEPPE MUSCHETTA, corso A, prof. Ferrari

package com.company;

import java.util.*;

public class FirstSecureDataContainer<E> implements SecureDataContainer<E> {
    /*

      OVERVIEW: SecureDataContainer<E> is a container of objects of type E. Intuitively, the collection behaves like
                a Data Storage for storing and sharing data (represented in the simulation by
                objects of type E). The collection must guarantee a data security mechanism by providing its own
                user identity management mechanism. Furthermore, the collection must provide a mechanism of
                access control that allows the owner of the data to perform a selective and restrictive
                access to his data in the collection. Some users may be authorized by the owner
                to access the data, while others can not access it without authorization.

      ABSTRACTION FUNCTION:
                            f: (users.contains(key) , container.get(index).data ) --> { Item1 , Item2, ... , last Item}
                            to each authenticated user is associated a set of his own E data elements

      REPRESENTATION INVARIANT:
                                users != null && container != null
                                && forall i. 0 <= i < container.size() ==> (container.get(i) != null)
                                && this.users cannot contain duplicate keys (where keys are user IDs)
                                && this.users cannot contain null keys
                                && this.users cannot contain null values (informal)
     */

    private class Item {

        /*
           OVERVIEW: Instance of class Item contains the data itself and two strings:
                     the first string is "owner" and indicates which user, in general, has access to that data;
                     the second string "firstOwner" indicates the user that inserted that particular data
                     in the container for the first time.
                     When a user adds his data to the container, the constructor will put his name
                     both on field "owner" and "firstOwner" so that he is the firstOwner of the object he put in

           Representation Invariant: owner != null && firstOwner != null && data != null
         */

        private String owner;
        private String firstOwner;
        private E data;

        public Item(String owner, String firstOwner, E data) {
            if(owner == null || firstOwner == null || data == null)
                throw new NullPointerException();
            this.owner = owner;
            this.firstOwner = firstOwner;
            this.data = data;
        }
    }

    //           Keys   Values            keys are users Id, values are their passwords
    private Map<String, String> users;
    private List<Item> container;

    public FirstSecureDataContainer() {
        users = new HashMap<>();
        container = new LinkedList<>();
    }


    @Override
    public void createUser(String id, String passw) throws ExistingUserException {
        /*
          REQUIRES: id != null && passw != null
            THROWS: NullPointerException if !(id != null && passw != null) (UNCHECKED)
                    ExistingUserException if user with that id already exists in the container (CHECKED)
          MODIFIES: this.users
           EFFECTS: Creates a new user in the container
         */
        credentialsNotNull(id, passw);
        if (!userNameExists(id))
            users.put(id, passw);
        else
            throw new ExistingUserException("User " + id + " already exists in the container");
    }

    @Override
    public void removeUser(String id, String passw) throws UserNotFoundException {
        /*
          REQUIRES: id != null && passw != null
            THROWS: NullPointerException if !(id != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user with that id already exists in the container (CHECKED)
          MODIFIES: this.users
           EFFECTS: Removes the user, user's data and user's shared data
         */
        credentialsNotNull(id, passw);
        if (userIsInTheContainer(id, passw)) {
            for (int i = 0; i < container.size(); i++) {
                if (container.get(i).owner.equals(id) || container.get(i).firstOwner.equals(id)) {
                    container.remove(i);
                    i--; //to compensate the removal
                }
            }
            users.remove(id, passw);
        } else
            throw new UserNotFoundException("User not found in the container");
    }

    @Override
    public int getSize(String owner, String passw) throws UserNotFoundException {
         /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException if !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container
           EFFECTS: Returns the cardinality of a user's items (shared from others plus his own data)
         */
        credentialsNotNull(owner, passw);
        if (userIsInTheContainer(owner, passw)) {
            int counter = 0;
            for (int i = 0; i < container.size(); i++) {
                if (container.get(i).owner.equals(owner)) {
                    counter++;
                }
            }
            return counter;
        } else
            throw new UserNotFoundException("User not found in the container");
    }

    @Override
    public boolean put(String owner, String passw, E data) throws UserNotFoundException {
         /*
          REQUIRES: owner != null && passw != null && data != null
            THROWS: NullPointerException if !(owner != null && passw != null)  (UNCHECKED)
                                      or if data == null
                    UserNotFoundException if user is not found in the container (CHECKED)
          MODIFIES: this.container
           EFFECTS: Adds a new item in the collection in which user is both the owner and
                    the firstOwner
         */
        credentialsNotNull(owner, passw);
        dataNotNull(data);
        if (userIsInTheContainer(owner, passw)) {
            Item newObj = new Item(owner, owner, data);
            return container.add(newObj);
        } else
            throw new UserNotFoundException("User not found in the container");
    }

    @Override
    public E get(String owner, String passw, E data) throws UserNotFoundException, DataNotFoundException {
         /*
          REQUIRES: owner != null && passw != null && data != null
            THROWS: NullPointerException if !(owner != null && passw != null)  (UNCHECKED)
                                      or if data == null
                    UserNotFoundException if user is not found in the container (CHECKED)
                    DataNotFoundException if data is not found in the user owned items (CHECKED)
           EFFECTS: Returns a copy of the item (that user already owns) from the collection
         */
        credentialsNotNull(owner, passw);
        dataNotNull(data);
        if (userIsInTheContainer(owner, passw)) {
            if (userOwnsTheData(owner, data)) {
                int index = indexOfUserOwnsTheData(owner, data);
                Item copy = new Item(container.get(index).owner,
                        container.get(index).firstOwner, container.get(index).data);
                return copy.data;
            } else
                throw new DataNotFoundException("Element " + data + " has not been found!");
        } else
            throw new UserNotFoundException("User not found in the container");
    }


    @Override
    public E remove(String owner, String passw, E data) throws UserNotFoundException, DataNotFoundException {
         /*
          REQUIRES: owner != null && passw != null && data != null
            THROWS: - NullPointerException if !(owner != null && passw != null)  (UNCHECKED)
                                      or if data == null
                    - UserNotFoundException if user is not found in the container (CHECKED)
                    - DataNotFoundException if data is not found in the user owned items (CHECKED)
          MODIFIES: this.container
           EFFECTS: If user is the firstOwner the method will delete his data and all the same data
                    that he shared to others, otherwise (if user is not the firstOwner) it will only
                    delete his data
         */
        credentialsNotNull(owner, passw);
        dataNotNull(data);
        if (userIsInTheContainer(owner, passw)) {
            if (userOwnsTheData(owner, data)) {
                int index = indexOfUserOwnsTheData(owner, data);
                E copy = container.get(index).data;
                if (container.get(index).firstOwner.equals(owner)) {
                    //sono il first owner
                    for (int i = 0; i < container.size(); i++) {
                        if (container.get(i).firstOwner.equals(owner)
                                && container.get(i).data.equals(data)
                                && !(container.get(i).owner.equals(owner))) {
                            container.remove(i);
                            i--; //to compensate the removal
                        }
                    }
                    int finalIndex = indexOfUserOwnsTheData(owner, data);
                    container.remove(finalIndex);
                } else {
                    //non sono il first owner
                    container.remove(index);
                }
                return copy;
            } else
                throw new DataNotFoundException("Element has not been found!");
        } else
            throw new UserNotFoundException("User not found in the container");
    }


    @Override
    public void copy(String owner, String passw, E data) throws UserNotFoundException, DataNotFoundException {
         /*
          REQUIRES: owner != null && passw != null && data != null
            THROWS: NullPointerException if !(owner != null && passw != null)  (UNCHECKED)
                                      or if data == null
                    UserNotFoundException if user is not found in the container (CHECKED)
                    DataNotFoundException if data is not found in the user owned items (CHECKED)
          MODIFIES: this.container because it will add the copy in the container, modifying the collection
           EFFECTS: Creates and inserts a copy of the item, using the equals method provided by the object
                    that user already owns, in the collection

         */
        credentialsNotNull(owner, passw);
        dataNotNull(data);
        if (userIsInTheContainer(owner, passw)) {
            if (userOwnsTheData(owner, data)) {
                int index = indexOfUserOwnsTheData(owner, data);
                String _owner = container.get(index).owner;
                String _firstOwner = container.get(index).firstOwner;
                E _data = container.get(index).data;
                Item copy = new Item(_owner, _firstOwner, _data);
                container.add(copy);
            } else
                throw new DataNotFoundException("Element has not been found!");
        } else
            throw new UserNotFoundException("User not found in the container");
    }

    @Override
    public void share(String owner, String passw, String other, E data) throws UserNotFoundException,
            DataNotFoundException {
         /*
          REQUIRES: owner != null && passw != null && other != null && data != null
            THROWS: NullPointerException if !(owner != null && passw != null)  (UNCHECKED)
                                      or if other == null or if data == null
                    UserNotFoundException if user is not found in the container (CHECKED)
                    DataNotFoundException if data is not found in the user owned items (CHECKED)
          MODIFIES: this.container because it will add the shared item in the container,
                    actually modifying the collection
           EFFECTS: Shares the data with another user in the container by creating a new object
                    where the firstOwner remains the same, and the "other" user becomes the new owner.
         */
        credentialsNotNull(owner, passw);
        otherNotNull(other);
        dataNotNull(data);
        if (userIsInTheContainer(owner, passw)) {
            if (userOwnsTheData(owner, data)) {
                if (otherIsInTheCollection(other)) {
                    int index = indexOfUserOwnsTheData(owner, data);
                    E temp = container.get(index).data;
                    String _firstOwner = container.get(index).firstOwner;
                    Item newObj = new Item(other, _firstOwner, temp);
                    container.add(newObj);
                } else
                    throw new UserNotFoundException("User not found in the container");
            } else
                throw new DataNotFoundException("Element has not been found!");
        } else
            throw new UserNotFoundException("User not found in the container");
    }

    @Override
    public Iterator<E> getIterator(String owner, String passw) throws UserNotFoundException {
        /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException if !(owner != null && passw != null)  (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
           EFFECTS: Returns an iterator that will iterate only through the items owned by the user;
                    removals are not supported.
         */
        credentialsNotNull(owner, passw);
        ArrayList<E> temp = new ArrayList<>();
        if (userIsInTheContainer(owner, passw)) {
            for (int i = 0; i < container.size(); i++) {
                if (container.get(i).owner.equals(owner))
                    temp.add(container.get(i).data);
            }
        } else
            throw new UserNotFoundException("User not found in the container");

        return new Iterator<E>() {

            int index = 0;

            @Override
            public boolean hasNext() {
                return index < temp.size();
            }

            @Override
            public E next() {
                if (hasNext()) {
                    E value = temp.get(index);
                    index = index + 1;
                    return value;
                } else
                    throw new NoSuchElementException("No more positions available"); //(unchecked)
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Removals are not supported"); //(unchecked)
            }

        };
    }


    //---------------------------------- OPTIONAL METHODS -------------------------------------

    public void removeAll(String owner, String passw, E data) throws UserNotFoundException, DataNotFoundException {
        /*
          REQUIRES: owner != null && passw != null && data != null
            THROWS: - NullPointerException if !(owner != null && passw != null)  (UNCHECKED)
                                      or if data == null
                    - UserNotFoundException if user is not found in the container (CHECKED)
                    - DataNotFoundException if data is not found in the user owned items (CHECKED)
          MODIFIES: this.container
           EFFECTS: Removes all elements equals to "data" from the user's collection. It will remove
                    his very items and also the items shared from others to him.
         */
        credentialsNotNull(owner, passw);
        if (userIsInTheContainer(owner, passw)) {
            if (userOwnsTheData(owner, data)) {
                for (int i = 0; i < container.size(); i++) {
                    if (container.get(i).data.equals(data)) {
                        if (container.get(i).owner.equals(owner) || container.get(i).firstOwner.equals(owner)) {
                            container.remove(i);
                            i--;
                        }
                    }
                }
            } else
                throw new DataNotFoundException("Element " + data + " has not been found!");
        } else
            throw new UserNotFoundException("User not found in the container");
    }

    public void printFirstOwnerData(String owner, String passw) throws UserNotFoundException {
        /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
           EFFECTS: Prints only the items in which the user is the firstOwner
         */
        credentialsNotNull(owner, passw);
        if (userIsInTheContainer(owner, passw)) {
            for (int i = 0; i < container.size(); i++) {
                if (container.get(i).firstOwner.equals(owner)
                        && container.get(i).owner.equals(owner)) {
                    System.out.println(container.get(i).data);
                }
            }
        } else
            throw new UserNotFoundException("User not found in the container");

    }

    public void printSharedFromOthersData(String owner, String passw) throws UserNotFoundException {
        /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
           EFFECTS: Prints only the items shared from others to the user
         */
        credentialsNotNull(owner, passw);
        if (userIsInTheContainer(owner, passw)) {
            for (int i = 0; i < container.size(); i++) {
                if (!(container.get(i).firstOwner.equals(owner))
                        && container.get(i).owner.equals(owner)) {
                    System.out.println(container.get(i).data);
                }
            }
        } else
            throw new UserNotFoundException("User not found in the container");
    }

    public void printTotalData(String owner, String passw) throws UserNotFoundException {
        /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
           EFFECTS: Prints all items in the user's collection
         */
        credentialsNotNull(owner, passw);
        if (userIsInTheContainer(owner, passw)) {
            int counter=0;
            for (int i = 0; i < container.size(); i++) {
                if (container.get(i).owner.equals(owner)) {
                    System.out.println(container.get(i).data);
                    counter++;
                }
            }
            if(counter!=0)
                System.out.println(" ");
            else
                System.out.println("[none]");
        } else
            throw new UserNotFoundException("User not found in the container");
    }


    public boolean isEmpty(String owner, String passw) throws UserNotFoundException {
        /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
           EFFECTS: Checks whether users's collection is emptty
         */
        credentialsNotNull(owner, passw);
        if (userIsInTheContainer(owner, passw)) {
            return (this.getSize(owner, passw) == 0);
        } else
            throw new UserNotFoundException("User not found in the container");
    }

    public boolean isIn(String owner, String passw, E data) throws UserNotFoundException {
        /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
           EFFECTS: Checks whether "data" belongs to user's collection
         */
        credentialsNotNull(owner, passw);
        if (userIsInTheContainer(owner, passw)) {
            return (userOwnsTheData(owner, data));
        } else
            throw new UserNotFoundException("User not found in the container");
    }

    public void clear(String owner, String passw) throws UserNotFoundException {
        /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
          MODIFIES: this.container
           EFFECTS: Removes the data both from the user and from the people to whom
                    the user has shared the data.
         */
        credentialsNotNull(owner, passw);
        if (userIsInTheContainer(owner, passw)) {
            for (int i = 0; i < container.size(); i++) {
                if (container.get(i).owner.equals(owner) || container.get(i).firstOwner.equals(owner)) {
                    container.remove(i);
                    i--;
                }
            }
        } else
            throw new UserNotFoundException("User not found in the container");
    }

    @Override
    public void printUsersId(String owner, String passw) throws UserNotFoundException {
        /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    NoUserException if user is not found in the container (CHECKED)
           EFFECTS: Prints all the users Id registered in the container
         */
        credentialsNotNull(owner, passw);
        if (userIsInTheContainer(owner, passw)) {
            Set<String> s = users.keySet();
            Object[] output = s.toArray();
            Arrays.sort(output);
            for (Object id : output)
                System.out.println(id);
        } else
            throw new UserNotFoundException("User not found in the container");
    }

    @Override
    public int totalUsers(String owner, String passw) throws UserNotFoundException {
        /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
           EFFECTS: Prints the amount of users registered in the container
         */
        credentialsNotNull(owner, passw);
        if (userIsInTheContainer(owner, passw))
            return users.size();
        else
            throw new UserNotFoundException("User not found in the container");
    }


    //--------------------------------- AUX METHODS --------------------------------------------

    private void credentialsNotNull(String id, String passw) {
        if (id == null || passw == null)
            throw new NullPointerException("Id or Pass must not be null!");
    }

    private boolean userNameExists(String id) {
        return users.containsKey(id);
    }

    private boolean userIsInTheContainer(String id, String passw) {
        return (this.users.containsKey(id) && this.users.get(id).equals(passw));
    }

    private void dataNotNull(E data) {
        if (data == null)
            throw new NullPointerException(data + " must not be null!");
    }

    private void otherNotNull(String other) {
        if (other == null)
            throw new NullPointerException("User must not be null!");
    }

    private boolean userOwnsTheData(String owner, E data) {
        boolean found = false;
        int i = 0;
        while (i < container.size() && !found) {
            if (container.get(i).owner.equals(owner) && container.get(i).data.equals(data))
                found = true;
            else
                i++;
        }
        return found;
    }

    private int indexOfUserOwnsTheData(String owner, E data) {
        boolean found = false;
        int i = 0;
        while (i < container.size() && !found) {
            if (container.get(i).owner.equals(owner) && container.get(i).data.equals(data))
                found = true;
            else
                i++;
        }
        if (found)
            return i;
        else
            return -1;
    }

    private boolean otherIsInTheCollection(String other) {
        return users.containsKey(other);
    }

}