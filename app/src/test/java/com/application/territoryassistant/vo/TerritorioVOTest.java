package com.application.territoryassistant.vo;

import com.application.territoryassistant.territorios.vo.TerritorioVO;

import org.junit.Test;

import static org.junit.Assert.*;

public class TerritorioVOTest {

    @Test
    public void testTerritorioVOGettersAndSetters() {
        TerritorioVO vo = new TerritorioVO(1, "T-01", "Obs", "/path/photo.jpg", 2, 1000L, false);

        assertEquals(Integer.valueOf(1), vo.getId());
        assertEquals("T-01", vo.getCod());
        assertEquals("Obs", vo.getObservacoes());
        assertEquals("/path/photo.jpg", vo.getFotoPath());
        assertEquals(Integer.valueOf(2), vo.getIdGrupo());
        assertEquals(Long.valueOf(1000L), vo.getUltimaDataFim());
        assertEquals(Boolean.FALSE, vo.getSuspenso());

        vo.setCod("T-02");
        assertEquals("T-02", vo.getCod());

        vo.setSuspenso(true);
        assertEquals(Boolean.TRUE, vo.getSuspenso());
    }
}
