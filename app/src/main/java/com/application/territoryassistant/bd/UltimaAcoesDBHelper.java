package com.application.territoryassistant.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.application.territoryassistant.territorios.vo.TerritorioVO;
import com.application.territoryassistant.territorios.vo.TerritorioVizinhoVO;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by moises on 30/12/15.
 */
public class UltimaAcoesDBHelper extends DBHelper {

    public static final String TAB_ULTIMA_ACOES = "ULTIMA_ACOES";

    public UltimaAcoesDBHelper(Context context){
        super(context);
    }

    public void gravarUltimaAcoes(UltimaAcaoVO vo) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();

        v.put("COD_ACAO", vo.getCodAcao());
        v.put("COD_TERRITORIOS", vo.getCodTerritorios());
        v.put("ID_DIRIGENTE", vo.getIdDirigente());
        v.put("DATA_INICIO", vo.getDataInicio());
        v.put("DATA_FIM", vo.getDataFim());

        long id = db.insert(TAB_ULTIMA_ACOES, null, v);
        vo.setId(Long.valueOf(id).intValue());

    }

    public List<UltimaAcaoVO> recuperarUltimasAcoes(Integer limit) {

        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder builder = new StringBuilder("select ULTIMA_ACOES.ID as ID, COD_ACAO, COD_TERRITORIOS, DATA_INICIO, DATA_FIM, NOME");
        builder.append(" from ").append(TAB_ULTIMA_ACOES);
        builder.append(" inner join ").append(DirigenteDBHelper.TAB_DIRIGENTES).append(" on ");
        builder.append(DirigenteDBHelper.TAB_DIRIGENTES).append(".ID=").append(TAB_ULTIMA_ACOES).append(".ID_DIRIGENTE");
        builder.append(" order by ULTIMA_ACOES.ID desc ");
        builder.append(" limit ").append(limit);

        Cursor cursor = db.rawQuery(builder.toString(), null);

        List<UltimaAcaoVO> ultimaAcaoVOs = new ArrayList<>();

        while (cursor.moveToNext()){
            Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            String codAcao = cursor.getString(cursor.getColumnIndexOrThrow("COD_ACAO"));
            String codTerritorios = cursor.getString(cursor.getColumnIndexOrThrow("COD_TERRITORIOS"));
            String nome = cursor.getString(cursor.getColumnIndexOrThrow("NOME"));
            Long dataInicio = cursor.getLong(cursor.getColumnIndexOrThrow("DATA_INICIO"));
            Long dataFim = cursor.getLong(cursor.getColumnIndexOrThrow("DATA_FIM"));

            Calendar c = Calendar.getInstance();
            DateFormat df = DateFormat.getDateInstance();

            c.setTimeInMillis(dataInicio);
            String dataInicioStr = df.format(c.getTime());
            c.setTimeInMillis(dataFim);
            String dataFimStr = df.format(c.getTime());

            ultimaAcaoVOs.add(new UltimaAcaoVO(id, codAcao, codTerritorios, nome, dataInicioStr, dataFimStr));
        }

        return ultimaAcaoVOs;
    }

    public static class UltimaAcaoVO {

        private Integer id;
        private String codAcao;
        private String codTerritorios;
        private Integer idDirigente;
        private Long dataInicio;
        private Long dataFim;

        //Apresentacao tela
        private String nome;
        private String dataInicioStr;
        private String dataFimStr;

        public UltimaAcaoVO(String codAcao, String codTerritorios, Integer idDirigente, String dataInicioStr, String dataFimStr) {
            this.codAcao = codAcao;
            this.codTerritorios = codTerritorios;
            this.idDirigente = idDirigente;
            this.dataInicioStr = dataInicioStr;
            this.dataFimStr = dataFimStr;
        }

        public UltimaAcaoVO(Integer id, String codAcao, String codTerritorios, String nome, String dataInicioStr, String dataFimStr) {
            this.id = id;
            this.codAcao = codAcao;
            this.codTerritorios = codTerritorios;
            this.nome = nome;
            this.dataInicioStr = dataInicioStr;
            this.dataFimStr = dataFimStr;
        }

        public UltimaAcaoVO(String codAcao, String codTerritorios, Integer idDirigente, Long dataInicio, Long dataFim) {
            this.codAcao = codAcao;
            this.codTerritorios = codTerritorios;
            this.idDirigente = idDirigente;
            this.dataInicio = dataInicio;
            this.dataFim = dataFim;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getCodAcao() {
            return codAcao;
        }

        public void setCodAcao(String codAcao) {
            this.codAcao = codAcao;
        }

        public String getCodTerritorios() {
            return codTerritorios;
        }

        public void setCodTerritorios(String codTerritorios) {
            this.codTerritorios = codTerritorios;
        }

        public Integer getIdDirigente() {
            return idDirigente;
        }

        public void setIdDirigente(Integer idDirigente) {
            this.idDirigente = idDirigente;
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

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getDataInicioStr() {
            return dataInicioStr;
        }

        public void setDataInicioStr(String dataInicioStr) {
            this.dataInicioStr = dataInicioStr;
        }

        public String getDataFimStr() {
            return dataFimStr;
        }

        public void setDataFimStr(String dataFimStr) {
            this.dataFimStr = dataFimStr;
        }
    }
}
