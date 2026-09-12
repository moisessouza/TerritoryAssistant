package com.application.territoryassistant.vo;

import com.application.territoryassistant.designar.vo.DesignacaoVO;

import org.junit.Test;

import static org.junit.Assert.*;

public class DesignacaoVOTest {

    @Test
    public void testDesignacaoVOGettersAndSetters() {
        long agora = System.currentTimeMillis();
        DesignacaoVO vo = new DesignacaoVO(10, 1, 2, "D", agora, null);

        assertEquals(Integer.valueOf(10), vo.getId());
        assertEquals(Integer.valueOf(1), vo.getIdTerritorio());
        assertEquals(Integer.valueOf(2), vo.getIdDirigente());
        assertEquals("D", vo.getTipo());
        assertEquals(Long.valueOf(agora), vo.getDataInicio());
        assertNull(vo.getDataFim());

        vo.setMarcado(1);
        assertEquals(Integer.valueOf(1), vo.getMarcado());

        long fim = agora + 10000L;
        vo.setDataFim(fim);
        assertEquals(Long.valueOf(fim), vo.getDataFim());
    }
}
