package org.rentalhouse.deal.rental.assets.model

import org.rentalhouse.deal.rental.assets.entity.IdProof
import org.rentalhouse.deal.rental.assets.entity.RentalDeal
import org.rentalhouse.deal.rental.assets.entity.Renter
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

class RentalDealRequest(val assetId: String, val renter: Renter, val agreedRent: Int)
class Renter           (private val name: String,
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                        private val dob: LocalDate,
                        private val idProof: IdProof)
class IdProof          (private val id: String, private val type: IdProofType)
enum class IdProofType {
    PAN
}

fun RentalDealRequest.toRentalDeal() = RentalDeal(assetId, renter.toRenter(), agreedRent)
fun Renter.toRenter()                = Renter(name, dob, idProof.toProof())
fun IdProof.toProof()                = IdProof(id, type)