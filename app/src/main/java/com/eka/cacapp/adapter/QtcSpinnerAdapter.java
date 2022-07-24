package com.eka.cacapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class QtcSpinnerAdapter extends ArrayAdapter<String> {

    public QtcSpinnerAdapter(Context context, int resource, ArrayList<String> items){
        super(context,resource,items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView)super.getView(position, convertView, parent);
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView v = null;
        if(position == 0){

            TextView tv = new TextView(getContext());
            tv.setVisibility(View.GONE);
            v = tv;

        }else {
            v = (TextView) super.getDropDownView(position, null, parent);
        }

        return v;
    }


}
