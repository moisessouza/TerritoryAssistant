package com.application.territoryassistant.repository

import com.application.territoryassistant.bd.room.ConfiguracoesDao
import com.application.territoryassistant.bd.room.ConfiguracoesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SettingsRepository(private val configuracoesDao: ConfiguracoesDao) {

    suspend fun getSettings(): ConfiguracoesEntity? = withContext(Dispatchers.IO) {
        configuracoesDao.getConfig()
    }

    suspend fun updateLeaderDefaultMessage(text: String) = withContext(Dispatchers.IO) {
        ensureConfig()
        configuracoesDao.updateTextoDirigente(text)
    }

    suspend fun updateTerritoryWaitDays(days: Int) = withContext(Dispatchers.IO) {
        ensureConfig()
        configuracoesDao.updateNumDiasEspera(days)
    }

    private fun ensureConfig() {
        if (configuracoesDao.getConfig() == null) {
            configuracoesDao.insertOrUpdate(ConfiguracoesEntity(1, "Texto padrão", 15))
        }
    }
}
