package com.park_and_go.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.park_and_go.R;
import com.park_and_go.common.PlacesResponse;

import java.util.List;

public class MyAdapter extends BaseAdapter {

    private Context mContext;
    private int layout;
    private List<PlacesResponse.Places> mPlaces;

    public MyAdapter(Context mContext, int layout, List<PlacesResponse.Places> mPlaces) {
        this.mContext = mContext;
        this.layout = layout;
        this.mPlaces = mPlaces;
    }

    @Override
    public int getCount() {
        return mPlaces.size();
    }

    @Override
    public Object getItem(int i) {
        return mPlaces.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        View v = null;

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = layoutInflater.inflate(R.layout.list_parks, null);
        } else {
            v = convertView;
        }

        ImageView icon = (ImageView) v.findViewById(R.id.parkImage);
        icon.setImageResource(R.drawable.car);
        TextView title = (TextView) v.findViewById(R.id.name);
        title.setText(mPlaces.get(i).title);

        return v;
    }
}