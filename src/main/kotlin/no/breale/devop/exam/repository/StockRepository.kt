package no.breale.devop.exam.repository

import no.breale.devop.exam.entity.StockEntity
import org.springframework.data.repository.CrudRepository

interface StockRepository : CrudRepository<StockEntity, Long>