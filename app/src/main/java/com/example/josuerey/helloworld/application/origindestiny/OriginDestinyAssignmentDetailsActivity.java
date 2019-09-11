package com.example.josuerey.helloworld.application.origindestiny;

import android.app.Application;
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
import com.example.josuerey.helloworld.domain.origindestiny.poll.OriginDestinyPollRepository;
import com.example.josuerey.helloworld.domain.origindestiny.poll.OriginDestinyPollWrapper;
import com.example.josuerey.helloworld.infrastructure.network.RemoteStorage;
import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

@Getter
public class OriginDestinyAssignmentDetailsActivity extends BaseActivity
        implements RemoteStorage<OriginDestinyPollWrapper, OriginDestinyPollRepository> {

    private String postParamName;
    private Application appContext;
    private String endpointUrl;
    private OriginDestinyAssignmentResponse assignment;
    private LinearLayout pollsRecordsLinearLayout;
    private final Gson gson = new Gson();
    private OriginDestinyPollRepository repository;
    private List<OriginDestinyPollWrapper> internalPolls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.origin_destiny_assignment_details);
        pollsRecordsLinearLayout = findViewById(R.id.polls_sent_layout);
        repository = new OriginDestinyPollRepository(getApplication());
        appContext = getApplication();
        endpointUrl = "/app/api/persist/pollAnswers";
        postParamName = "pollAnswersData";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            assignment = new Gson().fromJson(extras.getString("originDestinyAssignment"),
                    OriginDestinyAssignmentResponse.class);
        }

        internalPolls = repository.findByAssignmentId(this.assignment.getId());
        inflatePreviousPolls();
        retryBackup();
    }

    /**
     * Tries to backup polls pending to remotely backup.
     */
    private void retryBackup() {
        List<OriginDestinyPollWrapper> recordsPendingToBackUp = new LinkedList<>();
        for (OriginDestinyPollWrapper poll : internalPolls) {
            if (poll.getBackedUpRemotely() == 0) {
                recordsPendingToBackUp.add(poll);
            }
        }

        if (recordsPendingToBackUp.isEmpty()) {
            Log.d(TAG, "There are no records to back up");
        } else {
            Log.d(TAG, String.format("Retrying to backup %d polls", recordsPendingToBackUp.size()));
            postItemsInBatch(recordsPendingToBackUp);
        }
    }

    /**
     * Extract polls linked to current assignment from internal database and display them in the
     * screen.
     */
    private void inflatePreviousPolls() {
        final LayoutInflater questionInflater =
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
                new Intent(this, OriginDestinyPollActivity.class);
        startPollIntent.putExtra("originDestinyAssignment", gson.toJson(assignment));
        Log.d(TAG, "Handling OriginDestinyAssignment: " + assignment.getId());
        this.startActivity(startPollIntent);
    }
}
