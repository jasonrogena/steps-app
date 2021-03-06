package com.onaio.steps.helper;

import android.app.Activity;
import android.os.AsyncTask;

import com.onaio.steps.R;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static com.onaio.steps.helper.Constants.ENDPOINT_URL;

public class UploadFileTask extends AsyncTask<File, Void, Void> {
    private final Activity activity;

    public UploadFileTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(File... files) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(KeyValueStoreFactory.instance(activity).getString(ENDPOINT_URL));
        try {
            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.forName("UTF-8"));
            multipartEntity.addPart("file", new FileBody(files[0]));
            httpPost.setEntity(multipartEntity);
            httpClient.execute(httpPost);
            new CustomNotification().notify(activity, R.string.export_complete, R.string.export_complete_message);
        } catch (IOException e) {
            new Logger().log(e,"Export failed.");
            new CustomNotification().notify(activity, R.string.error_title, R.string.export_failed);
        }
        return null;
    }
}
