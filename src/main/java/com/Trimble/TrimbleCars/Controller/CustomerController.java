package com.Trimble.TrimbleCars.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Trimble.TrimbleCars.Exception.CarNotAvailableException;
import com.Trimble.TrimbleCars.Exception.CustomerNotFoundException;
import com.Trimble.TrimbleCars.Exception.LeaseLimitExceededException;
import com.Trimble.TrimbleCars.Model.Car;
import com.Trimble.TrimbleCars.Model.Customer;
import com.Trimble.TrimbleCars.Model.Lease;
import com.Trimble.TrimbleCars.Request.RequestDTO;
import com.Trimble.TrimbleCars.Response.ResponseDTO;
import com.Trimble.TrimbleCars.Service.CustomerService;

import java.util.List;



@RestController
@RequestMapping("/customer")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;
    
    

 // -------------------- 1. Register as a customer --------------------
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<Customer>> registerCustomer(@RequestBody RequestDTO requestDTO) {
        logger.info("Request received to register a new customer: {}", requestDTO.getName());
        try {
            Customer registeredCustomer = customerService.registerCustomer(requestDTO);
            logger.info("Customer registered successfully with ID: {}", registeredCustomer.getCustomerId());
            ResponseDTO<Customer> response=new ResponseDTO<>(HttpStatus.CREATED.value(), "Customer registered successfully", registeredCustomer);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error while registering customer: {}", e.getMessage());
            ResponseDTO<Customer> response=new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error while registering customer", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // -------------------- 2. View cars available for lease --------------------
    @GetMapping("/available-cars")
    public ResponseEntity<ResponseDTO<List<Car>>> viewAvailableCars() {
        logger.info("Request received to fetch available cars for lease.");
        try {
            List<Car> availableCars = customerService.getAvailableCars();
            logger.info("Available cars fetched successfully. Count: {}", availableCars.size());
            ResponseDTO<List<Car>> response=new ResponseDTO<>(HttpStatus.OK.value(), "Available cars fetched successfully", availableCars);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error while fetching available cars: {}", e.getMessage());
            ResponseDTO<List<Car>> response=new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error while fetching available cars", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // -------------------- 3. Start a lease --------------------
    @PostMapping("/start-lease")
    public ResponseEntity<ResponseDTO<Lease>> startLease(@RequestBody RequestDTO requestDTO) {
        logger.info("Request received to start a lease. Customer ID: {}, Car ID: {}", requestDTO.getCustomerId(), requestDTO.getCarId());
        try {
            Lease lease = customerService.startLease(requestDTO);
            logger.info("Lease started successfully. Lease ID: {}", lease.getLeaseId());
            ResponseDTO<Lease> response=new ResponseDTO<>(HttpStatus.CREATED.value(), "Lease started successfully", lease);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (CustomerNotFoundException | CarNotAvailableException | LeaseLimitExceededException e) {
            logger.error("Error while starting lease: {}", e.getMessage());
            ResponseDTO<Lease> response=new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            logger.error("Unexpected error while starting lease: {}", e.getMessage());
            ResponseDTO<Lease> response=new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error occurred", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // -------------------- 4. End a lease --------------------
    @PutMapping("/end-lease")
    public ResponseEntity<ResponseDTO<Void>> endLease(@RequestBody RequestDTO requestDTO) {
        logger.info("Request received to end a lease. Customer ID: {}, Lease ID: {}", requestDTO.getCustomerId(), requestDTO.getLeaseId());
        try {
            customerService.endLease(requestDTO);
            logger.info("Lease ended successfully for Lease ID: {}", requestDTO.getLeaseId());
            return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), "Lease ended successfully", null));
        } catch (CustomerNotFoundException e) {
            logger.error("Error ending lease - customer not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Unexpected error while ending lease: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error occurred", null));
        }
    }
    
    // -------------------- 5. View all cars and their Status --------------------
    @GetMapping("/cars-status")
    public ResponseEntity<ResponseDTO<List<Car>>> viewAllCarsStatus() {
        logger.info("Request received to fetch all cars and their status");
        try {
            List<Car> allCars = customerService.viewAllCarsStatus();
            logger.info("All cars fetched successfully. Count: {}", allCars.size());
            ResponseDTO<List<Car>> response=new ResponseDTO<>(HttpStatus.OK.value(), "All cars fetched successfully", allCars);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error while fetching all cars: {}", e.getMessage());
            ResponseDTO<List<Car>> response=new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error while fetching all cars", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // -------------------- 6. View lease history --------------------
    @GetMapping("/lease-history")
    public ResponseEntity<ResponseDTO<List<Lease>>> getLeaseHistory(@RequestBody RequestDTO requestDTO) {
        logger.info("Request received to fetch lease history for Customer ID: {}", requestDTO.getCustomerId());
        try {
            List<Lease> leaseHistory = customerService.getLeaseHistory(requestDTO);
            logger.info("Lease history fetched successfully for Customer ID: {}. Records count: {}", requestDTO.getCustomerId(), leaseHistory.size());
            return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), "Lease history fetched successfully", leaseHistory));
        } catch (CustomerNotFoundException e) {
            logger.error("Error fetching lease history - customer not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Unexpected error while fetching lease history: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error occurred", null));
        }
    }
}
