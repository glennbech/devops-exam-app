package no.breale.devop.exam.controller

import io.micrometer.core.instrument.DistributionSummary
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import no.breale.devop.exam.dto.StockDTO
import no.breale.devop.exam.service.StockService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import org.slf4j.LoggerFactory

@RestController
@RequestMapping("stock")
class StockController {

    @Autowired
    lateinit var stockService: StockService

    @Autowired
    private lateinit var registry: MeterRegistry

    private val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping(path = ["/{id}"],produces = [MediaType.APPLICATION_JSON_VALUE])
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
        if(stock === null) {
            log.warn("Stock with id: $stockId cannot be found")
            return ResponseEntity.status(404).build()
        }
        log.info("Stock with id: $stockId was found")
        return ResponseEntity.ok(stock)
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllStocks(): ResponseEntity<List<StockDTO>> {
        var stocks: List<StockDTO> = emptyList()
        registry.more().longTaskTimer("api.response.timer.collection.stock").recordCallable {
            stocks = stockService.getAllStock()
            registry.gaugeCollectionSize("api.response", listOf(Tag.of("type", "collection")), stocks)
            log.info("Stock count ${stocks.size}")
        }
        return ResponseEntity.ok(stocks)
    }

    @PostMapping(consumes = [(MediaType.APPLICATION_JSON_VALUE)])
    fun createStock(@RequestBody stockDTO: StockDTO): ResponseEntity<Unit> = registry.timer("api.creation.measurement").recordCallable {
        log.info("Attempting to create a stock")
        val id = stockService.createStock(stockDTO)

        if (id == -1L) {
            log.warn("Unable to create stock")
            return@recordCallable ResponseEntity.status(400).build()
        }

        val createdLink = URI.create("stock/$id")

        log.info("Stock with id: $id was created and put on $createdLink")
        registry.counter("api.response", "created", "stock").increment()
        return@recordCallable ResponseEntity.created(createdLink).build()
    }
}