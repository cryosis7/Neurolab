package com.soteria.neurolab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchPatientRecyclerAdapter extends RecyclerView.Adapter<SearchPatientRecyclerAdapter.ViewHolder> {
    private List<String> searchData;
    private LayoutInflater searchInflater;
    private ItemClickListener searchClickListener;

    SearchPatientRecyclerAdapter(Context context, List<String> passedData)
    {
        this.searchInflater = LayoutInflater.from(context);
        this.searchData = passedData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = searchInflater.inflate(R.layout.recycler_view_row, parent, false);
        return new ViewHolder(view);
    }

    public void updateList(List<String> newList){
        searchData = new ArrayList<>();
        searchData.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        String patientIdSet = searchData.get(position);
        holder.patientInfo.setText(patientIdSet);
    }

    @Override
    public int getItemCount()
    {
        return searchData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView patientInfo;

        ViewHolder(View itemView)
        {
            super(itemView);
            patientInfo=itemView.findViewById(R.id.recycler_view_row_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            if(searchClickListener != null) searchClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    String getItem(int id)
    {
        return searchData.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener)
    {
        this.searchClickListener = itemClickListener;
    }

    public interface ItemClickListener
    {
        void onItemClick(View view, int position);
    }
}
