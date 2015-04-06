package com.onaio.steps.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.onaio.steps.helper.Constants;
import com.onaio.steps.utils.CursorStub;
import com.onaio.steps.helper.DatabaseHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 16, manifest = "src/main/AndroidManifest.xml",shadows = {ShadowDatabaseHelper.class})
public class HouseholdTest {

    private final HouseholdStatus householdStatus = HouseholdStatus.NOT_SELECTED;
    private String currentDate = new SimpleDateFormat(Constants.DATE_FORMAT).format(new Date());
    @Mock
    private DatabaseHelper db;
    @Mock
    private Cursor cursor;
    private final String householdName = "Any household";
    private final String phoneNumber = "123456789";
    public static final String ID = "Id";
    private static final String NAME = "Name";
    private static final String STATUS = "Status";
    private static final String PHONE_NUMBER = "Phone_Number";
    private static final String SELECTED_MEMBER = "selected_member";
    private static final String CREATED_AT = "Created_At";
    private final long householdId = 1;
    private final String memberFamilyName = "Rana";
    private final String memberFirstName = "Manisha";
    private final String memberGender = Constants.FEMALE;
    private final int memberAge = 23;

    @Before
    public void Setup(){
        db = Mockito.mock(DatabaseHelper.class);
        cursor = Mockito.mock(Cursor.class);
    }

    @Test
    public void ShouldBeAbleToSaveTheHousehold(){
        Household household = new Household(householdName, phoneNumber, householdStatus, currentDate);
        stubDbForHousehold();

        household.save(db);

        Mockito.verify(db).save(Mockito.argThat(saveHouseholdMatcher(currentDate)),Mockito.eq(Household.TABLE_NAME));
    }

    @Test
    public void ShouldBeAbleToUpdateTheMember(){
        String selectedMember = "3";
        Household household = new Household(String.valueOf(householdId),householdName, phoneNumber, selectedMember,householdStatus, currentDate);
        stubDbForHousehold();

        household.update(db);

        Mockito.verify(db).update(Mockito.argThat(updateHouseholdMatcher(selectedMember)), Mockito.eq(Household.TABLE_NAME), Mockito.eq(ID + " = "+householdId),Mockito.any(String[].class));
    }

    @Test
    public void ShouldGetAllNumberOfHouseholdFromDatabase(){
        stubDbForHousehold();
        int householdCount = 5;
        Mockito.stub(cursor.getInt(0)).toReturn(householdCount);
        String FIND_ALL_COUNT_QUERY = "SELECT count(*) FROM HOUSEHOLD ORDER BY Id desc";

        assertEquals(5, Household.getAllCount(db));

        Mockito.verify(db).exec(String.format(FIND_ALL_COUNT_QUERY));
    }

    @Test
    public void ShouldGetAllHouseholds(){
        stubDbForHousehold();
        String FIND_ALL_QUERY = "SELECT * FROM HOUSEHOLD ORDER BY Id desc";
        new CursorStub(cursor).stubCursorForHousehold(householdName, phoneNumber, String.valueOf(householdId), householdStatus, currentDate, "");

        List<Household> households = Household.getAll(db);

        assertEquals(1,households.size());
        validateHousehold(households.get(0));
        Mockito.verify(db).exec(String.format(FIND_ALL_QUERY));
    }

    @Test
    public void ShouldFindTheHouseholdById(){
        stubDbForHousehold();
        new CursorStub(cursor).stubCursorForHousehold(householdName, phoneNumber, String.valueOf(householdId), householdStatus, currentDate, "");
        String FIND_BY_ID_QUERY = "SELECT * FROM HOUSEHOLD WHERE id = %d";

        Household household = Household.find_by(db, householdId);

        validateHousehold(household);
        Mockito.verify(db).exec(String.format(FIND_BY_ID_QUERY, householdId));
    }

    @Test
    public void ShouldGetNonDeletedNumberOfMembersFromDatabase(){
        int numberOfMembers = 2;
        Household household = new Household(String.valueOf(householdId), householdName, phoneNumber,"", HouseholdStatus.NOT_SELECTED, currentDate);
        stubDbForMember(numberOfMembers);

        assertEquals(numberOfMembers, household.numberOfNonDeletedMembers(db));

        Mockito.verify(db).exec(String.format(Member.FIND_ALL_QUERY,Member.HOUSEHOLD_ID,householdId, Member.DELETED, Member.NOT_DELETED_INT));
    }

    @Test
    public void ShouldGetAllNumberOfMembersFromDatabase(){
        int numberOfMembers = 1;
        Household household = new Household(String.valueOf(householdId), householdName, phoneNumber,"", HouseholdStatus.NOT_SELECTED, currentDate);
        stubDbForMember(numberOfMembers);

        assertEquals(numberOfMembers, household.numberOfMembers(db));

        Mockito.verify(db).exec(String.format(Member.FIND_ALL_WITH_DELETED_QUERY,Member.HOUSEHOLD_ID,householdId));
    }

