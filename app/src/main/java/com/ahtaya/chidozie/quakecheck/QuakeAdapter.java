package com.ahtaya.chidozie.quakecheck;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class QuakeAdapter extends ArrayAdapter<EarthQuake> {

    QuakeAdapter(Activity context, ArrayList<EarthQuake> earthQuakes) {
        super(context, 0, earthQuakes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listView = convertView;

        if (listView == null){
            listView = LayoutInflater.from(getContext()).inflate(
                    R.layout.my_list, parent, false);
        }

        EarthQuake earthQuake = getItem(position);

        TextView magView = listView.findViewById(R.id.mag);
        magView.setText(earthQuake.getmMag());
        if(Double.parseDouble(earthQuake.getmMag()) > 4.9)
            magView.setBackground(getContext().getResources().getDrawable(R.drawable.circle_blue));
        if(Double.parseDouble(earthQuake.getmMag()) > 5.1)
            magView.setBackground(getContext().getResources().getDrawable(R.drawable.circle_mid));
        if(Double.parseDouble(earthQuake.getmMag()) > 5.3)
            magView.setBackground(getContext().getResources().getDrawable(R.drawable.circle_red));

        TextView placeView = listView.findViewById(R.id.place);
        placeView.setText(earthQuake.getmPlace());

        TextView timeView = listView.findViewById(R.id.time);
        timeView.setText(earthQuake.getmTime());

        return listView;
    }
}
