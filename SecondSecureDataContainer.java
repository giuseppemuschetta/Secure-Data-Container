// GIUSEPPE MUSCHETTA, corso A, prof. Ferrari

package com.company;

import java.util.*;


public class SecondSecureDataContainer<E> implements SecureDataContainer<E> {

    /*
     OVERVIEW: SecureDataContainer<E> is a container of objects of type E. Intuitively, the collection behaves like
                a Data Storage for storing and sharing data (represented in the simulation by
                objects of type E). The collection must guarantee a data security mechanism by providing its own
                user identity management mechanism. Furthermore, the collection must provide a mechanism of
                access control that allows the owner of the data to perform a selective and restrictive
                access to his data in the collection. Some users may be authorized by the owner
                to access the data, while others can not access it without authorization.

      ABSTRACTION FUNCTION:
                               (users.get(i).id , users.get(i).passw) --> {Dato1, Dato2, ... DatoN}
                               to each user, recognized by his/her Id and passw,
                               is associated a set of E data type (informal)


      REPRESENTATION INVARIANT:
                                users != null
                                && forall i. 0 <= i < users.size() ==> (users.get(i) != null)
                                && forall i,j. 0 <= i,j <= users.size() ==> !(users.get(i).id.equals(users.get(j).id))
                                (no duplicate userId in the storage container, informal)
     */

    private class Dato<E> {

        /*
           Overview: Inner Class representing the data itself with a firstOwner label

           Abstraction Function:
                                 to each Dato<E> is associated the E data and the firstOwner String label

           Representation Invariant:
                                     firstOwner != null && data != null
         */

        private String firstOwner;
        private E data;

        public Dato(String id, E data) {
            if(id == null || data == null)
                throw new NullPointerException();
            firstOwner = id;
            this.data = data;
        }

        public Dato(Dato<E> other) {
            if(other == null)
                throw new NullPointerException();
            this.firstOwner = other.firstOwner;
            this.data = other.data;
        }

        @Override
        public boolean equals(Object other) {
            return this.data.equals(((Dato<E>) other).data);
        }

    }

    private class User<E> {

        /*
          Overview: Inner Class representing the user himself

          Abstraction Function: To each user is associated an arrayList of Data<E> objects
                                (id, passw) --> {Dato1, Dato2, ... last Dato}

          Representation Invariant:
                                    id != null && passw != null && container != null
                                    && forall i. 0 <= i < container.size() ==> (container.get(i) != null)
         */
        private String id;
        private String passw;
        ArrayList<Dato<E>> container;

        public User(String id, String passw) {
            if(id == null || passw == null)
                throw new NullPointerException();
            this.id = id;
            this.passw = passw;
            container = new ArrayList<>();
        }

        @Override
        public boolean equals(Object other) {
            if(other == null)
                throw new NullPointerException();
            return this.id.equals(((User<E>) other).id);
        }

    }


    //variabile d'istanza classe esterna
    private ArrayList<User<E>> users;

    //costruttore
    public SecondSecureDataContainer() {
        users = new ArrayList<>();
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
        userIsNotInTheContainer(id, passw);
        users.add(new User<>(id, passw));
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
        userIsInTheContainer(id, passw);
        for (User<E> user : users) {
            for (int i = 0; i < user.container.size(); i++) {
                if (user.container.get(i).firstOwner.equals(id)) {
                    user.container.remove(user.container.get(i));
                    i--;
                }
            }
        }
        int index = indexOfUserOwnsTheData(id);
        users.remove(index);
    }


    @Override
    public int getSize(String owner, String passw) throws UserNotFoundException {
        /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException if !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container
           EFFECTS: Returns the cardinality of a user's items (shared from others plus his own data)
         */
        userIsInTheContainer(owner, passw);
        int index = indexOfUserOwnsTheData(owner);
        return users.get(index).container.size();
    }


