package com.application.territoryassistant.bd;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ConfiguracoesDBHelperTest {

    private ConfiguracoesDBHelper dbHelper;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase(DBHelper.DATABASE_NAME);
        dbHelper = new ConfiguracoesDBHelper(context);
    }

    @After
    public void tearDown() {
        dbHelper.close();
        context.deleteDatabase(DBHelper.DATABASE_NAME);
    }

    @Test
    public void testConfiguracoesPadraoEAtualizacao() {
        assertNotNull(dbHelper.buscarTextoDirigente());
        assertEquals(Integer.valueOf(15), dbHelper.buscarNumDiasEsperaTerritorio());

        dbHelper.atualizarNumDiasDescanso(30);
        assertEquals(Integer.valueOf(30), dbHelper.buscarNumDiasEsperaTerritorio());

        dbHelper.atualizarTextoDirigente("Novo texto de mensagem");
        assertEquals("Novo texto de mensagem", dbHelper.buscarTextoDirigente());
    }
}
