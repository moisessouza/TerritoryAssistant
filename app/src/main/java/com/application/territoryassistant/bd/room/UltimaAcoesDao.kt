package com.application.territoryassistant.bd.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UltimaAcoesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(acao: UltimaAcoesEntity): Long

    @Query("SELECT * FROM ULTIMA_ACOES ORDER BY ID DESC LIMIT :limit")
    fun getRecent(limit: Int): List<UltimaAcoesEntity>
}
