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
import com.example.josuerey.helloworld.infrastructure.network.AscDescAssignmentResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AssignmentListAdapter extends ArrayAdapter<AscDescAssignmentResponse>
        implements View.OnClickListener{

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
        AscDescAssignmentResponse dataModel = (AscDescAssignmentResponse)object;

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
        SimpleDateFormat USER_DATE_FORMAT = new SimpleDateFormat("dd/MM/yy, h:mm a");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
            viewHolder.beginAtPlace = convertView.findViewById(R.id.begin_at_place_value);
            viewHolder.beginAtDate = convertView.findViewById(R.id.begin_at_date_value);

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

        try {
            Date date = format.parse(dataModel.getBeginAtDate());
            viewHolder.beginAtDate.setText(USER_DATE_FORMAT.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            viewHolder.beginAtDate.setText(dataModel.getBeginAtDate());
        }

        viewHolder.beginAtPlace.setText(dataModel.getBeginAtPlace());
        // Return the completed view to render on screen
        return convertView;
    }

}
