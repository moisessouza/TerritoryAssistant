package com.application.territoryassistant.dirigentes.vo;

/**
 * Created by moises on 31/12/15.
 */
public class DirigentesVO {

    private Integer id;
    private String nome;

    private String codsTerritorio;
    private String email;

    public DirigentesVO(){}

    public DirigentesVO(Integer id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
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

    public String getCodsTerritorio() {
        return codsTerritorio;
    }

    public void setCodsTerritorio(String codsTerritorio) {
        this.codsTerritorio = codsTerritorio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
