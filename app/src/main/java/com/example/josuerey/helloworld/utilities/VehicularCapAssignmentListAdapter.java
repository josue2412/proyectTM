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
import com.example.josuerey.helloworld.domain.movement.Movement;
import com.example.josuerey.helloworld.infrastructure.network.VehicularCapAssignmentResponse;

import java.util.List;

public class VehicularCapAssignmentListAdapter extends ArrayAdapter<VehicularCapAssignmentResponse>
        implements View.OnClickListener{

    private List<VehicularCapAssignmentResponse> dataSet;
    private final String TAG = this.getClass().getSimpleName();
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView crosses;
        TextView begin_date;
        TextView numberOfMovements;
        TextView remainingTime;
    }

    public VehicularCapAssignmentListAdapter(List<VehicularCapAssignmentResponse> data,
                                             Context context) {
        super(context, R.layout.assginment_view, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        VehicularCapAssignmentResponse dataModel = (VehicularCapAssignmentResponse)object;

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
        VehicularCapAssignmentResponse dataModel = getItem(position);
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

        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.crosses.setText(movementsSerialize(dataModel.getMovements()));
        viewHolder.begin_date.setText(dataModel.getBeginAtDate());
        viewHolder.numberOfMovements.setText(String.valueOf(dataModel.getMovements().size()));
        viewHolder.remainingTime.setText(String.valueOf(dataModel.getDurationInHours()));
        // Return the completed view to render on screen
        return convertView;
    }

    private String movementsSerialize(List<Movement> movements) {
        StringBuilder movementsSerialized = new StringBuilder();
        for(Movement movement: movements) {
            movementsSerialized.append(String.format("%s %s -> %s %s\n",
                    movement.getStreet_from(), movement.getStreet_from_direction(),
                    movement.getStreet_to(), movement.getStreet_to_direction()));
        }
        return movementsSerialized.toString();
    }
}