package org.rentalhouse.deal.rental.assets.event.publisher

import org.rentalhouse.deal.rental.assets.entity.RentalDeal
import org.rentalhouse.deal.rental.assets.event.channel.RentalDealChannels
import org.rentalhouse.deal.rental.assets.event.model.RentalDealInitializedEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component

@Component
class RentalDealEventsPublisher(
    private val rentalDealChannels: RentalDealChannels,
    private val eventBuilder: EventBuilder
) {

    private val logger: Logger = LoggerFactory.getLogger(RentalDealEventsPublisher::class.java)

    fun publishDealInitialized(rentalDeal: RentalDeal): RentalDealInitializedEvent {
        val message = eventBuilder.build(RentalDealInitializedEvent.from(rentalDeal))
        logger.info("Publishing message with id ${rentalDeal.id}")
        rentalDealChannels.rentalDeals().send(message)
        return message.payload
    }
}

@Component
class EventBuilder {
    fun <T> build(payload: T): Message<T> = MessageBuilder.withPayload(payload).build()
}