package com.application.territoryassistant.territorios.vo;

import java.io.Serializable;

/**
 * Created by moises on 31/12/15.
 */
public class TerritorioVO implements Serializable{

    private Integer id;
    private String cod;
    private String observacoes;
    private String fotoPath;
    private Integer idGrupo;
    private Long ultimaDataFim;
    private Boolean suspenso;

    public TerritorioVO () {}

    public TerritorioVO (Integer id, String cod, String fotoPath, Integer idGrupo, Long ultimaDataFim, Boolean suspenso) {
        this.id = id;
        this.cod = cod;
        this.idGrupo = idGrupo;
        this.ultimaDataFim = ultimaDataFim;
        this.suspenso = suspenso;
        this.fotoPath = fotoPath;
    }

    public TerritorioVO (Integer id, String cod, String observacoes, String fotoPath, Integer idGrupo, Long ultimaDataFim, Boolean suspenso) {
        this.id = id;
        this.cod = cod;
        this.observacoes = observacoes;
        this.idGrupo = idGrupo;
        this.ultimaDataFim = ultimaDataFim;
        this.suspenso = suspenso;
        this.fotoPath = fotoPath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public void setIdGrupo(Integer idGrupo) {
        this.idGrupo = idGrupo;
    }

    public Integer getIdGrupo() {
        return idGrupo;
    }

    public Long getUltimaDataFim() {
        return ultimaDataFim;
    }

    public void setUltimaDataFim(Long ultimaDataFim) {
        this.ultimaDataFim = ultimaDataFim;
    }

    public Boolean getSuspenso() {
        return suspenso;
    }

    public void setSuspenso(Boolean suspenso) {
        this.suspenso = suspenso;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getFotoPath() {
        return fotoPath;
    }

    public void setFotoPath(String fotoPath) {
        this.fotoPath = fotoPath;
    }
}
