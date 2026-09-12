package com.application.territoryassistant.bd.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        DirigenteEntity::class,
        GrupoEntity::class,
        TerritorioEntity::class,
        TerritorioVizinhoEntity::class,
        DesignacaoEntity::class,
        UltimaAcoesEntity::class,
        ConfiguracoesEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dirigenteDao(): DirigenteDao
    abstract fun grupoDao(): GrupoDao
    abstract fun territorioDao(): TerritorioDao
    abstract fun territorioVizinhoDao(): TerritorioVizinhoDao
    abstract fun designacaoDao(): DesignacaoDao
    abstract fun ultimaAcoesDao(): UltimaAcoesDao
    abstract fun configuracoesDao(): ConfiguracoesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "territories.db"
                )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
