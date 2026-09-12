package com.application.territoryassistant.viewmodel

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.application.territoryassistant.bd.room.AppDatabase
import com.application.territoryassistant.bd.room.TerritorioEntity
import com.application.territoryassistant.repository.GroupRepository
import com.application.territoryassistant.repository.LeaderRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewModelsTest {

    private lateinit var db: AppDatabase
    private lateinit var leaderViewModel: LeaderViewModel
    private lateinit var groupViewModel: GroupViewModel

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        val leaderRepository = LeaderRepository(db.dirigenteDao())
        val groupRepository = GroupRepository(db.grupoDao())

        leaderViewModel = LeaderViewModel(leaderRepository)
        groupViewModel = GroupViewModel(groupRepository)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testLeaderViewModel() = runBlocking {
        assertEquals(LeaderUiState.Loading, leaderViewModel.uiState.value)

        var addSuccess = false
        leaderViewModel.addLeader("Carlos", "carlos@test.com") { success ->
            addSuccess = success
        }
        assertTrue(addSuccess)

        val state = leaderViewModel.uiState.value
        assertTrue(state is LeaderUiState.Success)

        val leaders = (state as LeaderUiState.Success).leaders
        assertEquals(1, leaders.size)
        assertEquals("Carlos", leaders[0].nome)
        val leaderId = leaders[0].id ?: 0

        var updateSuccess = false
        leaderViewModel.updateLeader(leaderId, "Carlos Eduardo", "carlos.e@test.com") { success ->
            updateSuccess = success
        }
        assertTrue(updateSuccess)

        val updatedState = leaderViewModel.uiState.value as LeaderUiState.Success
        assertEquals("Carlos Eduardo", updatedState.leaders[0].nome)

        var deleteSuccess = false
        leaderViewModel.deleteLeader(leaderId) { success ->
            deleteSuccess = success
        }
        assertTrue(deleteSuccess)

        val emptyState = leaderViewModel.uiState.value as LeaderUiState.Success
        assertTrue(emptyState.leaders.isEmpty())
    }

    @Test
    fun testGroupViewModel() = runBlocking {
        assertEquals(GroupUiState.Loading, groupViewModel.uiState.value)

        var addSuccess = false
        groupViewModel.addGroup("Grupo Sul") { success ->
            addSuccess = success
        }
        assertTrue(addSuccess)

        val state = groupViewModel.uiState.value as GroupUiState.Success
        assertEquals(1, state.groups.size)
        assertEquals("Grupo Sul", state.groups[0].nome)
        val groupId = state.groups[0].id ?: 0

        var updateSuccess = false
        groupViewModel.updateGroup(groupId, "Grupo Leste") { success ->
            updateSuccess = success
        }
        assertTrue(updateSuccess)

        val updatedState = groupViewModel.uiState.value as GroupUiState.Success
        assertEquals("Grupo Leste", updatedState.groups[0].nome)

        // Add territory linked to group
        db.territorioDao().insert(TerritorioEntity(null, "T-100", groupId, null, 0, null, null))

        var deleteSuccess = false
        var errorMessage: String? = null
        groupViewModel.deleteGroup(groupId) { success, msg ->
            deleteSuccess = success
            errorMessage = msg
        }
        assertFalse(deleteSuccess)
        assertNotNull(errorMessage)

        // Delete territory and delete group again
        db.territorioDao().delete(1)
        groupViewModel.deleteGroup(groupId) { success, _ ->
            deleteSuccess = success
        }
        assertTrue(deleteSuccess)
    }
}
