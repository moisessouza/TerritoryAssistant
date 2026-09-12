package com.application.territoryassistant.vo;

import com.application.territoryassistant.dirigentes.vo.DirigentesVO;

import org.junit.Test;

import static org.junit.Assert.*;

public class DirigentesVOTest {

    @Test
    public void testDirigentesVOGettersAndSetters() {
        DirigentesVO vo = new DirigentesVO(1, "Marcos", "marcos@email.com");

        assertEquals(Integer.valueOf(1), vo.getId());
        assertEquals("Marcos", vo.getNome());
        assertEquals("marcos@email.com", vo.getEmail());

        vo.setNome("Marcos Oliveira");
        assertEquals("Marcos Oliveira", vo.getNome());
    }
}
