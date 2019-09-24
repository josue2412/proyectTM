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
import com.example.josuerey.helloworld.infrastructure.network.AssignmentResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<Assignment> implements View.OnClickListener{

    private List<Assignment> dataSet;
    private final String TAG = this.getClass().getSimpleName();
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView crosses;
        TextView begin_date;
        TextView numberOfMovements;
        TextView remainingTime;
    }

    public CustomAdapter(List<Assignment> data, Context context) {
        super(context, R.layout.assginment_view, data);
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
        SimpleDateFormat USER_DATE_FORMAT = new SimpleDateFormat("h:mm a");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Get the data item for this position
        Assignment dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.assginment_view, parent, false);
            viewHolder.crosses = convertView.findViewById(R.id.crosses_value);
            viewHolder.begin_date = convertView.findViewById(R.id.begin_date_value);
            viewHolder.numberOfMovements = convertView.findViewById(R.id.number_of_movements_value);
            viewHolder.remainingTime = convertView.findViewById(R.id.remainingTime_value);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        try {
            Date date = format.parse(dataModel.getBeginAt());
            viewHolder.begin_date.setText(USER_DATE_FORMAT.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            viewHolder.begin_date.setText(dataModel.getBeginAt());
        }

        viewHolder.crosses.setText(movementsSerialize(dataModel.getMovements()));
        viewHolder.numberOfMovements.setText(String.valueOf(dataModel.getMovements().size()));
        viewHolder.remainingTime.setText(String.valueOf(dataModel.getTimeOfStudy()));
        // Return the completed view to render on screen
        return convertView;
    }

    private String movementsSerialize(List<AssignmentResponse.Movement> movements) {
        StringBuilder movementsSerialized = new StringBuilder();
        for(AssignmentResponse.Movement movement: movements) {
            movementsSerialized.append(String.format("%s: %d (%s -> %s)\n",
                    movement.getMovement_name(), movement.getMovement_code(),
                    movement.getStreet_from(), movement.getStreet_to()));
        }
        return movementsSerialized.toString();
    }
}