package com.application.territoryassistant.vo;

import com.application.territoryassistant.grupos.vo.GrupoVO;

import org.junit.Test;

import static org.junit.Assert.*;

public class GrupoVOTest {

    @Test
    public void testGrupoVOGettersAndSetters() {
        GrupoVO vo = new GrupoVO(5, "Grupo Norte");

        assertEquals(Integer.valueOf(5), vo.getId());
        assertEquals("Grupo Norte", vo.getNome());

        vo.setNome("Grupo Sul");
        assertEquals("Grupo Sul", vo.getNome());
    }
}
