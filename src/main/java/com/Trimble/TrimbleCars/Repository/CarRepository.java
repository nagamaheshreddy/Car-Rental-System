package com.Trimble.TrimbleCars.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Trimble.TrimbleCars.Model.Car;
import com.Trimble.TrimbleCars.Model.CarStatus;
import java.util.List;
import java.util.Optional;


public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> findById(Long id);  // Method to find a car by its ID

    List<Car> findByStatus(CarStatus status); // Fetch cars by status

    Optional<Car> findByCarIdAndOwner_OwnerId(Long carId, Long ownerId);  // Method to find car by owner
}

