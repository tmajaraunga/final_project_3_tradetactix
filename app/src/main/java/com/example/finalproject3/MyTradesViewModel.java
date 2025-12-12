package com.example.finalproject3;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class MyTradesViewModel extends ViewModel {
    private final MutableLiveData<List<Trade>> trades = new MutableLiveData<>();

    public LiveData<List<Trade>> getTrades() {
        return trades;
    }

    public void fetchTrades() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        FirebaseFirestore.getInstance().collection("trades")
                .whereEqualTo("userId", currentUser.getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING) // Assuming you add a timestamp
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        // Handle error
                        return;
                    }

                    List<Trade> tradeList = new ArrayList<>();
                    if (snapshots != null) {
                        for (QueryDocumentSnapshot doc : snapshots) {
                            Trade trade = doc.toObject(Trade.class);
                            tradeList.add(trade);
                        }
                    }
                    trades.setValue(tradeList);
                });
    }
}

