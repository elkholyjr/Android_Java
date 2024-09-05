package com.example.fraggasapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fraggasapp.fragment.FragmentHome;

import java.util.ArrayList;
import java.util.List;

public class DemoAdapter extends RecyclerView.Adapter<DemoVH> {
    public List<areas> areas;
    private FragmentHome fragmentHome;

    public DemoAdapter(List<areas> areas, FragmentHome fragmentHome) {
        this.areas = areas;
        this.fragmentHome = fragmentHome;
    }

    @NonNull
    @Override
    public DemoVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.area, parent, false);
        return new DemoVH(view, fragmentHome).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull DemoVH holder, int position) {
        holder.textView.setText(areas.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return areas.size();
    }
}




class DemoVH extends RecyclerView.ViewHolder {
    TextView textView;
    private DemoAdapter demoAdapter;
    private FragmentHome fragmentHome;

    public DemoVH(@NonNull View itemView, FragmentHome fragmentHome) {
        super(itemView);
        this.fragmentHome = fragmentHome;

        textView = itemView.findViewById(R.id.areaName);

        itemView.findViewById(R.id.deleteBut).setOnClickListener(view -> {
            demoAdapter.areas.remove(getAdapterPosition());
            demoAdapter.notifyItemRemoved(getAdapterPosition());
            if(demoAdapter.areas.size()<2){
                fragmentHome.updateResultText();
            }else {
                fragmentHome.Calc(view);
            }
        });
    }

    public DemoVH linkAdapter(DemoAdapter adapter) {
        this.demoAdapter = adapter;
        return this;
    }
}


