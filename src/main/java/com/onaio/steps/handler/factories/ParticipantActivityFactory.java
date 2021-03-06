package com.onaio.steps.handler.factories;


import android.app.Activity;
import android.view.Menu;

import com.onaio.steps.handler.actions.BackHomeHandler;
import com.onaio.steps.handler.actions.DeferredHandler;
import com.onaio.steps.handler.actions.IncompleteRefusedHandler;
import com.onaio.steps.handler.activities.EditParticipantActivityHandler;
import com.onaio.steps.handler.interfaces.IActivityResultHandler;
import com.onaio.steps.handler.interfaces.IMenuHandler;
import com.onaio.steps.handler.interfaces.IMenuPreparer;
import com.onaio.steps.handler.actions.RefusedHandler;
import com.onaio.steps.handler.actions.TakeSurveyHandler;
import com.onaio.steps.handler.strategies.survey.DeferSurveyForParticipantStrategy;
import com.onaio.steps.handler.strategies.survey.RefuseIncompleteSurveyForParticipantStrategy;
import com.onaio.steps.handler.strategies.survey.RefuseSurveyForParticipantStrategy;
import com.onaio.steps.handler.strategies.survey.TakeSurveyForParticipantStrategy;
import com.onaio.steps.model.Participant;

import java.util.ArrayList;
import java.util.List;

public class ParticipantActivityFactory {
    public static List<IMenuHandler> getMenuHandlers(Activity activity, Participant participant) {
        ArrayList<IMenuHandler> handlers = new ArrayList<IMenuHandler>();
        handlers.add(new BackHomeHandler(activity));
        handlers.add(new EditParticipantActivityHandler(activity,participant));
        return handlers;
    }

    public static List<IActivityResultHandler> getResultHandlers(Activity activity, Participant participant){
        ArrayList<IActivityResultHandler> handlers = new ArrayList<IActivityResultHandler>();
        handlers.add(new EditParticipantActivityHandler(activity, participant));
        handlers.add(new TakeSurveyHandler(activity,new TakeSurveyForParticipantStrategy(participant,activity)));
        return handlers;
    }

    public static List<IMenuPreparer> getCustomMenuPreparer(Activity activity, Participant participant){
        ArrayList<IMenuPreparer> menuItems = new ArrayList<IMenuPreparer>();
        menuItems.add(new TakeSurveyHandler(activity,new TakeSurveyForParticipantStrategy(participant,activity)));
        menuItems.add(new DeferredHandler(activity, new DeferSurveyForParticipantStrategy(participant,activity)));
        menuItems.add(new RefusedHandler(activity,new RefuseSurveyForParticipantStrategy(participant,activity)));
        menuItems.add(new IncompleteRefusedHandler(activity,new RefuseIncompleteSurveyForParticipantStrategy(participant,activity)));
        return menuItems;
    }

    public static List<IMenuHandler> getCustomMenuHandler(Activity activity, Participant participant){
        ArrayList<IMenuHandler> handlers = new ArrayList<IMenuHandler>();
        handlers.add(new TakeSurveyHandler(activity, new TakeSurveyForParticipantStrategy(participant,activity)));
        handlers.add(new DeferredHandler(activity,new DeferSurveyForParticipantStrategy(participant,activity)));
        handlers.add(new RefusedHandler(activity,new RefuseSurveyForParticipantStrategy(participant,activity)));
        handlers.add(new IncompleteRefusedHandler(activity,new RefuseIncompleteSurveyForParticipantStrategy(participant,activity)));
        return handlers;
    }

    public static List<IMenuPreparer> getMenuPreparer(Activity activity, Participant participant, Menu menu)
        {
            ArrayList<IMenuPreparer> menuPreparers = new ArrayList<IMenuPreparer>();
            menuPreparers.add(new EditParticipantActivityHandler(activity, participant).withMenu(menu));
            return menuPreparers;
        }
}
