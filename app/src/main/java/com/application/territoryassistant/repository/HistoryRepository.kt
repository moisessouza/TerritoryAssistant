package com.application.territoryassistant.repository

import com.application.territoryassistant.bd.room.UltimaAcoesDao
import com.application.territoryassistant.bd.room.UltimaAcoesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HistoryRepository(private val ultimaAcoesDao: UltimaAcoesDao) {

    suspend fun insertAction(action: UltimaAcoesEntity): Long = withContext(Dispatchers.IO) {
        ultimaAcoesDao.insert(action)
    }

    suspend fun getRecentActions(limit: Int): List<UltimaAcoesEntity> = withContext(Dispatchers.IO) {
        ultimaAcoesDao.getRecent(limit)
    }
}
