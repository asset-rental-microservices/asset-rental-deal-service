package org.rentalhouse.deal.rental.assets.extension

import java.net.URI

fun String.toUri(): URI = URI.create(this)