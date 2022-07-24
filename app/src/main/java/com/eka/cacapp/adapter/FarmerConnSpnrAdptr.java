package com.eka.cacapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class FarmerConnSpnrAdptr extends ArrayAdapter<String> {

    public FarmerConnSpnrAdptr(Context context, int resource, ArrayList<String> items){
        super(context,resource,items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView)super.getView(position, convertView, parent);
        if(position==0){
            view.setTextColor(Color.GRAY);
        }else {
            view.setTextColor(Color.BLACK);
        }
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

