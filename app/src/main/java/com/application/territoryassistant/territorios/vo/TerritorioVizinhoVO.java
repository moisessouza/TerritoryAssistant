package com.application.territoryassistant.territorios.vo;

/**
 * Created by moises on 02/01/16.
 */
public class TerritorioVizinhoVO {

    private Integer id;
    private Integer idTerritorio;
    private Integer idVizinho;

    public TerritorioVizinhoVO(){}

    public TerritorioVizinhoVO(Integer idTerritorio, Integer idVizinho){
        this.idTerritorio = idTerritorio;
        this.idVizinho = idVizinho;
    }

    public TerritorioVizinhoVO(Integer id, Integer idTerritorio, Integer idVizinho){
        this.id = id;
        this.idTerritorio = idTerritorio;
        this.idVizinho = idVizinho;
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

    public void setIdTerritorio(Integer idTerritorio) {
        this.idTerritorio = idTerritorio;
    }

    public Integer getIdVizinho() {
        return idVizinho;
    }

    public void setIdVizinho(Integer idVizinho) {
        this.idVizinho = idVizinho;
    }
}
