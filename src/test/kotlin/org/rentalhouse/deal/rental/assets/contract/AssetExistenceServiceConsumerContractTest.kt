package org.rentalhouse.deal.rental.assets.contract

import au.com.dius.pact.consumer.Pact
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.model.RequestResponsePact
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.rentalhouse.deal.rental.assets.service.AssetExistenceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class, PactConsumerTestExt::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("contract-test")
@PactTestFor(providerName = "assetService", port = "8080")
class AssetExistenceServiceConsumerContractTest {

    @Autowired
    private lateinit var assetExistenceService: AssetExistenceService

    @Pact(consumer = "assetExistenceService", provider = "assetService")
    fun pactAssetExists(builder: PactDslWithProvider): RequestResponsePact {
        return builder.given("asset with id 1000 exists")
            .uponReceiving("A request to /v1/assets/1000")
            .path("/v1/assets/1000")
            .method(HttpMethod.GET.name)
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body("""{"id": "1000"}""", MediaType.APPLICATION_JSON_VALUE)
            .toPact()
    }

    @Pact(consumer = "assetExistenceService", provider = "assetService")
    fun pactAssetDoesNotExist(builder: PactDslWithProvider): RequestResponsePact {
        return builder.given("asset with id 1002 does not exist")
            .uponReceiving("A request to /v1/assets/1002")
            .path("/v1/assets/1002")
            .method(HttpMethod.GET.name)
            .willRespondWith()
            .status(HttpStatus.NOT_FOUND.value())
            .toPact()
    }

    @PactTestFor(pactMethod = "pactAssetExists")
    @Test
    fun `should return OK given asset with id exists`() {
        val either = assetExistenceService.exists("1000")
        assertThat(either.isRight()).isTrue()
    }

    @PactTestFor(pactMethod = "pactAssetDoesNotExist")
    @Test
    fun `should return NOT_FOUND given asset with id does not exist`() {
        val either = assetExistenceService.exists("1002")
        assertThat(either.isRight()).isFalse()
    }
}