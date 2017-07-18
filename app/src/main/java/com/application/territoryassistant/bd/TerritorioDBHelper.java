package com.application.territoryassistant.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.application.territoryassistant.dirigentes.vo.DirigentesVO;
import com.application.territoryassistant.territorios.vo.TerritorioVO;
import com.application.territoryassistant.territorios.vo.TerritorioVizinhoVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by moises on 30/12/15.
 */
public class TerritorioDBHelper extends DBHelper {

    public static final String TAB_TERRITORIO = "TERRITORIO";
    public static final String TAB_TERRITORIO_VIZINHO = "TERRITORIO_VIZINHO";

    public TerritorioDBHelper(Context context){
        super(context);
    }

    public long gravarTerritorio(String cod, Integer idGrupo, String observacoes, String fotoPath, Boolean suspenso){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("COD", cod);
        contentValues.put("ID_GRUPO", idGrupo);
        contentValues.put("SUSPENSO", suspenso);
        contentValues.put("OBSERVACOES", observacoes);
        contentValues.put("FOTO_PATH", fotoPath);

        return db.insert(TAB_TERRITORIO, null, contentValues);

    }

    public List<TerritorioVO> buscarTerritoriosNaoDesignados(){
        return buscarTerritoriosNaoDesignados(false, null);
    }

    public List<TerritorioVO> buscarTerritoriosNaoDesignados(boolean ordenarData, Integer idGrupo){

        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder builder = new StringBuilder();

        builder.append("select TERRITORIO.ID as ID, TERRITORIO.COD as COD, TERRITORIO.ID_GRUPO as ID_GRUPO, TERRITORIO.ULTIMA_DATA_FIM as ULTIMA_DATA_FIM, TERRITORIO.SUSPENSO as SUSPENSO, TERRITORIO.FOTO_PATH as FOTO_PATH ");
        builder.append(" from ").append(TAB_TERRITORIO);
        builder.append(" where TERRITORIO.ID not in (");
            builder.append(" select DESIGNACAO.ID_TERRITORIO from ").append(DesignacaoDBHelper.TAB_DESIGNACAO);
            builder.append(" where ").append(DesignacaoDBHelper.TAB_DESIGNACAO).append(".DATA_FIM IS NULL");
        builder.append(") and TERRITORIO.SUSPENSO=0 ");

        if (idGrupo != null){
            builder.append(" and ID_GRUPO=").append(idGrupo);
        }

        if (ordenarData){
            builder.append(" order by ULTIMA_DATA_FIM ");
        }

        Cursor cursor = db.rawQuery(builder.toString(), null);

        List<Integer> ids = new ArrayList<>();
        List<TerritorioVO> territorioVOs = new ArrayList<TerritorioVO>();

        while (cursor.moveToNext()){
            Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            if (!ids.contains(id)) {
                String cod = cursor.getString(cursor.getColumnIndexOrThrow("COD"));
                Integer grupo = cursor.getInt(cursor.getColumnIndexOrThrow("ID_GRUPO"));
                Long ultimaDataFim = cursor.getLong(cursor.getColumnIndexOrThrow("ULTIMA_DATA_FIM"));
                Long suspenso = cursor.getLong(cursor.getColumnIndexOrThrow("SUSPENSO"));
                String fotoPath = cursor.getString(cursor.getColumnIndexOrThrow("FOTO_PATH"));
                ids.add(id);
                territorioVOs.add(new TerritorioVO(id, cod, fotoPath, grupo, ultimaDataFim, suspenso == 0 ? false : true));
            }
        }

        return territorioVOs;
    }

    public List<TerritorioVO> buscarTerritorios() {
        return buscarTerritorios(null, false, null);
    }

