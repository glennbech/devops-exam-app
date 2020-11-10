package no.breale.devop.exam.service

import no.breale.devop.exam.converter.StockConverter
import no.breale.devop.exam.dto.StockDTO
import no.breale.devop.exam.entity.StockEntity
import no.breale.devop.exam.repository.StockRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StockService {

    @Autowired
    lateinit var stockRepository: StockRepository

    fun getStock(id: Long): StockDTO?{
        val stock = stockRepository.findById(id).orElse(null) ?: return null
        return StockConverter().transform(stock)
    }

    fun getAllStock(): List<StockDTO>{
        return StockConverter().transform(stockRepository.findAll())
    }

    fun createStock(stockDTO: StockDTO): Long {
        val stockEntity = StockEntity(
                name = stockDTO.name,
                description = stockDTO.description
        )

        stockRepository.save(stockEntity)
        return stockEntity.stockId ?: -1L
    }
}