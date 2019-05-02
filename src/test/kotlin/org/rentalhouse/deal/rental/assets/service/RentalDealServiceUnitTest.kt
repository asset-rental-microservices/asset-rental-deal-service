package org.rentalhouse.deal.rental.assets.service

import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.rentalhouse.deal.rental.assets.entity.RentalDeal
import org.rentalhouse.deal.rental.assets.fixture.rentalDeal
import org.rentalhouse.deal.rental.assets.repository.RentalDealRepository
import org.rentalhouse.deal.rental.assets.utils.Left
import org.rentalhouse.deal.rental.assets.event.publisher.RentalDealEventsPublisher
import org.rentalhouse.deal.rental.assets.utils.Right
import org.springframework.test.util.ReflectionTestUtils
import java.time.LocalDate

class RentalDealServiceUnitTest {

    private val rentalDealRepository                = mockk<RentalDealRepository>()
    private val assetExistenceService               = mockk<AssetExistenceService>()
    private val rentalDealInitiatedEventPublisher   = mockk<RentalDealEventsPublisher>(relaxed = true)
    private val rentalDealService                   = RentalDealService(rentalDealRepository, assetExistenceService, rentalDealInitiatedEventPublisher)

    @Test
    fun `should return a newly saved deal id given asset for the id exists`() {
        val deal = rentalDeal {
            assetId    = "1000"
            agreedRent = 14000
            renter {
                name = "JOHN"
                dob  = LocalDate.now()
                idProof("PNQ")
            }
        }

        every { assetExistenceService.exists(deal.assetId) }   returns Right(true)
        every { rentalDealRepository.save(any<RentalDeal>()) } returns savedRentalDeal(deal, id = "101")

        val dealId = rentalDealService.rentalDeal(deal)

        assertThat(dealId).isEqualTo("101")
    }

    @Test
    fun `should invoke publisher to publish an event after rental deal is initialized`() {
        val slotDeal = slot<RentalDeal>()

        val deal = rentalDeal {
            assetId    = "1000"
            agreedRent = 19000
            renter {
                name = "JOHN"
                dob  = LocalDate.now()
                idProof("PNQ")
            }
        }

        every { assetExistenceService.exists(deal.assetId) }   returns Right(true)
        every { rentalDealRepository.save(any<RentalDeal>()) } returns savedRentalDeal(deal, id = "1002")
        every { rentalDealInitiatedEventPublisher.publishDealInitialized(capture(slotDeal)) } returns mockk()

        rentalDealService.rentalDeal(deal)

        assertThat(slotDeal.captured.id).isEqualTo("1002")
    }

    @Test
    fun `should not save a new deal given the asset with id does not exist`() {
        val deal = rentalDeal {
            assetId    = "1000"
            agreedRent = 14000
            renter {
                name = "JOHN"
                dob  = LocalDate.now()
                idProof("PNQ")
            }
        }

        every { assetExistenceService.exists(deal.assetId) } returns Left(AssetNotFoundException("1000"))

        assertThrows<AssetNotFoundException> { rentalDealService.rentalDeal(deal) }
    }

    private fun savedRentalDeal(rentalDeal: RentalDeal, id: String): RentalDeal {
        return rentalDeal {
            assetId     = rentalDeal.assetId
            agreedRent  = rentalDeal.agreedRent
            renter(rentalDeal.renter)
        }.apply {
            ReflectionTestUtils.setField(this, "id", id)
        }
    }
}