    @Override
    public boolean put(String owner, String passw, E data) throws UserNotFoundException {
        /*
          REQUIRES: owner != null && passw != null && data != null
            THROWS: NullPointerException if !(owner != null && passw != null)  (UNCHECKED)
                                      or if data == null
                    UserNotFoundException if user is not found in the container (CHECKED)
          MODIFIES: this.users.get(index).container
           EFFECTS: Adds a new item in the collection in which user is both the owner and
                    the firstOwner
         */
        userIsInTheContainer(owner, passw);
        dataNotNull(data);
        int index = indexOfUserOwnsTheData(owner);
        users.get(index).container.add(new Dato<>(owner, data));
        return true;
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
        userIsInTheContainer(owner, passw);
        dataNotNull(data);
        int index = indexOfUserOwnsTheData(owner);
        Dato<E> temp = new Dato<>(owner, data);
        int index2 = users.get(index).container.indexOf(temp);
        if (index2 != -1) {
            Dato<E> temp2 = new Dato<>(users.get(index).container.get(index2));
            return temp2.data;
        } else
            throw new DataNotFoundException("Element " + data + " has not been found!");
    }


    @Override
    public E remove(String owner, String passw, E data) throws UserNotFoundException, DataNotFoundException {
        /*
          REQUIRES: owner != null && passw != null && data != null
            THROWS: - NullPointerException if !(owner != null && passw != null)  (UNCHECKED)
                                      or if data == null
                    - UserNotFoundException if user is not found in the container (CHECKED)
                    - DataNotFoundException if data is not found in the user owned items (CHECKED)
          MODIFIES: this.users.get(index).container
           EFFECTS: If user is the firstOwner the method will delete his data and all the same data
                    that he shared to others, otherwise (if user is not the firstOwner) it will only
                    delete his data
         */
        userIsInTheContainer(owner, passw);
        dataNotNull(data);
        int index = indexOfUserOwnsTheData(owner);
        Dato<E> temp = new Dato<>("", data);
        Dato<E> temp2;
        int index2 = users.get(index).container.indexOf(temp);
        if (index2 != -1) {
            if (users.get(index).container.get(index2).firstOwner.equals(owner)) {
                temp2 = new Dato<>(users.get(index).container.get(index2));
                users.get(index).container.remove(index2);
                removeSharedData(owner, data);
            } else {
                temp2 = new Dato<>(users.get(index).container.get(index2));
                users.get(index).container.remove(index2);
            }
        } else
            throw new DataNotFoundException("Element has not been found!");
        return temp2.data;
    }


    @Override
    public void copy(String owner, String passw, E data) throws UserNotFoundException, DataNotFoundException {
        /*
          REQUIRES: owner != null && passw != null && data != null
            THROWS: NullPointerException if !(owner != null && passw != null)  (UNCHECKED)
                                      or if data == null
                    UserNotFoundException if user is not found in the container (CHECKED)
                    DataNotFoundException if data is not found in the user owned items (CHECKED)
          MODIFIES: this.users.get(index).container because it will add the copy in the container,
                    modifying the collection
           EFFECTS: Creates and inserts a copy of the item, that user already owns, in the collection

         */
        userIsInTheContainer(owner, passw);
        dataNotNull(data);
        int index = indexOfUserOwnsTheData(owner);
        Dato<E> temp = new Dato<>("", data);
        int index2 = users.get(index).container.indexOf(temp);
        if (index2 != -1) {
            Dato<E> temp2 = new Dato<>(users.get(index).container.get(index2));
            users.get(index).container.add(temp2);
        } else
            throw new DataNotFoundException("Element has not been found!");
    }


