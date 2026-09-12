package com.application.territoryassistant.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DBHelperTest {

    private Context context;
    private DBHelper dbHelper;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase(DBHelper.DATABASE_NAME);
        dbHelper = new DBHelper(context);
    }

    @After
    public void tearDown() {
        dbHelper.close();
        context.deleteDatabase(DBHelper.DATABASE_NAME);
    }

    @Test
    public void testOnCreate() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        assertTrue(db.isOpen());
        assertEquals(4, db.getVersion());
    }

    @Test
    public void testOnUpgrade() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.onUpgrade(db, 1, 4);
        dbHelper.onUpgrade(db, 2, 4);
        dbHelper.onUpgrade(db, 3, 4);
        assertTrue(db.isOpen());
    }

    @Test
    public void testCopyEImportDatabase() throws Exception {
        File tempSrc = new File(context.getCacheDir(), "test_src.db");
        File tempDst = new File(context.getCacheDir(), "test_dst.db");

        String testData = "TerritoryAssistant Test DB Content";
        InputStream in = new ByteArrayInputStream(testData.getBytes());

        DBHelper.copy(in, tempDst);
        assertTrue(tempDst.exists());
        assertTrue(tempDst.length() > 0);

        DBHelper.copy(tempDst, tempSrc);
        assertTrue(tempSrc.exists());
        assertEquals(tempDst.length(), tempSrc.length());

        tempSrc.delete();
        tempDst.delete();
    }
}
