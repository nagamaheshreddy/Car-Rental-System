package com.Trimble.TrimbleCars.Repository;


import com.Trimble.TrimbleCars.Model.Lease;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LeaseRepository extends JpaRepository<Lease, Long> {

    int countByCustomer_CustomerIdAndActiveTrue(Long customerId); 

    Optional<Lease> findByLeaseIdAndCustomer_CustomerIdAndActiveTrue(Long leaseId, Long customerId); 

    List<Lease> findByCustomer_CustomerId(Long customerId);  // Find leases by customerId

    List<Lease> findByCar_Owner_OwnerId(Long ownerId);  // Find lease history by Car's ownerId

    List<Lease> findByCar_CarId(Long carId);  // Find leases by carId

}
