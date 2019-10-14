package com.sgcities.tdc.optimizer.utilities;

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

import com.sgcities.tdc.optimizer.R;
import com.sgcities.tdc.optimizer.infrastructure.network.VisualOccupationAssignmentResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class VisualOccAssignmentListAdapter extends ArrayAdapter<VisualOccupationAssignmentResponse>
        implements View.OnClickListener {

    private List<VisualOccupationAssignmentResponse> dataSet;
    private final String TAG = this.getClass().getSimpleName();
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView viaOfStudy;
        TextView directionLane;
        TextView beginAtDate;
        TextView crossroadUnderStudy;
    }

    public VisualOccAssignmentListAdapter(List<VisualOccupationAssignmentResponse> data, Context context) {
        super(context, R.layout.asc_desc_assignment_view, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        VisualOccupationAssignmentResponse dataModel = (VisualOccupationAssignmentResponse)object;

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
        VisualOccupationAssignmentResponse dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.visual_occupation_assignment_view, parent, false);
            viewHolder.viaOfStudy = convertView.findViewById(R.id.via_of_study_value);
            viewHolder.directionLane = convertView.findViewById(R.id.lane_direction_value);
            viewHolder.beginAtDate = convertView.findViewById(R.id.begin_at_date_value);
            viewHolder.crossroadUnderStudy = convertView.findViewById(R.id.crossroad_under_study_value);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ?
                R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.viaOfStudy.setText(dataModel.getViaOfStudy());
        viewHolder.directionLane.setText(dataModel.getDirectionLane());
        try {
            Date date = format.parse(dataModel.getBeginAtDate());
            viewHolder.beginAtDate.setText(USER_DATE_FORMAT.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            viewHolder.beginAtDate.setText(dataModel.getBeginAtDate());
        }
        viewHolder.crossroadUnderStudy.setText(dataModel.getBeginAtPlace());
        return convertView;
    }
}