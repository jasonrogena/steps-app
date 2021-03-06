package com.onaio.steps.model.ODKForm;

import android.app.Activity;
import android.content.ContentProviderClient;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.onaio.steps.exceptions.AppNotInstalledException;
import com.onaio.steps.exceptions.FormNotPresentException;

import java.util.ArrayList;
import java.util.List;

public class ODKBlankForm implements IForm{
    public static final String COLLECT_FORMS_AUTHORITY = "org.odk.collect.android.provider.odk.forms";
    private static final String URI_STRING = "content://"
            + COLLECT_FORMS_AUTHORITY + "/forms";
    public static final Uri URI = Uri.parse(URI_STRING);

    String formMediaPath;
    String _id;
    String jrFormId;
    String displayName;
    String jrVersion;


    protected ODKBlankForm(String id, String jrFormId, String displayName, String jrVersion, String formMediaPath){
        _id = id;
        this.jrFormId = jrFormId;
        this.displayName = displayName;
        this.jrVersion = jrVersion;
        this.formMediaPath = formMediaPath;
    }

    public static IForm find(Activity activity, String jrFormId) throws FormNotPresentException, AppNotInstalledException {
        List<IForm> forms = get(activity, jrFormId);
        if(forms.size() <= 0)
            throw new FormNotPresentException();
        return forms.get(0);
    }

    public String getPath() {
        return formMediaPath;
    }

    public Uri getUri() {
        return Uri.parse(URI_STRING + "/" + _id);
    }

    public static List<IForm> get(Activity activity, String odkFormId) throws AppNotInstalledException {
        ContentProviderClient formsContentProvider = activity.getContentResolver().acquireContentProviderClient(ODKBlankForm.URI);
        ArrayList<IForm> forms = new ArrayList<IForm>();
        try {
            if(formsContentProvider==null) throw new AppNotInstalledException();
            Cursor cursor = formsContentProvider.query(ODKBlankForm.URI, null, "jrFormId = ?", new String[]{odkFormId}, null);
            if(cursor.moveToFirst()){
                do{
                    String id = cursor.getString(cursor.getColumnIndex("_id"));
                    String jrFormId = cursor.getString(cursor.getColumnIndex("jrFormId"));
                    String displayName = cursor.getString(cursor.getColumnIndex("displayName"));
                    String jrVersion = cursor.getString(cursor.getColumnIndex("jrVersion"));
                    String formMediaPath = cursor.getString(cursor.getColumnIndex("formMediaPath"));
                    forms.add(new ODKBlankForm(id, jrFormId, displayName, jrVersion, formMediaPath));
                }while (cursor.moveToNext());
            }
        } catch (RemoteException e) {
            throw new AppNotInstalledException();
        }
        return forms;
    }
}
