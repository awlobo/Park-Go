package com.park_and_go.adapters;

import android.content.Context;
import android.util.Log;
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
    private int code;

    public MyAdapter(Context mContext, int layout, List<PlacesResponse.Places> mPlaces, int code) {
        this.mContext = mContext;
        this.layout = layout;
        this.mPlaces = mPlaces;
        this.code = code;
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

        Log.d("MyAdapter","Valor de code: "+code);

        ImageView icon = (ImageView) v.findViewById(R.id.parkImage);

        switch (code) {
            case 1:
                icon.setImageResource(R.drawable.car);
                break;
            case 2:
                Log.d("MyAdapter","imagen consulates");
                icon.setImageResource(R.drawable.consulate);
                break;
            case 3:
                icon.setImageResource(R.drawable.car);
                break;
        }
        TextView title = (TextView) v.findViewById(R.id.name);
        title.setText(mPlaces.get(i).title);

        return v;
    }
}
