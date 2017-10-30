package com.application.territoryassistant.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.application.territoryassistant.grupos.vo.GrupoVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moises on 30/12/15.
 */
public class GrupoDBHelper extends DBHelper {

    public static final String TAB_GRUPO = "GRUPO";

    public GrupoDBHelper(Context context){
        super(context);
    }

    public boolean gravarGrupo(String cod){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NOME", cod);
        db.insert(TAB_GRUPO, null, contentValues);
        return true;
    }

    public List<GrupoVO> buscarGrupos() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(true, TAB_GRUPO, new String[]{"ID", "NOME"}, null, null, null, null, "NOME"+ " ASC", null);

        List<GrupoVO> grupoVOs = new ArrayList<GrupoVO>();

        while (cursor.moveToNext()){
            Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            String nome = cursor.getString(cursor.getColumnIndexOrThrow("NOME"));
            grupoVOs.add(new GrupoVO(id, nome));
        }

        return grupoVOs;
    }

    public boolean deletarGrupo(Integer id){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TAB_GRUPO, "ID=?", new String [] {id.toString()});

        return true;

    }

    public GrupoVO buscarGrupo(Integer id) {

        SQLiteDatabase db = this.getWritableDatabase();

        //query (boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
        Cursor cursor = db.query(true, TAB_GRUPO, new String[]{"ID", "NOME"}, "ID=?", new String[] {id.toString()}, null, null, "NOME"+ " ASC", null);

        GrupoVO vo = null;

        if (cursor.moveToFirst()) {
            Integer i = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            String nome = cursor.getString(cursor.getColumnIndexOrThrow("NOME"));
            vo = new GrupoVO(i, nome);
        }

        return vo;
    }

    public GrupoVO atualizarGrupo(GrupoVO vo) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues v = new ContentValues();
        v.put("NOME", vo.getNome());

        db.update(TAB_GRUPO, v, "ID=?", new String[]{vo.getId().toString()});

        return vo;

    }

    public boolean possuiGrupo() {

        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder builder = new StringBuilder();
        builder.append("select count(ID) as COUNT from ").append(TAB_GRUPO);

        Cursor cursor = db.rawQuery(builder.toString(), null);

        if(cursor.moveToFirst()){

            Long count = cursor.getLong(cursor.getColumnIndexOrThrow("COUNT"));
            if (count > 0){
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }

    }

    public boolean possuiTerritorio (Integer id){
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder builder = new StringBuilder();
        builder.append("select count(ID) as COUNT from ").append(TerritorioDBHelper.TAB_TERRITORIO);
        builder.append(" where ID_GRUPO=?");

        Cursor cursor = db.rawQuery(builder.toString(), new String[]{id.toString()});

        if(cursor.moveToFirst()){
            Long count = cursor.getLong(cursor.getColumnIndexOrThrow("COUNT"));
            if (count > 0){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }
}
