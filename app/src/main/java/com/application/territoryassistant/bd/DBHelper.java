package com.application.territoryassistant.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.application.territoryassistant.R;
import com.application.territoryassistant.dirigentes.vo.DirigentesVO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by moises on 30/12/15.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "territories.db";

    Context context;

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, 4);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        StringBuilder q = new StringBuilder();
        q.append("create table DIRIGENTES( ");
        q.append("ID integer primary key,");
        q.append("NOME text not null,");
        q.append("EMAIL text);");
        db.execSQL(q.toString());

        q = new StringBuilder();
        q.append("create table GRUPO( ");
        q.append("ID integer primary key,");
        q.append("NOME text not null);");
        db.execSQL(q.toString());

        q = new StringBuilder();
        q.append("create table TERRITORIO( ");
        q.append("ID integer primary key,");
        q.append("COD text not null,");
        q.append("ID_GRUPO integer not null,");
        q.append("ULTIMA_DATA_FIM integer,");
        q.append("SUSPENSO integer,");
        q.append("OBSERVACOES TEXT,");
        q.append("FOTO_PATH TEXT,");
        q.append("FOREIGN KEY(ID_GRUPO) REFERENCES GRUPO(ID));");
        db.execSQL(q.toString());

        q = new StringBuilder();

        q.append("create table TERRITORIO_VIZINHO( ");
        q.append("ID integer primary key,");
        q.append("ID_TERRITORIO integer not null,");
        q.append("ID_VIZINHO integer not null,");
        q.append("FOREIGN KEY(ID_TERRITORIO) REFERENCES TERRITORIO(ID),");
        q.append("FOREIGN KEY(ID_VIZINHO) REFERENCES TERRITORIO(ID)");
        q.append(");");
        db.execSQL(q.toString());

        q = new StringBuilder();

        q.append("create table DESIGNACAO( ");
        q.append("ID integer primary key,");
        q.append("ID_TERRITORIO integer not null,");
        q.append("ID_DIRIGENTE integer not null,");
        q.append("TIPO text(1) not null,");
        q.append("DATA_INICIO integer not null,");
        q.append("DATA_FIM integer,");
        q.append("MARCADO integer,");
        q.append("FOREIGN KEY(ID_DIRIGENTE) REFERENCES DIRIGENTES(ID),");
        q.append("FOREIGN KEY(ID_TERRITORIO) REFERENCES TERRITORIO(ID)");
        q.append(");");
        db.execSQL(q.toString());

        q = new StringBuilder();

        q.append("create table ULTIMA_ACOES( ");
        q.append("ID integer primary key,");
        q.append("COD_ACAO text(1) not null,");
        q.append("COD_TERRITORIOS text(1) not null,");
        q.append("ID_DIRIGENTE integer not null,");
        q.append("DATA_INICIO integer,");
        q.append("DATA_FIM integer,");
        q.append("FOREIGN KEY(ID_DIRIGENTE) REFERENCES DIRIGENTES(ID)");
        q.append(");");
        db.execSQL(q.toString());

        q = new StringBuilder();

        q.append("create table CONFIGURACOES( ");
        q.append("ID integer primary key,");
        q.append("TEXTO_PADRAO_DIRIGENTE_TERRITORIO text, ");
        q.append("NUM_DIAS_ESPERA_TERRITORIO integer");
        q.append(");");
        db.execSQL(q.toString());

        ContentValues contentValues = new ContentValues();
        contentValues.put("TEXTO_PADRAO_DIRIGENTE_TERRITORIO", context.getString(R.string.verificar_territorios_estao_irmao));
        contentValues.put("NUM_DIAS_ESPERA_TERRITORIO", 15);

        db.insert("CONFIGURACOES", null, contentValues);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2){
            db.execSQL("alter table TERRITORIO add COLUMN OBSERVACOES TEXT");
            db.execSQL("alter table TERRITORIO add COLUMN FOTO_PATH TEXT");
        }

        if (oldVersion < 3) {
            StringBuilder q = new StringBuilder();

            q.append("create table CONFIGURACOES( ");
            q.append("ID integer primary key,");
            q.append("TEXTO_PADRAO_DIRIGENTE_TERRITORIO text, ");
            q.append("NUM_DIAS_ESPERA_TERRITORIO integer");
            q.append(");");
            db.execSQL(q.toString());

            ContentValues contentValues = new ContentValues();
            contentValues.put("TEXTO_PADRAO_DIRIGENTE_TERRITORIO", context.getString(R.string.verificar_territorios_estao_irmao));
            contentValues.put("NUM_DIAS_ESPERA_TERRITORIO", 15);

            db.insert("CONFIGURACOES", null, contentValues);
            
        }

        if (oldVersion < 4) {
            StringBuilder q = new StringBuilder();
            q.append("alter table DESIGNACAO add column MARCADO integer");
            db.execSQL(q.toString());
        }

    }

    String DB_FILEPATH = "/data/data/com.application.territoryassistant/databases/territories.db";

    /**
     * Copies the database file at the specified location over the current
     * internal application database.
     * */
    public void importDatabase(InputStream in) throws IOException {

        // Close the SQLiteOpenHelper so it will commit the created empty
        // database to internal storage.
        close();
        File oldDb = new File(DB_FILEPATH);

        copy(in, oldDb);
        // Access the copied database so SQLiteHelper will cache it and mark
        // it as created.
        getWritableDatabase().close();

    }

    public static void copy(File src, File dst) {

        InputStream in = null;
        OutputStream out = null;

        try {

            in = new FileInputStream(src);
            out = new FileOutputStream(dst);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            if (in != null)
                in.close();
            if (out != null)
                out.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void copy (InputStream in, File dest) {
        try {

            OutputStream out = new FileOutputStream(dest);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            if (in != null)
                in.close();
            if (out != null)
                out.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
