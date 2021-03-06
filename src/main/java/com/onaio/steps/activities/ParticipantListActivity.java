package com.onaio.steps.activities;


import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.onaio.steps.R;
import com.onaio.steps.adapters.ParticipantAdapter;
import com.onaio.steps.handler.factories.ParticipantListActivityFactory;
import com.onaio.steps.handler.interfaces.IActivityResultHandler;
import com.onaio.steps.handler.interfaces.IMenuHandler;
import com.onaio.steps.handler.interfaces.IMenuPreparer;
import com.onaio.steps.model.Participant;

import java.util.ArrayList;
import java.util.List;

import static com.onaio.steps.helper.Constants.HEADER_GREEN;

public class ParticipantListActivity extends BaseListActivity {


    @Override
    protected void prepareScreen() {
        setLayout();
        populateParticipants();
        bindParticipantItem();
    }

    protected void setLayout() {
        setContentView(R.layout.main);
        Button participantHeader = (Button) findViewById(R.id.action_add_new_item);
        participantHeader.setText(R.string.action_add_participant);
        participantHeader.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_new_member, 0, 0, 0);
        setTitle(R.string.participant_header);
        setTitleColor(Color.parseColor(HEADER_GREEN));
    }

    protected void populateParticipants() {
        List<Participant> participants = Participant.getAllParticipants(db);
        getListView().setAdapter(new ParticipantAdapter(this, participants));
    }

    protected void bindParticipantItem() {
        ListView households = getListView();
        households.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Participant participant = Participant.find_by(db, id);
                ParticipantListActivityFactory.getParticipantItemHandler(ParticipantListActivity.this, participant).open();
            }
        });
    }

    @Override
    protected int getMenuViewLayout() {
        return R.menu.participant_list_actions;
    }

    @Override
    protected List<IMenuHandler> getMenuHandlers() {
        return ParticipantListActivityFactory.getMenuHandlers(this);
    }

    @Override
    protected List<IActivityResultHandler> getResultHandlers() {
        return ParticipantListActivityFactory.getResultHandlers(this);
    }

    @Override
    protected List<IMenuPreparer> getMenuPreparer(Menu menu) {
        return new ArrayList<IMenuPreparer>();
    }

    @Override
    protected List<IMenuHandler> getCustomMenuHandler() {
        return ParticipantListActivityFactory.getCustomMenuHandler(this);
    }
}


