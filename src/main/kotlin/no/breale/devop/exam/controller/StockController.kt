package no.breale.devop.exam.controller

import io.micrometer.core.instrument.MeterRegistry
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
        log.info("Attempting to get a stock")
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
        return ResponseEntity.ok(stockService.getAllStock())
    }

    @PostMapping(consumes = [(MediaType.APPLICATION_JSON_VALUE)])
    fun createStock(@RequestBody stockDTO: StockDTO): ResponseEntity<Unit> {
        log.info("Attempting to create a stock")
        val id = stockService.createStock(stockDTO)

        if (id == -1L) {
            log.warn("Unable to create stock")
            return ResponseEntity.status(400).build()
        }

        val createdLink = URI.create("stock/$id")

        log.info("Stock with id: $id was created and put on $createdLink")
        registry.counter("api.response", "created", "stock").increment()
        return ResponseEntity.created(createdLink).build()
    }
}