    public List<TerritorioVO> buscarTerritorios(Integer notId, boolean ordenarData, Integer idGrupo) {

        SQLiteDatabase db = this.getWritableDatabase();

        String selection = "1=1";
        List<String> args = new ArrayList<>();

        if (notId != null){
            selection += " and ID not in (?)";
            args.add(notId.toString());
        }

        if (idGrupo != null){
            selection += " and ID_GRUPO=?";
            args.add(idGrupo.toString());
        }

        String orderBy = null;
        if (ordenarData){
            orderBy = "ULTIMA_DATA_FIM";
        }

        Cursor cursor = db.query(true, TAB_TERRITORIO, new String[]{"ID", "COD", "ID_GRUPO", "FOTO_PATH", "SUSPENSO", "ULTIMA_DATA_FIM"}, selection,
                !args.isEmpty() ? args.toArray(new String [] {}) : null, null, null, orderBy, null);

        List<TerritorioVO> territorioVOs = new ArrayList<TerritorioVO>();

        while (cursor.moveToNext()){
            Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            String cod = cursor.getString(cursor.getColumnIndexOrThrow("COD"));
            Integer grupo = cursor.getInt(cursor.getColumnIndexOrThrow("ID_GRUPO"));
            Long ultimaDataFim = cursor.getLong(cursor.getColumnIndexOrThrow("ULTIMA_DATA_FIM"));
            Long suspenso = cursor.getLong(cursor.getColumnIndexOrThrow("SUSPENSO"));
            String fotoPath = cursor.getString(cursor.getColumnIndexOrThrow("FOTO_PATH"));

            territorioVOs.add(new TerritorioVO(id, cod, fotoPath, grupo, ultimaDataFim, suspenso == 0 ? false : true));
        }

        return territorioVOs;
    }

    public boolean deletarTerritorio(Integer id){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TAB_TERRITORIO, "ID=?", new String [] {id.toString()});

