package com.application.territoryassistant.bd.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface GrupoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(grupo: GrupoEntity): Long

    @Query("SELECT * FROM GRUPO ORDER BY NOME ASC")
    fun getAll(): List<GrupoEntity>

    @Query("SELECT * FROM GRUPO WHERE ID = :id LIMIT 1")
    fun getById(id: Int): GrupoEntity?

    @Update
    fun update(grupo: GrupoEntity): Int

    @Query("DELETE FROM GRUPO WHERE ID = :id")
    fun delete(id: Int): Int

    @Query("SELECT COUNT(ID) FROM GRUPO")
    fun count(): Long

    @Query("SELECT COUNT(ID) FROM TERRITORIO WHERE ID_GRUPO = :idGrupo")
    fun countTerritorios(idGrupo: Int): Long
}
