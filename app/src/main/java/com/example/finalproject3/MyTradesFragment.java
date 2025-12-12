package com.example.finalproject3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MyTradesFragment extends Fragment {

    private MyTradesViewModel myTradesViewModel;
    private MatchListViewModel matchListViewModel; // To get match data
    private TradeAdapter tradeAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_trades, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_trades);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tradeAdapter = new TradeAdapter();
        recyclerView.setAdapter(tradeAdapter);

        myTradesViewModel = new ViewModelProvider(this).get(MyTradesViewModel.class);
        matchListViewModel = new ViewModelProvider(requireActivity()).get(MatchListViewModel.class);

        // Observe matches first to build our map
        matchListViewModel.getMatches().observe(getViewLifecycleOwner(), matches -> {
            Map<String, Match> matchMap = matches.stream().collect(Collectors.toMap(Match::getId, match -> match));

            // Now observe trades and pass the match map to the adapter
            myTradesViewModel.getTrades().observe(getViewLifecycleOwner(), trades -> {
                tradeAdapter.setTrades(trades, matchMap);
            });
        });

        myTradesViewModel.fetchTrades();
    }
}
