package com.park_and_go;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.park_and_go.Favorito.Tipos;

import java.util.List;

public class FavoritosAdapter extends BaseAdapter {
    private Context mContext;
    private int layout;
    private List<Favorito> mFavoritos;

    public FavoritosAdapter(Context mContext, int layout, List<Favorito> mFavoritos) {
        this.mContext = mContext;
        this.layout = layout;
        this.mFavoritos = mFavoritos;
    }

    @Override
    public int getCount() {
        return mFavoritos.size();
    }

    @Override
    public Object getItem(int i) {
        return mFavoritos.get(i);
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

            v = layoutInflater.inflate(R.layout.list_favoritos, null);
        } else {
            v = convertView;
        }

        ImageView icon =(ImageView) v.findViewById(R.id.favImage);

        if (mFavoritos.get(i).getTipo() == Tipos.CONSULADO) {
            icon.setImageResource(R.drawable.consulate);
        } else if (mFavoritos.get(i).getTipo() == Tipos.OCIO) {
            icon.setImageResource(R.drawable.location);
        } else {
            icon.setImageResource(R.drawable.car);
        }

        TextView title = v.findViewById(R.id.favName);
        title.setText(mFavoritos.get(i).getTitle());

        return v;
    }
}
