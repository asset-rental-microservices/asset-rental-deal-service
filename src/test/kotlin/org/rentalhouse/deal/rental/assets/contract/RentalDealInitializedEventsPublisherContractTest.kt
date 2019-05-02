package org.rentalhouse.deal.rental.assets.contract

import au.com.dius.pact.provider.IProviderVerifier
import au.com.dius.pact.provider.PactVerifyProvider
import au.com.dius.pact.provider.junit.Provider
import au.com.dius.pact.provider.junit.State
import au.com.dius.pact.provider.junit.loader.PactFolder
import au.com.dius.pact.provider.junit5.AmpqTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.rentalhouse.deal.rental.assets.event.model.RentalDealInitializedEvent
import org.rentalhouse.deal.rental.assets.event.publisher.RentalDealEventsPublisher
import org.rentalhouse.deal.rental.assets.fixture.rentalDeal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.net.URLClassLoader
import java.time.LocalDate
import java.util.function.Supplier
import java.util.function.Function

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = ["server.port=8080"])
@ActiveProfiles("contract-test")
@Provider("assetRentalService")
@PactFolder("src/test/resources/pacts")
class RentalDealInitializedEventsPublisherContractTest {

    @Autowired
    private lateinit var rentalDealEventsPublisher: RentalDealEventsPublisher

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider::class)
    fun pactVerificationTestTemplate(context: PactVerificationContext) {
        context.verifyInteraction()
    }

    @BeforeEach
    fun before(context: PactVerificationContext) {
        context.target = AmpqTestTarget(listOf())
    }

    @PactVerifyProvider(value = "a rental deal initialized event")
    fun publishRentalDealInitializedEvent(): String {
        val deal = rentalDeal(id = "55543") {
            assetId    = "1001"
            agreedRent = 19000
            renter {
                name = "JOHN"
                dob  = LocalDate.now()
                idProof("PNQ")
            }
        }
        val event: RentalDealInitializedEvent = rentalDealEventsPublisher.publishDealInitialized(deal)

        return ObjectMapper().writeValueAsString(event)
    }

    @State("rental deal for asset with id 1001 is INITIALIZED")
    fun state() {}
}
//
//class A(val packagesToScan1: List<String> = emptyList()) : AmpqTestTarget(packagesToScan1) {
//
//    override fun prepareVerifier(verifier: IProviderVerifier, testInstance: Any) {
//        verifier.projectClasspath = Supplier {
//            val classLoader = RentalDealInitializedEventsPublisherContractTest::class.java.classLoader
//            when (classLoader) {
//                is URLClassLoader -> classLoader.urLs.toList()
//                else -> emptyList()
//            }
//        }
//        val defaultProviderMethodInstance = verifier.providerMethodInstance
//        verifier.providerMethodInstance = Function { m ->
//            if (m.declaringClass == testInstance.javaClass) {
//                testInstance
//            } else {
//                defaultProviderMethodInstance.apply(m)
//            }
//        }
//    }
//}