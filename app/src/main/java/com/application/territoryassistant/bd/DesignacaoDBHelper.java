package com.application.territoryassistant.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.application.territoryassistant.designar.vo.DesignacaoVO;
import com.application.territoryassistant.territorios.vo.TerritorioVO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by moises on 03/01/16.
 */
public class DesignacaoDBHelper extends DBHelper {

    public static final String TAB_DESIGNACAO = "DESIGNACAO";

    public DesignacaoDBHelper(Context context){
        super(context);
    }

    public void gravarDesignacoes(List<DesignacaoVO> designacaoVOs) {

        if (designacaoVOs != null && !designacaoVOs.isEmpty()) {

            SQLiteDatabase db = this.getWritableDatabase();

            for (DesignacaoVO vo : designacaoVOs) {

                ContentValues contentValues = new ContentValues();
                contentValues.put("ID_TERRITORIO", vo.getIdTerritorio());
                contentValues.put("ID_DIRIGENTE", vo.getIdDirigente());
                contentValues.put("TIPO", vo.getTipo());
                contentValues.put("DATA_INICIO", vo.getDataInicio());
                contentValues.put("DATA_FIM", vo.getDataFim());

                long insert = db.insert(TAB_DESIGNACAO, null, contentValues);
                if (insert != -1){
                    vo.setId(Long.valueOf(insert).intValue());
                }

            }
        }

    }

    //public DesignacaoVO buscarDesignacoesEmAberto(Integer idTerritorio){
    public DesignacaoVO buscarDesignacao(Integer idDesignacao){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(true, TAB_DESIGNACAO, new String[]{"ID", "ID_TERRITORIO", "ID_DIRIGENTE", "TIPO", "DATA_INICIO", "DATA_FIM"},
                "ID=?", new String[]{idDesignacao.toString()}, null, null, null, null);

        DesignacaoVO vo = null;

        if(cursor.moveToFirst()){
            Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            Integer idTer = cursor.getInt(cursor.getColumnIndexOrThrow("ID_TERRITORIO"));
            Integer idDirigente = cursor.getInt(cursor.getColumnIndexOrThrow("ID_DIRIGENTE"));
            String tipo = cursor.getString(cursor.getColumnIndexOrThrow("TIPO"));
            Long dataInicio = cursor.getLong(cursor.getColumnIndexOrThrow("DATA_INICIO"));
            Long dataFim = cursor.getLong(cursor.getColumnIndexOrThrow("DATA_FIM"));
            vo = new DesignacaoVO(id, idTer, idDirigente, tipo, dataInicio, dataFim);
        }

        return  vo;

    }

    public boolean existeDesignacaoAberto(Integer idTerritorio){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TAB_DESIGNACAO, new String[]{"ID"}, "DATA_FIM is null and ID_TERRITORIO=?", new String[] {idTerritorio.toString()}, null, null, null);
        if(cursor.moveToFirst()){
            return true;
        }

