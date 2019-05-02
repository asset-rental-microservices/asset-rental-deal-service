package org.rentalhouse.deal.rental.assets.service

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.rentalhouse.deal.rental.assets.utils.Left
import org.rentalhouse.deal.rental.assets.utils.Right
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

class AssetExistenceServiceUnitTest {

    private val assertServiceBaseUri  = "http://asset-service"
    private val restTemplate          = mockk<RestTemplate>()
    private val assetExistenceService = AssetExistenceService(restTemplate, assertServiceBaseUri = assertServiceBaseUri)

    @Test
    fun `should find an asset by id`() {
        every { restTemplate.getForEntity("$assertServiceBaseUri/v1/assets/1000", Map::class.java) } returns
                ResponseEntity(mapOf("assetId" to "1000"), HttpStatus.OK)

        val assetExists = assetExistenceService.exists("1000") as Right
        assertThat(assetExists.value).isTrue()
    }

    @Test
    fun `should not find an asset by id`() {
        every { restTemplate.getForEntity("$assertServiceBaseUri/v1/assets/1000", Map::class.java) } returns
                ResponseEntity(mapOf<String, Any>(), HttpStatus.NOT_FOUND)

        val either = assetExistenceService.exists("1000")
        assertThat(either).isInstanceOf(Left::class.java)
    }

    @Test
    fun `should not find an asset by id given HttpClientErrorException is thrown`() {
        every { restTemplate.getForEntity("$assertServiceBaseUri/v1/assets/1000", Map::class.java) } throws
                HttpClientErrorException.create(HttpStatus.NOT_FOUND, "not found", HttpHeaders.EMPTY, "not found".toByteArray(), null)

        val either = assetExistenceService.exists("1000")
        assertThat(either).isInstanceOf(Left::class.java)
    }
}