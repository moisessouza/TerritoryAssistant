package com.application.territoryassistant.bd;

import android.content.Context;

import com.application.territoryassistant.bd.room.AppDatabase;
import com.application.territoryassistant.bd.room.DesignacaoDao;
import com.application.territoryassistant.bd.room.DesignacaoEntity;
import com.application.territoryassistant.bd.room.DirigenteEntity;
import com.application.territoryassistant.bd.room.TerritorioEntity;
import com.application.territoryassistant.designar.vo.DesignacaoVO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DesignacaoDBHelper extends DBHelper {

    public static final String TAB_DESIGNACAO = "DESIGNACAO";
    private final DesignacaoDao dao;
    private final AppDatabase db;

    public DesignacaoDBHelper(Context context){
        super(context);
        this.db = AppDatabase.getInstance(context);
        this.dao = db.designacaoDao();
    }

    public void gravarDesignacoes(List<DesignacaoVO> designacaoVOs) {
        if (designacaoVOs != null && !designacaoVOs.isEmpty()) {
            for (DesignacaoVO vo : designacaoVOs) {
                DesignacaoEntity entity = new DesignacaoEntity(
                        null,
                        vo.getIdTerritorio() != null ? vo.getIdTerritorio() : 0,
                        vo.getIdDirigente() != null ? vo.getIdDirigente() : 0,
                        vo.getTipo() != null ? vo.getTipo() : "",
                        vo.getDataInicio() != null ? vo.getDataInicio() : System.currentTimeMillis(),
                        vo.getDataFim(),
                        vo.getMarcado()
                );
                long id = dao.insert(entity);
                if (id != -1) {
                    vo.setId((int) id);
                }
            }
        }
    }

    public DesignacaoVO buscarDesignacao(Integer idDesignacao){
        if (idDesignacao == null) return null;
        DesignacaoEntity e = dao.getById(idDesignacao);
        if (e != null) {
            int id = e.getId() != null ? e.getId() : 0;
            return new DesignacaoVO(id, e.getIdTerritorio(), e.getIdDirigente(), e.getTipo(), e.getDataInicio(), e.getDataFim());
        }
        return null;
    }

    public boolean existeDesignacaoAberto(Integer idTerritorio){
        if (idTerritorio == null) return false;
        return dao.getAbertoByTerritorio(idTerritorio) != null;
    }

    public List<DesignacaoVO> buscarDesignacoesEmAberto(){
        List<DesignacaoEntity> entities = dao.getEmAberto();
        List<DesignacaoVO> result = new ArrayList<>();
        for (DesignacaoEntity e : entities) {
            int id = e.getId() != null ? e.getId() : 0;
            result.add(new DesignacaoVO(id, e.getIdTerritorio(), e.getIdDirigente(), e.getTipo(), e.getDataInicio(), e.getDataFim()));
        }
        return result;
    }

    public DesignacaoVO atualizarDesignacao(DesignacaoVO vo) {
        if (vo == null || vo.getId() == null) return vo;
        DesignacaoEntity entity = new DesignacaoEntity(
                vo.getId(),
                vo.getIdTerritorio() != null ? vo.getIdTerritorio() : 0,
                vo.getIdDirigente() != null ? vo.getIdDirigente() : 0,
                vo.getTipo() != null ? vo.getTipo() : "",
                vo.getDataInicio() != null ? vo.getDataInicio() : System.currentTimeMillis(),
                vo.getDataFim(),
                vo.getMarcado()
        );
        dao.update(entity);
        return vo;
    }

    public boolean deletarDesignacao(Integer idDesignacao) {
        if (idDesignacao == null) return false;
        dao.delete(idDesignacao);
        return true;
    }

    public List<DesignacaoVO> buscarDesignacoesTerritorioAberto(String cod) {
        return buscarDesignacoesTerritorioAberto(cod, false);
    }

    public List<DesignacaoVO> buscarDesignacoesTerritorioAberto(String codOrNome, boolean incluirNome) {
        List<DesignacaoEntity> abertas = dao.getEmAberto();
        List<DesignacaoVO> result = new ArrayList<>();

        DateFormat df = DateFormat.getDateInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        Calendar c = Calendar.getInstance();

        for (DesignacaoEntity e : abertas) {
            TerritorioEntity territorio = db.territorioDao().getById(e.getIdTerritorio());
            DirigenteEntity dirigente = db.dirigenteDao().getById(e.getIdDirigente());

            String codTerritorio = territorio != null ? territorio.getCod() : "";
            String nomeDirigente = dirigente != null ? dirigente.getNome() : "";
            String fotoPath = territorio != null ? territorio.getFotoPath() : null;

            boolean matches = true;
            if (codOrNome != null && !codOrNome.isEmpty()) {
                String filter = codOrNome.toLowerCase();
                if (incluirNome) {
                    matches = codTerritorio.toLowerCase().contains(filter) || nomeDirigente.toLowerCase().contains(filter);
                } else {
                    matches = codTerritorio.toLowerCase().contains(filter);
                }
            }

            if (matches) {
                c.setTimeInMillis(e.getDataInicio());
                Date date = c.getTime();
                String data = df.format(date);
                String diaSemana = sdf.format(date);

                int id = e.getId() != null ? e.getId() : 0;
                result.add(new DesignacaoVO(id, e.getIdDirigente(), e.getIdTerritorio(), nomeDirigente, codTerritorio, data, fotoPath, e.getMarcado(), diaSemana));
            }
        }

        return result;
    }

    public boolean dirigenteJaDesignado(Integer id) {
        if (id == null) return false;
        return dao.countByDirigente(id) > 0;
    }

    public boolean possuiDesignacao(Integer id) {
        if (id == null) return false;
        return dao.countByTerritorio(id) > 0;
    }

    public List<DesignacaoVO> buscarDesignacaoPorIdTerritorio(Integer id) {
        if (id == null) return new ArrayList<>();
        List<DesignacaoEntity> abertas = dao.getEmAberto();
        List<DesignacaoVO> result = new ArrayList<>();

        for (DesignacaoEntity e : abertas) {
            if (e.getIdTerritorio() == id) {
                DirigenteEntity dirigente = db.dirigenteDao().getById(e.getIdDirigente());
                String nome = dirigente != null ? dirigente.getNome() : "";
                int eId = e.getId() != null ? e.getId() : 0;
                result.add(new DesignacaoVO(eId, nome, e.getTipo(), e.getDataInicio(), e.getDataFim()));
            }
        }

        return result;
    }

    public void marcarRegistro(DesignacaoVO vo) {
        if (vo != null && vo.getId() != null) {
            dao.updateMarcado(vo.getId(), 1);
            vo.setMarcado(1);
        }
    }

    public void desmarcarRegistro(DesignacaoVO vo) {
        if (vo != null && vo.getId() != null) {
            dao.updateMarcado(vo.getId(), 0);
            vo.setMarcado(0);
        }
    }
}