        return false;

    }

    public List<DesignacaoVO> buscarDesignacoesEmAberto(){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(true, TAB_DESIGNACAO, new String[]{"ID", "ID_TERRITORIO", "ID_DIRIGENTE", "TIPO", "DATA_INICIO", "DATA_FIM"}, "DATA_FIM is null",
                null, null, null, null, null);

        List<DesignacaoVO> designacaoVOs = new ArrayList<>();

        while(cursor.moveToNext()){
            Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            Integer idTerritorio = cursor.getInt(cursor.getColumnIndexOrThrow("ID_TERRITORIO"));
            Integer idDirigente = cursor.getInt(cursor.getColumnIndexOrThrow("ID_DIRIGENTE"));
            String tipo = cursor.getString(cursor.getColumnIndexOrThrow("TIPO"));
            Long dataInicio = cursor.getLong(cursor.getColumnIndexOrThrow("DATA_INICIO"));
            Long dataFim = cursor.getLong(cursor.getColumnIndexOrThrow("DATA_FIM"));

            designacaoVOs.add(new DesignacaoVO(id, idTerritorio, idDirigente, tipo, dataInicio, dataFim));
        }

        return  designacaoVOs;
    }

    public DesignacaoVO atualizarDesignacao(DesignacaoVO vo) {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues v = new ContentValues();
            v.put("ID_TERRITORIO", vo.getIdTerritorio());
            v.put("ID_DIRIGENTE", vo.getIdDirigente());
            v.put("TIPO", vo.getTipo());
            v.put("DATA_INICIO", vo.getDataInicio());
            v.put("DATA_FIM", vo.getDataFim());

            db.update(TAB_DESIGNACAO, v, "ID=?", new String[]{vo.getId().toString()});

            return vo;
    }

    public boolean deletarDesignacao(Integer idDesignacao) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TAB_DESIGNACAO, "ID=?", new String[]{idDesignacao.toString()});
        return true;
    }

    public List<DesignacaoVO> buscarDesignacoesTerritorioAberto(String cod) {
        return buscarDesignacoesTerritorioAberto(cod, false);
    }

    public List<DesignacaoVO> buscarDesignacoesTerritorioAberto(String codOrNome, boolean incluirNome) {

        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder builder = new StringBuilder();

        builder.append("select ");
        builder.append(TAB_DESIGNACAO).append(".ID as ID,");
        builder.append(TAB_DESIGNACAO).append(".DATA_INICIO as DATA_INICIO,");
        builder.append(TAB_DESIGNACAO).append(".ID_DIRIGENTE as ID_DIRIGENTE,");
        builder.append(TAB_DESIGNACAO).append(".ID_TERRITORIO as ID_TERRITORIO,");
        builder.append(TAB_DESIGNACAO).append(".MARCADO as MARCADO,");
        builder.append(TerritorioDBHelper.TAB_TERRITORIO).append(".COD as COD,");
        builder.append(TerritorioDBHelper.TAB_TERRITORIO).append(".FOTO_PATH as FOTO_PATH,");
        builder.append(DirigenteDBHelper.TAB_DIRIGENTES).append(".NOME as NOME");
        builder.append(" from ").append(TAB_DESIGNACAO);
        builder.append(" inner join ").append(TerritorioDBHelper.TAB_TERRITORIO).append(" on ").append(TAB_DESIGNACAO).append(".ID_TERRITORIO=").append(TerritorioDBHelper.TAB_TERRITORIO).append(".ID");
        builder.append(" inner join ").append(DirigenteDBHelper.TAB_DIRIGENTES).append(" on ").append(TAB_DESIGNACAO).append(".ID_DIRIGENTE=").append(DirigenteDBHelper.TAB_DIRIGENTES).append(".ID");
        builder.append(" where ").append(TAB_DESIGNACAO).append(".DATA_FIM is null ");

        if (codOrNome != null && !codOrNome.isEmpty()){
            builder.append(" and ");
            if (incluirNome){
                builder.append("(").append(TerritorioDBHelper.TAB_TERRITORIO).append(".COD like ? ");
                builder.append(" or ").append(DirigenteDBHelper.TAB_DIRIGENTES).append(".NOME like ? ").append(")");
            } else {
                builder.append(TerritorioDBHelper.TAB_TERRITORIO).append(".COD like ? ");
            }
        }

        builder.append("order by ").append(TAB_DESIGNACAO).append(".DATA_INICIO");

        Cursor cursor = null;
        if (codOrNome != null && !codOrNome.isEmpty()) {
            if (!incluirNome) {
                cursor = db.rawQuery(builder.toString(), new String[]{"%" + codOrNome + "%"});
            } else {
                cursor = db.rawQuery(builder.toString(), new String[]{"%" + codOrNome + "%", "%" + codOrNome + "%"});
            }
        } else {
            cursor = db.rawQuery(builder.toString(), null);
        }

        List<DesignacaoVO> designacaoVOs = new ArrayList<>();

        DateFormat df = DateFormat.getDateInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());

        Calendar c;
        Date date;

        while(cursor.moveToNext()){
            Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            Integer idDirigente = cursor.getInt(cursor.getColumnIndexOrThrow("ID_DIRIGENTE"));
            Integer idTerritorio = cursor.getInt(cursor.getColumnIndexOrThrow("ID_TERRITORIO"));
            String codTerritorio = cursor.getString(cursor.getColumnIndexOrThrow("COD"));
            String nomeDirigente = cursor.getString(cursor.getColumnIndexOrThrow("NOME"));
            Long dataInicio = cursor.getLong(cursor.getColumnIndexOrThrow("DATA_INICIO"));
            String fotoPath = cursor.getString(cursor.getColumnIndexOrThrow("FOTO_PATH"));
            Integer marcado = cursor.getInt(cursor.getColumnIndexOrThrow("MARCADO"));

            c = Calendar.getInstance();
            c.setTimeInMillis(dataInicio);
            date = c.getTime();

            String data = df.format(date);
            String diaSemana = sdf.format(date);

            designacaoVOs.add(new DesignacaoVO(id, idDirigente, idTerritorio, nomeDirigente, codTerritorio, data, fotoPath, marcado, diaSemana));
        }


        return designacaoVOs;
    }

    public boolean dirigenteJaDesignado(Integer id) {

        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder builder = new StringBuilder();

        builder.append("select count(ID_DIRIGENTE) as COUNT from ").append(TAB_DESIGNACAO);
        builder.append(" where ID_DIRIGENTE=?");

        Cursor cursor = db.rawQuery(builder.toString(), new String[]{id.toString()});

        if (cursor.moveToFirst()){
            Long count = cursor.getLong(cursor.getColumnIndexOrThrow("COUNT"));
            if (count > 0){
                return true;
            } else{
                return false;
            }
        } else {
            return false;
        }

    }

    public boolean possuiDesignacao(Integer id) {

        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder builder = new StringBuilder();

        builder.append("select count(ID_TERRITORIO) as COUNT from ").append(TAB_DESIGNACAO);
        builder.append(" where ID_TERRITORIO=?");

        Cursor cursor = db.rawQuery(builder.toString(), new String[]{id.toString()});

        if (cursor.moveToFirst()){
            Long count = cursor.getLong(cursor.getColumnIndexOrThrow("COUNT"));
            if (count > 0){
                return true;
            } else{
                return false;
            }
        } else {
            return false;
        }

    }

    public List<DesignacaoVO> buscarDesignacaoPorIdTerritorio(Integer id) {

        StringBuilder builder = new StringBuilder();

        builder.append("select DESIGNACAO.ID as ID, DIRIGENTES.NOME as NOME, DESIGNACAO.TIPO as TIPO, DESIGNACAO.DATA_INICIO as DATA_INICIO, DESIGNACAO.DATA_FIM as DATA_FIM ");
        builder.append(" from ").append(TAB_DESIGNACAO);
        builder.append(" inner join ").append(DirigenteDBHelper.TAB_DIRIGENTES).append(" on ").append(DirigenteDBHelper.TAB_DIRIGENTES).append(".ID=");
        builder.append(TAB_DESIGNACAO).append(".ID_DIRIGENTE");
        builder.append(" where ").append(TAB_DESIGNACAO).append(".ID_TERRITORIO=?");
        builder.append(" order by DESIGNACAO.DATA_INICIO, DESIGNACAO.DATA_FIM");

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(builder.toString(), new String[]{id.toString()});

        List<DesignacaoVO> designacaoVOs = new ArrayList<>();

        while (cursor.moveToNext()){

            Integer idDesignacao = cursor.getInt(cursor.getColumnIndex("ID"));
            String nome = cursor.getString(cursor.getColumnIndexOrThrow("NOME"));
            String tipo = cursor.getString(cursor.getColumnIndexOrThrow("TIPO"));
            Long dataInicio = cursor.getLong(cursor.getColumnIndexOrThrow("DATA_INICIO"));
            Long dataFim = cursor.getLong(cursor.getColumnIndexOrThrow("DATA_FIM"));

            designacaoVOs.add(new DesignacaoVO(idDesignacao, nome, tipo, dataInicio, dataFim == 0 ? null : dataFim));

        }


        return designacaoVOs;
    }

    public void marcarRegistro(DesignacaoVO vo) {
        ContentValues values = new ContentValues();
        values.put("MARCADO", 1);
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TAB_DESIGNACAO, values, "ID=?", new String[]{vo.getId().toString()});
    }

    public void desmarcarRegistro(DesignacaoVO vo) {
        ContentValues values = new ContentValues();
        values.put("MARCADO", 0);
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TAB_DESIGNACAO, values, "ID=?", new String[]{vo.getId().toString()});
    }

}
