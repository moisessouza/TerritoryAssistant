package com.application.territoryassistant.bd;

import android.content.Context;

import com.application.territoryassistant.bd.room.AppDatabase;
import com.application.territoryassistant.bd.room.UltimaAcoesDao;
import com.application.territoryassistant.bd.room.UltimaAcoesEntity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UltimaAcoesDBHelper extends DBHelper {

    public static final String TAB_ULTIMA_ACOES = "ULTIMA_ACOES";
    private final UltimaAcoesDao dao;
    private final Context context;

    public UltimaAcoesDBHelper(Context context){
        super(context);
        this.context = context;
        this.dao = AppDatabase.getInstance(context).ultimaAcoesDao();
    }

    public void gravarUltimaAcoes(UltimaAcaoVO vo) {
        if (vo == null) return;
        UltimaAcoesEntity entity = new UltimaAcoesEntity(
                0,
                vo.getCodAcao() != null ? vo.getCodAcao() : "",
                vo.getCodTerritorios() != null ? vo.getCodTerritorios() : "",
                vo.getIdDirigente() != null ? vo.getIdDirigente() : 0,
                vo.getDataInicio(),
                vo.getDataFim()
        );
        long id = dao.insert(entity);
        vo.setId((int) id);
    }

    public List<UltimaAcaoVO> recuperarUltimasAcoes(Integer limit) {
        int maxLimit = limit != null ? limit : 10;
        List<UltimaAcoesEntity> entities = dao.getRecent(maxLimit);
        List<UltimaAcaoVO> result = new ArrayList<>();

        DirigenteDBHelper dirigenteDBHelper = new DirigenteDBHelper(context);

        Calendar c = Calendar.getInstance();
        DateFormat df = DateFormat.getDateInstance();

        for (UltimaAcoesEntity e : entities) {
            String nome = "";
            com.application.territoryassistant.dirigentes.vo.DirigentesVO dirigente = dirigenteDBHelper.buscarDirigente(e.getIdDirigente());
            if (dirigente != null) {
                nome = dirigente.getNome();
            }

            String dataInicioStr = "";
            if (e.getDataInicio() != null) {
                c.setTimeInMillis(e.getDataInicio());
                dataInicioStr = df.format(c.getTime());
            }

            String dataFimStr = "";
            if (e.getDataFim() != null) {
                c.setTimeInMillis(e.getDataFim());
                dataFimStr = df.format(c.getTime());
            }

            result.add(new UltimaAcaoVO(e.getId(), e.getCodAcao(), e.getCodTerritorios(), nome, dataInicioStr, dataFimStr));
        }

        return result;
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
