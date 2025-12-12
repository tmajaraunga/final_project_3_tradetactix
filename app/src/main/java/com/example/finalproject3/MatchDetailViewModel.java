package com.example.finalproject3;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MatchDetailViewModel extends ViewModel {private final MatchRepository repository;
    private final MutableLiveData<Match> selectedMatch = new MutableLiveData<>();

    public MatchDetailViewModel() {
        this.repository = MatchRepository.getInstance();
    }

    public LiveData<Match> getSelectedMatch() {
        return selectedMatch;
    }

    public void loadMatch(String matchId) {
        if (matchId == null || matchId.isEmpty()) {
            // Post null if id is invalid
            selectedMatch.setValue(null);
            return;
        }
        Match match = repository.findMatchById(matchId);
        selectedMatch.setValue(match);
    }
}
