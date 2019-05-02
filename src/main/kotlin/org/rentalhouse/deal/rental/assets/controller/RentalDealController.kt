package org.rentalhouse.deal.rental.assets.controller

import org.rentalhouse.deal.rental.assets.extension.toUri
import org.rentalhouse.deal.rental.assets.model.RentalDealRequest
import org.rentalhouse.deal.rental.assets.model.toRentalDeal
import org.rentalhouse.deal.rental.assets.service.AssetNotFoundException
import org.rentalhouse.deal.rental.assets.service.RentalDealService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@RestController
class RentalDealController(private val rentalDealService: RentalDealService) {

    private val logger: Logger = LoggerFactory.getLogger(RentalDealController::class.java)

    @PostMapping(value = ["/v1/assets/{assetId}/deal"])
    fun rentalDeal(@PathVariable assetId: String, @RequestBody rentalDealRequest: RentalDealRequest):
            ResponseEntity<String> {
        logger.info("Creating rental deal for assetId $assetId")
        val dealId = rentalDealService.rentalDeal(rentalDealRequest.toRentalDeal())
        return ResponseEntity.created("/v1/assets/$assetId/deals/$dealId".toUri()).build()
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun assetNotFoundHandler(ex: AssetNotFoundException) {
        logger.error("Asset with id ${ex.id} not found", ex)
    }
}