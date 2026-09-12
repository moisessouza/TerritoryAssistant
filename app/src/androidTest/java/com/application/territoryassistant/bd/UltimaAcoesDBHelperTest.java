package com.application.territoryassistant.bd;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class UltimaAcoesDBHelperTest {

    private UltimaAcoesDBHelper ultimaAcoesDBHelper;
    private DirigenteDBHelper dirigenteDBHelper;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase(DBHelper.DATABASE_NAME);
        ultimaAcoesDBHelper = new UltimaAcoesDBHelper(context);
        dirigenteDBHelper = new DirigenteDBHelper(context);

        dirigenteDBHelper.gravarDirigente("Pedro", "pedro@email.com");
    }

    @After
    public void tearDown() {
        ultimaAcoesDBHelper.close();
        dirigenteDBHelper.close();
        context.deleteDatabase(DBHelper.DATABASE_NAME);
    }

    @Test
    public void testGravarERecuperarUltimasAcoes() {
        long inicio = System.currentTimeMillis();
        long fim = inicio + 86400000L;

        UltimaAcoesDBHelper.UltimaAcaoVO vo = new UltimaAcoesDBHelper.UltimaAcaoVO("D", "T-01", 1, inicio, fim);
        ultimaAcoesDBHelper.gravarUltimaAcoes(vo);

        List<UltimaAcoesDBHelper.UltimaAcaoVO> lista = ultimaAcoesDBHelper.recuperarUltimasAcoes(10);
        assertEquals(1, lista.size());
        assertEquals("D", lista.get(0).getCodAcao());
        assertEquals("T-01", lista.get(0).getCodTerritorios());
        assertEquals("Pedro", lista.get(0).getNome());
    }
}
