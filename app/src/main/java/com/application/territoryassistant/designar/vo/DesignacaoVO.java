package com.application.territoryassistant.designar.vo;

import java.util.Date;

/**
 * Created by moises on 03/01/16.
 */
public class DesignacaoVO {

    private Integer id;
    private Integer idDirigente;
    private Integer idTerritorio;
    private String tipo;
    private Long dataInicio;
    private Long dataFim;

    // Dados para tela
    private String nomeDirigente;
    private String codTerritorio;
    private String dataDesignacao;
    private String fotoPath;
    private Integer marcado;
    private String diaSemana;

    public DesignacaoVO (Integer idTerritorio, Integer idDirigente, String tipo, Long dataInicio, Long dataFim) {
        this.idTerritorio = idTerritorio;
        this.idDirigente = idDirigente;
        this.tipo = tipo;
        this.dataInicio = dataInicio;

        this.dataFim = dataFim;
    }

    public DesignacaoVO (Integer id, Integer idTerritorio, Integer idDirigente, String tipo, Long dataInicio, Long dataFim) {
        this.id = id;
        this.idTerritorio = idTerritorio;
        this.idDirigente = idDirigente;
        this.tipo = tipo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public DesignacaoVO(Integer id, Integer idDirigente, Integer idTerritorio, String nomeDirigente, String codTerritorio, String dataDesignacao, String fotoPath, Integer marcado, String diaSemana) {
        this.id = id;
        this.idDirigente = idDirigente;
        this.idTerritorio = idTerritorio;
        this.nomeDirigente = nomeDirigente;
        this.codTerritorio = codTerritorio;
        this.dataDesignacao = dataDesignacao;
        this.marcado = marcado;
        this.fotoPath = fotoPath;
        this.diaSemana = diaSemana;
    }

    public DesignacaoVO(Integer id, String nome, String tipo, Long dataInicio, Long dataFim){
        this.id = id;
        this.nomeDirigente = nome;
        this.tipo = tipo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdTerritorio() {
        return idTerritorio;
    }

    public Integer getIdDirigente() {
        return idDirigente;
    }

    public void setIdDirigente(Integer idDirigente) {
        this.idDirigente = idDirigente;
    }

    public void setIdTerritorio(Integer idTerritorio) {
        this.idTerritorio = idTerritorio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Long dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Long getDataFim() {
        return dataFim;
    }

    public void setDataFim(Long dataFim) {
        this.dataFim = dataFim;
    }

    public String getCodTerritorio() {
        return codTerritorio;
    }

    public void setCodTerritorio(String codTerritorio) {
        this.codTerritorio = codTerritorio;
    }

    public String getDataDesignacao() {
        return dataDesignacao;
    }

    public void setDataDesignacao(String dataDesignacao) {
        this.dataDesignacao = dataDesignacao;
    }

    public String getNomeDirigente() {
        return nomeDirigente;
    }

    public void setNomeDirigente(String nomeDirigente) {
        this.nomeDirigente = nomeDirigente;
    }

    public String getFotoPath() {
        return fotoPath;
    }

    public void setFotoPath(String fotoPath) {
        this.fotoPath = fotoPath;
    }

    public void setMarcado(Integer marcado) {
        this.marcado = marcado;
    }

    public Integer getMarcado() {
        return marcado;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }
}
