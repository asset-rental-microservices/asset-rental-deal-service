package org.rentalhouse.deal.rental.assets.service

import org.rentalhouse.deal.rental.assets.utils.Either
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Service
class AssetExistenceService(
    private val restTemplate: RestTemplate,
    @Value("\${rental.house.asset.service.base.uri}")
    private val assertServiceBaseUri: String
) {

    fun exists(assetId: String): Either<AssetNotFoundException, Boolean> {
        return try {
            if ( restTemplate.getForEntity("$assertServiceBaseUri/v1/assets/$assetId", Map::class.java).statusCode
                    == HttpStatus.OK )
                Either.right(true)
            else
                Either.left(AssetNotFoundException(assetId))
        } catch (ex: HttpClientErrorException.NotFound) {
            Either.left(AssetNotFoundException(assetId))
        }
    }
}

class AssetNotFoundException(val id: String) : RuntimeException("Asset not found for $id")