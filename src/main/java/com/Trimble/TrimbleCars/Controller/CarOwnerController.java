package com.Trimble.TrimbleCars.Controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Trimble.TrimbleCars.Exception.CarNotFoundException;
import com.Trimble.TrimbleCars.Exception.CarOwnerNotFoundException;
import com.Trimble.TrimbleCars.Model.Car;
import com.Trimble.TrimbleCars.Model.CarOwner;
import com.Trimble.TrimbleCars.Model.Lease;
import com.Trimble.TrimbleCars.Request.RequestDTO;
import com.Trimble.TrimbleCars.Response.ResponseDTO;
import com.Trimble.TrimbleCars.Service.CarOwnerService;

import java.util.List;

@RestController
@RequestMapping("/car-owner")
public class CarOwnerController {

    private static final Logger logger = LoggerFactory.getLogger(CarOwnerController.class);

    @Autowired
    private CarOwnerService carOwnerService;
    

    // -------------------- 1. Register and enroll the car --------------------
    
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<CarOwner>> registerOwner(@RequestBody RequestDTO requestDTO) {
        logger.info("Request received to register a new customer: {}", requestDTO.getName());
        try {
            CarOwner registeredCustomer = carOwnerService.registerOwner(requestDTO);
            logger.info("Customer registered successfully with ID: {}", registeredCustomer.getOwnerId());

            ResponseDTO<CarOwner> response =new ResponseDTO<CarOwner>(HttpStatus.CREATED.value(), "Customer registered successfully", registeredCustomer);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error while registering customer: {}", e.getMessage());
            ResponseDTO<CarOwner> response=new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/register-car")
    public ResponseEntity<ResponseDTO<Car>> registerCar(@RequestBody RequestDTO requestDTO) {
        logger.info("Request received to register a car for owner ID: {}", requestDTO.getOwnerId());
        try {
            Car registeredCar = carOwnerService.registerCar(requestDTO);
            logger.info("Car registered successfully with ID: {}", registeredCar.getCarId());
            ResponseDTO<Car> response =new ResponseDTO<>(HttpStatus.CREATED.value(), "Car registered successfully", registeredCar);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (CarOwnerNotFoundException e) {
            logger.error("Error registering car: {}", e.getMessage());
            ResponseDTO<Car> response =new ResponseDTO<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    

    // -------------------- 2. View car current status & details --------------------

    @GetMapping("/car-status")
    public ResponseEntity<ResponseDTO<Car>> getCarStatus(@RequestBody RequestDTO requestDTO) {
        logger.info("Request received to fetch car status. Owner ID: {}, Car ID: {}", requestDTO.getOwnerId(), requestDTO.getCarId());
        try {
            Car carStatus = carOwnerService.getCarStatus(requestDTO);
            logger.info("Car status fetched successfully for Car ID: {}", requestDTO.getCarId());

            ResponseDTO<Car> response =new ResponseDTO<>(HttpStatus.OK.value(), "Car status fetched successfully", carStatus);
            return ResponseEntity.ok(response);
            
        } catch (CarOwnerNotFoundException | CarNotFoundException e) {
            logger.error("Error fetching car status: {}", e.getMessage());
            ResponseDTO<Car> response =new ResponseDTO<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    
    

    // -------------------- 3. View lease history of cars owned by this owner --------------------

    @GetMapping("/lease-history")
    public ResponseEntity<ResponseDTO<List<Lease>>> getLeaseHistory(@RequestBody RequestDTO requestDTO) {
        logger.info("Request received to fetch lease history for Owner ID: {}", requestDTO.getOwnerId());
        try {
            // Fetch lease history from the service
            List<Lease> leaseHistory = carOwnerService.getLeaseHistory(requestDTO);
            logger.info("Lease history fetched successfully for Owner ID: {}. Records count: {}", requestDTO.getOwnerId(), leaseHistory.size());

            // Return success response with data
            ResponseDTO<List<Lease>> response =new ResponseDTO<>(HttpStatus.OK.value(),"Lease history fetched successfully",leaseHistory);
            return ResponseEntity.ok(response);
            
        } catch (CarOwnerNotFoundException e) {
            // Handle exception and return error response
            logger.error("Error fetching lease history: {}", e.getMessage());

            ResponseDTO<List<Lease>> response=new ResponseDTO<>(HttpStatus.NOT_FOUND.value(),e.getMessage(),null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

}







