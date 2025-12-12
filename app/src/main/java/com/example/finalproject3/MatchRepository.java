package com.example.finalproject3;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class MatchRepository {

    private static volatile MatchRepository instance;
    private final ArrayList<Match> sampleMatches = new ArrayList<>();

    private MatchRepository() {
        // Private constructor to prevent instantiation.
        // This is where you would initialize a database or network service.
        seedSampleData();
    }

    public static MatchRepository getInstance() {
        if (instance == null) {
            synchronized (MatchRepository.class) {
                if (instance == null) {
                    instance = new MatchRepository();
                }
            }
        }
        return instance;
    }

    private void seedSampleData() {
        sampleMatches.add(new Match("1", "Manchester United", "Arsenal", "15:00 GMT", "Premier League"));
        sampleMatches.add(new Match("2", "Real Madrid", "FC Barcelona", "20:00 GMT", "La Liga"));
        sampleMatches.add(new Match("3", "Bayern Munich", "Borussia Dortmund", "17:30 GMT", "Bundesliga"));
        sampleMatches.add(new Match("4", "New York Red Bulls", "Inter Miami", "19:00 EST", "MLS"));
    }

    public LiveData<List<Match>> getMatches() {
        MutableLiveData<List<Match>> data = new MutableLiveData<>();
        data.setValue(sampleMatches);
        return data;
    }

    public Match findMatchById(String matchId) {
        for (Match match : sampleMatches) {
            if (match.getId().equals(matchId)) {
                return match;
            }
        }
        return null; // Return null if not found
    }
}
