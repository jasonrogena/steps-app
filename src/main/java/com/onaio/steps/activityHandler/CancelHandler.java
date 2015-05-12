package com.onaio.steps.activityHandler;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.onaio.steps.R;
import com.onaio.steps.activityHandler.Factory.HouseholdActivityFactory;
import com.onaio.steps.activityHandler.Interface.IMenuHandler;
import com.onaio.steps.activityHandler.Interface.IMenuPreparer;
import com.onaio.steps.adapter.MemberAdapter;
import com.onaio.steps.helper.CustomDialog;
import com.onaio.steps.helper.DatabaseHelper;
import com.onaio.steps.model.Household;
import com.onaio.steps.model.HouseholdStatus;
import com.onaio.steps.model.ReElectReason;

import java.util.List;

import static com.onaio.steps.model.HouseholdStatus.NOT_SELECTED;

/**
 * Created by manisharana on 5/12/15.
 */
public class CancelHandler implements IMenuPreparer,IMenuHandler {

    private Household household;
    private ListActivity activity;
    private DatabaseHelper databaseHelper;

    public CancelHandler(ListActivity activity, Household household) {
        this.activity=activity;
        this.household=household;
        this.databaseHelper=new DatabaseHelper(activity);
    }

    @Override
    public boolean shouldInactivate() {
        boolean selectedStatus = household.getStatus() == HouseholdStatus.NOT_DONE;
        return !(selectedStatus);
    }

    @Override
    public void inactivate() {
        View item = activity.findViewById(R.id.action_cancel_participant);
        item.setVisibility(View.GONE);
    }

    @Override
    public void activate() {
        View item = activity.findViewById(R.id.action_cancel_participant);
        item.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean shouldOpen(int menu_id) {
        return menu_id==R.id.action_cancel_participant;
    }

    @Override
    public boolean open() {
        final ListView membersView = activity.getListView();
        final View confirmation = getView();
        CustomDialog customDialog = new CustomDialog();
        DialogInterface.OnClickListener confirmationDialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveReason(confirmation);
                updateHousehold();
                updateView(membersView);

            }
        };
        customDialog.confirm(activity, confirmationDialogListener, CustomDialog.EmptyListener, confirmation, R.string.confirm_ok);

        return true;
    }

    private void prepareCustomMenus() {
        List<IMenuPreparer> bottomMenus = HouseholdActivityFactory.getCustomMenuPreparer(activity, household);
        for(IMenuPreparer menu:bottomMenus)
            if(menu.shouldInactivate())
                menu.inactivate();
            else
                menu.activate();
    }

    private void updateView(ListView membersView) {
        MemberAdapter membersAdapter = (MemberAdapter) membersView.getAdapter();
        membersAdapter.reinitialize(household.getAllNonDeletedMembers(databaseHelper),null);
        membersAdapter.notifyDataSetChanged();
        prepareCustomMenus();
    }

    private void updateHousehold() {
        household.setSelectedMemberId(null);
        household.setStatus(NOT_SELECTED);
        household.update(new DatabaseHelper(activity));
    }


    private void saveReason(View confirmation) {
        TextView reasonView = (TextView) confirmation.findViewById(R.id.reason);
        ReElectReason reason = new ReElectReason(reasonView.getText().toString(), household);
        reason.save(databaseHelper);
    }



    protected View getView() {
        LayoutInflater factory = LayoutInflater.from(activity);
        return factory.inflate(R.layout.selection_confirm, null);
    }
}
