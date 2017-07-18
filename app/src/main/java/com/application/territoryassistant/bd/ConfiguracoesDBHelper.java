package com.application.territoryassistant.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by moi09 on 20/02/2016.
 */
public class ConfiguracoesDBHelper extends DBHelper{

     public static final String TAB_CONFIGURACOES = "CONFIGURACOES";

    public ConfiguracoesDBHelper(Context context) {
        super(context);
    }

    public void atualizarTextoDirigente(String texto) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("TEXTO_PADRAO_DIRIGENTE_TERRITORIO", texto);

        db.update(TAB_CONFIGURACOES, contentValues, "ID=?", new String[]{"1"});

    }

    public void atualizarNumDiasDescanso(Integer numDias) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("NUM_DIAS_ESPERA_TERRITORIO", numDias);

        db.update(TAB_CONFIGURACOES, contentValues, "ID=?", new String[]{"1"});

    }

    public String buscarTextoDirigente(){

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query(TAB_CONFIGURACOES, new String[]{"TEXTO_PADRAO_DIRIGENTE_TERRITORIO"}, "ID=?", new String[]{"1"}, null, null, null);

        if(c.moveToFirst()) {
            return c.getString(c.getColumnIndexOrThrow("TEXTO_PADRAO_DIRIGENTE_TERRITORIO"));
        }

        return "";

    }

    public Integer buscarNumDiasEsperaTerritorio(){

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query(TAB_CONFIGURACOES, new String[]{"NUM_DIAS_ESPERA_TERRITORIO"}, "ID=?", new String[]{"1"}, null, null, null);

        if(c.moveToFirst()) {
            return c.getInt(c.getColumnIndexOrThrow("NUM_DIAS_ESPERA_TERRITORIO"));
        }

        return 0;

    }

}
