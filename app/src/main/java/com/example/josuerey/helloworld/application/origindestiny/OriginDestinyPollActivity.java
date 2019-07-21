package com.example.josuerey.helloworld.application.origindestiny;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.josuerey.helloworld.R;
import com.example.josuerey.helloworld.application.shared.TrackableBaseActivity;
import com.example.josuerey.helloworld.domain.origindestiny.OriginDestinyAssignmentResponse;
import com.example.josuerey.helloworld.domain.origindestiny.poll.OriginDestinyPoll;
import com.example.josuerey.helloworld.domain.origindestiny.answer.OriginDestinyPollAnswer;
import com.example.josuerey.helloworld.domain.origindestiny.poll.OriginDestinyPollRepository;
import com.example.josuerey.helloworld.domain.origindestiny.poll.OriginDestinyPollWrapper;
import com.example.josuerey.helloworld.domain.origindestiny.answer.OriginDestinyPollAnswerRepository;
import com.example.josuerey.helloworld.domain.origindestiny.Question;
import com.example.josuerey.helloworld.infrastructure.network.RemoteStorage;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Getter;


@Getter
public class OriginDestinyPollActivity extends TrackableBaseActivity
        implements RemoteStorage<OriginDestinyPollWrapper, OriginDestinyPollRepository> {

    private OriginDestinyAssignmentResponse assignment;
    private OriginDestinyPollAnswerRepository answerRepository;
    private OriginDestinyPollRepository repository;
    private LinearLayout questionaryParentLinearLayout;
    private HashMap<Integer, View> questionsMap;
    private List<OriginDestinyPollAnswer> answersGiven;
    private String postParamName;
    private Application appContext;
    private String endpointUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.origin_destiny_poll_questionary);
        questionaryParentLinearLayout = findViewById(R.id.parent_questions_layout);
        appContext = getApplication();
        endpointUrl = "api/persist/pollAnswers";
        postParamName = "pollAnswersData";
        answerRepository = new OriginDestinyPollAnswerRepository(getApplication());
        repository = new OriginDestinyPollRepository(getApplication());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            assignment = new Gson().fromJson(
                    extras.getString("originDestinyAssignment"), OriginDestinyAssignmentResponse.class);
        }

        questionsMap = new HashMap<>();
        answersGiven = new LinkedList<>();
        inflateQuestions(assignment.getOriginDestinyQuestionary().getQuestions());
        requestPermissions();
    }

    /**
     * Dynamically inflate the poll questions in the display.
     *
     * @param questions list of questions to be displayed in the screen
     */
    private void inflateQuestions(List<Question> questions) {
        LayoutInflater questionInflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (Question question : questions) {
            View currentQuestionView =
                    questionInflater.inflate(R.layout.origin_destiny_poll_field, null);
            currentQuestionView.setId(question.getId());
            questionsMap.put(question.getId(), currentQuestionView);

            TextView questionTextView = currentQuestionView.findViewById(R.id.question_value);
            questionTextView.setText(question.getName());

            if (!question.getAnswers().isEmpty()) {

                Spinner possibleAnswersSpinner =
                        currentQuestionView.findViewById(R.id.answer_option_spinner);
                possibleAnswersSpinner.setVisibility(View.VISIBLE);
                possibleAnswersSpinner.setAdapter(new ArrayAdapter<>(this,
                                android.R.layout.select_dialog_item, question.getAnswers()));
                EditText answerEditText = currentQuestionView.findViewById(R.id.answer_edit_text);
                answerEditText.setHint("Otra respuesta");
            }
            questionaryParentLinearLayout.addView(
                    currentQuestionView, questionaryParentLinearLayout.getChildCount());
        }
    }

    /**
     * This method is called when the poll finish and is ready to be storage remotely.
     * @param view
     */
    public void onClickSendPoll(View view) {

        OriginDestinyPoll poll =
                repository.createOriginDestinyPoll(currentLocation, this.assignment.getId());
        repository.save(poll);
        List<OriginDestinyPollAnswer> answers = collectAnswers(poll.getId());
        answerRepository.saveAll(answers);

        postItemsInBatch(Collections.singletonList(OriginDestinyPollWrapper.builder()
                .poll(poll)
                .answers(answers)
                .build()));
        createAlertDialog();
    }

    /**
     * Iterates over the question {@link View}'s and erase the previous answers.
     */
    private void cleanAnswers() {
        answersGiven.clear();
        for(int i = 0; i < questionaryParentLinearLayout.getChildCount(); i++) {
            View currentView = questionaryParentLinearLayout.getChildAt(i);
            EditText answerGiven = currentView.findViewById(R.id.answer_edit_text);
            answerGiven.setText("");
        }
    }

    /**
     * Creates a new {@link AlertDialog} once the poll has been complete.
     */
    private void createAlertDialog() {
        AlertDialog.Builder askForNewPollDialog = new AlertDialog.Builder(this);
        askForNewPollDialog.setTitle("Encuesta enviada");
        askForNewPollDialog.setMessage("¿Desea hacer una nueva encuesta?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cleanAnswers();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        AlertDialog myDialog = askForNewPollDialog.create();
        myDialog.show();
    }

    /**
     * Iterates over the available answers and return them.
     *
     * @param pollId that is going to be assigned to the answers.
     * @return List of {@link OriginDestinyPollAnswer}'s given by the user.
     */
    private List<OriginDestinyPollAnswer> collectAnswers(long pollId) {
        Spinner answerOptionsSpinner;
        EditText answerEditText;
        String finalAnswer = null;
        for (Map.Entry<Integer, View> entry : questionsMap.entrySet()) {
            answerOptionsSpinner = entry.getValue().findViewById(R.id.answer_option_spinner);
            answerEditText = entry.getValue().findViewById(R.id.answer_edit_text);
            if (answerEditText.getText().toString().isEmpty()) {
                if (answerOptionsSpinner.getAdapter().getCount() > 0) {
                    finalAnswer = answerOptionsSpinner.getSelectedItem().toString();
                }
            } else {
                finalAnswer = answerEditText.getText().toString();
            }
            answersGiven.add(OriginDestinyPollAnswer.builder().answerGiven(finalAnswer)
                    .questionId(entry.getValue().getId()).pollId(pollId).build());
        }
        return answersGiven;
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (requestPermissions())
            locationStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestPermissions())
            locationStart();
    }
}
