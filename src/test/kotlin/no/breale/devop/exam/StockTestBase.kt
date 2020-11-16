package no.breale.devop.exam

import io.restassured.RestAssured
import no.breale.devop.exam.dto.StockDTO
import no.breale.devop.exam.entity.StockEntity
import no.breale.devop.exam.repository.StockRepository
import no.breale.devop.exam.service.StockService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [ (App::class) ], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
abstract class StockTestBase {

    @Autowired
    protected lateinit var stockRepository: StockRepository

    @LocalServerPort
    protected val port = 0

    @BeforeEach
    @AfterEach
    fun init() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/stock"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        stockRepository.deleteAll()
    }

    protected fun createStock(name: String, description: String): Long?{
        val stockEntity = StockEntity(
                name = name,
                description = description
        )
        return stockRepository.save(stockEntity).stockId
    }

}