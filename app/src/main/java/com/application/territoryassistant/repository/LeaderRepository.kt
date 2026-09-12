package com.application.territoryassistant.repository

import com.application.territoryassistant.bd.room.DirigenteDao
import com.application.territoryassistant.bd.room.DirigenteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LeaderRepository(private val dirigenteDao: DirigenteDao) {

    suspend fun getAllLeaders(): List<DirigenteEntity> = withContext(Dispatchers.IO) {
        dirigenteDao.getAll()
    }

    suspend fun getLeaderById(id: Int): DirigenteEntity? = withContext(Dispatchers.IO) {
        dirigenteDao.getById(id)
    }

    suspend fun getLeadersByIds(ids: List<Int>): List<DirigenteEntity> = withContext(Dispatchers.IO) {
        if (ids.isEmpty()) emptyList() else dirigenteDao.getByIds(ids)
    }

    suspend fun insertLeader(name: String, email: String?): Long = withContext(Dispatchers.IO) {
        dirigenteDao.insert(DirigenteEntity(null, name, email))
    }

    suspend fun updateLeader(dirigente: DirigenteEntity): Int = withContext(Dispatchers.IO) {
        dirigenteDao.update(dirigente)
    }

    suspend fun deleteLeader(id: Int): Int = withContext(Dispatchers.IO) {
        dirigenteDao.delete(id)
    }

    suspend fun hasLeaders(): Boolean = withContext(Dispatchers.IO) {
        dirigenteDao.count() > 0
    }
}
