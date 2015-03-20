package com.onaio.steps.activityHandler;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.onaio.steps.R;
import com.onaio.steps.activityHandler.Factory.HouseholdActivityFactory;
import com.onaio.steps.activityHandler.Interface.IHandler;
import com.onaio.steps.activityHandler.Interface.IPrepare;
import com.onaio.steps.adapter.MemberAdapter;
import com.onaio.steps.helper.DatabaseHelper;
import com.onaio.steps.model.Household;
import com.onaio.steps.model.HouseholdStatus;
import com.onaio.steps.model.Member;
import com.onaio.steps.model.ReElectReason;

import java.util.List;
import java.util.Random;

import static com.onaio.steps.model.HouseholdStatus.SELECTED;

public class SelectParticipantHandler implements IHandler, IPrepare {

    private final int MENU_ID = R.id.action_select_participant;
    private ListActivity activity;
    private Household household;
    private Menu menu;

    public SelectParticipantHandler(ListActivity activity, Household household) {
        this.activity = activity;
        this.household = household;
    }

    @Override
    public boolean shouldOpen(int menu_id) {
        return menu_id == MENU_ID;
    }

    @Override
    public boolean open() {
        switch(household.getStatus()){
            case OPEN: selectParticipant();
                break;
            case SELECTED: confirm();
                break;
            default: canNotReElect();
        }
        return true;
    }

    private void canNotReElect() {
        new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.participant_no_re_elect_title))
                .setMessage(activity.getString(R.string.participant_no_re_elect_message))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create().show();
    }

    private void confirm() {
        LayoutInflater factory = LayoutInflater.from(activity);
        final View confirmation = factory.inflate(R.layout.selection_confirm, null);
        new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.participant_re_elect_reason_title))
                .setView(confirmation)
                .setPositiveButton(R.string.confirm_ok,new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveReason(confirmation);
                        selectParticipant();
                    }
                })
                .setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create().show();
    }

    private void saveReason(View confirmation) {
        TextView reasonView = (TextView) confirmation.findViewById(R.id.reason);
        ReElectReason reason = new ReElectReason(reasonView.getText().toString(), household);
        DatabaseHelper db = new DatabaseHelper(activity);
        reason.save(db);
    }

    private void selectParticipant() {
        ListView listView = activity.getListView();
        Member selectedMember = getSelectedMember(listView);
        updateHousehold(selectedMember);
        updateView(listView);
    }

    private void updateView(ListView listView) {
        MemberAdapter membersAdapter = (MemberAdapter) listView.getAdapter();
        membersAdapter.setSelectedMemberId(household.getSelectedMember());
        membersAdapter.notifyDataSetChanged();
        prepareBottomMenuItems();
    }

    private void prepareBottomMenuItems() {
        List<IPrepare> bottomMenus = HouseholdActivityFactory.getHouseholdBottomMenuItemPreparer(activity, household);
        for(IPrepare menu:bottomMenus)
            if(menu.shouldInactivate())
                menu.inactivate();
            else
                menu.activate();
    }


    private void updateHousehold(Member selectedMember) {
        household.setSelectedMember(String.valueOf(selectedMember.getId()));
        household.setStatus(SELECTED);
        household.update(new DatabaseHelper(activity.getApplicationContext()));
    }

    private Member getSelectedMember(ListView listView) {
        int totalMembers = Member.numberOfMembers(new DatabaseHelper(activity.getApplicationContext()), household);
        Random random = new Random();
        int selectedParticipant = random.nextInt(totalMembers);
        return (Member) listView.getItemAtPosition(selectedParticipant);
    }

    @Override
    public void handleResult(Intent data, int resultCode) {

    }

    @Override
    public boolean canHandleResult(int requestCode) {
        return false;
    }

    @Override
    public boolean shouldInactivate() {
        boolean noMember = Member.numberOfMembers(new DatabaseHelper(activity), household) == 0;
        boolean noSelection = household.getStatus() == HouseholdStatus.OPEN;
        boolean selected = household.getStatus() == HouseholdStatus.SELECTED;
        boolean deferred = household.getStatus() == HouseholdStatus.DEFERRED;
        boolean canSelectParticipant = noSelection || selected || deferred;
        return noMember || !canSelectParticipant;
    }

    @Override
    public void inactivate() {
        MenuItem menuItem = menu.findItem(MENU_ID);
        menuItem.setEnabled(false);
    }

    @Override
    public void activate() {
        MenuItem menuItem = menu.findItem(MENU_ID);
        menuItem.setEnabled(true);
    }

    public SelectParticipantHandler withMenu(Menu menu){
        this.menu = menu;
        return this;
    }
}