    @Override
    public void share(String owner, String passw, String other, E data) throws UserNotFoundException, DataNotFoundException {
        /*
          REQUIRES: owner != null && passw != null && other != null && data != null
            THROWS: NullPointerException if !(owner != null && passw != null)  (UNCHECKED)
                                      or if other == null or if data == null
                    UserNotFoundException if user is not found in the container (CHECKED)
                    DataNotFoundException if data is not found in the user owned items (CHECKED)
          MODIFIES: this.users.get(index).container because it will add the shared item in the container,
                    actually modifying the collection
           EFFECTS: Shares the data with another user in the container by creating a new object
                    where the firstOwner remains the same, and the "other" user becomes the new owner.
         */
        userIsInTheContainer(owner, passw);
        dataNotNull(data);
        otherNotNull(other);
        int indexOwner = indexOfUserOwnsTheData(owner);
        int indexOther = indexOfUserOwnsTheData(other);
        if (indexOther != -1) {
            Dato<E> temp = new Dato<>("", data);
            int indexOwner2 = users.get(indexOwner).container.indexOf(temp);
            if (indexOwner2 != -1)
                users.get(indexOther).container.add(new Dato<>(users.get(indexOwner).container.get(indexOwner2)));
            else
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
        userIsInTheContainer(owner, passw);
        int index = indexOfUserOwnsTheData(owner);
        return new Iterator<E>() {

            private int i = 0;

            @Override
            public boolean hasNext() {
                return (i < users.get(index).container.size());

            }

            @Override
            public E next() {
                if (hasNext()) {
                    i++;
                    return users.get(index).container.get(i - 1).data;
                }
                else
                    throw new NoSuchElementException("No more positions available"); //(unchecked)
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Removals are not supported");
            }
        };
    }



    //---------------------------------- OPTIONAL METHODS -------------------------------------


    @Override
    public void removeAll(String owner, String passw, E data) throws UserNotFoundException, DataNotFoundException {
        /*
          REQUIRES: owner != null && passw != null && data != null
            THROWS: - NullPointerException if !(owner != null && passw != null)  (UNCHECKED)
                                      or if data == null
                    - UserNotFoundException if user is not found in the container (CHECKED)
                    - DataNotFoundException if data is not found in the user owned items (CHECKED)
          MODIFIES: this.users.get(index).container
           EFFECTS: Removes all elements equals to "data" from the user's collection. It will remove
                    his very items and also the items shared from others to him.
         */
        userIsInTheContainer(owner, passw);
        dataNotNull(data);
        int index = indexOfUserOwnsTheData(owner);
        Dato<E> temp = new Dato<>("", data);
        int index2 = users.get(index).container.indexOf(temp);
        if (index2 == -1)
            throw new DataNotFoundException("Element " + data + " has not been found!");
        removeData(owner, data);
    }


    @Override
    public boolean isIn(String owner, String passw, E data) throws UserNotFoundException {
        /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
           EFFECTS: Checks whether "data" belongs to user's collection
         */
        userIsInTheContainer(owner, passw);
        dataNotNull(data);
        int index = indexOfUserOwnsTheData(owner);
        Dato<E> temp = new Dato<>("", data);
        return users.get(index).container.contains(temp);
    }

    @Override
    public void clear(String owner, String passw) throws UserNotFoundException {
        /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
          MODIFIES: this.users.get(index).container
           EFFECTS: Removes the data both from the user and from the people to whom
                    the user has shared the data.
         */
        userIsInTheContainer(owner,passw);
        for(int i=0;i<users.size();i++){
            for(int j=0;j<users.get(i).container.size();j++){
                if(users.get(i).id.equals(owner) || users.get(i).container.get(j).firstOwner.equals(owner)) {
                    users.get(i).container.remove(j);
                    j--; //to compensate the arrayList internal shrink
                }
            }
        }
    }


    @Override
    public boolean isEmpty(String owner, String passw) throws UserNotFoundException {
        /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
           EFFECTS: Checks whether users's collection is emptty
         */
        userIsInTheContainer(owner, passw);
        int index = indexOfUserOwnsTheData(owner);
        return users.get(index).container.isEmpty();
    }


    @Override
    public void printSharedFromOthersData(String id, String passw) throws UserNotFoundException {
        /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
           EFFECTS: Prints only the items shared from others to the user
         */
        userIsInTheContainer(id, passw);
        int index = indexOfUserOwnsTheData(id);
        for (int i = 0; i < users.get(index).container.size(); i++)
            if (!(users.get(index).container.get(i).firstOwner.equals(id)))
                System.out.println(users.get(index).container.get(i).data);
    }

    @Override
    public void printTotalData(String owner, String passw) throws UserNotFoundException {
        /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
           EFFECTS: Prints all items in the user's collection
         */
        userIsInTheContainer(owner, passw);
        Iterator<E> itr = getIterator(owner, passw);
        while (itr.hasNext())
            System.out.println(itr.next());
        System.out.println(" ");
    }

    @Override
    public void printFirstOwnerData(String owner, String passw) throws UserNotFoundException {
        /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
           EFFECTS: Prints only the items in which the user is the firstOwner
         */
        userIsInTheContainer(owner, passw);
        int index = indexOfUserOwnsTheData(owner);
        for (int i = 0; i < users.get(index).container.size(); i++)
            if (users.get(index).container.get(i).firstOwner.equals(owner))
                System.out.println(users.get(index).container.get(i).data);
    }


    @Override
    public void printUsersId(String owner, String passw) throws UserNotFoundException {
         /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    NoUserException if user is not found in the container (CHECKED)
           EFFECTS: Prints all the users Id registered in the container
         */
        if (users.isEmpty())
            throw new UserNotFoundException("User not found in the container");
        String[] list = new String[users.size()];
        for(int i=0;i<users.size();i++){
            list[i] = users.get(i).id;
        }
        Arrays.sort(list);
        for(int i=0;i<list.length;i++){
            System.out.println(list[i]);
        }

    }

    @Override
    public int totalUsers(String owner, String passw) throws UserNotFoundException {
        /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
           EFFECTS: Prints the amount of users registered in the container
         */
        userIsInTheContainer(owner, passw);
        return users.size();
    }



    //--------------------------------- AUX METHODS --------------------------------------------


    private int indexOfUserOwnsTheData(String id) {
        User<E> temp = new User<>(id, "");
        return users.indexOf(temp);
    }

    private void credentialsNotNull(String id, String passw) {
        if (id == null || passw == null)
            throw new NullPointerException("Id or Pass must not be null!");
    }

    private boolean userIsInTheContainer(String id, String passw) throws UserNotFoundException {
        credentialsNotNull(id, passw);
        User<E> temp = new User<>(id, passw);
        if (users.contains(temp)) {
            int index = indexOfUserOwnsTheData(id);
            if (!(users.get(index).passw.equals(passw)))
                throw new UserNotFoundException("User not found in the container");
        } else
            throw new UserNotFoundException("User not found in the container");
        return true;
    }

    private boolean userIsNotInTheContainer(String id, String passw) throws ExistingUserException {
        credentialsNotNull(id, passw);
        User<E> temp = new User<>(id, "");
        if (users.contains(temp))
            throw new ExistingUserException("User" + id + "already exists in the container");
        return true;
    }

    private void dataNotNull(E data) {
        if (data == null)
            throw new NullPointerException(data + "must not be null!");
    }

    private void otherNotNull(String other) {
        if (other == null)
            throw new NullPointerException("User must not be null!");
    }

    private void removeSharedData(String owner, E data) {
        for (User<E> user : users) {
            for (int i = 0; i < user.container.size(); i++) {
                if (user.container.get(i).firstOwner.equals(owner) && user.container.get(i).data.equals(data) &&
                        !(user.id.equals(owner))) {
                    user.container.remove(i);
                    i--;
                }
            }
        }
    }

    private void removeData(String owner, E data) {
        for (User<E> user : users) {
            for (int i = 0; i < user.container.size(); i++) {
                if (user.container.get(i).firstOwner.equals(owner) && user.container.get(i).data.equals(data)) {
                    user.container.remove(i);
                    i--;
                }
            }
        }
    }



}
