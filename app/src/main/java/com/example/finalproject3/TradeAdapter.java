package com.example.finalproject3;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class TradeAdapter extends RecyclerView.Adapter<TradeAdapter.TradeViewHolder> {

    private List<Trade> trades = new ArrayList<>();
    private Map<String, Match> matchMap = new HashMap<>(); // To get match details

    @NonNull
    @Override
    public TradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trade_item, parent, false);
        return new TradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TradeViewHolder holder, int position) {
        Trade trade = trades.get(position);
        holder.bind(trade, matchMap.get(trade.getMatchId()));
    }

    @Override
    public int getItemCount() {
        return trades.size();
    }

    public void setTrades(List<Trade> trades, Map<String, Match> matchMap) {
        this.trades = trades;
        this.matchMap = matchMap;
        notifyDataSetChanged();
    }

    static class TradeViewHolder extends RecyclerView.ViewHolder {
        private final TextView matchInfoText, predictionText, amountText, statusText, resultText;

        public TradeViewHolder(@NonNull View itemView) {
            super(itemView);
            matchInfoText = itemView.findViewById(R.id.trade_item_match_info);
            predictionText = itemView.findViewById(R.id.trade_item_prediction);
            amountText = itemView.findViewById(R.id.trade_item_amount);
            statusText = itemView.findViewById(R.id.trade_item_status);
            resultText = itemView.findViewById(R.id.trade_item_result);
        }

        public void bind(Trade trade, Match match) {
            if (match != null) {
                matchInfoText.setText(String.format("%s vs %s", match.getHomeTeam(), match.getAwayTeam()));
            } else {
                matchInfoText.setText("Match ID: " + trade.getMatchId());
            }

            predictionText.setText("Prediction: " + trade.getTradeType().name());
            amountText.setText(String.format("Wager: %d TP", trade.getAmount()));
            statusText.setText(trade.getStatus());

            // Set status color
            if ("OPEN".equals(trade.getStatus())) {
                statusText.setBackgroundColor(Color.parseColor("#FF9800")); // Orange
            } else {
                statusText.setBackgroundColor(Color.parseColor("#4CAF50")); // Green
            }

            // Show result text only if the trade is closed
            if ("CLOSED".equals(trade.getStatus())) {
                resultText.setVisibility(View.VISIBLE);
                // This is a placeholder. A real implementation would store the win/loss state.
                // For now, we'll just indicate it's settled.
                if (trade.getResult() != null) {
                    if (trade.getResult().equals("WIN")) {
                        long winnings = (long) (trade.getAmount() * 2.0);
                        resultText.setText(String.format("Result: WIN (+%d TP)", winnings));
                        resultText.setTextColor(Color.parseColor("#4CAF50")); // Green
                    } else {
                        resultText.setText("Result: LOSS");
                        resultText.setTextColor(Color.parseColor("#F44336")); // Red
                    }
                } else {
                    resultText.setText("Result: Settled");
                }
            } else {
                resultText.setVisibility(View.GONE);
            }
        }
    }
}

