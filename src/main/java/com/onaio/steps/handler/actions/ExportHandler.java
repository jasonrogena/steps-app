package com.onaio.steps.handler.actions;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.onaio.steps.R;
import com.onaio.steps.handler.interfaces.IMenuHandler;
import com.onaio.steps.handler.interfaces.IMenuPreparer;
import com.onaio.steps.helper.Constants;
import com.onaio.steps.helper.CustomDialog;
import com.onaio.steps.helper.DatabaseHelper;
import com.onaio.steps.helper.FileUtil;
import com.onaio.steps.helper.KeyValueStoreFactory;
import com.onaio.steps.helper.Logger;
import com.onaio.steps.helper.NetworkConnectivity;
import com.onaio.steps.helper.UploadFileTask;
import com.onaio.steps.model.Household;
import com.onaio.steps.model.Member;
import com.onaio.steps.model.ReElectReason;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.onaio.steps.helper.Constants.*;

public class ExportHandler implements IMenuHandler,IMenuPreparer {

    private List<Household> households;
    private ListActivity activity;
    private DatabaseHelper databaseHelper;
    private Menu menu;
    private int MENU_ID = R.id.action_export;

    public static final String APP_DIR = "STEPS";

    public ExportHandler(ListActivity activity) {
        this.activity = activity;
        databaseHelper = new DatabaseHelper(activity.getApplicationContext());
        households = new ArrayList<Household>();
    }

    @Override
    public boolean shouldOpen(int menu_id) {
        return menu_id == MENU_ID;
    }

    @Override
    public boolean open() {
        DialogInterface.OnClickListener uploadConfirmListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    File file = saveFile();
                    if (NetworkConnectivity.isNetworkAvailable(activity)) {
                        new UploadFileTask(activity).execute(file);
                    } else {
                        new CustomDialog().notify(activity, CustomDialog.EmptyListener, R.string.error_title, R.string.fail_no_connectivity);
                    }
                } catch (IOException e) {
                    new Logger().log(e,"Not able to write CSV file for export.");
                    new CustomDialog().notify(activity, CustomDialog.EmptyListener, R.string.error_title, R.string.something_went_wrong_try_again);
                }
            }
        };
        new CustomDialog().confirm(activity, uploadConfirmListener, CustomDialog.EmptyListener, R.string.export_start_message, R.string.action_export);
        return true;
    }

    private File saveFile() throws IOException {
        //Remove whitespaces from header names
        String[] headers = EXPORT_FIELDS.split(",");
        for (int i = 0; i < headers.length; i++) {
            headers[i] = headers[i].trim();
        }
        String deviceId = getDeviceId();

        FileUtil fileUtil = new FileUtil().withHeader(headers);
        for(Household household: households) {
            List<ReElectReason> reasons = ReElectReason.getAll(databaseHelper, household);
            List<Member> membersPerHousehold = household.getAllMembersForExport(databaseHelper);
            for(Member member: membersPerHousehold){
                ArrayList<String> row = new ArrayList<String>();
                row.add(household.getPhoneNumber());
                row.add(household.getName());
                row.add(household.getComments());
                row.add(member.getMemberHouseholdId());
                row.add(member.getFamilySurname());
                row.add(member.getFirstName());
                row.add(String.valueOf(member.getAge()));
                row.add(member.getGender().toString());
                row.add(member.getDeletedString());
                setStatus(household, member, row);
                row.add(String.valueOf(reasons.size()));
                row.add(StringUtils.join(reasons.toArray(), ';'));
                row.add(deviceId);
                row.add(KeyValueStoreFactory.instance(activity).getString(CAMPAIGN_ID));
                fileUtil.withData(row.toArray(new String[row.size()]));
            }
        }
        //Write the csv to external storage for the user to access.
        saveToExternalStorage(fileUtil);

        return fileUtil.writeCSV(activity.getFilesDir() + "/" + Constants.EXPORT_FILE_NAME + "_" + deviceId + ".csv");
    }

    //
    public void saveToExternalStorage(FileUtil fileUtil) throws IOException {
        if (createAppDir()) {
            fileUtil.writeCSV(Environment.getExternalStorageDirectory() + "/"
                    + APP_DIR + "/" + Constants.EXPORT_FILE_NAME + "_" + getDeviceId() + ".csv");
        } else {
            Toast.makeText(activity, "Could not save file to sdcard", Toast.LENGTH_LONG).show();
        }
    }

    //Create a steps directory in external storage if it does not exist.
    public static boolean createAppDir() {
        File folder = new File(Environment.getExternalStorageDirectory() + "/"
                + APP_DIR);
        boolean createStatus = true;
        if (!folder.exists()) {
            createStatus = folder.mkdirs() ? true : false;
        }
        return createStatus;
    }

    private void setStatus(Household household, Member member, ArrayList<String> row) {
        if(household.getSelectedMemberId() == null || household.getSelectedMemberId().equals("") || household.getSelectedMemberId().equals(String.valueOf(member.getId())))
            row.add(household.getStatus().toString());
        else {
            row.add(SURVEY_NA);
        }
    }

    public ExportHandler with(List<Household> households){
        this.households = households;
        return this;
    }

    @Override
    public boolean shouldInactivate() {
           return households.isEmpty();
    }

    @Override
    public void inactivate() {
        MenuItem item = menu.findItem(MENU_ID);
        item.setEnabled(false);
    }

    @Override
    public void activate() {
        MenuItem item = menu.findItem(MENU_ID);
        item.setEnabled(true);
    }

    public IMenuPreparer withMenu(Menu menu) {
        this.menu = menu;
        return this;
    }

    public String getDeviceId() {
        return KeyValueStoreFactory.instance(activity).getString(PHONE_ID);
    }
}
