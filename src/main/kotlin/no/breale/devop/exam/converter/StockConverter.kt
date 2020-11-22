package no.breale.devop.exam.converter

import no.breale.devop.exam.dto.StockDTO
import no.breale.devop.exam.entity.StockEntity

class StockConverter {

    fun transform(entity: StockEntity): StockDTO {
        return StockDTO(
                name = entity.name,
                description = entity.description,
                stockId = entity.stockId.toString()
        )
    }

    fun transform(enteties: Iterable<StockEntity>): List<StockDTO> {
        return enteties.map { transform(it) }.toList()
    }
}