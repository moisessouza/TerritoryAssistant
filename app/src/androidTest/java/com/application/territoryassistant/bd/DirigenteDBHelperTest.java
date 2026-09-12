package com.application.territoryassistant.bd;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.application.territoryassistant.dirigentes.vo.DirigentesVO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DirigenteDBHelperTest {

    private DirigenteDBHelper dbHelper;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase(DBHelper.DATABASE_NAME);
        dbHelper = new DirigenteDBHelper(context);
    }

    @After
    public void tearDown() {
        dbHelper.close();
        context.deleteDatabase(DBHelper.DATABASE_NAME);
    }

    @Test
    public void testGravarEBuscarDirigente() {
        assertFalse(dbHelper.possuiDirigentesCadastrado());
        assertTrue(dbHelper.gravarDirigente("João Silva", "joao@email.com"));
        assertTrue(dbHelper.possuiDirigentesCadastrado());

        List<DirigentesVO> lista = dbHelper.buscarDirigentes();
        assertEquals(1, lista.size());
        assertEquals("João Silva", lista.get(0).getNome());
        assertEquals("joao@email.com", lista.get(0).getEmail());

        DirigentesVO vo = dbHelper.buscarDirigente(lista.get(0).getId());
        assertNotNull(vo);
        assertEquals("João Silva", vo.getNome());

        assertNull(dbHelper.buscarDirigente(99999));
    }

    @Test
    public void testBuscarDirigentesPorId() {
        assertTrue(dbHelper.buscarDirigentesPorId((Integer[]) null).isEmpty());

        dbHelper.gravarDirigente("Ana", "ana@email.com");
        dbHelper.gravarDirigente("Bruno", "bruno@email.com");

        List<DirigentesVO> todos = dbHelper.buscarDirigentes();
        assertEquals(2, todos.size());

        Integer id1 = todos.get(0).getId();
        Integer id2 = todos.get(1).getId();

        List<DirigentesVO> porIds = dbHelper.buscarDirigentesPorId(id1, id2);
        assertEquals(2, porIds.size());
    }

    @Test
    public void testAtualizarEDeletarDirigente() {
        dbHelper.gravarDirigente("Maria", "maria@email.com");
        DirigentesVO vo = dbHelper.buscarDirigentes().get(0);

        vo.setNome("Maria Santos");
        vo.setEmail("maria.santos@email.com");
        dbHelper.atualizarDirigente(vo);

        DirigentesVO atualizado = dbHelper.buscarDirigente(vo.getId());
        assertEquals("Maria Santos", atualizado.getNome());
        assertEquals("maria.santos@email.com", atualizado.getEmail());

        assertTrue(dbHelper.deletarDirigente(vo.getId()));
        assertTrue(dbHelper.buscarDirigentes().isEmpty());
    }
}
