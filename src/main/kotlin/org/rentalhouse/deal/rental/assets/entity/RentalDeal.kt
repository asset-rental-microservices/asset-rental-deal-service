package org.rentalhouse.deal.rental.assets.entity

import org.rentalhouse.deal.rental.assets.model.IdProofType
import org.springframework.data.annotation.Id
import java.time.LocalDate

class RentalDeal(val assetId: String, val renter: Renter, val agreedRent: Int) {

    @Id
    lateinit var id: String
        private set

    fun renterName()        = renter.name
    fun renterDob()         = renter.dob
    fun renterProofId()     = renter.idProof.id
    fun renterProofType()   = renter.idProof.type
}

class Renter           (val name: String, val dob: LocalDate, val idProof: IdProof)
class IdProof          (val id: String, val type: IdProofType)