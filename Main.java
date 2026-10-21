// GIUSEPPE MUSCHETTA, corso A, prof. Ferrari

package com.company;

import java.util.Iterator;

public class Main {

    public static void main(String[] args) {


        //oggetto di prova con cui testeremo le due implementazioni:
        Esame pr1 = new Esame("pr1","m1",30);
        Esame pr2 = new Esame("pr2","m2",25);
        Esame algoritmica = new Esame("algoritmica","m3",27);
        Esame logica = new Esame("logica","m4",26);
        Esame analisi = new Esame("analisi","m5",23);
        Esame elaboratori = new Esame("elaboratori","m6",21);

        /*via al test delle due implementazioni che operano allo stesso modo seppur implementate
          in maniera abbastanza diversa, la prima implementazione e' gia' pronta, il test della seconda
          implementazione andra' decommentato e si trova alla fine del primo test*/


        // TEST PRIMA IMPLEMENTAZIONE:
        SecureDataContainer<Esame> test1 = new FirstSecureDataContainer<>();
        try {
            test1.createUser("paolo", "paolo111");
            test1.createUser("giuseppe", "giuseppe111");
            test1.createUser("francesco", "francesco111");
            //test1.createUser("paolo","altraPass"); lancia l'eccezione ExistingUserException
        }catch(ExistingUserException e){
            e.printStackTrace();
        }

        System.out.println("\n-----------------PRIMA IMPLEMENTAZIONE-------------------");
        System.out.println("Nel contenitore ci sono i seguenti utenti registrati:\n"); //in ordine lessicografico
        try {
            test1.printUsersId("giuseppe","giuseppe111");
            //test1.printUsersId("giuseppe","passwordErrata"); lancia l'eccezione UserNotFoundException
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        try {
            test1.put("paolo", "paolo111", pr1);
            test1.put("paolo", "paolo111", algoritmica);
            test1.put("francesco", "francesco111", logica);
            test1.put("francesco", "francesco111", analisi);
            test1.put("giuseppe", "giuseppe111", pr2);
            test1.put("giuseppe", "giuseppe111", elaboratori);
            //test1.put("paolo","wrongPassword",pr2); //lancia un'eccezione
        }catch(UserNotFoundException e){
            e.printStackTrace();
        }

        try {
            System.out.println("\nGiuseppe ora possiede:");
            test1.printTotalData("giuseppe","giuseppe111");
            System.out.println("Paolo ora possiede:");
            test1.printTotalData("paolo","paolo111");
            System.out.println("Francesco ora possiede:");
            test1.printTotalData("francesco","francesco111");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("giuseppe ha condiviso l'oggetto pr2, di cui lui e' il " +
                "first owner, a paolo e francesco");
        System.out.println("francesco e paolo hanno condiviso a giuseppe rispettivamente " +
                "logica e algoritmica\n");
        try {
            test1.share("giuseppe","giuseppe111","paolo",pr2);
            test1.share("giuseppe","giuseppe111","francesco",pr2);
            test1.share("francesco","francesco111","giuseppe",logica);
            test1.share("paolo","paolo111","giuseppe",algoritmica);
        }catch(UserNotFoundException | DataNotFoundException e){
            e.printStackTrace();
        }

        //qui testiamo l'iteratore la cui implementazione non contempla la remove
        System.out.println("giuseppe quindi ha accesso ai seguenti oggetti:");
        try {
            Iterator<Esame> itr = test1.getIterator("giuseppe", "giuseppe111");
            while (itr.hasNext()) {
                System.out.println(itr.next());
            }
        }catch(UserNotFoundException e){
            e.printStackTrace();
        }

        System.out.println("\npaolo ha accesso ai seguenti oggetti:");
        try {
            Iterator<Esame> itr2 = test1.getIterator("paolo", "paolo111");
            while (itr2.hasNext()) {
                System.out.println(itr2.next());
            }
        }catch(UserNotFoundException e){
            e.printStackTrace();
        }

        System.out.println("\nfrancesco ha accesso ai seguenti oggetti:");
        try {
            Iterator<Esame> itr3 = test1.getIterator("francesco", "francesco111");
            while (itr3.hasNext()) {
                System.out.println(itr3.next());
            }
        }catch(UserNotFoundException e){
            e.printStackTrace();
        }

        System.out.println("\ngiuseppe adesso rimuovera' l'oggetto pr2 di cui era il primo " +
                "proprietario sottolineando il fatto che anche " +
                "tutti gli altri utenti (a cui ha condiviso pr2) perderanno il dato\n");
        try {
            test1.remove("giuseppe","giuseppe111",pr2);
        } catch (DataNotFoundException | UserNotFoundException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Giuseppe ora possiede:");
            test1.printTotalData("giuseppe","giuseppe111");
            System.out.println("Paolo ora possiede:");
            test1.printTotalData("paolo","paolo111");
            System.out.println("Francesco ora possiede:");
            test1.printTotalData("francesco","francesco111");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }


        try {
            test1.remove("giuseppe","giuseppe111",logica);
        } catch (DataNotFoundException | UserNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("giuseppe prova a cancellare logica sottolineando il fatto che " +
                "non essendo lui il first owner di logica lo potra' eliminare solo per se stesso\n");
        try {
            System.out.println("Giuseppe non possiede piu' logica, ma possiede solo:");
            test1.printTotalData("giuseppe","giuseppe111");
            System.out.println("Francesco, firstOwner di logica, continua a possedere logica:");
            test1.printTotalData("francesco","francesco111");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Giuseppe si crea una copia del dato algoritmica, NON diventandone pero' il proprietario" +
                ", infatti il proprietario restera' sempre Paolo per quell'oggetto!");
        try {
            test1.copy("giuseppe", "giuseppe111", algoritmica);
            //test1.copy("giuseppe", "giuseppe111", pr1); //DataNotFoundException
            //test1.copy("matteo", "matteo111", algoritmica); //UserNotFoundException
        }catch(DataNotFoundException | UserNotFoundException e){
            e.printStackTrace();
        }

        System.out.println("Giuseppe adesso ha i seguenti dati:");
        try {
            test1.printTotalData("giuseppe","giuseppe111");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Paolo, proprietario del dato algoritmica, invochera' il metodo opzionale clear" +
                " rimuovendo tutti i suoi dati e quelli condivisi agli altri di cui lui e' il first owner");
        try {
            test1.clear("paolo","paolo111");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Giuseppe, che aveva 3 oggetti, dopo la chimata clear da parte di Paolo" +
                " perdera' gli oggetti di Paolo rimanendo solo con:");
        try {
            test1.printTotalData("giuseppe","giuseppe111");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Paolo, dopo l'invocazione di clear non ha piu' nulla:");
        try {
            test1.printTotalData("paolo","paolo111");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("\nGiuseppe, a cui e' rimasto solo elaboratori, decide di condividerlo" +
                " con Francesco");
        try {
            test1.share("giuseppe","giuseppe111","francesco",elaboratori);
        } catch (UserNotFoundException | DataNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("\nFrancesco ora possiede:");
        try {
            test1.printTotalData("francesco","francesco111");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Giuseppe decide di rimuoversi dal container di storage, perdendo cosi' ogni " +
                "dato. Se lui era il proprietario di dati condivisi a terzi, anche questi perderanno il dato.");
        try {
            test1.removeUser("giuseppe","giuseppe111");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Francesco, dal momento che giuseppe si e' cancellato dal container," +
                " perdera' il dato elaboratori di cui giuseppe era proprietario");
        System.out.println("\nFrancesco ora possiede:");
        try {
            test1.printTotalData("francesco","francesco111");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }


        /* decommentare qui


        //TEST SECONDA IMPLEMENTAZIONE:
        SecureDataContainer<Esame> test2 = new SecondSecureDataContainer<>();
        try {
            test2.createUser("paolo", "paolo111");
            test2.createUser("giuseppe", "giuseppe111");
            test2.createUser("francesco", "francesco111");
            //test1.createUser("paolo","altraPass"); lancia l'eccezione ExistingUserException
        }catch(ExistingUserException e){
            e.printStackTrace();
        }



        System.out.println("\n\n-----------------SECONDA IMPLEMENTAZIONE-------------------");
        System.out.println("Nel contenitore ci sono i seguenti utenti registrati:\n"); //in ordine lessicografico
        try {
            test2.printUsersId("giuseppe","giuseppe111");
            //test1.printUsersId("giuseppe","passwordErrata"); lancia l'eccezione UserNotFoundException
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        try {
            test2.put("paolo", "paolo111", pr1);
            test2.put("paolo", "paolo111", algoritmica);
            test2.put("francesco", "francesco111", logica);
            test2.put("francesco", "francesco111", analisi);
            test2.put("giuseppe", "giuseppe111", pr2);
            test2.put("giuseppe", "giuseppe111", elaboratori);
            //test1.put("paolo","wrongPassword",pr2); //lancia un'eccezione
        }catch(UserNotFoundException e){
            e.printStackTrace();
        }

        try {
            System.out.println("\nGiuseppe ora possiede:");
            test2.printTotalData("giuseppe","giuseppe111");
            System.out.println("Paolo ora possiede:");
            test2.printTotalData("paolo","paolo111");
            System.out.println("Francesco ora possiede:");
            test2.printTotalData("francesco","francesco111");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("giuseppe ha condiviso l'oggetto pr2, di cui lui e' il " +
                "first owner, a paolo e francesco");
        System.out.println("francesco e paolo hanno condiviso a giuseppe rispettivamente " +
                "logica e algoritmica\n");
        try {
            test2.share("giuseppe","giuseppe111","paolo",pr2);
            test2.share("giuseppe","giuseppe111","francesco",pr2);
            test2.share("francesco","francesco111","giuseppe",logica);
            test2.share("paolo","paolo111","giuseppe",algoritmica);
        }catch(UserNotFoundException | DataNotFoundException e){
            e.printStackTrace();
        }

        //qui testiamo l'iteratore la cui implementazione non contempla la remove
        System.out.println("giuseppe quindi ha accesso ai seguenti oggetti:");
        try {
            Iterator<Esame> itr = test2.getIterator("giuseppe", "giuseppe111");
            while (itr.hasNext()) {
                System.out.println(itr.next());
            }
        }catch(UserNotFoundException e){
            e.printStackTrace();
        }

        System.out.println("\npaolo ha accesso ai seguenti oggetti:");
        try {
            Iterator<Esame> itr2 = test2.getIterator("paolo", "paolo111");
            while (itr2.hasNext()) {
                System.out.println(itr2.next());
            }
        }catch(UserNotFoundException e){
            e.printStackTrace();
        }

        System.out.println("\nfrancesco ha accesso ai seguenti oggetti:");
        try {
            Iterator<Esame> itr3 = test2.getIterator("francesco", "francesco111");
            while (itr3.hasNext()) {
                System.out.println(itr3.next());
            }
        }catch(UserNotFoundException e){
            e.printStackTrace();
        }

        System.out.println("\ngiuseppe adesso rimuovera' l'oggetto pr2 di cui era il primo " +
                "proprietario sottolineando il fatto che anche " +
                "tutti gli altri utenti (a cui ha condiviso pr2) perderanno il dato\n");
        try {
            test2.remove("giuseppe","giuseppe111",pr2);
        } catch (DataNotFoundException | UserNotFoundException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Giuseppe ora possiede:");
            test2.printTotalData("giuseppe","giuseppe111");
            System.out.println("Paolo ora possiede:");
            test2.printTotalData("paolo","paolo111");
            System.out.println("Francesco ora possiede:");
            test2.printTotalData("francesco","francesco111");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }


        try {
            test2.remove("giuseppe","giuseppe111",logica);
        } catch (DataNotFoundException | UserNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("giuseppe prova a cancellare logica sottolineando il fatto che " +
                "non essendo lui il first owner di logica lo potra' eliminare solo per se stesso\n");
        try {
            System.out.println("Giuseppe non possiede piu' logica, ma possiede solo:");
            test2.printTotalData("giuseppe","giuseppe111");
            System.out.println("Francesco, firstOwner di logica, continua a possedere logica:");
            test2.printTotalData("francesco","francesco111");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Giuseppe si crea una copia del dato algoritmica, NON diventandone pero' il proprietario" +
                ", infatti il proprietario restera' sempre Paolo per quell'oggetto!");
        try {
            test2.copy("giuseppe", "giuseppe111", algoritmica);
            //test1.copy("giuseppe", "giuseppe111", pr1); //DataNotFoundException
            //test1.copy("matteo", "matteo111", algoritmica); //UserNotFoundException
        }catch(DataNotFoundException | UserNotFoundException e){
            e.printStackTrace();
        }

        System.out.println("Giuseppe adesso ha i seguenti dati:");
        try {
            test2.printTotalData("giuseppe","giuseppe111");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Paolo, proprietario del dato algoritmica, invochera' il metodo opzionale clear" +
                " rimuovendo tutti i suoi dati e quelli condivisi agli altri di cui lui e' il first owner");
        try {
            test2.clear("paolo","paolo111");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Giuseppe, che aveva 3 oggetti, dopo la chimata clear da parte di Paolo" +
                " perdera' gli oggetti di Paolo rimanendo solo con:");
        try {
            test2.printTotalData("giuseppe","giuseppe111");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Paolo, dopo l'invocazione di clear non ha piu' nulla:");
        try {
            test2.printTotalData("paolo","paolo111");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("\nGiuseppe, a cui e' rimasto solo elaboratori, decide di condividerlo" +
                " con Francesco");
        try {
            test2.share("giuseppe","giuseppe111","francesco",elaboratori);
        } catch (UserNotFoundException | DataNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("\nFrancesco ora possiede:");
        try {
            test2.printTotalData("francesco","francesco111");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Giuseppe decide di rimuoversi dal container di storage, perdendo cosi' ogni " +
                "dato. Se lui era il proprietario di dati condivisi a terzi, anche questi perderanno il dato.");
        try {
            test2.removeUser("giuseppe","giuseppe111");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Francesco, dal momento che giuseppe si e' cancellato dal container," +
                " perdera' il dato elaboratori di cui giuseppe era proprietario");
        System.out.println("\nFrancesco ora possiede:");
        try {
            test2.printTotalData("francesco","francesco111");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }


        */

    }

}
