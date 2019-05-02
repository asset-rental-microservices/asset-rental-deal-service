package org.rentalhouse.deal.rental.assets.fixture

import org.rentalhouse.deal.rental.assets.entity.IdProof
import org.rentalhouse.deal.rental.assets.entity.RentalDeal
import org.rentalhouse.deal.rental.assets.entity.Renter
import org.rentalhouse.deal.rental.assets.model.IdProofType
import org.springframework.test.util.ReflectionTestUtils
import java.time.LocalDate

class RentalDealBuilder {

    var assetId: String         = ""
    var agreedRent: Int         = 0
    private var renter: Renter  = Renter("", LocalDate.now(), IdProof("", IdProofType.PAN))

    fun renter(block: RenterBuilder.() -> Unit): Renter =
        RenterBuilder()
            .apply(block)
            .build()
            .also { this.renter = it }

    fun renter(renter: Renter) {
        this.renter = renter
    }

    fun build(): RentalDeal = RentalDeal(assetId, renter, agreedRent)
}

class RenterBuilder {

    var name: String             = ""
    var dob: LocalDate           = LocalDate.now()
    private var idProof: IdProof = IdProof("", IdProofType.PAN)

    fun idProof(id: String, type: IdProofType = IdProofType.PAN) {
        this.idProof = IdProof(id, type)
    }

    fun build(): Renter = Renter(name, dob, idProof)
}

fun rentalDeal(block: RentalDealBuilder.() -> Unit) = RentalDealBuilder().apply(block).build()

fun rentalDeal(id: String, block: RentalDealBuilder.() -> Unit) =
    RentalDealBuilder()
        .apply(block)
        .build()
        .run {
            ReflectionTestUtils.setField(this, "id", id)
            this
        }
