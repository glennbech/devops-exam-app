package no.breale.devop.exam.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "STOCKS")
class StockEntity(
        var name: String?,
        var description: String?,
        @get:Id
        @get:GeneratedValue
        var stockId: Long? = null
)