package com.soteria.neurolab;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class VisualMemoryGridAdapter extends BaseAdapter {

    private final Context mContext;
    private final Button[] gridButtons;

    // 1
    public VisualMemoryGridAdapter(Context context, Button[] gridButtons) {
        this.mContext = context;
        this.gridButtons = gridButtons;
    }

    // 2
    @Override
    public int getCount() {
        return gridButtons.length;
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView dummyTextView = new TextView(mContext);
        dummyTextView.setText(String.valueOf(position));
        return dummyTextView;
    }
}


