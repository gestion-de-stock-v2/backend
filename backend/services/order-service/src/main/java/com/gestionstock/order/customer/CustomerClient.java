package com.gestionstock.order.customer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

/**
 * Adresse customer-service par son nom Eureka. L'attribut {@code url} a ete retire :
 * il pointait sur la passerelle, ce qui faisait transiter le trafic interne par elle
 * et annulait la repartition de charge cote client.
 */
@FeignClient(name = "customer-service", path = "/api/v1/customers")
public interface CustomerClient {

    @GetMapping("/{customer-id}")
    Optional<CustomerResponse> findCustomerById(@PathVariable("customer-id") String customerId);
}
