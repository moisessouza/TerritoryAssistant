package com.application.territoryassistant.bd;

import android.content.Context;

import com.application.territoryassistant.bd.room.AppDatabase;
import com.application.territoryassistant.bd.room.ConfiguracoesDao;
import com.application.territoryassistant.bd.room.ConfiguracoesEntity;

public class ConfiguracoesDBHelper extends DBHelper {

    public static final String TAB_CONFIGURACOES = "CONFIGURACOES";
    private final ConfiguracoesDao dao;

    public ConfiguracoesDBHelper(Context context) {
        super(context);
        this.dao = AppDatabase.getInstance(context).configuracoesDao();
    }

    public void atualizarTextoDirigente(String texto) {
        ensureDefaultConfig();
        dao.updateTextoDirigente(texto);
    }

    public void atualizarNumDiasDescanso(Integer numDias) {
        ensureDefaultConfig();
        dao.updateNumDiasEspera(numDias != null ? numDias : 15);
    }

    public String buscarTextoDirigente(){
        ConfiguracoesEntity config = dao.getConfig();
        if (config != null && config.getTextoPadraoDirigenteTerritorio() != null) {
            return config.getTextoPadraoDirigenteTerritorio();
        }
        return "";
    }

    public Integer buscarNumDiasEsperaTerritorio(){
        ConfiguracoesEntity config = dao.getConfig();
        if (config != null && config.getNumDiasEsperaTerritorio() != null) {
            return config.getNumDiasEsperaTerritorio();
        }
        return 15;
    }

    private void ensureDefaultConfig() {
        if (dao.getConfig() == null) {
            dao.insertOrUpdate(new ConfiguracoesEntity(1, "Texto padrão", 15));
        }
    }
}
