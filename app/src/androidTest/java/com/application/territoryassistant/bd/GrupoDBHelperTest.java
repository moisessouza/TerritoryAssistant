package com.application.territoryassistant.bd;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.application.territoryassistant.grupos.vo.GrupoVO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class GrupoDBHelperTest {

    private GrupoDBHelper dbHelper;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase(DBHelper.DATABASE_NAME);
        dbHelper = new GrupoDBHelper(context);
    }

    @After
    public void tearDown() {
        dbHelper.close();
        context.deleteDatabase(DBHelper.DATABASE_NAME);
    }

    @Test
    public void testGravarEBuscarGrupo() {
        assertFalse(dbHelper.possuiGrupo());
        assertTrue(dbHelper.gravarGrupo("Grupo 1"));
        assertTrue(dbHelper.possuiGrupo());

        List<GrupoVO> lista = dbHelper.buscarGrupos();
        assertEquals(1, lista.size());
        assertEquals("Grupo 1", lista.get(0).getNome());

        GrupoVO vo = dbHelper.buscarGrupo(lista.get(0).getId());
        assertNotNull(vo);
        assertEquals("Grupo 1", vo.getNome());

        assertNull(dbHelper.buscarGrupo(99999));
    }

    @Test
    public void testPossuiTerritorio() {
        dbHelper.gravarGrupo("Grupo 1");
        GrupoVO grupo = dbHelper.buscarGrupos().get(0);

        assertFalse(dbHelper.possuiTerritorio(grupo.getId()));
        assertFalse(dbHelper.possuiTerritorio(99999));

        TerritorioDBHelper territorioDBHelper = new TerritorioDBHelper(context);
        territorioDBHelper.gravarTerritorio("T-01", grupo.getId(), "", "", false);

        assertTrue(dbHelper.possuiTerritorio(grupo.getId()));
        territorioDBHelper.close();
    }

    @Test
    public void testAtualizarEDeletarGrupo() {
        dbHelper.gravarGrupo("Grupo A");
        GrupoVO vo = dbHelper.buscarGrupos().get(0);

        vo.setNome("Grupo B");
        dbHelper.atualizarGrupo(vo);

        GrupoVO atualizado = dbHelper.buscarGrupo(vo.getId());
        assertEquals("Grupo B", atualizado.getNome());

        assertTrue(dbHelper.deletarGrupo(vo.getId()));
        assertTrue(dbHelper.buscarGrupos().isEmpty());
    }
}
