package com.Trimble.TrimbleCars.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.Trimble.TrimbleCars.Model.CarOwner;



@Repository
public interface CarOwnerRepository extends JpaRepository<CarOwner, Long> {
}

