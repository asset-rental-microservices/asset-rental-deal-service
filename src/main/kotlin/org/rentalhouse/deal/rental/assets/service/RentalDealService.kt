package org.rentalhouse.deal.rental.assets.service

import org.rentalhouse.deal.rental.assets.entity.RentalDeal
import org.rentalhouse.deal.rental.assets.repository.RentalDealRepository
import org.rentalhouse.deal.rental.assets.utils.Left
import org.rentalhouse.deal.rental.assets.event.publisher.RentalDealEventsPublisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RentalDealService(
    private val rentalDealRepository: RentalDealRepository,
    private val assetExistenceService: AssetExistenceService,
    private val rentalDealEventsPublisher: RentalDealEventsPublisher
) {

    private val logger: Logger = LoggerFactory.getLogger(RentalDealService::class.java)

    fun rentalDeal(deal: RentalDeal): String {
        val either = assetExistenceService.exists(deal.assetId)
        logger.info("Asset with assetId ${deal.assetId} exists? ${either.isRight()}")
        if (either.isRight())
            return saveAndNotify(deal)
        else
            throw (either as Left).error
    }

    private fun saveAndNotify(deal: RentalDeal): String {
        return rentalDealRepository.save(deal).run {
            rentalDealEventsPublisher.publishDealInitialized(this)
            this.id
        }
    }
}