package com.aesuriagasalazar.arenillasturismo.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aesuriagasalazar.arenillasturismo.model.data.remote.RemoteRepository
import com.aesuriagasalazar.arenillasturismo.model.network.NetworkStatus
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashViewModelTest {

    private lateinit var viewModel: SplashViewModel

    @RelaxedMockK
    lateinit var repository: RemoteRepository

    @RelaxedMockK
    lateinit var networkStatus: NetworkStatus

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        viewModel = SplashViewModel(repository, networkStatus)
    }

    @Test
    fun `load remote data if device have a connection and return greater than zero`() =
        runBlocking {
            coEvery { repository.getItemsCountFromLocal() } returns 10

            viewModel.loadDataFromRemote()

            assert(viewModel.dataStatus.value == DataStatus.UPDATING)

            coVerify(exactly = 1) { repository.updateLocalDataBaseFromRemoteDataSource() }
        }


    @Test
    fun `load remote data if device have a connection and return lees than zero`() = runBlocking {
        coEvery { repository.getItemsCountFromLocal() } returns 0

        viewModel.loadDataFromRemote()

        assert(viewModel.dataStatus.value == DataStatus.DOWNLOADING)

        coVerify(exactly = 1) { repository.saveLocalDataBaseFromDataSourceRemote() }
    }

    @Test
    fun `load local data if device don't have a connection and if data`() = runBlocking {
        coEvery { repository.getItemsCountFromLocal() } returns 10

        viewModel.loadDataFromLocal()

        assert(viewModel.dataStatus.value == DataStatus.LOCAL)
    }

    @Test
    fun `load local data if device don't have a connection and data`() = runBlocking {
        coEvery { repository.getItemsCountFromLocal() } returns 0

        viewModel.loadDataFromLocal()

        assert(viewModel.dataStatus.value == DataStatus.NO_DATA)
    }

}