package com.application.territoryassistant.bd;

import android.content.Context;

import com.application.territoryassistant.bd.room.AppDatabase;
import com.application.territoryassistant.bd.room.GrupoDao;
import com.application.territoryassistant.bd.room.GrupoEntity;
import com.application.territoryassistant.grupos.vo.GrupoVO;

import java.util.ArrayList;
import java.util.List;

public class GrupoDBHelper extends DBHelper {

    public static final String TAB_GRUPO = "GRUPO";
    private final GrupoDao dao;

    public GrupoDBHelper(Context context){
        super(context);
        this.dao = AppDatabase.getInstance(context).grupoDao();
    }

    public boolean gravarGrupo(String cod){
        GrupoEntity entity = new GrupoEntity(null, cod);
        dao.insert(entity);
        return true;
    }

    public List<GrupoVO> buscarGrupos() {
        List<GrupoEntity> entities = dao.getAll();
        List<GrupoVO> result = new ArrayList<>();
        for (GrupoEntity e : entities) {
            int id = e.getId() != null ? e.getId() : 0;
            result.add(new GrupoVO(id, e.getNome()));
        }
        return result;
    }

    public boolean deletarGrupo(Integer id){
        if (id == null) return false;
        dao.delete(id);
        return true;
    }

    public GrupoVO buscarGrupo(Integer id) {
        if (id == null) return null;
        GrupoEntity e = dao.getById(id);
        if (e != null) {
            int eId = e.getId() != null ? e.getId() : 0;
            return new GrupoVO(eId, e.getNome());
        }
        return null;
    }

    public GrupoVO atualizarGrupo(GrupoVO vo) {
        if (vo == null || vo.getId() == null) return vo;
        GrupoEntity entity = new GrupoEntity(vo.getId(), vo.getNome());
        dao.update(entity);
        return vo;
    }

    public boolean possuiGrupo() {
        return dao.count() > 0;
    }

    public boolean possuiTerritorio(Integer id){
        if (id == null) return false;
        return dao.countTerritorios(id) > 0;
    }
}
