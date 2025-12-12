package com.example.finalproject3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.Random;

public class MatchListFragment extends Fragment {

    private MatchListViewModel matchListViewModel;
    private WalletViewModel walletViewModel;
    private MatchAdapter matchAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupMenu();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_matches);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        matchAdapter = new MatchAdapter();
        recyclerView.setAdapter(matchAdapter);

        matchAdapter.setOnItemClickListener(match -> {
            MatchListFragmentDirections.ActionMatchListFragmentToMatchDetailFragment action =
                    MatchListFragmentDirections.actionMatchListFragmentToMatchDetailFragment(match.getId());
            NavHostFragment.findNavController(MatchListFragment.this).navigate(action);
        });

        matchListViewModel = new ViewModelProvider(this).get(MatchListViewModel.class);
        walletViewModel = new ViewModelProvider(this).get(WalletViewModel.class);

        matchListViewModel.getMatches().observe(getViewLifecycleOwner(), matches -> {
            matchAdapter.setMatches(matches);
        });

        walletViewModel.getBalance().observe(getViewLifecycleOwner(), balance -> {
            if (getActivity() != null && balance != null) {
                Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                if (toolbar != null) {
                    toolbar.setSubtitle(String.format("Balance: %d TP", balance));
                }
            }
        });
    }

    private void setupMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.action_sign_out) {
                    FirebaseAuth.getInstance().signOut();
                    // Clear the subtitle on logout
                    if (getActivity() != null) {
                        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                        if (toolbar != null) {
                            toolbar.setSubtitle(null);
                        }
                    }
                    NavHostFragment.findNavController(MatchListFragment.this)
                            .navigate(R.id.loginFragment);
                    return true;
                } else if (itemId == R.id.action_simulate_results) {
                    simulateMatchResults();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void simulateMatchResults() {
        Toast.makeText(getContext(), "Simulating results...", Toast.LENGTH_SHORT).show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = user.getUid();

        db.collection("trades")
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", "OPEN")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(getContext(), "No open trades to simulate.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final double payoutMultiplier = 2.0;
                    final Random random = new Random();
                    long totalWinnings = 0;

                    WriteBatch batch = db.batch();

                    for (QueryDocumentSnapshot tradeDoc : queryDocumentSnapshots) {
                        String tradeTypeStr = tradeDoc.getString("tradeType");
                        Long tradeAmount = tradeDoc.getLong("amount");
                        if(tradeAmount == null) continue; // Skip if amount is missing

                        int matchResult = random.nextInt(3); // 0, 1, or 2
                        boolean isWin = false;

                        if (tradeTypeStr.equals("HOME_WIN") && matchResult == 0) isWin = true;
                        else if (tradeTypeStr.equals("AWAY_WIN") && matchResult == 1) isWin = true;
                        else if (tradeTypeStr.equals("DRAW") && matchResult == 2) isWin = true;

                        if (isWin) {
                            totalWinnings += (long) (tradeAmount * payoutMultiplier);
                        }

                        batch.update(tradeDoc.getReference(), "status", "CLOSED");
                    }

                    if (totalWinnings > 0) {
                        DocumentReference userDocRef = db.collection("users").document(userId);
                        batch.update(userDocRef, "balance", FieldValue.increment(totalWinnings));
                    }

                    final long finalWinnings = totalWinnings;
                    batch.commit().addOnSuccessListener(aVoid -> {
                        String resultMessage = (finalWinnings > 0)
                                ? "Simulation complete! You won " + finalWinnings + " TP!"
                                : "Simulation complete. No winning trades this time.";
                        Toast.makeText(getContext(), resultMessage, Toast.LENGTH_LONG).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Simulation failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching trades: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
