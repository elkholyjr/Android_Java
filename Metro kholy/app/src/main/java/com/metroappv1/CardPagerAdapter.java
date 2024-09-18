package com.metroappv1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardPagerAdapter extends RecyclerView.Adapter<CardPagerAdapter.CardViewHolder> {

    private final List<CardItem> cardItems; // List of items to display

    public CardPagerAdapter(List<CardItem> cardItems) {
        this.cardItems = cardItems;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        CardItem item = cardItems.get(position);
        holder.routeTitle.setText(item.getRouteTitle());

        // Use the custom StationAdapter
        StationAdapter stationAdapter = new StationAdapter(holder.itemView.getContext(), item.getStations());
        holder.outputStations.setAdapter(stationAdapter);
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }

    public List<CardItem> getCardItems() {
        return cardItems;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public TextView routeTitle;
        public ListView outputStations;

        public CardViewHolder(View view) {
            super(view);
            routeTitle = view.findViewById(R.id.routeTitle);
            outputStations = view.findViewById(R.id.stationsListView);
        }
    }
}

