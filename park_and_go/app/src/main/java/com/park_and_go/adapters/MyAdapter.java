package com.park_and_go.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.park_and_go.R;
import com.park_and_go.assets.Constants;
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
            v = layoutInflater.inflate(R.layout.list_places, null);
        } else {
            v = convertView;
        }

        ImageView icon = v.findViewById(R.id.image);
        ImageView fav = v.findViewById(R.id.favorito);
        TextView title = v.findViewById(R.id.name);
        TextView distance = v.findViewById(R.id.dista);
        switch (mPlaces.get(i).getTipo()) {
            case Constants.PARKING:
                icon.setImageResource(R.drawable.carcolor);
                break;
            case Constants.CONSULADO:
                icon.setImageResource(R.drawable.embajada);
                break;
            case Constants.THEATRE:
                icon.setImageResource(R.drawable.ocio);
                break;
        }
        if (mPlaces.get(i).isFavorito()) {
            fav.setImageResource(R.drawable.ic_favorite_black_24dp);
        }else{
            fav.setImageResource(0);
        }
        title.setText(mPlaces.get(i).title);
        distance.setText(mContext.getString(R.string.metros, mPlaces.get(i).distance));
        return v;
    }
}
