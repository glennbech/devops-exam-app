/**
 * Used some inspiration regarding micrometer
 * URL: https://micrometer.io/docs/concepts
 * DATE: 17.11.2020
 */
package no.breale.devop.exam.controller

import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.DistributionSummary
import io.micrometer.core.instrument.MeterRegistry
import no.breale.devop.exam.dto.StockDTO
import no.breale.devop.exam.service.StockService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("stock")
class StockController(
        @Autowired private val stockService: StockService,
        @Autowired private val registry: MeterRegistry
) {

    private val createdCounter = Counter.builder("api.count")
            .description("Number of stocks created")
            .tag("stock", "create")
            .register(registry)

    private val createdDistributionSummery = DistributionSummary.builder("api.summary")
            .description("Body size of request")
            .tag("stock", "create")
            .register(registry)

    private val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping(path = ["/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getStock(@PathVariable("id") stockId: Long): ResponseEntity<StockDTO> {
        log.info("Attempting to get a stock with id $stockId")
        val id: Long

        try {
            id = stockId.toLong()
        } catch (nfe: NumberFormatException) {
            log.warn("Attempted getting stock with invalid id: $stockId")
            return ResponseEntity.status(400).build()
        }

        val stock = stockService.getStock(id)
        if (stock === null) {
            log.warn("Stock with id: $stockId cannot be found")
            return ResponseEntity.status(404).build()
        }
        log.info("Stock with id: $stockId was found")
        return ResponseEntity.ok(stock)
    }

    @Timed(value = "api.timer", extraTags = ["stock", "all"], description = "Time spent listing stocks", longTask = true)
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllStocks(): ResponseEntity<List<StockDTO>> {
        val stocks = stockService.getAllStock()
        TimeUnit.MILLISECONDS.sleep((Math.random() * 300).toLong()) // Mock a db with much resources
        log.info("Stock count ${stocks.size}")
        registry.gauge("api.gauge", stocks.size)
        return ResponseEntity.ok(stocks)
    }

    @PostMapping(consumes = [(MediaType.APPLICATION_JSON_VALUE)])
    @Timed(value = "api.timer", extraTags = ["stock", "create"], description = "Time spent creating a new stock")
    fun createStock(@RequestHeader(value = "Content-Length") contentLength: String, @RequestBody stockDTO: StockDTO): ResponseEntity<Unit> {
        log.info("Attempting to create a stock")
        if (contentLength.isNotEmpty()) {
            val contentLengthAsNumber = contentLength.toDouble()
            createdDistributionSummery.record(contentLengthAsNumber) // Simple example of how it could work
            log.info("Stock body size: $contentLengthAsNumber B")
        }
        val id = stockService.createStock(stockDTO)

        if (id == -1L) {
            log.warn("Unable to create stock")
            return ResponseEntity.status(400).build()
        }

        val createdLink = URI.create("stock/$id")

        createdCounter.increment()
        log.info("Stock with id: $id was created and put on $createdLink")
        return ResponseEntity.created(createdLink).build()
    }
}