package com.example.finalproject3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private final List<Match> matches = new ArrayList<>();
    private OnItemClickListener listener; // <-- ADD THIS

    // --- INTERFACE FOR CLICK EVENTS ---
    public interface OnItemClickListener {
        void onItemClick(Match match);
    }

    // --- METHOD TO SET THE LISTENER ---
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public void setMatches(List<Match> newMatches) {
        matches.clear();
        matches.addAll(newMatches);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.match_list_item, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Match currentMatch = matches.get(position);
        holder.bind(currentMatch);
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    // --- UPDATE THE VIEWHOLDER ---
    class MatchViewHolder extends RecyclerView.ViewHolder {
        private final TextView homeTeam;
        private final TextView awayTeam;
        private final TextView league;
        private final TextView matchTime;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            homeTeam = itemView.findViewById(R.id.textView_homeTeam);
            awayTeam = itemView.findViewById(R.id.textView_awayTeam);
            league = itemView.findViewById(R.id.textView_league);
            matchTime = itemView.findViewById(R.id.textView_matchTime);

            // --- SET THE CLICK LISTENER ON THE ITEM VIEW ---
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(matches.get(position));
                }
            });
        }

        public void bind(Match match) {
            homeTeam.setText(match.getHomeTeam());
            awayTeam.setText(match.getAwayTeam());
            league.setText(match.getLeague());
            matchTime.setText(match.getMatchTime());
        }
    }
}
