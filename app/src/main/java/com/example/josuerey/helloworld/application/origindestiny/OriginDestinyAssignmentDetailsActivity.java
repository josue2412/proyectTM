package com.example.josuerey.helloworld.application.origindestiny;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.josuerey.helloworld.R;
import com.example.josuerey.helloworld.application.shared.BaseActivity;
import com.example.josuerey.helloworld.domain.origindestiny.OriginDestinyAssignmentResponse;
import com.example.josuerey.helloworld.domain.origindestiny.OriginDestinyPollRepository;
import com.example.josuerey.helloworld.domain.origindestiny.OriginDestinyPollWrapper;
import com.google.gson.Gson;

import java.util.List;

public class OriginDestinyAssignmentDetailsActivity extends BaseActivity {

    private OriginDestinyAssignmentResponse assignment;
    private LinearLayout pollsRecordsLinearLayout;
    private final Gson gson = new Gson();
    private OriginDestinyPollRepository pollRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.origin_destiny_assignment_details);
        pollsRecordsLinearLayout = findViewById(R.id.polls_sent_layout);
        pollRepository = new OriginDestinyPollRepository(getApplication());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            assignment = new Gson().fromJson(extras.getString("originDestinyAssignment"),
                    OriginDestinyAssignmentResponse.class);
        }

        inflatePreviousPolls();
    }

    /**
     * Extract polls linked to current assignment from internal database and display them in the
     * screen.
     */
    private void inflatePreviousPolls() {
        List<OriginDestinyPollWrapper> internalPolls =
                pollRepository.findByAssignmentId(this.assignment.getId());

        LayoutInflater questionInflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (!internalPolls.isEmpty()) {
            for (OriginDestinyPollWrapper poll : internalPolls) {
                addView(poll.getPoll().getTimeStamp(), pollsRecordsLinearLayout,
                        questionInflater, true);
            }
        } else {
            addView("No hay encuestas disponibles", pollsRecordsLinearLayout,
                    questionInflater, false);
        }
    }

    /**
     *
     * @param text to be displayed in the screen.
     * @param affectedLayout in which the view is going to be placed.
     * @param inflater used to inflate the view.
     * @param showLabel true if you want to show the view label
     */
    private void addView(String text, LinearLayout affectedLayout, LayoutInflater inflater,
                         boolean showLabel) {
        View currentPollInfoView =
                inflater.inflate(R.layout.origin_destiny_poll_info_view, null);

        TextView pollTextView = currentPollInfoView.findViewById(R.id.poll_information_value);

        if (!showLabel) {
            TextView pollTextLabelView =
                    currentPollInfoView.findViewById(R.id.poll_information_label);
            pollTextLabelView.setVisibility(View.INVISIBLE);
        }
        pollTextView.setText(text);
        affectedLayout.addView(currentPollInfoView, affectedLayout.getChildCount());
    }


    /**
     * Triggered on the user clicks the star poll button.
     * @param view
     */
    public void createNewPoll(View view) {

        Intent startPollIntent =
                new Intent(this.getApplicationContext(), OriginDestinyPollActivity.class);
        startPollIntent.putExtra("originDestinyAssignment", gson.toJson(assignment));
        Log.d(TAG, "Handling OriginDestinyAssignment: " + assignment.getId());
        getApplicationContext().startActivity(startPollIntent);
    }
}
