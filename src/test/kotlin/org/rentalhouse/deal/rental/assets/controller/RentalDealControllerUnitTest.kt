package org.rentalhouse.deal.rental.assets.controller

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.rentalhouse.deal.rental.assets.service.AssetNotFoundException
import org.rentalhouse.deal.rental.assets.service.RentalDealService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class RentalDealControllerUnitTest {

    private val rentalService    = mockk<RentalDealService>(relaxed = true)

    private val rentalController = RentalDealController(rentalService)

    private val mockMvc = MockMvcBuilders.standaloneSetup(rentalController).build()

    private val idProof = """{"id": "AQPUM3371D", "type": "PAN"}"""
    private val renter  = """{"name": "John", "dob": "1986-10-28", "idProof": $idProof}"""
    private val deal    = """{"assetId": "1000", "renter": $renter, "agreedRent": 10000}"""

    @Test
    fun `should initiate rental deal with status CREATED`() {

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/v1/assets/1000/deal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(deal)
        ).andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    fun `should return the newly created deal id`() {

        every { rentalService.rentalDeal(any()) } returns "2091"

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/v1/assets/1000/deal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(deal)
        ).andExpect(MockMvcResultMatchers.header().string(HttpHeaders.LOCATION, "/v1/assets/1000/deals/2091"))
    }

    @Test
    fun `should return NOT_FOUND given asset for the asset id does not exist`() {

        every { rentalService.rentalDeal(any()) } throws AssetNotFoundException("1000")

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/v1/assets/1000/deal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(deal)
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
    }
}