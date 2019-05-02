package org.rentalhouse.deal.rental.assets.repository

import org.rentalhouse.deal.rental.assets.entity.RentalDeal
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface RentalDealRepository : MongoRepository<RentalDeal, String>