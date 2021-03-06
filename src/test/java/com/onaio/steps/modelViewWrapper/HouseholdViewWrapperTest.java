package com.onaio.steps.modelViewWrapper;

import android.widget.EditText;
import android.widget.TextView;

import com.onaio.steps.R;
import com.onaio.steps.activities.NewHouseholdActivity;
import com.onaio.steps.exceptions.InvalidDataException;
import com.onaio.steps.helper.Constants;
import com.onaio.steps.model.Household;
import com.onaio.steps.model.InterviewStatus;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.text.SimpleDateFormat;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@Config(emulateSdk = 16,manifest = "src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class HouseholdViewWrapperTest {

    private NewHouseholdActivity activity;
    private String currentDate;

    @Before
    public void Setup(){
        activity = Robolectric.setupActivity(NewHouseholdActivity.class);
        currentDate = new SimpleDateFormat(Constants.DATE_FORMAT).format(new Date());

    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void ShouldGiveHouseholdWhenPhoneNumberAndCommentsAreEmpty() throws InvalidDataException {

        HouseholdViewWrapper householdViewWrapper = new HouseholdViewWrapper(activity);
        TextView nameView = ((TextView) activity.findViewById(R.id.generated_household_id));
        nameView.setText("new name");
        TextView numberView = (TextView) activity.findViewById(R.id.household_number);
        numberView.setText("");
        EditText commentsView = (EditText) activity.findViewById(R.id.household_comments);
        commentsView.setText("");
        Household household = householdViewWrapper.getHousehold(R.id.generated_household_id, R.id.household_number,R.id.household_comments);
        assertTrue(household.getName().equals("new name"));
        assertTrue(household.getStatus().equals(InterviewStatus.NOT_SELECTED));
    }

    @Test
    public void ShouldGiveHousehold() throws InvalidDataException {
        HouseholdViewWrapper householdViewWrapper = new HouseholdViewWrapper(activity);
        TextView nameView = ((TextView) activity.findViewById(R.id.generated_household_id));
        nameView.setText("new name");
        TextView numberView = (TextView) activity.findViewById(R.id.household_number);
        numberView.setText("123456789");
        EditText commentsView = (EditText) activity.findViewById(R.id.household_comments);
        commentsView.setText("Dummy Comments");
        Household household = householdViewWrapper.getHousehold(R.id.generated_household_id, R.id.household_number,R.id.household_comments);
        assertTrue(household.getName().equals("new name"));
        assertTrue(household.getPhoneNumber().equals("123456789"));
        assertEquals("Dummy Comments", household.getComments());
        assertTrue(household.getStatus().equals(InterviewStatus.NOT_SELECTED));

    }

    @Test
    public void ShouldUpdateHouseholdAndShouldNotUpdateGeneratedId() throws InvalidDataException {
        Household anotherHousehold = new Household("5", "1234-10", "80503456", "", InterviewStatus.NOT_DONE, currentDate, "Some Comments");
        HouseholdViewWrapper householdViewWrapper = new HouseholdViewWrapper(activity);
        TextView nameView = ((TextView) activity.findViewById(R.id.generated_household_id));
        TextView numberView = (TextView) activity.findViewById(R.id.household_number);
        EditText commentsView = (EditText) activity.findViewById(R.id.household_comments);
        numberView.setText("123456789");
        nameView.setText("123-20");
        commentsView.setText("Dummy Comments");

        Household household = householdViewWrapper.updateHousehold(anotherHousehold, R.id.household_number,R.id.household_comments);

       assertEquals("1234-10",household.getName());
        assertEquals("123456789",household.getPhoneNumber());
        assertEquals("Dummy Comments",household.getComments());
        assertTrue(household.getStatus().equals(InterviewStatus.NOT_DONE));

    }

}