package com.onaio.steps.model.ODKForm;

import com.onaio.steps.model.ShadowDatabaseHelper;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 16, manifest = "src/main/AndroidManifest.xml",shadows = {ShadowDatabaseHelper.class})
public class ODKSavedFormTest extends TestCase {

    private ODKSavedForm form;

    @Before
    public void Setup(){
        form = new ODKSavedForm("id", "jrFormId", "displayName", "jrVersion", "path", "complete");
    }

    @Test
    public void ShouldValidateURIWithoutIdButShouldNotHaveOnlyThat(){
        String uriWithoutID = "content://org.odk.collect.android.provider.odk.instances/instances";

        Assert.assertTrue(form.getUri().toString().contains(uriWithoutID));
        assertFalse(form.getUri().toString().equals(uriWithoutID));
    }

    @Test
    public void ShouldGetTheFormURIWithId(){
        Assert.assertEquals("content://org.odk.collect.android.provider.odk.instances/instances/id",form.getUri().toString());
    }

    @Test
    public void ShouldGetTheMediaPath(){
        Assert.assertEquals("path",form.getPath());
    }
}