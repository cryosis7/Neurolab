package com.soteria.neurolab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soteria.neurolab.viewModels.DeletePatientRecyclerItem;

import java.util.ArrayList;

public class DeletePatientRecyclerAdapter extends RecyclerView.Adapter<DeletePatientRecyclerAdapter.MyViewHolder>{

    private ArrayList<DeletePatientRecyclerItem> searchData;
    private LayoutInflater searchInflater;
    private ItemClickListener searchClickListener;

    DeletePatientRecyclerAdapter(Context context, ArrayList<DeletePatientRecyclerItem> passedData)
    {
        this.searchInflater = LayoutInflater.from(context);
        this.searchData = passedData;
    }

    @NonNull
    @Override
    public DeletePatientRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = searchInflater.inflate(R.layout.delete_patient_recycler_view_row, parent, false);
        return new DeletePatientRecyclerAdapter.MyViewHolder(view);
    }

    public void updateList(ArrayList<DeletePatientRecyclerItem> newList){
        searchData = new ArrayList<>();
        searchData.addAll(newList);
        notifyDataSetChanged();
    }

    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final DeletePatientRecyclerItem patientIdSet = searchData.get(position);
        holder.checkBox.setText(patientIdSet.getPatientReference());
        holder.checkBox.setTag(position);

        holder.checkBox.setOnCheckedChangeListener(null);

        holder.checkBox.setChecked(patientIdSet.getIsSelected());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                patientIdSet.setIsSelected(b);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchData.size();
    }

    /**
     * The view holder for each item in the recycler view
     *
     * The commented out code is potentially a way to fix the usability issue of only being able to
     * select the checkbox as opposed to selecting the entire item in the recycler view.
     * To be used in refactor
     */
    class MyViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;

        public MyViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.delete_patient_recycler_checkbox);
        }
    }

    void setClickListener(DeletePatientRecyclerAdapter.ItemClickListener itemClickListener)
    {
        this.searchClickListener = itemClickListener;
    }

    public interface ItemClickListener
    {
        void onItemClick(View view, int position);
    }
}
