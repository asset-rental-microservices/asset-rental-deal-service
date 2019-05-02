package org.rentalhouse.deal.rental.assets.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.rentalhouse.deal.rental.assets.repository.RentalDealRepository
import org.rentalhouse.deal.rental.assets.event.channel.RentalDealChannels
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.test.binder.MessageCollector
import org.springframework.http.MediaType
import org.springframework.messaging.Message
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.ResponseActions
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.client.RestTemplate
import java.util.concurrent.BlockingQueue

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
class RentalDealPublishIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var rentalDealRepository: RentalDealRepository

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Value("\${rental.house.asset.service.base.uri}")
    private lateinit var assertServiceBaseUri: String

    @Autowired
    private lateinit var channels: RentalDealChannels

    @Autowired
    private lateinit var collector: MessageCollector

    private val idProof = """{"id": "AQPUM3371D", "type": "PAN"}"""
    private val renter  = """{"name": "John", "dob": "1986-10-28", "idProof": $idProof}"""
    private val deal    = """{"assetId": "1000", "renter": $renter, "agreedRent": 22000}"""

    @BeforeEach
    fun setUp() {
        rentalDealRepository.deleteAll()
    }

    @Test
    fun `should save a rental deal`() {
        setUpAssetServer(assetId = "1000") {
            andRespond(MockRestResponseCreators.withSuccess("""{"assetId": "1000"}""", MediaType.APPLICATION_JSON))
        }

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/v1/assets/1000/deal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(deal)
        ).andExpect(MockMvcResultMatchers.status().isCreated)

        val messageQueue: BlockingQueue<Message<*>> = collector.forChannel(channels.rentalDeals())

        val eventPayload = ObjectMapper().readValue(messageQueue.take().payload as String, Map::class.java)

        assertThat(eventPayload["id"]).isNotNull()
        assertThat(eventPayload["assetId"]).isEqualTo("1000")
        assertThat(eventPayload["renterName"]).isEqualTo("John")
        assertThat(eventPayload["renterDob"]).isEqualTo("1986-10-28")
        assertThat(eventPayload["renterProofId"]).isEqualTo("AQPUM3371D")
        assertThat(eventPayload["renterProofType"]).isEqualTo("PAN")
        assertThat(eventPayload["agreedRent"]).isEqualTo(22000)
    }

    private fun setUpAssetServer(assetId: String, block: ResponseActions.() -> Unit) {
        MockRestServiceServer
            .createServer(restTemplate)
            .expect(MockRestRequestMatchers.requestTo("$assertServiceBaseUri/v1/assets/$assetId"))
            .block()
    }
}