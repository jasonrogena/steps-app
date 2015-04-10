package com.onaio.steps.activity;


import android.content.Intent;
import android.database.Cursor;
import android.widget.TextView;

import com.onaio.steps.R;
import com.onaio.steps.helper.Constants;
import com.onaio.steps.helper.DatabaseHelper;
import com.onaio.steps.model.Household;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Robolectric.shadowOf;

@Config(emulateSdk = 16,manifest = "src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class NewHouseholdActivityTest {

    private final String PHONE_ID = "123456789";
    private final String HOUSEHOLD_SEED = "100";
    private NewHouseholdActivity newHouseholdActivity;

    @Before
    public void setup(){
        Intent intent = new Intent();
        intent.putExtra(Constants.PHONE_ID, PHONE_ID);
        intent.putExtra(Constants.HOUSEHOLD_SEED, HOUSEHOLD_SEED);

        newHouseholdActivity = Robolectric.buildActivity(NewHouseholdActivity.class)
                                .withIntent(intent)
                                .create()
                                .get();
    }
    @Test
    public void ShouldPopulateView(){
//        DatabaseHelper db = Mockito.mock(DatabaseHelper.class);
//        Cursor cursorMock = Mockito.mock(Cursor.class);
//        Mockito.stub(db.exec(Mockito.anyString())).toReturn(cursorMock);

        assertEquals(R.id.household_form, shadowOf(newHouseholdActivity).getContentView().getId());
        TextView header = (TextView)newHouseholdActivity.findViewById(R.id.form_header);
        TextView generatedHouseholdId = (TextView)newHouseholdActivity.findViewById(R.id.generated_household_id);
        TextView phoneNumber = (TextView)newHouseholdActivity.findViewById(R.id.household_number);

        assertNotNull(header);
        assertNotNull(generatedHouseholdId);
        assertNotNull(phoneNumber);
        assertEquals("Add New Household", header.getText().toString());
        assertEquals("123456789-101", generatedHouseholdId.getText().toString());
    }

    @Test
    public void ShouldFinishActivityOnCancel(){
        newHouseholdActivity.cancel(null);

        assertTrue(newHouseholdActivity.isFinishing());
    }
}