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
import com.onaio.steps.model.Household;
import com.onaio.steps.modelViewWrapper.HouseholdViewWrapper;

import static com.onaio.steps.helper.Constants.HOUSEHOLD_SEED;
import static com.onaio.steps.helper.Constants.PHONE_ID;

public class NewHouseholdActivity extends Activity {

    private final DatabaseHelper db = new DatabaseHelper(this);
    private String phoneId;
    private int householdSeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        populateView();
        populateDataFromIntent();
        populateGeneratedHouseholdId();
    }

    private void populateView() {
        setContentView(R.layout.household_form);
        TextView header = (TextView) findViewById(R.id.form_header);
        header.setText(R.string.household_new_header);
        Button doneButton = (Button) findViewById(R.id.ic_done);
        doneButton.setText(R.string.add);
    }

    private void populateGeneratedHouseholdId() {
        TextView phoneIdView = (TextView) findViewById(R.id.generated_household_id);
        int householdsCount = Household.getAllCount(db);
        int generatedId = householdSeed + householdsCount;
        phoneIdView.setText(String.format("%s-%d",phoneId, generatedId));
    }

    private void populateDataFromIntent() {
        Intent intent = getIntent();
        phoneId = intent.getStringExtra(PHONE_ID);
        String householdSeedString = intent.getStringExtra(HOUSEHOLD_SEED);
        householdSeedString = householdSeedString == null || householdSeedString.equals("") ? "1" : householdSeedString;
        householdSeed = Integer.parseInt(householdSeedString);
    }

    public void save(View view) {
        try {
            Intent intent = this.getIntent();
            Household household = new HouseholdViewWrapper(this).getHousehold(R.id.generated_household_id, R.id.household_number,R.id.household_comments);
            household.save(db);
            intent.putExtra(Constants.HOUSEHOLD,household);
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
