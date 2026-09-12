package com.application.territoryassistant.repository

import com.application.territoryassistant.bd.room.GrupoDao
import com.application.territoryassistant.bd.room.GrupoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GroupRepository(private val grupoDao: GrupoDao) {

    suspend fun getAllGroups(): List<GrupoEntity> = withContext(Dispatchers.IO) {
        grupoDao.getAll()
    }

    suspend fun getGroupById(id: Int): GrupoEntity? = withContext(Dispatchers.IO) {
        grupoDao.getById(id)
    }

    suspend fun insertGroup(name: String): Long = withContext(Dispatchers.IO) {
        grupoDao.insert(GrupoEntity(null, name))
    }

    suspend fun updateGroup(grupo: GrupoEntity): Int = withContext(Dispatchers.IO) {
        grupoDao.update(grupo)
    }

    suspend fun deleteGroup(id: Int): Int = withContext(Dispatchers.IO) {
        grupoDao.delete(id)
    }

    suspend fun hasGroups(): Boolean = withContext(Dispatchers.IO) {
        grupoDao.count() > 0
    }

    suspend fun hasTerritories(groupId: Int): Boolean = withContext(Dispatchers.IO) {
        grupoDao.countTerritorios(groupId) > 0
    }
}
