package org.rentalhouse.deal.rental.assets.utils

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.rentalhouse.deal.rental.assets.event.channel.RentalDealChannels
import org.rentalhouse.deal.rental.assets.event.model.RentalDealInitializedEvent
import org.rentalhouse.deal.rental.assets.event.publisher.EventBuilder
import org.rentalhouse.deal.rental.assets.event.publisher.RentalDealEventsPublisher
import org.rentalhouse.deal.rental.assets.fixture.rentalDeal
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import java.time.LocalDate

class RentalDealEventsPublisherTest {

    private val messageChannel: RentalDealChannels = mockk()
    private val eventBuilder: EventBuilder = mockk()
    private val rentalDealEventsPublisher           =
        RentalDealEventsPublisher(messageChannel, eventBuilder)

    @Test
    fun `should publish a Rental Deal Initialized event`() {

        val deal = rentalDeal(id = "1901") {
            assetId    = "1000"
            agreedRent = 19000
            renter {
                name = "JOHN"
                dob  = LocalDate.now()
                idProof("PNQ")
            }
        }

        val message: Message<RentalDealInitializedEvent>    = mockk()
        val channel                                         = mockk<MessageChannel>()

        every { eventBuilder.build(RentalDealInitializedEvent.from(deal)) } returns message
        every { messageChannel.rentalDeals() }                              returns channel
        every { channel.send(message) }                                     returns true
        every { message.payload }                                           returns mockk()

        rentalDealEventsPublisher.publishDealInitialized(deal)

        verify { channel.send(message) }
    }
}