package com.example.josuerey.helloworld.utilidades;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.josuerey.helloworld.R;
import com.example.josuerey.helloworld.network.AssignmentResponse;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<AssignmentResponse> implements View.OnClickListener{

    private ArrayList<AssignmentResponse> dataSet;
    private final String TAG = this.getClass().getSimpleName();
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView origin;
        TextView destiny;
        TextView begin_date;
        TextView duration;
    }

    public CustomAdapter(ArrayList<AssignmentResponse> data, Context context) {
        super(context, R.layout.assginment_view, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        AssignmentResponse dataModel = (AssignmentResponse)object;

        switch (v.getId())
        {
            case R.id.origin_value:
                Log.i(TAG, dataModel.getMovement());
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        AssignmentResponse dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.assginment_view, parent, false);
            viewHolder.origin = (TextView) convertView.findViewById(R.id.origin_value);
            viewHolder.destiny = (TextView) convertView.findViewById(R.id.destiny_value);
            viewHolder.begin_date = (TextView) convertView.findViewById(R.id.begin_date_value);
            viewHolder.duration = (TextView) convertView.findViewById(R.id.duration_value);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.origin.setText(dataModel.getStreet_from() + " " + dataModel.getStreet_from_direction());
        viewHolder.destiny.setText(dataModel.getStreet_to() + " " + dataModel.getStreet_to_direction());
        viewHolder.begin_date.setText(dataModel.getBegin_at());
        viewHolder.duration.setText(String.valueOf(dataModel.getDuration_in_hours()));
        // Return the completed view to render on screen
        return convertView;
    }
}