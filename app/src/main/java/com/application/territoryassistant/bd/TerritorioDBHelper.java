package com.application.territoryassistant.bd;

import android.content.Context;

import com.application.territoryassistant.bd.room.AppDatabase;
import com.application.territoryassistant.bd.room.DesignacaoEntity;
import com.application.territoryassistant.bd.room.TerritorioDao;
import com.application.territoryassistant.bd.room.TerritorioEntity;
import com.application.territoryassistant.bd.room.TerritorioVizinhoDao;
import com.application.territoryassistant.bd.room.TerritorioVizinhoEntity;
import com.application.territoryassistant.territorios.vo.TerritorioVO;
import com.application.territoryassistant.territorios.vo.TerritorioVizinhoVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TerritorioDBHelper extends DBHelper {

    public static final String TAB_TERRITORIO = "TERRITORIO";
    public static final String TAB_TERRITORIO_VIZINHO = "TERRITORIO_VIZINHO";

    private final TerritorioDao dao;
    private final TerritorioVizinhoDao vizinhoDao;
    private final AppDatabase db;

    public TerritorioDBHelper(Context context){
        super(context);
        this.db = AppDatabase.getInstance(context);
        this.dao = db.territorioDao();
        this.vizinhoDao = db.territorioVizinhoDao();
    }

    public long gravarTerritorio(String cod, Integer idGrupo, String observacoes, String fotoPath, Boolean suspenso){
        TerritorioEntity entity = new TerritorioEntity(
                0,
                cod != null ? cod : "",
                idGrupo != null ? idGrupo : 0,
                null,
                suspenso != null && suspenso ? 1 : 0,
                observacoes,
                fotoPath
        );
        return dao.insert(entity);
    }

    public List<TerritorioVO> buscarTerritoriosNaoDesignados(){
        return buscarTerritoriosNaoDesignados(false, null);
    }

    public List<TerritorioVO> buscarTerritoriosNaoDesignados(boolean ordenarData, Integer idGrupo){
        List<TerritorioEntity> all = dao.getAll();
        List<DesignacaoEntity> abertas = db.designacaoDao().getEmAberto();
        Set<Integer> territotiosOcupados = new HashSet<>();
        for (DesignacaoEntity d : abertas) {
            territotiosOcupados.add(d.getIdTerritorio());
        }

        List<TerritorioVO> result = new ArrayList<>();
        for (TerritorioEntity t : all) {
            if (!territotiosOcupados.contains(t.getId()) && (t.getSuspenso() == null || t.getSuspenso() == 0)) {
                if (idGrupo == null || t.getIdGrupo() == idGrupo) {
                    boolean isSuspenso = t.getSuspenso() != null && t.getSuspenso() != 0;
                    result.add(new TerritorioVO(t.getId(), t.getCod(), t.getFotoPath(), t.getIdGrupo(), t.getUltimaDataFim(), isSuspenso));
                }
            }
        }

        if (ordenarData) {
            Collections.sort(result, new Comparator<TerritorioVO>() {
                @Override
                public int compare(TerritorioVO o1, TerritorioVO o2) {
                    Long d1 = o1.getUltimaDataFim() != null ? o1.getUltimaDataFim() : 0L;
                    Long d2 = o2.getUltimaDataFim() != null ? o2.getUltimaDataFim() : 0L;
                    return d1.compareTo(d2);
                }
            });
        }

        return result;
    }

    public List<TerritorioVO> buscarTerritorios() {
        return buscarTerritorios(null, false, null);
    }

    public List<TerritorioVO> buscarTerritorios(Integer notId, boolean ordenarData, Integer idGrupo) {
        List<TerritorioEntity> all = dao.getAll();
        List<TerritorioVO> result = new ArrayList<>();

        for (TerritorioEntity t : all) {
            if (notId != null && t.getId() == notId) continue;
            if (idGrupo != null && t.getIdGrupo() != idGrupo) continue;

            boolean isSuspenso = t.getSuspenso() != null && t.getSuspenso() != 0;
            result.add(new TerritorioVO(t.getId(), t.getCod(), t.getFotoPath(), t.getIdGrupo(), t.getUltimaDataFim(), isSuspenso));
        }

        if (ordenarData) {
            Collections.sort(result, new Comparator<TerritorioVO>() {
                @Override
                public int compare(TerritorioVO o1, TerritorioVO o2) {
                    Long d1 = o1.getUltimaDataFim() != null ? o1.getUltimaDataFim() : 0L;
                    Long d2 = o2.getUltimaDataFim() != null ? o2.getUltimaDataFim() : 0L;
                    return d1.compareTo(d2);
                }
            });
        }

        return result;
    }

    public boolean deletarTerritorio(Integer id){
        if (id == null) return false;
        dao.delete(id);
        return true;
    }

    public List<TerritorioVO> buscarTerritoriosPorCod(String cods){
        if (cods == null || cods.isEmpty()) return new ArrayList<>();
        String[] query = cods.split(",");
        Set<String> setCods = new HashSet<>(Arrays.asList(query));

        List<TerritorioEntity> all = dao.getAll();
        List<TerritorioVO> result = new ArrayList<>();

        for (TerritorioEntity t : all) {
            if (setCods.contains(t.getCod())) {
                boolean isSuspenso = t.getSuspenso() != null && t.getSuspenso() != 0;
                result.add(new TerritorioVO(t.getId(), t.getCod(), t.getObservacoes(), t.getFotoPath(), t.getIdGrupo(), t.getUltimaDataFim(), isSuspenso));
            }
        }

        return result;
    }

    public TerritorioVO buscarTerritorioPorCod(String cod, Integer ... notIds) {
        if (cod == null) return null;
        Set<Integer> excluded = notIds != null ? new HashSet<>(Arrays.asList(notIds)) : new HashSet<>();

        List<TerritorioEntity> all = dao.getAll();
        for (TerritorioEntity t : all) {
            if (cod.equals(t.getCod()) && !excluded.contains(t.getId())) {
                boolean isSuspenso = t.getSuspenso() != null && t.getSuspenso() != 0;
                return new TerritorioVO(t.getId(), t.getCod(), t.getFotoPath(), t.getIdGrupo(), t.getUltimaDataFim(), isSuspenso);
            }
        }
        return null;
    }

    public TerritorioVO buscarTerritorio(Integer id) {
        if (id == null) return null;
        TerritorioEntity t = dao.getById(id);
        if (t != null) {
            boolean isSuspenso = t.getSuspenso() != null && t.getSuspenso() != 0;
            return new TerritorioVO(t.getId(), t.getCod(), t.getObservacoes(), t.getFotoPath(), t.getIdGrupo(), t.getUltimaDataFim(), isSuspenso);
        }
        return null;
    }

    public TerritorioVO atualizarTerritorio(TerritorioVO vo) {
        if (vo == null || vo.getId() == null) return vo;
        TerritorioEntity entity = new TerritorioEntity(
                vo.getId(),
                vo.getCod() != null ? vo.getCod() : "",
                vo.getIdGrupo() != null ? vo.getIdGrupo() : 0,
                vo.getUltimaDataFim(),
                vo.getSuspenso() != null && vo.getSuspenso() ? 1 : 0,
                vo.getObservacoes(),
                vo.getFotoPath()
        );
        dao.update(entity);
        return vo;
    }

    public List<TerritorioVizinhoVO> gravarVizinhos(Integer idTerritorio, List<TerritorioVO> territorioVOs) {
        if (idTerritorio == null) return new ArrayList<>();
        deletarVizinhos(idTerritorio);

        List<TerritorioVizinhoVO> result = new ArrayList<>();
        if (territorioVOs != null && !territorioVOs.isEmpty()) {
            for (TerritorioVO voSelecionado : territorioVOs) {
                if (voSelecionado != null && voSelecionado.getId() != null) {
                    if (vizinhoDao.getByTerritorioEVizinho(idTerritorio, voSelecionado.getId()) == null) {
                        long id1 = vizinhoDao.insert(new TerritorioVizinhoEntity(0, idTerritorio, voSelecionado.getId()));
                        result.add(new TerritorioVizinhoVO((int) id1, idTerritorio, voSelecionado.getId()));
                    }
                    if (vizinhoDao.getByTerritorioEVizinho(voSelecionado.getId(), idTerritorio) == null) {
                        long id2 = vizinhoDao.insert(new TerritorioVizinhoEntity(0, voSelecionado.getId(), idTerritorio));
                        result.add(new TerritorioVizinhoVO((int) id2, voSelecionado.getId(), idTerritorio));
                    }
                }
            }
        }
        return result;
    }

    public boolean deletarVizinhos(Integer idTerritorio) {
        if (idTerritorio == null) return false;
        vizinhoDao.deleteByTerritorio(idTerritorio);
        return true;
    }

    public List<TerritorioVizinhoVO> buscarVizinhos(Integer idTerritorio) {
        if (idTerritorio == null) return new ArrayList<>();
        List<TerritorioVizinhoEntity> entities = vizinhoDao.getByTerritorio(idTerritorio);
        List<TerritorioVizinhoVO> result = new ArrayList<>();
        for (TerritorioVizinhoEntity e : entities) {
            result.add(new TerritorioVizinhoVO(e.getId(), e.getIdTerritorio(), e.getIdVizinho()));
        }
        return result;
    }

    public List<TerritorioVO> buscarTerritoriosPorId(Integer ... idsTerritorio) {
        if (idsTerritorio == null || idsTerritorio.length == 0) return new ArrayList<>();
        List<Integer> idsList = new ArrayList<>();
        for (Integer id : idsTerritorio) {
            if (id != null) idsList.add(id);
        }
        if (idsList.isEmpty()) return new ArrayList<>();

        List<TerritorioEntity> entities = dao.getByIds(idsList);
        List<TerritorioVO> result = new ArrayList<>();
        for (TerritorioEntity t : entities) {
            boolean isSuspenso = t.getSuspenso() != null && t.getSuspenso() != 0;
            result.add(new TerritorioVO(t.getId(), t.getCod(), t.getFotoPath(), t.getIdGrupo(), t.getUltimaDataFim(), isSuspenso));
        }
        return result;
    }

    public List<String> buscarTerritoriosDesignadosParaDirigente(Integer id) {
        if (id == null) return new ArrayList<>();
        List<DesignacaoEntity> abertas = db.designacaoDao().getEmAberto();
        Set<String> cods = new HashSet<>();

        for (DesignacaoEntity d : abertas) {
            if (d.getIdDirigente() == id) {
                TerritorioEntity t = dao.getById(d.getIdTerritorio());
                if (t != null) {
                    cods.add(t.getCod());
                }
            }
        }
        return new ArrayList<>(cods);
    }

    public TerritorioVO buscarTerritorioMaisTempoTrabalhado(Integer idGrupo, Integer[] inIdsVizinhos, Integer[] notIds, Long dataLimite){
        List<TerritorioEntity> all = dao.getAll();
        Set<Integer> vizinhosSet = inIdsVizinhos != null ? new HashSet<>(Arrays.asList(inIdsVizinhos)) : null;
        Set<Integer> notIdsSet = notIds != null ? new HashSet<>(Arrays.asList(notIds)) : null;

        TerritorioEntity candidate = null;

        for (TerritorioEntity t : all) {
            if (t.getSuspenso() != null && t.getSuspenso() != 0) continue;
            if (idGrupo != null && t.getIdGrupo() != idGrupo) continue;
            if (notIdsSet != null && notIdsSet.contains(t.getId())) continue;

            if (vizinhosSet != null && !vizinhosSet.isEmpty()) {
                List<TerritorioVizinhoEntity> vizinhosOfT = vizinhoDao.getByTerritorio(t.getId());
                boolean hasVizinhoMatch = false;
                for (TerritorioVizinhoEntity v : vizinhosOfT) {
                    if (vizinhosSet.contains(v.getIdVizinho())) {
                        hasVizinhoMatch = true;
                        break;
                    }
                }
                if (!hasVizinhoMatch) continue;
            }

            if (dataLimite != null && t.getUltimaDataFim() != null && t.getUltimaDataFim() > dataLimite) {
                continue;
            }

            if (candidate == null) {
                candidate = t;
            } else {
                Long candidateTime = candidate.getUltimaDataFim();
                Long currentTime = t.getUltimaDataFim();
                if (currentTime == null || (candidateTime != null && currentTime < candidateTime)) {
                    candidate = t;
                }
            }
        }

        if (candidate != null) {
            boolean isSuspenso = candidate.getSuspenso() != null && candidate.getSuspenso() != 0;
            return new TerritorioVO(candidate.getId(), candidate.getCod(), candidate.getFotoPath(), candidate.getIdGrupo(), candidate.getUltimaDataFim(), isSuspenso);
        }

        return null;
    }

    public boolean possuiGrupo(Integer id) {
        if (id == null) return false;
        return dao.countByGrupo(id) > 0;
    }

    public boolean possuiTerritoriosCadastrado() {
        return dao.count() > 0;
    }

    public boolean territorioSuspenso(Integer id) {
        if (id == null) return false;
        Integer suspenso = dao.isSuspenso(id);
        return suspenso != null;
    }
}
