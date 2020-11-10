package no.breale.devop.exam

import io.restassured.RestAssured
import io.restassured.http.ContentType
import no.breale.devop.exam.dto.StockDTO
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.Test

class StockTest : StockTestBase() {

    @Test
    fun `can post a stock`() {
        val name = "name"
        val description = "description"
        val stock = StockDTO(name, description)
        val location = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(stock)
                .post()
                .then()
                .statusCode(201)
                .extract()
                .header("location")

        RestAssured.given()
                .accept(ContentType.JSON)
                .basePath("")
                .get(location)
                .then()
                .statusCode(200)
                .body("name", CoreMatchers.equalTo(name))
                .body("description", CoreMatchers.equalTo(description))
    }


    @Test
    fun `can retrieve a stock`() {
        val name = "name"
        val description = "description"

        val id = createStock(name, description)

        RestAssured.given()
                .accept(ContentType.JSON)
                .basePath("")
                .get("stock/$id")
                .then()
                .statusCode(200)
                .body("name", CoreMatchers.equalTo(name))
                .body("description", CoreMatchers.equalTo(description))

    }

    @Test
    fun `can retrieve all stocks`() {
        val name = "name"
        val description = "description"
        val name2 = "name2"
        val description2 = "description2"

        val stockCount = stockRepository.count()

        createStock(name, description)
        createStock(name2, description2)

        RestAssured.given()
                .accept(ContentType.JSON)
                .basePath("")
                .get("stock")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", CoreMatchers.equalTo((stockCount+2).toInt()))

    }

    @Test
    fun `cant retrieve a non existent stock`() {
        RestAssured.given()
                .accept(ContentType.JSON)
                .basePath("")
                .get("stock/-1")
                .then()
                .statusCode(404)
    }

    @Test
    fun `cant retrieve a stock with NaN or illegal id`() {
        RestAssured.given()
                .accept(ContentType.JSON)
                .basePath("")
                .get("stock/b2s")
                .then()
                .statusCode(400)
    }

}