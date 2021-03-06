package com.onaio.steps.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.onaio.steps.R;
import com.onaio.steps.exceptions.InvalidDataException;
import com.onaio.steps.helper.Constants;
import com.onaio.steps.helper.CustomDialog;
import com.onaio.steps.helper.DatabaseHelper;
import com.onaio.steps.model.Participant;
import com.onaio.steps.modelViewWrapper.ParticipantViewWrapper;

public class NewParticipantActivity  extends Activity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        populateView();
    }

    private void populateView() {
        setContentView(R.layout.participant_form);
        TextView header = (TextView) findViewById(R.id.form_header);
        header.setText(R.string.action_add_participant);
        Button doneButton = (Button) findViewById(R.id.ic_done);
        doneButton.setText(R.string.add);
    }

    public void save(View view) {
        Intent intent =new Intent();
        try{

            Participant participant = new ParticipantViewWrapper(this)
                    .getFromView();
            participant.save(new DatabaseHelper(this));
            intent.putExtra(Constants.PARTICIPANT,participant);
            setResult(RESULT_OK, intent);
            finish();
        } catch (InvalidDataException e) {
            new CustomDialog().notify(this, CustomDialog.EmptyListener,e.getMessage(),R.string.error_title);
        }
    }

    public void cancel(View view){
        finish();
    }
}
