package com.application.territoryassistant.grupos.vo;

import java.io.Serializable;

/**
 * Created by moises on 31/12/15.
 */
public class GrupoVO implements Serializable{

    private Integer id;
    private String nome;

    public GrupoVO(){}

    public GrupoVO(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
