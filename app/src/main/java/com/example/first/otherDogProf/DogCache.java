package com.example.first.otherDogProf;

public class DogCache {
    private static DogCache accountCash;
    private static String dogsID;

    private DogRepository dogRepo;

    public DogRepository getAccountRepo() {
        return dogRepo;
    }

    public static DogCache getInstance() {
        if (accountCash == null) {
            accountCash = new DogCache(dogsID);
        }

        return accountCash;
    }

    public DogCache(String id) {
        dogsID = id;
        dogRepo = new DogRepository(dogsID);
    }
}