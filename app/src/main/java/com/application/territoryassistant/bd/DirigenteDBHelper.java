package com.application.territoryassistant.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.application.territoryassistant.dirigentes.vo.DirigentesVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moises on 30/12/15.
 */
public class DirigenteDBHelper extends DBHelper {

    public static final String TAB_DIRIGENTES = "DIRIGENTES";

    public DirigenteDBHelper(Context context){
        super(context);
    }

    public boolean gravarDirigente(String nome, String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NOME", nome);
        contentValues.put("EMAIL", email);
        db.insert(TAB_DIRIGENTES, null, contentValues);
        return true;
    }

    public List<DirigentesVO> buscarDirigentes() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(true, TAB_DIRIGENTES, new String[]{"ID", "NOME", "EMAIL"}, null, null, null, null, null, null);

        List<DirigentesVO> dirigentes = new ArrayList<DirigentesVO>();

        while (cursor.moveToNext()){
            Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            String nome = cursor.getString(cursor.getColumnIndexOrThrow("NOME"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("EMAIL"));
            dirigentes.add(new DirigentesVO(id, nome, email));
        }

        return dirigentes;
    }

    public boolean deletarDirigente(Integer id){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TAB_DIRIGENTES, "ID=?", new String [] {id.toString()});

        return true;

    }

    public DirigentesVO buscarDirigente(Integer id) {

        SQLiteDatabase db = this.getWritableDatabase();

        //query (boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
        Cursor cursor = db.query(true, TAB_DIRIGENTES, new String[]{"ID", "NOME", "EMAIL"}, "ID=?", new String[] {id.toString()}, null, null, null, null);

        DirigentesVO vo = null;

        if (cursor.moveToFirst()) {
            Integer i = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            String nome = cursor.getString(cursor.getColumnIndexOrThrow("NOME"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("EMAIL"));
            vo = new DirigentesVO(i, nome, email);
        }

        return vo;
    }

    public DirigentesVO atualizarDirigente(DirigentesVO vo) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues v = new ContentValues();
        v.put("NOME", vo.getNome());
        v.put("EMAIL", vo.getEmail());

        db.update(TAB_DIRIGENTES, v, "ID=?", new String[]{vo.getId().toString()});

        return vo;

    }

    public List<DirigentesVO> buscarDirigentesPorId(Integer ... ids){

        if (ids == null){
            return new ArrayList<>();
        }

        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder args = new StringBuilder();
        List<String> content = new ArrayList<>();

        for (int i = 0; i < ids.length; i++) {
            args.append("?");
            content.add(ids[i].toString());

            if (i != (ids.length - 1)){
                args.append(",");
            }
        }

        Cursor cursor = db.query(true, TAB_DIRIGENTES, new String[]{"ID", "NOME", "EMAIL"}, "ID in (" + args.toString() + ")", content.toArray(new String[]{}), null, null, null, null);

        DirigentesVO vo = null;
        List<DirigentesVO> dirigentesVOs = new ArrayList<>();

        while (cursor.moveToNext()) {
            Integer i = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            String nome = cursor.getString(cursor.getColumnIndexOrThrow("NOME"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("EMAIL"));
            vo = new DirigentesVO(i, nome, email);
            dirigentesVOs.add(vo);
        }

        return dirigentesVOs;

    }

    public boolean possuiDirigentesCadastrado() {

        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder builder = new StringBuilder();
        builder.append("select count(ID) as COUNT from ").append(TAB_DIRIGENTES);

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
}
