package com.application.territoryassistant.bd;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.application.territoryassistant.designar.vo.DesignacaoVO;
import com.application.territoryassistant.territorios.vo.TerritorioVO;
import com.application.territoryassistant.territorios.vo.TerritorioVizinhoVO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class TerritorioDBHelperTest {

    private TerritorioDBHelper dbHelper;
    private GrupoDBHelper grupoDBHelper;
    private DirigenteDBHelper dirigenteDBHelper;
    private DesignacaoDBHelper designacaoDBHelper;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase(DBHelper.DATABASE_NAME);
        dbHelper = new TerritorioDBHelper(context);
        grupoDBHelper = new GrupoDBHelper(context);
        dirigenteDBHelper = new DirigenteDBHelper(context);
        designacaoDBHelper = new DesignacaoDBHelper(context);

        grupoDBHelper.gravarGrupo("Grupo Centro");
        dirigenteDBHelper.gravarDirigente("Carlos", "carlos@email.com");
    }

    @After
    public void tearDown() {
        dbHelper.close();
        grupoDBHelper.close();
        dirigenteDBHelper.close();
        designacaoDBHelper.close();
        context.deleteDatabase(DBHelper.DATABASE_NAME);
    }

    @Test
    public void testGravarEBuscarTerritorios() {
        assertFalse(dbHelper.possuiTerritoriosCadastrado());

        long id = dbHelper.gravarTerritorio("T-01", 1, "Rua A", "/path/foto.jpg", false);
        assertTrue(id > 0);
        assertTrue(dbHelper.possuiTerritoriosCadastrado());

        List<TerritorioVO> lista = dbHelper.buscarTerritorios();
        assertEquals(1, lista.size());
        assertEquals("T-01", lista.get(0).getCod());

        TerritorioVO vo = dbHelper.buscarTerritorio((int) id);
        assertNotNull(vo);
        assertEquals("T-01", vo.getCod());
        assertEquals("/path/foto.jpg", vo.getFotoPath());

        assertNull(dbHelper.buscarTerritorio(99999));
        assertFalse(dbHelper.territorioSuspenso(99999));
    }

    @Test
    public void testBuscarTerritoriosComFiltros() {
        long id1 = dbHelper.gravarTerritorio("T-01", 1, "Obs 1", "", false);
        long id2 = dbHelper.gravarTerritorio("T-02", 1, "Obs 2", "", false);

        List<TerritorioVO> filtrados = dbHelper.buscarTerritorios((int) id1, false, 1);
        assertEquals(1, filtrados.size());
        assertEquals("T-02", filtrados.get(0).getCod());

        List<TerritorioVO> ordenadosPorData = dbHelper.buscarTerritorios(null, true, 1);
        assertEquals(2, ordenadosPorData.size());

        assertTrue(dbHelper.possuiGrupo(1));
        assertFalse(dbHelper.possuiGrupo(99999));
    }

    @Test
    public void testBuscarTerritoriosNaoDesignados() {
        long id1 = dbHelper.gravarTerritorio("T-01", 1, "", "", false);
        long id2 = dbHelper.gravarTerritorio("T-02", 1, "", "", false);

        DesignacaoVO designacao = new DesignacaoVO((int) id1, 1, "D", System.currentTimeMillis(), null);
        designacaoDBHelper.gravarDesignacoes(Collections.singletonList(designacao));

        List<TerritorioVO> naoDesignados = dbHelper.buscarTerritoriosNaoDesignados();
        assertEquals(1, naoDesignados.size());
        assertEquals("T-02", naoDesignados.get(0).getCod());

        List<TerritorioVO> naoDesignadosComOrdenacao = dbHelper.buscarTerritoriosNaoDesignados(true, 1);
        assertEquals(1, naoDesignadosComOrdenacao.size());
    }

    @Test
    public void testAtualizarEDeletarTerritorio() {
        long id = dbHelper.gravarTerritorio("T-01", 1, "Antiga obs", "", false);
        TerritorioVO vo = dbHelper.buscarTerritorio((int) id);

        vo.setCod("T-01-ALT");
        vo.setObservacoes("Nova obs");
        vo.setSuspenso(true);
        dbHelper.atualizarTerritorio(vo);

        TerritorioVO atualizado = dbHelper.buscarTerritorio((int) id);
        assertEquals("T-01-ALT", atualizado.getCod());
        assertEquals("Nova obs", atualizado.getObservacoes());
        assertTrue(dbHelper.territorioSuspenso((int) id));

        assertTrue(dbHelper.deletarTerritorio((int) id));
        assertNull(dbHelper.buscarTerritorio((int) id));
    }

    @Test
    public void testBuscarPorCodEPorId() {
        assertNull(dbHelper.buscarTerritorioPorCod(null));

        long id1 = dbHelper.gravarTerritorio("T-10", 1, "", "", false);
        long id2 = dbHelper.gravarTerritorio("T-20", 1, "", "", false);

        List<TerritorioVO> listaPorCods = dbHelper.buscarTerritoriosPorCod("T-10,T-20");
        assertEquals(2, listaPorCods.size());

        TerritorioVO voPorCod = dbHelper.buscarTerritorioPorCod("T-10", (int) id2);
        assertNotNull(voPorCod);
        assertEquals("T-10", voPorCod.getCod());

        assertTrue(dbHelper.buscarTerritoriosPorId((Integer[]) null).isEmpty());

        List<TerritorioVO> listaPorIds = dbHelper.buscarTerritoriosPorId((int) id1, (int) id2);
        assertEquals(2, listaPorIds.size());
    }

    @Test
    public void testGravarEBuscarVizinhos() {
        assertTrue(dbHelper.gravarVizinhos(null, Collections.emptyList()).isEmpty());
        assertTrue(dbHelper.gravarVizinhos(1, null).isEmpty());

        long id1 = dbHelper.gravarTerritorio("T-01", 1, "", "", false);
        long id2 = dbHelper.gravarTerritorio("T-02", 1, "", "", false);

        TerritorioVO vo2 = dbHelper.buscarTerritorio((int) id2);
        List<TerritorioVizinhoVO> vizinhos = dbHelper.gravarVizinhos((int) id1, Collections.singletonList(vo2));
        assertFalse(vizinhos.isEmpty());

        // Testar duplicados (não deve inserir duplicado)
        dbHelper.gravarVizinhos((int) id1, Collections.singletonList(vo2));

        List<TerritorioVizinhoVO> vizinhosBuscados = dbHelper.buscarVizinhos((int) id1);
        assertFalse(vizinhosBuscados.isEmpty());

        assertTrue(dbHelper.deletarVizinhos((int) id1));
        assertTrue(dbHelper.buscarVizinhos((int) id1).isEmpty());
    }

    @Test
    public void testBuscarTerritoriosDesignadosParaDirigente() {
        long idTer = dbHelper.gravarTerritorio("T-01", 1, "", "", false);

        DesignacaoVO designacao = new DesignacaoVO((int) idTer, 1, "D", System.currentTimeMillis(), null);
        designacaoDBHelper.gravarDesignacoes(Collections.singletonList(designacao));

        List<String> cods = dbHelper.buscarTerritoriosDesignadosParaDirigente(1);
        assertEquals(1, cods.size());
        assertEquals("T-01", cods.get(0));

        assertTrue(dbHelper.buscarTerritoriosDesignadosParaDirigente(99999).isEmpty());
    }

    @Test
    public void testBuscarTerritorioMaisTempoTrabalhado() {
        long id1 = dbHelper.gravarTerritorio("T-01", 1, "", "", false);
        long id2 = dbHelper.gravarTerritorio("T-02", 1, "", "", false);

        TerritorioVO vo1 = dbHelper.buscarTerritorio((int) id1);
        vo1.setUltimaDataFim(1000L);
        dbHelper.atualizarTerritorio(vo1);

        TerritorioVO vo2 = dbHelper.buscarTerritorio((int) id2);
        vo2.setUltimaDataFim(5000L);
        dbHelper.atualizarTerritorio(vo2);

        // Inserir vizinho
        dbHelper.gravarVizinhos((int) id1, Collections.singletonList(vo2));

        TerritorioVO maisAntigo = dbHelper.buscarTerritorioMaisTempoTrabalhado(1, new Integer[]{(int) id2}, new Integer[]{(int) id2}, 10000L);
        assertNotNull(maisAntigo);
        assertEquals("T-01", maisAntigo.getCod());

        TerritorioVO semFiltro = dbHelper.buscarTerritorioMaisTempoTrabalhado(null, null, null, null);
        assertNotNull(semFiltro);
    }
}
