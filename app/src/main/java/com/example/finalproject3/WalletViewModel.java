package com.example.finalproject3;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class WalletViewModel extends ViewModel {
    private final MutableLiveData<Long> balance = new MutableLiveData<>();
    private ListenerRegistration walletListener;

    public WalletViewModel() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DocumentReference userDocRef = FirebaseFirestore.getInstance()
                    .collection("users").document(currentUser.getUid());

            // Listen for real-time updates to the user's document
            walletListener = userDocRef.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    // Handle error
                    System.err.println("Listen failed: " + e);
                    balance.postValue(0L); // Post a default value on error
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Long currentBalance = snapshot.getLong("balance");
                    if (currentBalance != null) {
                        balance.postValue(currentBalance);
                    }
                } else {
                    System.out.println("Current data: null");
                }
            });
        }
    }

    public LiveData<Long> getBalance() {
        return balance;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Stop listening for updates when the ViewModel is no longer used
        if (walletListener != null) {
            walletListener.remove();
        }
    }
}

