package org.rentalhouse.deal.rental.assets.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.rentalhouse.deal.rental.assets.repository.RentalDealRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.ResponseActions
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.client.RestTemplate
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
class RentalDealControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var rentalDealRepository: RentalDealRepository

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Value("\${rental.house.asset.service.base.uri}")
    private lateinit var assertServiceBaseUri: String

    private val idProof = """{"id": "AQPUM3371D", "type": "PAN"}"""
    private val renter  = """{"name": "John", "dob": "1986-10-28", "idProof": $idProof}"""
    private val deal    = """{"assetId": "1000", "renter": $renter, "agreedRent": 10000}"""

    @BeforeEach
    fun setUp() {
        rentalDealRepository.deleteAll()
    }

    @Test
    fun `should save a rental deal`() {
        setUpAssetServer(assetId = "1000") {
            andRespond(withSuccess("""{"assetId": "1000"}""", MediaType.APPLICATION_JSON))
        }

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/v1/assets/1000/deal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(deal)
        ).andExpect(MockMvcResultMatchers.status().isCreated)

        val rentalDeal = rentalDealRepository.findAll().first()

        assertThat(rentalDeal.id).isNotNull()
        assertThat(rentalDeal.assetId).isEqualTo("1000")
        assertThat(rentalDeal.agreedRent).isEqualTo(10000)
        assertThat(rentalDeal.renterName()).isEqualTo("John")
        assertThat(rentalDeal.renterDob()).isEqualTo(LocalDate.parse("1986-10-28"))
        assertThat(rentalDeal.renterProofId()).isEqualTo("AQPUM3371D")
    }

    @Test
    fun `should return NOT_FOUND given asset for asset id does not exist`() {
        setUpAssetServer(assetId = "1000") {
            andRespond(withStatus(HttpStatus.NOT_FOUND))
        }

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/v1/assets/1000/deal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(deal)
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    private fun setUpAssetServer(assetId: String, block: ResponseActions.() -> Unit) {
        MockRestServiceServer
            .createServer(restTemplate)
            .expect(requestTo("$assertServiceBaseUri/v1/assets/$assetId"))
            .block()
    }
}