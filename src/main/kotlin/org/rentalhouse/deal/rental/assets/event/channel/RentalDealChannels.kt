package org.rentalhouse.deal.rental.assets.event.channel

import org.springframework.cloud.stream.annotation.Output
import org.springframework.messaging.MessageChannel

interface RentalDealChannels {

    @Output("rentalDeals")
    fun rentalDeals(): MessageChannel
}