        return true;

    }

    public List<TerritorioVO> buscarTerritoriosPorCod(String cods){

        String[] query = cods.split(",");

        StringBuilder selection = new StringBuilder();
        List<String> args = new ArrayList<>();

        boolean isFirst = true;

        if (query.length > 0){
            for (String q: query) {
                if (!isFirst){
                    selection.append(" or ");
                } else {
                    isFirst = false;
                }
                selection.append(" COD=? ");
                args.add(q);
            }
        }


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, TAB_TERRITORIO, new String[]{"ID", "COD", "ID_GRUPO", "OBSERVACOES", "FOTO_PATH", "SUSPENSO", "ULTIMA_DATA_FIM"}, selection.toString(),
                args.toArray(new String[]{}), null, null, null, null);

        List<TerritorioVO> territorioVOs = new ArrayList<>();

        while (cursor.moveToNext()){
            Integer i = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            String cod = cursor.getString(cursor.getColumnIndexOrThrow("COD"));
            Integer idGrupo = cursor.getInt(cursor.getColumnIndexOrThrow("ID_GRUPO"));
            Long ultimaDataFim = cursor.getLong(cursor.getColumnIndexOrThrow("ULTIMA_DATA_FIM"));
            Long suspenso = cursor.getLong(cursor.getColumnIndexOrThrow("SUSPENSO"));
            String observacoes = cursor.getString(cursor.getColumnIndexOrThrow("OBSERVACOES"));
            String fotoPath = cursor.getString(cursor.getColumnIndexOrThrow("FOTO_PATH"));
            TerritorioVO vo = new TerritorioVO(i, cod, observacoes, fotoPath, idGrupo, ultimaDataFim, suspenso == 0 ? false : true);
            territorioVOs.add(vo);
        }

        return territorioVOs;

    }

    public TerritorioVO buscarTerritorioPorCod(String cod, Integer ... notIds) {

        if (cod == null) {
            return null;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        String selection = "COD=?";
        List<String> args = new ArrayList<>();

        args.add(cod);

        if (notIds != null && !Arrays.asList(notIds).isEmpty()){

            selection += " and ID not in (?)";
            String arg = "";

            boolean isFirst = true;

            for (Integer notId: notIds) {

                if (isFirst){
                    arg+=notId;
                    isFirst = false;
                } else {
                    arg+="," + notId;
                }

            }

            args.add(arg);

        }

        Cursor cursor = db.query(true, TAB_TERRITORIO, new String[]{"ID", "COD", "ID_GRUPO", "FOTO_PATH", "SUSPENSO", "ULTIMA_DATA_FIM"}, selection, args.toArray(new String[]{}), null, null, null, null);

        TerritorioVO territorioVO = null;

        if (cursor.moveToFirst()){
            Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            String c = cursor.getString(cursor.getColumnIndexOrThrow("COD"));
            Integer idGrupo = cursor.getInt(cursor.getColumnIndexOrThrow("ID_GRUPO"));
            Long ultimaDataFim = cursor.getLong(cursor.getColumnIndexOrThrow("ULTIMA_DATA_FIM"));
            Long suspenso = cursor.getLong(cursor.getColumnIndexOrThrow("SUSPENSO"));
            String fotoPath = cursor.getString(cursor.getColumnIndexOrThrow("FOTO_PATH"));

            territorioVO = new TerritorioVO(id, cod, fotoPath, idGrupo, ultimaDataFim, suspenso == 0 ? false : true);
        }

        return territorioVO;

    }

    public TerritorioVO buscarTerritorio(Integer id) {

        SQLiteDatabase db = this.getWritableDatabase();

        //query (boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
        Cursor cursor = db.query(true, TAB_TERRITORIO, new String[]{"ID", "COD", "ID_GRUPO",  "OBSERVACOES", "FOTO_PATH", "SUSPENSO", "ULTIMA_DATA_FIM"}, "ID=?", new String[] {id.toString()}, null, null, null, null);

        TerritorioVO vo = null;

        if (cursor.moveToFirst()) {
            Integer i = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            String cod = cursor.getString(cursor.getColumnIndexOrThrow("COD"));
            Integer idGrupo = cursor.getInt(cursor.getColumnIndexOrThrow("ID_GRUPO"));
            Long ultimaDataFim = cursor.getLong(cursor.getColumnIndexOrThrow("ULTIMA_DATA_FIM"));
            Long suspenso = cursor.getLong(cursor.getColumnIndexOrThrow("SUSPENSO"));
            String observacoes = cursor.getString(cursor.getColumnIndexOrThrow("OBSERVACOES"));
            String fotoPath = cursor.getString(cursor.getColumnIndexOrThrow("FOTO_PATH"));
            vo = new TerritorioVO(i, cod, observacoes, fotoPath, idGrupo, ultimaDataFim, suspenso == 0 ? false : true);
        }

        return vo;
    }

    public TerritorioVO atualizarTerritorio(TerritorioVO vo) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues v = new ContentValues();
        v.put("COD", vo.getCod());
        v.put("ID_GRUPO", vo.getIdGrupo());
        v.put("ULTIMA_DATA_FIM", vo.getUltimaDataFim());
        v.put("SUSPENSO", vo.getSuspenso());
        v.put("OBSERVACOES", vo.getObservacoes());
        v.put("FOTO_PATH", vo.getFotoPath());

        db.update(TAB_TERRITORIO, v, "ID=?", new String[]{vo.getId().toString()});

        return vo;

    }

    public List<TerritorioVizinhoVO> gravarVizinhos(Integer idTerritorio, List<TerritorioVO> territorioVOs) {

        if (idTerritorio != null){
            deletarVizinhos(idTerritorio);
        } else {
            return new ArrayList<>();
        }

        List<TerritorioVizinhoVO> territorioVizinhoVOs = new ArrayList<>();

        if (territorioVOs != null && !territorioVOs.isEmpty()) {
            for (TerritorioVO voSelecionado : territorioVOs) {
                TerritorioVizinhoVO territorioVizinhoVO = new TerritorioVizinhoVO(idTerritorio, voSelecionado.getId());
                territorioVizinhoVOs.add(territorioVizinhoVO);
                territorioVizinhoVO = new TerritorioVizinhoVO(voSelecionado.getId(), idTerritorio);
                territorioVizinhoVOs.add(territorioVizinhoVO);
            }
        }

        if (territorioVizinhoVOs != null && !territorioVizinhoVOs.isEmpty()) {

            SQLiteDatabase db = this.getWritableDatabase();

            for (TerritorioVizinhoVO vo : territorioVizinhoVOs) {

                Cursor cursor = db.query(true, TAB_TERRITORIO_VIZINHO, new String[]{"ID"}, "ID_TERRITORIO=? and ID_VIZINHO=?",
                        new String[] {vo.getIdTerritorio().toString(), vo.getIdVizinho().toString()}, null, null, null, null);

                if (!cursor.moveToFirst()){
                    ContentValues v = new ContentValues();
                    v.put("ID_TERRITORIO", vo.getIdTerritorio());
                    v.put("ID_VIZINHO", vo.getIdVizinho());
                    long id = db.insert(TAB_TERRITORIO_VIZINHO, null, v);
                    if (id != -1) {
                        vo.setId(Long.valueOf(id).intValue());
                    }
                }

            }

        }

        return territorioVizinhoVOs;

    }

    public boolean deletarVizinhos(Integer idTerritorio) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TAB_TERRITORIO_VIZINHO, "ID_TERRITORIO=?", new String [] {idTerritorio.toString()});
        db.delete(TAB_TERRITORIO_VIZINHO, "ID_VIZINHO=?", new String [] {idTerritorio.toString()});

        return true;
    }

    public List<TerritorioVizinhoVO> buscarVizinhos(Integer idTerritorio) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(true, TAB_TERRITORIO_VIZINHO, new String[]{"ID", "ID_TERRITORIO", "ID_VIZINHO"}, "ID_TERRITORIO=?", new String[] { idTerritorio.toString()} , null, null, null, null);

        List<TerritorioVizinhoVO> territorioVizinhoVOs = new ArrayList<>();

        while (cursor.moveToNext()){
            Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            Integer idTer = cursor.getInt(cursor.getColumnIndexOrThrow("ID_TERRITORIO"));
            Integer idVizinho = cursor.getInt(cursor.getColumnIndexOrThrow("ID_VIZINHO"));
            territorioVizinhoVOs.add(new TerritorioVizinhoVO(id, idTer, idVizinho));
        }

        return territorioVizinhoVOs;
    }

    public List<TerritorioVO> buscarTerritoriosPorId(Integer ... idsTerritorio) {

        if(idsTerritorio == null || Arrays.asList(idsTerritorio).isEmpty()){
            return new ArrayList<>();
        }

        SQLiteDatabase db = this.getWritableDatabase();

        String selection = "";
        String [] args = null;

        boolean isFirst = true;

        if (idsTerritorio != null && !Arrays.asList(idsTerritorio).isEmpty()){

            List<String> argsList = new ArrayList<>();

            for (Integer idTerritorio: idsTerritorio) {
                if (isFirst){
                    selection+="ID=?";
                    isFirst = false;
                } else {
                    selection+=" or ID=?";
                }

                argsList.add(idTerritorio.toString());
            }

            args = argsList.toArray(new String[]{});

        }

        Cursor cursor = db.query(true, TAB_TERRITORIO, new String[]{"ID", "COD", "ID_GRUPO", "FOTO_PATH", "SUSPENSO", "ULTIMA_DATA_FIM"}, selection, args, null, null, null, null);

        List<TerritorioVO> territorioVOs = new ArrayList<TerritorioVO>();

        while (cursor.moveToNext()){
            Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            String cod = cursor.getString(cursor.getColumnIndexOrThrow("COD"));
            Integer idGrupo = cursor.getInt(cursor.getColumnIndexOrThrow("ID_GRUPO"));
            Long ultimaDataFim = cursor.getLong(cursor.getColumnIndexOrThrow("ULTIMA_DATA_FIM"));
            Long suspenso = cursor.getLong(cursor.getColumnIndexOrThrow("SUSPENSO"));
            String fotoPath = cursor.getString(cursor.getColumnIndexOrThrow("FOTO_PATH"));

            territorioVOs.add(new TerritorioVO(id, cod, fotoPath, idGrupo, ultimaDataFim, suspenso == 0 ? false : true));
        }

        return territorioVOs;
    }

    public List<String> buscarTerritoriosDesignadosParaDirigente(Integer id) {

        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder builder = new StringBuilder();

        builder.append("select ").append(TAB_TERRITORIO).append(".COD");
        builder.append(" from ").append(TAB_TERRITORIO);
        builder.append(" inner join ").append(DesignacaoDBHelper.TAB_DESIGNACAO).append(" on ")
                .append(DesignacaoDBHelper.TAB_DESIGNACAO).append(".ID_TERRITORIO=").append(TAB_TERRITORIO).append(".ID");
        builder.append(" where ").append(DesignacaoDBHelper.TAB_DESIGNACAO).append(".ID_DIRIGENTE=?");
        builder.append(" and ").append(DesignacaoDBHelper.TAB_DESIGNACAO).append(".DATA_FIM is null");

        Cursor cursor = db.rawQuery(builder.toString(), new String[]{id.toString()});

        Set<String> cods = new HashSet<>();

        while (cursor.moveToNext()){
            String cod = cursor.getString(cursor.getColumnIndexOrThrow("COD"));
            cods.add(cod);
        }

        return new ArrayList<>(cods);

    }

    public TerritorioVO buscarTerritorioMaisTempoTrabalhado(Integer idGrupo, Integer[] inIdsVizinhos, Integer[] notIds, Long dataLimite){

        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder builder = new StringBuilder();

        builder.append("select TERRITORIO.ID as ID, TERRITORIO.COD as COD, TERRITORIO.ID_GRUPO as ID_GRUPO, TERRITORIO.ULTIMA_DATA_FIM as ULTIMA_DATA_FIM, TERRITORIO.SUSPENSO, TERRITORIO.FOTO_PATH as FOTO_PATH ");
        builder.append(" from ").append(TAB_TERRITORIO);

        StringBuilder where = new StringBuilder(" where ");

        boolean possuiWhere = false;

        if (idGrupo != null ||
                (inIdsVizinhos != null && inIdsVizinhos.length > 0) ||
                (notIds != null && notIds.length > 0)){

            possuiWhere = true;

            if (idGrupo != null){
                where.append(TAB_TERRITORIO).append(".ID_GRUPO=").append(idGrupo).append(" ");

                if ((inIdsVizinhos != null && inIdsVizinhos.length > 0) ||
                        (notIds != null && notIds.length > 0)){
                    where.append(" and ");
                }
            }

            if (inIdsVizinhos != null && inIdsVizinhos.length > 0){
                builder.append(" inner join ").append(TAB_TERRITORIO_VIZINHO).append(" on ").append(TAB_TERRITORIO_VIZINHO).append(".ID_TERRITORIO=")
                        .append(TAB_TERRITORIO).append(".ID ");

                where.append(TAB_TERRITORIO_VIZINHO).append(".ID_VIZINHO in (");

                boolean isFirst = true;

                for (Integer idVizinho: inIdsVizinhos) {
                    if (isFirst) {
                        where.append(idVizinho);
                        isFirst = false;
                    } else {
                        where.append(",").append(idVizinho);
                    }
                }

                where.append(") ");

                if (notIds != null){
                    where.append(" and ");
                }

            }

            if (notIds != null && notIds.length > 0) {

                where.append(TAB_TERRITORIO).append(".ID not in (");

                boolean isFirst = true;

                for (Integer notId: notIds) {
                    if (isFirst) {
                        where.append(notId);
                        isFirst = false;
                    } else {
                        where.append(",").append(notId);
                    }
                }

                where.append(")");


            }

        }

        if (possuiWhere){
            builder.append(where.toString());
            builder.append(" and TERRITORIO.SUSPENSO=0 ");
        } else {
            builder.append(" where TERRITORIO.SUSPENSO=0 ");
        }

        if (dataLimite != null) {
            builder.append(" and (ULTIMA_DATA_FIM is null or ULTIMA_DATA_FIM<=").append(dataLimite).append(") ");
        }

        builder.append("order by ULTIMA_DATA_FIM limit 1");

        Cursor cursor = db.rawQuery(builder.toString(), null);

        TerritorioVO territorioVO = null;

        if (cursor.moveToFirst()){
            Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            String cod = cursor.getString(cursor.getColumnIndexOrThrow("COD"));
            Integer idGrup = cursor.getInt(cursor.getColumnIndexOrThrow("ID_GRUPO"));
            Long ultimaDataFim = cursor.getLong(cursor.getColumnIndexOrThrow("ULTIMA_DATA_FIM"));
            Long suspenso = cursor.getLong(cursor.getColumnIndexOrThrow("SUSPENSO"));
            String fotoPath = cursor.getString(cursor.getColumnIndexOrThrow("FOTO_PATH"));

            territorioVO = new TerritorioVO(id, cod, fotoPath, idGrup, ultimaDataFim, suspenso == 0 ? false : true);
        }

        return territorioVO;


    }

    public boolean possuiGrupo(Integer id) {

        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder builder = new StringBuilder();

        builder.append("select count(ID_GRUPO) from ").append(TAB_TERRITORIO);
        builder.append(" where ID_GRUPO=?");

        Cursor cursor = db.rawQuery(builder.toString(), new String[]{id.toString()});

        if (cursor.moveToFirst()){
            long count = cursor.getLong(cursor.getColumnIndexOrThrow("ID_GRUPO"));

            if(count > 0){
                return true;
            } else {
                return false;
            }

        }else {
            return false;
        }
    }

    public boolean possuiTerritoriosCadastrado() {

        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder builder = new StringBuilder();
        builder.append("select count(ID) as COUNT from ").append(TAB_TERRITORIO);

        Cursor cursor = db.rawQuery(builder.toString(), null);
        if (cursor.moveToFirst()){

            long count = cursor.getLong(cursor.getColumnIndexOrThrow("COUNT"));
            if (count > 0){
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
    }

    public boolean territorioSuspenso(Integer id) {
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder builder = new StringBuilder();
        builder.append("select ID from ").append(TAB_TERRITORIO);
        builder.append(" where SUSPENSO=1 and ID=?");

        Cursor cursor = db.rawQuery(builder.toString(), new String[]{id.toString()});

        boolean result = false;

        if (cursor.moveToFirst()){
            result = true;
        }

        return result;
    }
}
