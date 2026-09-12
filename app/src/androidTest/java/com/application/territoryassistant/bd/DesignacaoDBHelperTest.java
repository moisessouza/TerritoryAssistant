package com.application.territoryassistant.bd;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.application.territoryassistant.designar.vo.DesignacaoVO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DesignacaoDBHelperTest {

    private DesignacaoDBHelper designacaoDBHelper;
    private TerritorioDBHelper territorioDBHelper;
    private DirigenteDBHelper dirigenteDBHelper;
    private GrupoDBHelper grupoDBHelper;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase(DBHelper.DATABASE_NAME);
        designacaoDBHelper = new DesignacaoDBHelper(context);
        territorioDBHelper = new TerritorioDBHelper(context);
        dirigenteDBHelper = new DirigenteDBHelper(context);
        grupoDBHelper = new GrupoDBHelper(context);

        grupoDBHelper.gravarGrupo("Grupo A");
        territorioDBHelper.gravarTerritorio("T-01", 1, "", "", false);
        dirigenteDBHelper.gravarDirigente("Carlos", "carlos@email.com");
    }

    @After
    public void tearDown() {
        designacaoDBHelper.close();
        territorioDBHelper.close();
        dirigenteDBHelper.close();
        grupoDBHelper.close();
        context.deleteDatabase(DBHelper.DATABASE_NAME);
    }

    @Test
    public void testGravarDesignacoesComValoresNulosOuVazios() {
        designacaoDBHelper.gravarDesignacoes(null);
        designacaoDBHelper.gravarDesignacoes(Collections.emptyList());
        assertTrue(designacaoDBHelper.buscarDesignacoesEmAberto().isEmpty());
    }

    @Test
    public void testGravarEBuscarDesignacao() {
        assertFalse(designacaoDBHelper.dirigenteJaDesignado(1));
        assertFalse(designacaoDBHelper.possuiDesignacao(1));
        assertFalse(designacaoDBHelper.dirigenteJaDesignado(99999));
        assertFalse(designacaoDBHelper.possuiDesignacao(99999));

        long agora = System.currentTimeMillis();
        DesignacaoVO vo = new DesignacaoVO(1, 1, "D", agora, null);
        designacaoDBHelper.gravarDesignacoes(Collections.singletonList(vo));

        assertTrue(designacaoDBHelper.existeDesignacaoAberto(1));
        assertFalse(designacaoDBHelper.existeDesignacaoAberto(99999));
        assertTrue(designacaoDBHelper.dirigenteJaDesignado(1));
        assertTrue(designacaoDBHelper.possuiDesignacao(1));

        List<DesignacaoVO> abertas = designacaoDBHelper.buscarDesignacoesEmAberto();
        assertEquals(1, abertas.size());
        assertEquals(Integer.valueOf(1), abertas.get(0).getIdTerritorio());

        DesignacaoVO buscada = designacaoDBHelper.buscarDesignacao(abertas.get(0).getId());
        assertNotNull(buscada);
        assertEquals("D", buscada.getTipo());

        assertNull(designacaoDBHelper.buscarDesignacao(99999));

        DesignacaoVO aberta = abertas.get(0);
        aberta.setDataFim(System.currentTimeMillis());
        designacaoDBHelper.atualizarDesignacao(aberta);

        assertFalse(designacaoDBHelper.existeDesignacaoAberto(1));
    }

    @Test
    public void testBuscarDesignacoesTerritorioAbertoEConsultasPorTerritorio() {
        long agora = System.currentTimeMillis();
        DesignacaoVO vo = new DesignacaoVO(1, 1, "D", agora, null);
        designacaoDBHelper.gravarDesignacoes(Collections.singletonList(vo));

        List<DesignacaoVO> abertasTodas = designacaoDBHelper.buscarDesignacoesTerritorioAberto(null);
        assertEquals(1, abertasTodas.size());

        List<DesignacaoVO> abertasCod = designacaoDBHelper.buscarDesignacoesTerritorioAberto("T-01");
        assertEquals(1, abertasCod.size());
        assertEquals("T-01", abertasCod.get(0).getCodTerritorio());

        List<DesignacaoVO> abertasNome = designacaoDBHelper.buscarDesignacoesTerritorioAberto("Carlos", true);
        assertEquals(1, abertasNome.size());

        List<DesignacaoVO> porTerritorio = designacaoDBHelper.buscarDesignacaoPorIdTerritorio(1);
        assertEquals(1, porTerritorio.size());

        assertTrue(designacaoDBHelper.buscarDesignacaoPorIdTerritorio(99999).isEmpty());
    }

    @Test
    public void testMarcarEDesmarcarERemoverDesignacao() {
        long agora = System.currentTimeMillis();
        DesignacaoVO vo = new DesignacaoVO(1, 1, "D", agora, null);
        designacaoDBHelper.gravarDesignacoes(Collections.singletonList(vo));

        DesignacaoVO salva = designacaoDBHelper.buscarDesignacoesEmAberto().get(0);

        designacaoDBHelper.marcarRegistro(salva);
        List<DesignacaoVO> marcadas = designacaoDBHelper.buscarDesignacoesTerritorioAberto("T-01");
        assertEquals(Integer.valueOf(1), marcadas.get(0).getMarcado());

        designacaoDBHelper.desmarcarRegistro(salva);
        List<DesignacaoVO> desmarcadas = designacaoDBHelper.buscarDesignacoesTerritorioAberto("T-01");
        assertEquals(Integer.valueOf(0), desmarcadas.get(0).getMarcado());

        assertTrue(designacaoDBHelper.deletarDesignacao(salva.getId()));
        assertNull(designacaoDBHelper.buscarDesignacao(salva.getId()));
    }
}
