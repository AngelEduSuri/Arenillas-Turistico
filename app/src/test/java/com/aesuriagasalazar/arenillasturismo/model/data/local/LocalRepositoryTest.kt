package com.aesuriagasalazar.arenillasturismo.model.data.local

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class LocalRepositoryTest {

    @RelaxedMockK
    private lateinit var dao: PlaceDao

    private lateinit var repository: LocalRepository

    private val fakeList = listOf(
        PlaceEntity(1, "Parque 1", "parque"),
        PlaceEntity(2, "Parque 2", "parque"),
        PlaceEntity(3, "Hotel 1", "hospedaje"),
        PlaceEntity(4, "Naturaleza 1", "naturaleza"),
        PlaceEntity(5, "Natureleza 2", "naturaleza"),
        PlaceEntity(6, "Historico 1", "histórico"),
        PlaceEntity(7, "Historico 1", "parque"),
        PlaceEntity(8, "Cancha 1", "deportivo"),
        PlaceEntity(9, "Cancha 2", "deportivo"),
        PlaceEntity(10, "Hotel 2", "hospedaje"),
        PlaceEntity(11, "Bar 2", "entretenimiento"),
        PlaceEntity(12, "Bar 2", "entretenimiento"),
    )

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        repository = LocalRepository(dao)
    }

    @Test
    fun `get all places from local database when category is park`() = runBlocking {
        val list = fakeList.filter { it.categoria == "parque" }
        coEvery { dao.getPlacesForCategory(any()) } returns list

        val parks = repository.getPlacesCategory("")

        assert(parks == list)
    }

    @Test
    fun `get all places from local database when category is nature`() = runBlocking {
        val list = fakeList.filter { it.categoria == "naturaleza" }
        coEvery { dao.getPlacesForCategory(any()) } returns list

        val nature = repository.getPlacesCategory("")

        assert(nature == list)
    }

    @Test
    fun `get all places from local database when category is historical`() = runBlocking {
        val list = fakeList.filter { it.categoria == "histórico" }
        coEvery { dao.getPlacesForCategory(any()) } returns list

        val historical = repository.getPlacesCategory("")

        assert(historical == list)
    }
    @Test
    fun `get all places from local database when category is hotel`() = runBlocking {
        val list = fakeList.filter { it.categoria == "hospedaje" }
        coEvery { dao.getPlacesForCategory(any()) } returns list

        val hotels = repository.getPlacesCategory("")

        assert(hotels == list)
    }

    @Test
    fun `get all places from local database when category is entertainment`() = runBlocking {
        val list = fakeList.filter { it.categoria == "entretenimiento" }
        coEvery { dao.getPlacesForCategory(any()) } returns list

        val entertainments = repository.getPlacesCategory("")

        assert(entertainments == list)
    }

    @Test
    fun `get all places from local database when category is sport`() = runBlocking {
        val list = fakeList.filter { it.categoria == "deportivo" }
        coEvery { dao.getPlacesForCategory(any()) } returns list

        val sports = repository.getPlacesCategory("")

        assert(sports == list)
    }

    @Test
    fun `get all places from local database`() = runBlocking {
        coEvery { dao.getPlaces() } returns fakeList

        val list = repository.getAllPlaces()

        assert(fakeList == list)
    }

    @Test
    fun `get all places from local database when is searching for an id`() = runBlocking {
        val place = fakeList.find { it.id == 3 }
        coEvery { dao.getPlaceForId(any()) } returns place!!

        val count = repository.getPlaceForId(3)

        assert(count == place)
    }
}