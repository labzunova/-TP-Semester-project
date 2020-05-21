package com.example.first.mainScreen.database;

import androidx.annotation.MainThread;

import com.example.first.mainScreen.repositories.InfoRepo;

public interface CredentialDatabase {
    public static final int BAD_ERROR = 1;

    void getMyCaseProfile(GetCaseProfileCallback caseProfileCallback);
    void getCaseProfile(GetCaseProfileCallback caseCallback);
    void changeProfileByCase(InfoRepo.CaseProfile caseProfile);

    interface GetCaseProfileCallback {
        @MainThread
        void onSuccess(InfoRepo.CaseProfile caseProfile);

        @MainThread
        void onError(final int codeError);

        void onNotFound();
    }
}
