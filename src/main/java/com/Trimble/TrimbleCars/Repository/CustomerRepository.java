package com.Trimble.TrimbleCars.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.Trimble.TrimbleCars.Model.Customer;



@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

	String findCustomerNameBycustomerId(Long customerId);
}

