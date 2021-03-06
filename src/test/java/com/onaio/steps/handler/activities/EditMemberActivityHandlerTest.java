package com.onaio.steps.handler.activities;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.onaio.steps.R;
import com.onaio.steps.activities.EditMemberActivity;
import com.onaio.steps.activities.MemberActivity;
import com.onaio.steps.helper.Constants;
import com.onaio.steps.model.Household;
import com.onaio.steps.model.InterviewStatus;
import com.onaio.steps.model.Member;
import com.onaio.steps.model.RequestCode;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.stub;

@Config(emulateSdk = 16,manifest = "src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class EditMemberActivityHandlerTest {

    private MemberActivity memberActivityMock;
    private Member memberMock;
    private EditMemberActivityHandler editMemberActivityHandler;

    @Before
    public void setup(){
        memberActivityMock = Mockito.mock(MemberActivity.class);
        memberMock= Mockito.mock(Member.class);
        editMemberActivityHandler = new EditMemberActivityHandler(memberActivityMock, memberMock);
    }

    @Test
    public void ShouldHandleResultForResultOkCode(){
        Intent intentMock = Mockito.mock(Intent.class);
        editMemberActivityHandler.handleResult(intentMock, Activity.RESULT_OK);
        Mockito.verify(memberActivityMock).finish();
    }

    @Test
    public void ShouldBeAbleToOpenEditMemberActivityWhenMenuIdMatches(){
        assertTrue(editMemberActivityHandler.shouldOpen(R.id.action_edit));
    }

    @Test
    public void ShouldNotBeAbleToOpenEditMemberActivityForOtheRMenuId(){
        assertFalse(editMemberActivityHandler.shouldOpen(R.id.action_settings));
    }

    @Test
    public void ShouldCheckWhetherResultForProperRequestCodeCanBeHandled(){
        assertTrue(editMemberActivityHandler.canHandleResult(RequestCode.EDIT_MEMBER.getCode()));
    }

    @Test
    public void ShouldCheckWhetherResultForOtherRequestCodeCanNotBeHandled(){
        assertFalse(editMemberActivityHandler.canHandleResult(RequestCode.NEW_MEMBER.getCode()));
    }

    @Test
    public void ShouldOpenWhenMemberIsNotNull(){
        editMemberActivityHandler.open();
        Mockito.verify(memberActivityMock).startActivityForResult(Mockito.argThat(matchIntent()), Mockito.eq(RequestCode.EDIT_MEMBER.getCode()));
    }

    private ArgumentMatcher<Intent> matchIntent() {
        return new ArgumentMatcher<Intent>() {
            @Override
            public boolean matches(Object argument) {
                Intent intent = (Intent) argument;
                Member actualMember = (Member) intent.getSerializableExtra(Constants.HH_MEMBER);
                Assert.assertEquals(memberMock, actualMember);
                Assert.assertEquals(EditMemberActivity.class.getName(),intent.getComponent().getClassName());
                return true;
            }
        };
    }

    @Test
    public void ShouldInactivateEditOptionForSelectedMember(){
        Menu menuMock = Mockito.mock(Menu.class);
        Household household = new Household("1234", "any name", "123456789", "1", InterviewStatus.NOT_SELECTED, "","Dummy comments");
        Mockito.stub(memberMock.getHousehold()).toReturn(household);
        Mockito.stub(memberMock.getId()).toReturn(1);

        assertTrue(editMemberActivityHandler.withMenu(menuMock).shouldInactivate());
    }

    @Test
    public void ShouldInactivateWhenHouseholdIsSurveyed(){
        Menu menuMock = Mockito.mock(Menu.class);
        stub(memberMock.getId()).toReturn(1);
        stub(memberMock.getHousehold()).toReturn(new Household("12","name","321","", InterviewStatus.DONE,"12-12-2001","Dummy comments"));
        Assert.assertTrue(editMemberActivityHandler.withMenu(menuMock).shouldInactivate());
    }

    @Test
    public void ShouldInactivateWhenSurveyIsRefused(){
        Menu menuMock = Mockito.mock(Menu.class);
        stub(memberMock.getId()).toReturn(1);
        stub(memberMock.getHousehold()).toReturn(new Household("12","name","321","", InterviewStatus.REFUSED,"12-12-2001","Dummy comments"));
        Assert.assertTrue(editMemberActivityHandler.withMenu(menuMock).shouldInactivate());

    }

    @Test
    public void ShouldBeAbleToActivateEditOptionInMenuItem(){
        Menu menuMock = Mockito.mock(Menu.class);
        MenuItem menuItemMock = Mockito.mock(MenuItem.class);
        Mockito.stub(menuMock.findItem(R.id.action_edit)).toReturn(menuItemMock);

        editMemberActivityHandler.withMenu(menuMock).activate();

        Mockito.verify(menuItemMock).setEnabled(true);
    }

    @Test
    public void ShouldBeAbleToInactivateEditOptionInMenuItem(){
        Menu menuMock = Mockito.mock(Menu.class);
        MenuItem menuItemMock = Mockito.mock(MenuItem.class);
        Mockito.stub(menuMock.findItem(R.id.action_edit)).toReturn(menuItemMock);

        editMemberActivityHandler.withMenu(menuMock).inactivate();

        Mockito.verify(menuItemMock).setEnabled(false);
    }
    }

