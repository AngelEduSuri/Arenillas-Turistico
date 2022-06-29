package com.aesuriagasalazar.arenillasturismo.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aesuriagasalazar.arenillasturismo.model.data.local.LocalRepository
import com.aesuriagasalazar.arenillasturismo.model.data.local.asEntityModelList
import com.aesuriagasalazar.arenillasturismo.model.domain.Place
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlaceListViewModelTest {

    private lateinit var viewModel: PlaceListViewModel
    private val listCategories =
        listOf("parque", "histórico", "naturaleza", "deportivo", "hospedaje", "entretenimiento")

    @RelaxedMockK
    lateinit var repository: LocalRepository

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        viewModel = PlaceListViewModel(repository)
    }

    @Test
    fun `get places when category is parks`() = runBlocking {
        val category = listCategories.first { it == "parque" }

        val list = listOf(
            Place(id = 1, nombre = "Parque a la madre", "parque"),
            Place(id = 2, nombre = "Parque Alajuela", "parque"),
        ).asEntityModelList()

        coEvery { repository.getPlacesCategory(category) } returns list

        viewModel.loadDataFromRepository(category)

        assert(viewModel.categoryList.value!!.all { it.categoria == category })
    }

    @Test
    fun `get places when category is historical`() = runBlocking {
        val category = listCategories.first { it == "histórico" }

        val list = listOf(
            Place(id = 1, nombre = "Puente de Arenillas", "histórico"),
            Place(id = 2, nombre = "Casas Patrinoniales", "histórico"),
        ).asEntityModelList()

        coEvery { repository.getPlacesCategory(category) } returns list

        viewModel.loadDataFromRepository(category)

        assert(viewModel.categoryList.value!!.all { it.categoria == category })
    }

    @Test
    fun `get places when category is nature`() = runBlocking {
        val category = listCategories.first { it == "naturaleza" }

        val list = listOf(
            Place(id = 1, nombre = "Represa Tahuin", "naturaleza"),
            Place(id = 2, nombre = "Cascadas El Blanco", "naturaleza"),
        ).asEntityModelList()

        coEvery { repository.getPlacesCategory(category) } returns list

        viewModel.loadDataFromRepository(category)

        assert(viewModel.categoryList.value!!.all { it.categoria == category })
    }

    @Test
    fun `get places when category is sports`() = runBlocking {
        val category = listCategories.first { it == "naturaleza" }

        val list = listOf(
            Place(id = 1, nombre = "Represa Tahuin", "naturaleza"),
            Place(id = 2, nombre = "Cascadas El Blanco", "naturaleza"),
        ).asEntityModelList()

        coEvery { repository.getPlacesCategory(category) } returns list

        viewModel.loadDataFromRepository(category)

        assert(viewModel.categoryList.value!!.all { it.categoria == category })
    }

    @Test
    fun `get places when category is hotel`() = runBlocking {
        val category = listCategories.first { it == "hospedaje" }

        val list = listOf(
            Place(id = 1, nombre = "Hotel Sindicato de Choferes", "hospedaje"),
            Place(id = 2, nombre = "Hillary Resort", "hospedaje"),
        ).asEntityModelList()

        coEvery { repository.getPlacesCategory(category) } returns list

        viewModel.loadDataFromRepository(category)

        assert(viewModel.categoryList.value!!.all { it.categoria == category })
    }

    @Test
    fun `get places when category is entertainment`() = runBlocking {
        val category = listCategories.first { it == "entretenimiento" }

        val list = listOf(
            Place(id = 1, nombre = "Jaleo Bar", "entretenimiento"),
            Place(id = 2, nombre = "Bar El Manaba", "entretenimiento"),
        ).asEntityModelList()

        coEvery { repository.getPlacesCategory(category) } returns list

        viewModel.loadDataFromRepository(category)

        assert(viewModel.categoryList.value!!.all { it.categoria == category })
    }
}