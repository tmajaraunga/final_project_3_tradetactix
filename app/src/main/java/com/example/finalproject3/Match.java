package com.example.finalproject3;

public class Match {
    private final String id;
    private final String homeTeam;
    private final String awayTeam;
    private final String matchTime;
    private final String league;

    public Match(String id, String homeTeam, String awayTeam, String matchTime, String league) {
        this.id = id;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.matchTime = matchTime;
        this.league = league;
    }

    // --- Getters ---
    public String getId() {
        return id;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public String getMatchTime() {
        return matchTime;
    }

    public String getLeague() {
        return league;
    }
}
