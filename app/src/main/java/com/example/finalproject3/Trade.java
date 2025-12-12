package com.example.finalproject3;

public class Trade {

    public enum TradeType {
        HOME_WIN,
        AWAY_WIN,
        DRAW
    }

    private final String matchId;
    private final TradeType tradeType;
    private final int amount; // Amount of "Tactix Points" risked

    public Trade(String matchId, TradeType tradeType, int amount) {
        this.matchId = matchId;
        this.tradeType = tradeType;
        this.amount = amount;
    }

    // --- Getters ---
    public String getMatchId() {
        return matchId;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "matchId='" + matchId + '\'' +
                ", tradeType=" + tradeType +
                ", amount=" + amount +
                '}';
    }
}
