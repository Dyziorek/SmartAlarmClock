package com.example.smartalarmclock.ui.notifications;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.NotifyHolder> {

    class NotifyHolder extends RecyclerView.ViewHolder{

        public NotifyHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public NotifyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotifyHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifyHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }




}
