package com.sgcities.tdc.optimizer.utilities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sgcities.tdc.optimizer.R;
import com.sgcities.tdc.optimizer.domain.origindestiny.OriginDestinyAssignmentResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OriginDestinyAssignmentListAdapter extends ArrayAdapter<OriginDestinyAssignmentResponse>
        implements View.OnClickListener {

    private List<OriginDestinyAssignmentResponse> dataSet;
    private final String TAG = this.getClass().getSimpleName();
    private int lastPosition = -1;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView questionaryType;
        TextView beginAtPlace;
        TextView beginAtDate;
        TextView numberOfPolls;
    }

    public OriginDestinyAssignmentListAdapter(List<OriginDestinyAssignmentResponse> data, Context context) {
        super(context, R.layout.origin_destiny_poll_assignment_view, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        OriginDestinyAssignmentResponse dataModel = (OriginDestinyAssignmentResponse)object;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SimpleDateFormat USER_DATE_FORMAT = new SimpleDateFormat("dd/MM/yy, h:mm a");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        OriginDestinyAssignmentResponse dataModel = getItem(position);

        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.origin_destiny_poll_assignment_view, parent, false);
            viewHolder.beginAtDate = convertView.findViewById(R.id.begin_at_date_value);
            viewHolder.beginAtPlace = convertView.findViewById(R.id.begin_place_value);
            viewHolder.questionaryType = convertView.findViewById(R.id.poll_type_value);
            viewHolder.numberOfPolls = convertView.findViewById(R.id.number_of_polls_value);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ?
                R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        try {
            Date date = format.parse(dataModel.getBeginAtDate());
            viewHolder.beginAtDate.setText(USER_DATE_FORMAT.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            viewHolder.beginAtDate.setText(dataModel.getBeginAtDate());
        }
        viewHolder.beginAtPlace.setText(dataModel.getBeginAtPlace());
        viewHolder.questionaryType.setText(dataModel.getOriginDestinyQuestionary().getName());
        viewHolder.numberOfPolls.setText(String.valueOf(dataModel.getNumberOfPolls()));
        return convertView;
    }
}