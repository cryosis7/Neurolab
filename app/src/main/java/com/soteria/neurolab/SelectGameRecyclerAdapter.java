package com.soteria.neurolab;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * A recycler adapter that is used to manage the select game list.
 *
 * @author Scott Curtis
 */
public class SelectGameRecyclerAdapter extends RecyclerView.Adapter<SelectGameRecyclerAdapter.MyViewHolder> {
    private List<String> gameList;
    private ItemClickListener gameClickListener;

    private String getItem(int id)
    {
        return gameList.get(id);
    }

    /**
     * The view holder for each item in the recycler view
     *
     * The commented out code is potentially a way to fix the usability issue of only being able to
     * select the checkbox as opposed to selecting the entire item in the recycler view.
     * To be used in refactor
     */
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView gameInfo;

        MyViewHolder(View itemView)
        {
            super(itemView);
            gameInfo = itemView.findViewById(R.id.recycler_view_row_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            if(gameClickListener != null) gameClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public SelectGameRecyclerAdapter(List<String> gameList) {
        this.gameList = gameList;
    }

    @NonNull
    @Override
    public SelectGameRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_row, parent, false);
        return new SelectGameRecyclerAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.gameInfo.setText(gameList.get(position));
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    void setClickListener(ItemClickListener itemClickListener)
    {
        this.gameClickListener = itemClickListener;
    }

    public interface ItemClickListener
    {
        void onItemClick(View view, int position);
    }
}