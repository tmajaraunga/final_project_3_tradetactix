package com.example.finalproject3;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class MatchListViewModel extends ViewModel {

    private final MatchRepository repository;
    private final LiveData<List<Match>> matches;

    public MatchListViewModel() {
        repository = MatchRepository.getInstance();
        matches = repository.getMatches();
    }

    public LiveData<List<Match>> getMatches() {
        return matches;
    }
}

