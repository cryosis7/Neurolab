package com.soteria.neurolab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VisualMemoryRecyclerAdapter extends RecyclerView.Adapter<VisualMemoryRecyclerAdapter.ViewHolder>
{
    private String[] visualData;
    private LayoutInflater visualInflater;
    private ItemClickListener visualClickListener;
    TextView temp;

    // data is passed into the constructor
    VisualMemoryRecyclerAdapter(Context context, String[] data) {
        this.visualInflater = LayoutInflater.from(context);
        this.visualData = data;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = visualInflater.inflate(R.layout.vstm_recycler_view_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.memoryButton.setText(visualData[position]);
    }

    // total number of cells
    @Override
    public int getItemCount()
    {
        return visualData.length;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView memoryButton;

        ViewHolder(View itemView)
        {
            super(itemView);
            memoryButton = itemView.findViewById(R.id.visual_memory_button);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (visualClickListener != null) visualClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return visualData[id];
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener)
    {
        this.visualClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener
    {
        void onItemClick(View view, int position);
    }
}
