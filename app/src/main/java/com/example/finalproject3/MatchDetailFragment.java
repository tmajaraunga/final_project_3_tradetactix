package com.example.finalproject3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;

public class MatchDetailFragment extends Fragment {

    private MatchDetailViewModel viewModel;
    private TextView homeTeamText, awayTeamText, leagueText, timeText;
    private Button buyHomeButton, buyAwayButton, buyDrawButton;

    private String currentMatchId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);

        if (getArguments() != null) {
            currentMatchId = MatchDetailFragmentArgs.fromBundle(getArguments()).getMatchId();
        } else {
            currentMatchId = "No ID";
        }

        viewModel = new ViewModelProvider(this).get(MatchDetailViewModel.class);

        viewModel.getSelectedMatch().observe(getViewLifecycleOwner(), match -> {
            if (match != null) {
                updateUiWithMatch(match);
            } else {
                Toast.makeText(getContext(), "Match details not found.", Toast.LENGTH_SHORT).show();
            }
        });

        setupTradeButtonListeners();

        viewModel.loadMatch(currentMatchId);
    }

    private void initializeViews(View view) {
        homeTeamText = view.findViewById(R.id.detail_home_team);
        awayTeamText = view.findViewById(R.id.detail_away_team);
        leagueText = view.findViewById(R.id.detail_league_name);
        timeText = view.findViewById(R.id.detail_match_time);
        buyHomeButton = view.findViewById(R.id.button_buy_home);
        buyAwayButton = view.findViewById(R.id.button_buy_away);
        buyDrawButton = view.findViewById(R.id.button_buy_draw);
    }

    private void updateUiWithMatch(Match match) {
        homeTeamText.setText(match.getHomeTeam());
        awayTeamText.setText(match.getAwayTeam());
        leagueText.setText(match.getLeague());
        timeText.setText(match.getMatchTime());

        buyHomeButton.setText("Buy " + match.getHomeTeam());
        buyAwayButton.setText("Buy " + match.getAwayTeam());
    }

    private void setupTradeButtonListeners() {
        buyHomeButton.setOnClickListener(v -> executeTrade(Trade.TradeType.HOME_WIN));
        buyAwayButton.setOnClickListener(v -> executeTrade(Trade.TradeType.AWAY_WIN));
        buyDrawButton.setOnClickListener(v -> executeTrade(Trade.TradeType.DRAW));
    }

    private void executeTrade(Trade.TradeType type) {
        if (currentMatchId == null || currentMatchId.equals("No ID")) {
            Toast.makeText(getContext(), "Error: Invalid Match ID", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "You must be logged in to trade.", Toast.LENGTH_SHORT).show();
            return;
        }

        final int tradeAmount = 100;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference userDocRef = db.collection("users").document(user.getUid());
        final DocumentReference tradeDocRef = db.collection("trades").document();

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            long currentBalance = transaction.get(userDocRef).getLong("balance");

            if (currentBalance < tradeAmount) {
                throw new FirebaseFirestoreException("Not enough funds to place trade.",
                        FirebaseFirestoreException.Code.ABORTED);
            }

            long newBalance = currentBalance - tradeAmount;
            transaction.update(userDocRef, "balance", newBalance);

            Map<String, Object> newTradeData = new HashMap<>();
            newTradeData.put("userId", user.getUid());
            newTradeData.put("matchId", currentMatchId);
            newTradeData.put("tradeType", type.name());
            newTradeData.put("amount", tradeAmount);
            newTradeData.put("status", "OPEN");
            newTradeData.put("timestamp", com.google.firebase.Timestamp.now());

            transaction.set(tradeDocRef, newTradeData);

            return null;
        }).addOnSuccessListener(aVoid -> {
            String confirmationMessage = "Trade placed for " + tradeAmount + " TP!";
            Toast.makeText(getContext(), confirmationMessage, Toast.LENGTH_LONG).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Trade failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}
