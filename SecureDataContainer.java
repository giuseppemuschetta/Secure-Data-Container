// GIUSEPPE MUSCHETTA, corso A, prof. Ferrari

package com.company;

import java.util.Iterator;

public interface SecureDataContainer<E> {

    /*
      OVERVIEW: SecureDataContainer<E> is a container of objects of type E. Intuitively, the collection behaves like
                a Data Storage for storing and sharing data (represented in the simulation by
                objects of type E). The collection must guarantee a data security mechanism by providing its own
                user identity management mechanism. Furthermore, the collection must provide a mechanism of
                access control that allows the owner of the data to perform a selective and restrictive
                access to his data in the collection. Some users may be authorized by the owner
                to access the data, while others can not access it without authorization.

      Typical Element: Each user in the collection is associated with 0 or n Data elements

     */


    void createUser(String id, String passw) throws ExistingUserException;
        /*
          REQUIRES: id != null && passw != null
            THROWS: NullPointerException if !(id != null && passw != null) (UNCHECKED)
                    ExistingUserException if user with that id already exists in the container (CHECKED)
          MODIFIES: this
           EFFECTS: Creates a new user in the container
         */



    void removeUser(String id, String passw) throws UserNotFoundException;
        /*
          REQUIRES: id != null && passw != null
            THROWS: NullPointerException if !(id != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user with that id already exists in the container (CHECKED)
          MODIFIES: this
           EFFECTS: Removes the user, user's data and user's shared data
         */



    int getSize(String owner, String passw) throws UserNotFoundException;
         /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException if !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container
           EFFECTS: Returns the cardinality of a user's items (shared from others plus his own data)
         */



    boolean put(String owner, String passw, E data) throws UserNotFoundException;
         /*
          REQUIRES: owner != null && passw != null && data != null
            THROWS: NullPointerException if !(owner != null && passw != null)  (UNCHECKED)
                                      or if data == null
                    UserNotFoundException if user is not found in the container (CHECKED)
          MODIFIES: this
           EFFECTS: Inserts a new data in the collection after user's authentication check
         */



    E get(String owner, String passw, E data) throws UserNotFoundException, DataNotFoundException;
         /*
          REQUIRES: owner != null && passw != null && data != null
            THROWS: NullPointerException if !(owner != null && passw != null)  (UNCHECKED)
                                      or if data == null
                    UserNotFoundException if user is not found in the container (CHECKED)
                    DataNotFoundException if data is not found in the user owned items (CHECKED)
           EFFECTS: Returns a copy of the item (that user already owns) from the collection
                    after user's authentication check
         */



    E remove(String owner, String passw, E data) throws DataNotFoundException, UserNotFoundException;
          /*
          REQUIRES: owner != null && passw != null && data != null
            THROWS: - NullPointerException if !(owner != null && passw != null)  (UNCHECKED)
                                      or if data == null
                    - UserNotFoundException if user is not found in the container (CHECKED)
                    - DataNotFoundException if data is not found in the user owned items (CHECKED)
          MODIFIES: this
           EFFECTS: If user is the firstOwner the method will delete his data and all the same data
                    that he shared to others, otherwise (if user is not the firstOwner) it will only
                    delete his data
         */



    void copy(String owner, String passw, E data) throws UserNotFoundException, DataNotFoundException;
        /*
          REQUIRES: owner != null && passw != null && data != null
            THROWS: NullPointerException if !(owner != null && passw != null)  (UNCHECKED)
                                      or if data == null
                    UserNotFoundException if user is not found in the container (CHECKED)
                    DataNotFoundException if data is not found in the user owned items (CHECKED)
          MODIFIES: this because it will add the copy in the container, modifying the collection
           EFFECTS: Creates a copy of the data, that user already owns, in the collection
                    after user's authentication check,
         */




    void share(String owner, String passw, String other, E data) throws UserNotFoundException, DataNotFoundException;
        /*
          REQUIRES: owner != null && passw != null && other != null && data != null
            THROWS: NullPointerException if !(owner != null && passw != null)  (UNCHECKED)
                                      or if other == null or if data == null
                    UserNotFoundException if user is not found in the container (CHECKED)
                    DataNotFoundException if data is not found in the user owned items (CHECKED)
          MODIFIES: this because it will add the shared item in the container,
                    actually modifying the collection
           EFFECTS: Shares the data with another user in the container after user's authentication check
         */



    Iterator<E> getIterator(String owner, String passw) throws UserNotFoundException;
         /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException if !(owner != null && passw != null)  (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
           EFFECTS: Returns an iterator (removals are not supported) that will iterate only
                    through the items owned by the user after user's authentication check
         */




         //---------------------------------- OPTIONAL METHODS -------------------------------------


    void removeAll(String owner, String passw, E data) throws UserNotFoundException, DataNotFoundException;
         /*
          REQUIRES: owner != null && passw != null && data != null
            THROWS: - NullPointerException if !(owner != null && passw != null)  (UNCHECKED)
                                      or if data == null
                    - UserNotFoundException if user is not found in the container (CHECKED)
                    - DataNotFoundException if data is not found in the user owned items (CHECKED)
          MODIFIES: this
           EFFECTS: Removes all elements equals to "data" from the user's collection.
         */



    void printFirstOwnerData(String owner, String passw) throws UserNotFoundException;
        /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
           EFFECTS: Prints only the items inserted in the collection by the user
         */


    void printSharedFromOthersData(String owner, String passw) throws UserNotFoundException;
         /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
           EFFECTS: Prints only the items shared from other people to the user
         */



    void printTotalData(String owner, String passw) throws UserNotFoundException;
         /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
           EFFECTS: Prints all items in the user's collection
         */



    boolean isEmpty(String owner, String passw) throws UserNotFoundException;
        /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
           EFFECTS: Checks whether users's collection is emptty
         */




    boolean isIn(String owner, String passw, E data) throws UserNotFoundException;
        /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
           EFFECTS: Checks whether "data" belongs to user's collection
         */


    void clear(String owner, String passw) throws UserNotFoundException;
         /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
          MODIFIES: this
           EFFECTS: Removes the data both from the user and from the people to whom
                    the user has shared the data.
         */


    void printUsersId(String owner, String passw) throws UserNotFoundException;
        /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    NoUsersException if user is not found in the container (CHECKED)
           EFFECTS: Prints all the users Id registered in the container
         */


    int totalUsers(String owner, String passw) throws UserNotFoundException;
        /*
          REQUIRES: owner != null && passw != null
            THROWS: NullPointerException is !(owner != null && passw != null) (UNCHECKED)
                    UserNotFoundException if user is not found in the container (CHECKED)
           EFFECTS: Prints the amount of users registered in the container
         */

}
