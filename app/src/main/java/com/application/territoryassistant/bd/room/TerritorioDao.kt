package com.application.territoryassistant.bd.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TerritorioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(territorio: TerritorioEntity): Long

    @Query("SELECT * FROM TERRITORIO ORDER BY COD ASC")
    fun getAll(): List<TerritorioEntity>

    @Query("SELECT * FROM TERRITORIO WHERE ID = :id LIMIT 1")
    fun getById(id: Int): TerritorioEntity?

    @Query("SELECT * FROM TERRITORIO WHERE ID IN (:ids) ORDER BY COD ASC")
    fun getByIds(ids: List<Int>): List<TerritorioEntity>

    @Update
    fun update(territorio: TerritorioEntity): Int

    @Query("DELETE FROM TERRITORIO WHERE ID = :id")
    fun delete(id: Int): Int

    @Query("SELECT COUNT(ID) FROM TERRITORIO")
    fun count(): Long

    @Query("SELECT COUNT(ID) FROM TERRITORIO WHERE ID_GRUPO = :idGrupo")
    fun countByGrupo(idGrupo: Int): Long

    @Query("SELECT ID FROM TERRITORIO WHERE SUSPENSO = 1 AND ID = :id LIMIT 1")
    fun isSuspenso(id: Int): Int?
}
