package com.onaio.steps.activityHandler;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;
import com.onaio.steps.activity.NewHouseholdActivity;
import com.onaio.steps.R;
import com.onaio.steps.helper.Constants;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class NewHouseholdActivityHandler implements IActivityHandler {

    private ListActivity activity;

    public NewHouseholdActivityHandler(ListActivity activity) {

        this.activity = activity;
    }

    @Override
    public boolean shouldOpen(int menu_id) {
        return menu_id == R.id.action_add;
    }

    @Override
    public boolean open() {
        if (getPhoneId(activity)== null) {
            alertUserToSetPhoneId(activity);
        } else {
            Intent intent = new Intent(activity.getBaseContext(), NewHouseholdActivity.class);
            intent.putExtra(Constants.PHONE_ID,getPhoneId(activity));
            activity.startActivityForResult(intent, Constants.NEW_HOUSEHOLD_IDENTIFIER);
        }
        return true;
    }

    @Override
    public void handleResult(Intent data, int resultCode) {
        if (resultCode == RESULT_OK) {
            ArrayAdapter<String> listAdapter = (ArrayAdapter<String>) activity.getListView().getAdapter();
            if (listAdapter == null)
                return;
            listAdapter.insert(data.getStringExtra(Constants.HOUSEHOLD_NAME),0);
        }
    }

    @Override
    public boolean canHandleResult(int requestCode) {
        return requestCode == Constants.NEW_HOUSEHOLD_IDENTIFIER;
    }

    private void alertUserToSetPhoneId(ListActivity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.phone_id_message_title).setMessage(R.string.phone_id_message);
        builder.create().show();
    }

    private String getPhoneId(ListActivity activity) {
        return dataStore(activity).getString(Constants.PHONE_ID, null) ;
    }

    private SharedPreferences dataStore(ListActivity activity) {
        return activity.getPreferences(MODE_PRIVATE);
    }

}
