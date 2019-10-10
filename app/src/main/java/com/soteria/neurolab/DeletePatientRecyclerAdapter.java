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


/**
 * @author Jason Krieg
 * Class that handles the list of patients in the delete fragment
 */
public class DeletePatientRecyclerAdapter extends RecyclerView.Adapter<DeletePatientRecyclerAdapter.MyViewHolder>{

    private ArrayList<DeletePatientRecyclerItem> searchData;
    private LayoutInflater searchInflater;
    private ItemClickListener searchClickListener;

    /**
     * Default Constructor
     * @param context
     * @param passedData
     */
    DeletePatientRecyclerAdapter(Context context, ArrayList<DeletePatientRecyclerItem> passedData)
    {
        this.searchInflater = LayoutInflater.from(context);
        this.searchData = passedData;
    }

    /**
     * inflates the row item in the recycler
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public DeletePatientRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = searchInflater.inflate(R.layout.delete_patient_recycler_view_row, parent, false);
        return new DeletePatientRecyclerAdapter.MyViewHolder(view);
    }

    /**
     * Updates the recycler with the new list - Happens when the list is filtered by the search box
     * @param newList
     */
    public void updateList(ArrayList<DeletePatientRecyclerItem> newList){
        searchData = new ArrayList<>();
        searchData.addAll(newList);
        notifyDataSetChanged();
    }

    /**
     * Sets the values of each element for each individual row item.
     * @param holder
     * @param position
     */
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
