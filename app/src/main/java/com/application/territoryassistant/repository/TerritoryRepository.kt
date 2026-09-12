package com.application.territoryassistant.repository

import com.application.territoryassistant.bd.room.TerritorioDao
import com.application.territoryassistant.bd.room.TerritorioEntity
import com.application.territoryassistant.bd.room.TerritorioVizinhoDao
import com.application.territoryassistant.bd.room.TerritorioVizinhoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TerritoryRepository(
    private val territorioDao: TerritorioDao,
    private val vizinhoDao: TerritorioVizinhoDao
) {

    suspend fun getAllTerritories(): List<TerritorioEntity> = withContext(Dispatchers.IO) {
        territorioDao.getAll()
    }

    suspend fun getTerritoryById(id: Int): TerritorioEntity? = withContext(Dispatchers.IO) {
        territorioDao.getById(id)
    }

    suspend fun getTerritoriesByIds(ids: List<Int>): List<TerritorioEntity> = withContext(Dispatchers.IO) {
        if (ids.isEmpty()) emptyList() else territorioDao.getByIds(ids)
    }

    suspend fun insertTerritory(territory: TerritorioEntity): Long = withContext(Dispatchers.IO) {
        territorioDao.insert(territory)
    }

    suspend fun updateTerritory(territory: TerritorioEntity): Int = withContext(Dispatchers.IO) {
        territorioDao.update(territory)
    }

    suspend fun deleteTerritory(id: Int): Int = withContext(Dispatchers.IO) {
        territorioDao.delete(id)
    }

    suspend fun hasTerritories(): Boolean = withContext(Dispatchers.IO) {
        territorioDao.count() > 0
    }

    suspend fun isSuspenso(id: Int): Boolean = withContext(Dispatchers.IO) {
        territorioDao.isSuspenso(id) != null
    }

    suspend fun getNeighbors(territoryId: Int): List<TerritorioVizinhoEntity> = withContext(Dispatchers.IO) {
        vizinhoDao.getByTerritorio(territoryId)
    }

    suspend fun saveNeighbors(territoryId: Int, neighborIds: List<Int>) = withContext(Dispatchers.IO) {
        vizinhoDao.deleteByTerritorio(territoryId)
        for (neighborId in neighborIds) {
            if (vizinhoDao.getByTerritorioEVizinho(territoryId, neighborId) == null) {
                vizinhoDao.insert(TerritorioVizinhoEntity(null, territoryId, neighborId))
            }
            if (vizinhoDao.getByTerritorioEVizinho(neighborId, territoryId) == null) {
                vizinhoDao.insert(TerritorioVizinhoEntity(null, neighborId, territoryId))
            }
        }
    }

    suspend fun deleteNeighbors(territoryId: Int): Int = withContext(Dispatchers.IO) {
        vizinhoDao.deleteByTerritorio(territoryId)
    }
}
