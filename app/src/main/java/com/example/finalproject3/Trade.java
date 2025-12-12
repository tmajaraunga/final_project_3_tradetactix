package com.example.finalproject3;

public class Trade {

    public enum TradeType {
        HOME_WIN,
        AWAY_WIN,
        DRAW
    }

    private String matchId;
    private TradeType tradeType;
    private int amount;
    private String status;
    private String result;
    public Trade() {}

    public Trade(String matchId, TradeType tradeType, int amount) {
        this.matchId = matchId;
        this.tradeType = tradeType;
        this.amount = amount;
        this.status = "OPEN"; // Default status
    }

    // --- Getters ---
    public String getMatchId() { return matchId; }
    public TradeType getTradeType() { return tradeType; }
    public int getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getResult() { return result; }

    // --- Setters (needed for Firestore) ---
    public void setMatchId(String matchId) { this.matchId = matchId; }
    public void setTradeType(TradeType tradeType) { this.tradeType = tradeType; }
    public void setAmount(int amount) { this.amount = amount; }
    public void setStatus(String status) { this.status = status; }
    public void setResult(String result) { this.result = result; }


    @Override
    public String toString() {
        return "Trade{" +
                "matchId='" + matchId + '\'' +
                ", tradeType=" + tradeType +
                ", amount=" + amount +
                '}';
    }
}
