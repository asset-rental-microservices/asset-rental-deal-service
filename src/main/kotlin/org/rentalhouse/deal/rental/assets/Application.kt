package org.rentalhouse.deal.rental.assets

import org.rentalhouse.deal.rental.assets.event.channel.RentalDealChannels
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
@EnableWebMvc
@EnableEurekaClient
@EnableBinding(RentalDealChannels::class)
class Application

fun main() {
    SpringApplication.run(Application::class.java)
}