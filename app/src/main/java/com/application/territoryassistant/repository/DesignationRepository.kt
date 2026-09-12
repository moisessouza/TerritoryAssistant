package com.application.territoryassistant.repository

import com.application.territoryassistant.bd.room.DesignacaoDao
import com.application.territoryassistant.bd.room.DesignacaoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DesignationRepository(private val designacaoDao: DesignacaoDao) {

    suspend fun getDesignationById(id: Int): DesignacaoEntity? = withContext(Dispatchers.IO) {
        designacaoDao.getById(id)
    }

    suspend fun getOpenDesignations(): List<DesignacaoEntity> = withContext(Dispatchers.IO) {
        designacaoDao.getEmAberto()
    }

    suspend fun isTerritoryAssigned(territoryId: Int): Boolean = withContext(Dispatchers.IO) {
        designacaoDao.getAbertoByTerritorio(territoryId) != null
    }

    suspend fun isLeaderAssigned(leaderId: Int): Boolean = withContext(Dispatchers.IO) {
        designacaoDao.countByDirigente(leaderId) > 0
    }

    suspend fun hasDesignationsForTerritory(territoryId: Int): Boolean = withContext(Dispatchers.IO) {
        designacaoDao.countByTerritorio(territoryId) > 0
    }

    suspend fun insertDesignation(designation: DesignacaoEntity): Long = withContext(Dispatchers.IO) {
        designacaoDao.insert(designation)
    }

    suspend fun updateDesignation(designation: DesignacaoEntity): Int = withContext(Dispatchers.IO) {
        designacaoDao.update(designation)
    }

    suspend fun deleteDesignation(id: Int): Int = withContext(Dispatchers.IO) {
        designacaoDao.delete(id)
    }

    suspend fun setMarked(id: Int, marked: Boolean) = withContext(Dispatchers.IO) {
        designacaoDao.updateMarcado(id, if (marked) 1 else 0)
    }
}
