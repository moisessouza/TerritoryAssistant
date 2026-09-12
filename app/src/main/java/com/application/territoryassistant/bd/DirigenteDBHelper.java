package com.application.territoryassistant.bd;

import android.content.Context;

import com.application.territoryassistant.bd.room.AppDatabase;
import com.application.territoryassistant.bd.room.DirigenteDao;
import com.application.territoryassistant.bd.room.DirigenteEntity;
import com.application.territoryassistant.dirigentes.vo.DirigentesVO;

import java.util.ArrayList;
import java.util.List;

public class DirigenteDBHelper extends DBHelper {

    public static final String TAB_DIRIGENTES = "DIRIGENTES";
    private final DirigenteDao dao;

    public DirigenteDBHelper(Context context){
        super(context);
        this.dao = AppDatabase.getInstance(context).dirigenteDao();
    }

    public boolean gravarDirigente(String nome, String email){
        DirigenteEntity entity = new DirigenteEntity(null, nome, email);
        dao.insert(entity);
        return true;
    }

    public List<DirigentesVO> buscarDirigentes() {
        List<DirigenteEntity> entities = dao.getAll();
        List<DirigentesVO> result = new ArrayList<>();
        for (DirigenteEntity e : entities) {
            int id = e.getId() != null ? e.getId() : 0;
            result.add(new DirigentesVO(id, e.getNome(), e.getEmail()));
        }
        return result;
    }

    public boolean deletarDirigente(Integer id){
        if (id == null) return false;
        dao.delete(id);
        return true;
    }

    public DirigentesVO buscarDirigente(Integer id) {
        if (id == null) return null;
        DirigenteEntity e = dao.getById(id);
        if (e != null) {
            int eId = e.getId() != null ? e.getId() : 0;
            return new DirigentesVO(eId, e.getNome(), e.getEmail());
        }
        return null;
    }

    public DirigentesVO atualizarDirigente(DirigentesVO vo) {
        if (vo == null || vo.getId() == null) return vo;
        DirigenteEntity entity = new DirigenteEntity(vo.getId(), vo.getNome(), vo.getEmail());
        dao.update(entity);
        return vo;
    }

    public List<DirigentesVO> buscarDirigentesPorId(Integer ... ids){
        if (ids == null || ids.length == 0) return new ArrayList<>();
        List<Integer> listIds = new ArrayList<>();
        for (Integer id : ids) {
            if (id != null) listIds.add(id);
        }
        if (listIds.isEmpty()) return new ArrayList<>();
        List<DirigenteEntity> entities = dao.getByIds(listIds);
        List<DirigentesVO> result = new ArrayList<>();
        for (DirigenteEntity e : entities) {
            int eId = e.getId() != null ? e.getId() : 0;
            result.add(new DirigentesVO(eId, e.getNome(), e.getEmail()));
        }
        return result;
    }

    public boolean possuiDirigentesCadastrado() {
        return dao.count() > 0;
    }
}
