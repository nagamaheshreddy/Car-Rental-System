package com.Trimble.TrimbleCars.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Trimble.TrimbleCars.Exception.CarNotFoundException;
import com.Trimble.TrimbleCars.Exception.CarOwnerNotFoundException;
import com.Trimble.TrimbleCars.Model.Car;
import com.Trimble.TrimbleCars.Model.CarOwner;
import com.Trimble.TrimbleCars.Model.CarStatus;
import com.Trimble.TrimbleCars.Model.Lease;
import com.Trimble.TrimbleCars.Repository.CarOwnerRepository;
import com.Trimble.TrimbleCars.Repository.CarRepository;
import com.Trimble.TrimbleCars.Repository.LeaseRepository;
import com.Trimble.TrimbleCars.Request.RequestDTO;

import java.util.List;


@Service
public class CarOwnerService {

    private static final Logger logger = LoggerFactory.getLogger(CarOwnerService.class);

    @Autowired
    private CarRepository carRepository;
    
    @Autowired
    private LeaseRepository leaseRepository;
    
    
    @Autowired
    private CarOwnerRepository carOwnerRepository;
    
    public CarOwner registerOwner(RequestDTO requestDTO) {
        logger.info("Registering new customer: {}", requestDTO.getName());
        
        CarOwner carOwner = new CarOwner();
        carOwner.setOwnerId(requestDTO.getOwnerId());
        carOwner.setName(requestDTO.getName());
        carOwner.setEmail(requestDTO.getEmail());
        
        return carOwnerRepository.save(carOwner);
    }


    public Car registerCar(RequestDTO requestDTO) {
        logger.info("Registering car for owner with ID: {}", requestDTO.getOwnerId());
        
        Car car = new Car();
        car.setModel(requestDTO.getModel());
        car.setMake(requestDTO.getMake());
        car.setColor(requestDTO.getColor());
        car.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : CarStatus.IDEAL);

        Long ownerId = requestDTO.getOwnerId();
        
        CarOwner owner = carOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new CarOwnerNotFoundException("No car owner found with ID : ownerId"));

        car.setOwner(owner);
        Car savedCar = carRepository.save(car);
        logger.info("Car registered successfully with ID: {}", savedCar.getCarId());
        return savedCar;
    }

    public Car getCarStatus(RequestDTO requestDTO) {
        logger.info("Fetching status for car with ID: {} and owner ID: {}", requestDTO.getCarId(), requestDTO.getOwnerId());
        
        return carRepository.findById(requestDTO.getCarId())
                .orElseThrow(() -> {
                    logger.error("Car not found for owner ID: {} and car ID: {}",requestDTO.getOwnerId(), requestDTO.getCarId());
                    return new CarNotFoundException("Car not found or does not belong to this owner.");
                });
    }

    public List<Lease> getLeaseHistory(RequestDTO requestDTO) {
        logger.info("Fetching lease history for owner with ID: {}", requestDTO.getOwnerId());

    	Long ownerId=requestDTO.getOwnerId();

        // Check if the owner exists
        boolean ownerExists = carOwnerRepository.existsById(ownerId);
        if (!ownerExists) {
            logger.error("Owner not found with ID: {}", ownerId);
            throw new CarOwnerNotFoundException("Owner with ID " + ownerId + " not found.");
        }

        // Fetch lease history if owner exists
        List<Lease> history = leaseRepository.findByCar_Owner_OwnerId(ownerId);
        logger.info("Lease history fetched successfully for owner ID: {}. Records count: {}", ownerId, history.size());
        return history;
    }

}
