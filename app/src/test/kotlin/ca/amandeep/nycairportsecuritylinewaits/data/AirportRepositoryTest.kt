package ca.amandeep.nycairportsecuritylinewaits.data

import ca.amandeep.nycairportsecuritylinewaits.data.model.Queue
import ca.amandeep.nycairportsecuritylinewaits.data.model.QueueType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.util.Date
import kotlin.time.Duration.Companion.minutes

class AirportRepositoryTest {
    private val initialQueue = Queue(
        timeInMinutes = 12,
        gate = "All gates",
        terminal = "1",
        queueType = QueueType.Reg,
        queueOpen = true,
        updateTime = Date(0),
        isWaitTimeAvailable = true,
        status = "Open",
    )
    private var response: suspend () -> List<Queue> = { listOf(initialQueue) }
    private val repository = AirportRepository(
        airportRemoteDataSource = AirportRemoteDataSource(
            airportApi = object : AirportApiService {
                override suspend fun getWaitTimes(airport: String) = response()
            },
            ioDispatcher = Dispatchers.Unconfined,
        ),
        networkUpdateInterval = 5.minutes,
        networkCacheTTL = 5.minutes,
    )

    @Test
    fun failedRefreshKeepsCachedReadingsMarkedStaleAcrossRetries() = runBlocking {
        val original = repository.getWaitTimes(AirportCode.JFK).first()
        response = { throw IOException("Server unavailable") }
        repeat(3) {
            val emissions = mutableListOf<AirportRepository.Result>()
            val failure = runCatching {
                repository.getWaitTimes(AirportCode.JFK).take(2).toList(emissions)
            }.exceptionOrNull()
            assertTrue(failure is IOException)
            if (it > 0) assertTrue(emissions.single().metadata.hasError)
            val cached = repository.getWaitTimes(AirportCode.JFK).first()
            assertTrue(cached.metadata.hasError)
            assertEquals(original.queues, cached.queues)
            assertEquals(original.metadata.lastUpdated, cached.metadata.lastUpdated)
        }
    }

    @Test
    fun emptyResponseDoesNotReplaceLastUsableReadings() = runBlocking {
        val original = repository.getWaitTimes(AirportCode.JFK).first()
        response = { emptyList() }
        val failure = runCatching {
            repository.getWaitTimes(AirportCode.JFK).take(2).toList()
        }.exceptionOrNull()
        assertTrue(failure is IOException)
        val cached = repository.getWaitTimes(AirportCode.JFK).first()
        assertEquals(original.queues, cached.queues)
        assertEquals(original.metadata.lastUpdated, cached.metadata.lastUpdated)
        assertTrue(cached.metadata.hasError)
    }

    @Test
    fun successfulRefreshClearsErrorAndReplacesReadings() = runBlocking {
        repository.getWaitTimes(AirportCode.JFK).first()
        response = { throw IOException("Server unavailable") }
        runCatching { repository.getWaitTimes(AirportCode.JFK).take(2).toList() }
        val updatedQueue = initialQueue.copy(timeInMinutes = 4)
        response = { listOf(updatedQueue) }
        val emissions = repository.getWaitTimes(AirportCode.JFK).take(2).toList()
        assertTrue(emissions.first().metadata.hasError)
        assertFalse(emissions.last().metadata.hasError)
        assertEquals(listOf(updatedQueue), emissions.last().queues)
    }
}
