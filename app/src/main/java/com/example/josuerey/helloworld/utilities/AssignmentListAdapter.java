package com.example.josuerey.helloworld.utilities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.josuerey.helloworld.R;
import com.example.josuerey.helloworld.domain.assignment.Assignment;
import com.example.josuerey.helloworld.network.AscDescAssignmentResponse;

import java.util.List;

public class AssignmentListAdapter extends ArrayAdapter<AscDescAssignmentResponse> implements View.OnClickListener{

    private List<AscDescAssignmentResponse> dataSet;
    private final String TAG = this.getClass().getSimpleName();
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView route;
        TextView via;
        TextView beginAtDate;
        TextView beginAtPlace;
    }

    public AssignmentListAdapter(List<AscDescAssignmentResponse> data, Context context) {
        super(context, R.layout.asc_desc_assignment_view, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Assignment dataModel = (Assignment)object;

        switch (v.getId())
        {
            case R.id.crosses_value:
                Log.i(TAG, String.format("AssignmentId: %i", dataModel.getId()));
                break;
        }
    }

    private int lastPosition = -1;

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        AscDescAssignmentResponse dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.asc_desc_assignment_view, parent, false);
            viewHolder.route = convertView.findViewById(R.id.route_value);
            viewHolder.via = convertView.findViewById(R.id.via_value);
            viewHolder.beginAtDate = convertView.findViewById(R.id.begin_at_date_value);
            viewHolder.beginAtPlace = convertView.findViewById(R.id.begin_at_place_value);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.route.setText(dataModel.getRoute());
        viewHolder.via.setText(dataModel.getVia());
        viewHolder.beginAtDate.setText(dataModel.getBeginAtDate());
        viewHolder.beginAtPlace.setText(dataModel.getBeginAtPlace());
        // Return the completed view to render on screen
        return convertView;
    }

}
