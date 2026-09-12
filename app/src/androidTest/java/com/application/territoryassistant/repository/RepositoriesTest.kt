package com.application.territoryassistant.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.application.territoryassistant.bd.room.AppDatabase
import com.application.territoryassistant.bd.room.DesignacaoEntity
import com.application.territoryassistant.bd.room.DirigenteEntity
import com.application.territoryassistant.bd.room.GrupoEntity
import com.application.territoryassistant.bd.room.TerritorioEntity
import com.application.territoryassistant.bd.room.UltimaAcoesEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoriesTest {

    private lateinit var db: AppDatabase
    private lateinit var leaderRepository: LeaderRepository
    private lateinit var groupRepository: GroupRepository
    private lateinit var territoryRepository: TerritoryRepository
    private lateinit var designationRepository: DesignationRepository
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var historyRepository: HistoryRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        leaderRepository = LeaderRepository(db.dirigenteDao())
        groupRepository = GroupRepository(db.grupoDao())
        territoryRepository = TerritoryRepository(db.territorioDao(), db.territorioVizinhoDao())
        designationRepository = DesignationRepository(db.designacaoDao())
        settingsRepository = SettingsRepository(db.configuracoesDao())
        historyRepository = HistoryRepository(db.ultimaAcoesDao())
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testLeaderRepository() = runBlocking {
        assertFalse(leaderRepository.hasLeaders())

        val id = leaderRepository.insertLeader("João Silva", "joao@email.com")
        assertTrue(id > 0)
        assertTrue(leaderRepository.hasLeaders())

        val leaders = leaderRepository.getAllLeaders()
        assertEquals(1, leaders.size)
        assertEquals("João Silva", leaders[0].nome)

        val leader = leaderRepository.getLeaderById(id.toInt())
        assertNotNull(leader)
        assertEquals("joao@email.com", leader?.email)

        val byIds = leaderRepository.getLeadersByIds(listOf(id.toInt()))
        assertEquals(1, byIds.size)

        val emptyByIds = leaderRepository.getLeadersByIds(emptyList())
        assertTrue(emptyByIds.isEmpty())

        val updated = DirigenteEntity(id.toInt(), "João Paulo", "joao.p@email.com")
        leaderRepository.updateLeader(updated)

        val fetchedAfterUpdate = leaderRepository.getLeaderById(id.toInt())
        assertEquals("João Paulo", fetchedAfterUpdate?.nome)

        leaderRepository.deleteLeader(id.toInt())
        assertFalse(leaderRepository.hasLeaders())
    }

    @Test
    fun testGroupRepository() = runBlocking {
        assertFalse(groupRepository.hasGroups())

        val id = groupRepository.insertGroup("Grupo Centro")
        assertTrue(id > 0)
        assertTrue(groupRepository.hasGroups())

        val groups = groupRepository.getAllGroups()
        assertEquals(1, groups.size)

        val group = groupRepository.getGroupById(id.toInt())
        assertNotNull(group)
        assertEquals("Grupo Centro", group?.nome)

        val updated = GrupoEntity(id.toInt(), "Grupo Norte")
        groupRepository.updateGroup(updated)

        val fetched = groupRepository.getGroupById(id.toInt())
        assertEquals("Grupo Norte", fetched?.nome)

        assertFalse(groupRepository.hasTerritories(id.toInt()))

        groupRepository.deleteGroup(id.toInt())
        assertFalse(groupRepository.hasGroups())
    }

    @Test
    fun testTerritoryRepository() = runBlocking {
        assertFalse(territoryRepository.hasTerritories())

        val gId = groupRepository.insertGroup("Grupo 1")
        val tEntity = TerritorioEntity(null, "T-01", gId.toInt(), null, 0, "Obs", "foto.jpg")

        val tId = territoryRepository.insertTerritory(tEntity)
        assertTrue(tId > 0)
        assertTrue(territoryRepository.hasTerritories())

        val all = territoryRepository.getAllTerritories()
        assertEquals(1, all.size)

        val fetched = territoryRepository.getTerritoryById(tId.toInt())
        assertNotNull(fetched)
        assertEquals("T-01", fetched?.cod)

        val byIds = territoryRepository.getTerritoriesByIds(listOf(tId.toInt()))
        assertEquals(1, byIds.size)

        val emptyByIds = territoryRepository.getTerritoriesByIds(emptyList())
        assertTrue(emptyByIds.isEmpty())

        assertFalse(territoryRepository.isSuspenso(tId.toInt()))

        val updated = TerritorioEntity(tId.toInt(), "T-01", gId.toInt(), System.currentTimeMillis(), 1, "Nova Obs", "foto2.jpg")
        territoryRepository.updateTerritory(updated)

        assertTrue(territoryRepository.isSuspenso(tId.toInt()))

        // Test neighbors
        val tEntity2 = TerritorioEntity(null, "T-02", gId.toInt(), null, 0, null, null)
        val tId2 = territoryRepository.insertTerritory(tEntity2)

        territoryRepository.saveNeighbors(tId.toInt(), listOf(tId2.toInt()))
        val neighbors = territoryRepository.getNeighbors(tId.toInt())
        assertEquals(1, neighbors.size)

        territoryRepository.deleteNeighbors(tId.toInt())
        val neighborsAfterDelete = territoryRepository.getNeighbors(tId.toInt())
        assertTrue(neighborsAfterDelete.isEmpty())

        territoryRepository.deleteTerritory(tId.toInt())
        assertEquals(1, territoryRepository.getAllTerritories().size)
    }

    @Test
    fun testDesignationRepository() = runBlocking {
        val lId = leaderRepository.insertLeader("Dirigente", "email")
        val gId = groupRepository.insertGroup("Grupo")
        val tId = territoryRepository.insertTerritory(TerritorioEntity(null, "T-10", gId.toInt(), null, 0, null, null))

        val dEntity = DesignacaoEntity(null, tId.toInt(), lId.toInt(), "P", System.currentTimeMillis(), null, 0)
        val dId = designationRepository.insertDesignation(dEntity)
        assertTrue(dId > 0)

        assertTrue(designationRepository.isTerritoryAssigned(tId.toInt()))
        assertTrue(designationRepository.isLeaderAssigned(lId.toInt()))
        assertTrue(designationRepository.hasDesignationsForTerritory(tId.toInt()))

        val fetched = designationRepository.getDesignationById(dId.toInt())
        assertNotNull(fetched)

        val openList = designationRepository.getOpenDesignations()
        assertEquals(1, openList.size)

        designationRepository.setMarked(dId.toInt(), true)

        val updatedEntity = DesignacaoEntity(dId.toInt(), tId.toInt(), lId.toInt(), "P", dEntity.dataInicio, System.currentTimeMillis(), 1)
        designationRepository.updateDesignation(updatedEntity)

        designationRepository.deleteDesignation(dId.toInt())
        assertFalse(designationRepository.isTerritoryAssigned(tId.toInt()))
    }

    @Test
    fun testSettingsRepository() = runBlocking {
        settingsRepository.updateLeaderDefaultMessage("Novo texto")
        val settings = settingsRepository.getSettings()
        assertNotNull(settings)
        assertEquals("Novo texto", settings?.textoPadraoDirigenteTerritorio)

        settingsRepository.updateTerritoryWaitDays(30)
        val updatedSettings = settingsRepository.getSettings()
        assertEquals(30, updatedSettings?.numDiasEsperaTerritorio)
    }

    @Test
    fun testHistoryRepository() = runBlocking {
        val action = UltimaAcoesEntity(null, "DESIGNAR", "T-01,T-02", 1, System.currentTimeMillis(), null)
        val id = historyRepository.insertAction(action)
        assertTrue(id > 0)

        val recent = historyRepository.getRecentActions(10)
        assertEquals(1, recent.size)
        assertEquals("DESIGNAR", recent[0].codAcao)
    }
}
