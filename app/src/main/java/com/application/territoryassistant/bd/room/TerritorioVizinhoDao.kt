package com.application.territoryassistant.bd.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TerritorioVizinhoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vizinho: TerritorioVizinhoEntity): Long

    @Query("SELECT * FROM TERRITORIO_VIZINHO WHERE ID_TERRITORIO = :idTerritorio")
    fun getByTerritorio(idTerritorio: Int): List<TerritorioVizinhoEntity>

    @Query("SELECT * FROM TERRITORIO_VIZINHO WHERE ID_TERRITORIO = :idTerritorio AND ID_VIZINHO = :idVizinho LIMIT 1")
    fun getByTerritorioEVizinho(idTerritorio: Int, idVizinho: Int): TerritorioVizinhoEntity?

    @Query("DELETE FROM TERRITORIO_VIZINHO WHERE ID_TERRITORIO = :idTerritorio OR ID_VIZINHO = :idTerritorio")
    fun deleteByTerritorio(idTerritorio: Int): Int
}