    @Test
    public void ShouldGetAllNonDeletedMember(){
        long memberId = 1L;
        int numberOfMembers = 1;
        Household household = new Household(String.valueOf(householdId), householdName, phoneNumber,"", HouseholdStatus.NOT_SELECTED, currentDate);
        stubDbForMember(numberOfMembers);
        new CursorStub(cursor).stubCursorForMember(memberId, memberFamilyName, memberFirstName, memberGender, String.valueOf(memberAge), String.valueOf(householdId), Member.NOT_DELETED_INT, householdName + "-1");

        List<Member> members = household.getAllMembers(db);

        assertEquals(1,members.size());
        validateMember(members.get(0),false);
        Mockito.verify(db).exec(String.format(Member.FIND_ALL_QUERY,Member.HOUSEHOLD_ID,householdId,Member.DELETED, Member.NOT_DELETED_INT));
    }

    @Test
    public void ShouldGetAllMember(){
        long memberId = 1L;
        int numberOfMembers = 1;
        Household household = new Household(String.valueOf(householdId), householdName, phoneNumber,"", HouseholdStatus.NOT_SELECTED, currentDate);
        stubDbForMember(numberOfMembers);
        new CursorStub(cursor).stubCursorForMember(memberId,memberFamilyName,memberFirstName,memberGender,String.valueOf(memberAge),String.valueOf(householdId), Member.DELETED_INT, householdName + "-1");

        List<Member> allMembers = household.getAllMembersForExport(db);

        assertEquals(1,allMembers.size());
        validateMember(allMembers.get(0),true);
        Mockito.verify(db).exec(String.format(Member.FIND_ALL_WITH_DELETED_QUERY,Member.HOUSEHOLD_ID,householdId));
    }

    @Test
    public void ShouldFindTheMemberById(){
        long memberId = 1L;
        int numberOfMembers = 1;
        Household household = new Household(String.valueOf(householdId), householdName, phoneNumber,"", HouseholdStatus.NOT_SELECTED, currentDate);
        stubDbForMember(numberOfMembers);
        new CursorStub(cursor).stubCursorForMember(memberId, memberFamilyName, memberFirstName, memberGender, String.valueOf(memberAge), String.valueOf(householdId), Member.NOT_DELETED_INT, householdName + "-1");

        Member member = household.findMember(db, memberId);

        validateMember(member,false);
        Mockito.verify(db).exec(String.format(Member.FIND_BY_ID_QUERY, memberId));
    }

    private void validateHousehold(Household household) {
        assertEquals(householdName,household.getName());
        assertEquals(householdStatus,household.getStatus());
        assertEquals(phoneNumber,household.getPhoneNumber());
        assertEquals(currentDate,household.getCreatedAt());
    }

    private ArgumentMatcher<ContentValues> saveHouseholdMatcher(final String created_at) {
        return new ArgumentMatcher<ContentValues>() {
            @Override
            public boolean matches(Object argument) {
                ContentValues contentValues = (ContentValues) argument;
                assertBasicDetails(contentValues);
                assertTrue(contentValues.containsKey(CREATED_AT));
                assertTrue(contentValues.getAsString(CREATED_AT).equals(created_at));
                return true;
            }
        };
    }

    private ArgumentMatcher<ContentValues> updateHouseholdMatcher(final String selectedMember) {
        return new ArgumentMatcher<ContentValues>() {
            @Override
            public boolean matches(Object argument) {
                ContentValues contentValues = (ContentValues) argument;
                assertBasicDetails(contentValues);
                assertTrue(contentValues.containsKey(SELECTED_MEMBER));
                assertTrue(contentValues.getAsString(SELECTED_MEMBER).equals(selectedMember));
                return true;
            }
        };
    }

    private void assertBasicDetails(ContentValues contentValues) {
        assertTrue(contentValues.containsKey(NAME));
        assertTrue(contentValues.getAsString(NAME).equals(householdName));
        assertTrue(contentValues.containsKey(PHONE_NUMBER));
        assertTrue(contentValues.getAsString(PHONE_NUMBER).equals(phoneNumber));
        assertTrue(contentValues.containsKey(STATUS));
        assertTrue(contentValues.getAsString(STATUS).equals(householdStatus.toString()));

    }

    private void stubDbForHousehold() {
        Mockito.stub(db.exec(Mockito.anyString())).toReturn(cursor);
    }

    private void stubDbForMember(int numberOfMembers) {

        Mockito.stub(cursor.getCount()).toReturn(numberOfMembers);
        Mockito.stub(db.exec(Mockito.anyString())).toReturn(cursor);
    }

    private void validateMember(Member member,boolean isDeleted){
        assertEquals(memberFamilyName,member.getFamilySurname());
        assertEquals(memberFirstName,member.getFirstName());
        assertEquals(memberAge,member.getAge());
        assertEquals(memberGender,member.getGender());
        String isDeletedString = isDeleted?"Yes":"No";
        assertEquals(isDeletedString,member.getDeletedString());
    }


}