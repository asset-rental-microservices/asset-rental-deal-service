package org.rentalhouse.deal.rental.assets.event.model

import org.rentalhouse.deal.rental.assets.entity.RentalDeal
import org.rentalhouse.deal.rental.assets.model.IdProofType
import java.time.LocalDate

data class RentalDealInitializedEvent(
    val id: String,
    val assetId: String,
    val renterName: String,
    val renterDob: String,
    val renterProofId: String,
    val renterProofType: IdProofType,
    val agreedRent: Int
) {

    companion object {

        fun from(rentalDeal: RentalDeal): RentalDealInitializedEvent {
            return RentalDealInitializedEvent(
                rentalDeal.id,
                rentalDeal.assetId,
                rentalDeal.renterName(),
                rentalDeal.renterDob().toString(),
                rentalDeal.renterProofId(),
                rentalDeal.renterProofType(),
                rentalDeal.agreedRent
            )
        }
    }
}