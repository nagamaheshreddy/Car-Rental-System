package com.Trimble.TrimbleCars.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.Trimble.TrimbleCars.Exception.CarNotFoundException;
import com.Trimble.TrimbleCars.Exception.CustomerNotFoundException;
import com.Trimble.TrimbleCars.Exception.LeaseNotFoundException;
import com.Trimble.TrimbleCars.Model.Car;
import com.Trimble.TrimbleCars.Model.Customer;
import com.Trimble.TrimbleCars.Model.Lease;
import com.Trimble.TrimbleCars.Request.RequestDTO;
import com.Trimble.TrimbleCars.Response.ResponseDTO;
import com.Trimble.TrimbleCars.Service.AdminService;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/admin")

public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    // ------------------- Car Management -------------------

    @PostMapping("/cars")
    public ResponseEntity<ResponseDTO<Car>> registerCar(@Validated @RequestBody RequestDTO requestDTO) {
        logger.info("Admin requested to register a new car: {}", requestDTO.getCarId());

        Car registeredCar = adminService.registerCar(requestDTO);
        logger.info("Car registered successfully with ID: {}", registeredCar.getCarId());

        ResponseDTO<Car> response = new ResponseDTO<Car>( HttpStatus.CREATED.value(),"Car registered successfully.", registeredCar);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/cars/status")
    public ResponseEntity<ResponseDTO<Car>> updateCarStatus(@Validated @RequestBody RequestDTO requestDTO) {
        logger.info("Admin requested to update status of car with ID: {} to {}", requestDTO.getCarId(), requestDTO.getStatus());

        try {
            Car updatedCar = adminService.updateCarStatus(requestDTO);
            logger.info("Car status updated successfully for ID: {}", requestDTO.getCarId());

            ResponseDTO<Car> response = new ResponseDTO<Car>(HttpStatus.OK.value(),"Car status updated successfully.",  updatedCar);
            return ResponseEntity.ok(response);
        } catch (CarNotFoundException e) {
            logger.error("Error updating car status: {}", e.getMessage());
            ResponseDTO<Car> response = new ResponseDTO<Car>(HttpStatus.NOT_FOUND.value(),e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    

    // ------------------- Customer Management -------------------

    @PostMapping("/customers")
    public ResponseEntity<ResponseDTO<Customer>> registerCustomer(@Validated @RequestBody RequestDTO requestDTO) {
        logger.info("Admin requested to register a new customer: {}", requestDTO.getName());

        Customer registeredCustomer = adminService.registerCustomer(requestDTO);
        logger.info("Customer registered successfully with ID: {}", registeredCustomer.getCustomerId());

        ResponseDTO<Customer> response = new ResponseDTO<Customer>( HttpStatus.CREATED.value(),"Customer registered successfully.", registeredCustomer);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/customers")
    public ResponseEntity<ResponseDTO<List<Customer>>> getAllCustomers() {
        logger.info("Admin requested to fetch all customers");

        List<Customer> customers = adminService.getAllCustomers();
        logger.info("Total customers fetched: {}", customers.size());

        ResponseDTO<List<Customer>> response = new ResponseDTO<List<Customer>>(HttpStatus.OK.value(),"Customers fetched successfully.",  customers);
        return ResponseEntity.ok(response);
    }

    // ------------------- Lease Management -------------------

    @PostMapping("/start-lease")
    public ResponseEntity<ResponseDTO<Lease>> startLease(@RequestBody RequestDTO requestDTO) {
        logger.info("Admin requested to start lease for customer ID: {} and car ID: {}", requestDTO.getCustomerId(), requestDTO.getCarId());

        try {
            Lease lease = adminService.startLease(requestDTO);
            logger.info("Lease started successfully. Lease ID: {}", lease.getLeaseId());

            ResponseDTO<Lease> response = new ResponseDTO<Lease>(HttpStatus.CREATED.value(),"Lease started successfully.",  lease);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (CustomerNotFoundException | CarNotFoundException e) {
            logger.error("Error starting lease: {}", e.getMessage());
            ResponseDTO<Lease> response = new ResponseDTO<Lease>( HttpStatus.NOT_FOUND.value(),e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalStateException e) {
            logger.error("Invalid state for starting lease: {}", e.getMessage());
            ResponseDTO<Lease> response = new ResponseDTO<Lease>(HttpStatus.BAD_REQUEST.value(),e.getMessage(),  null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/end-lease")
    public ResponseEntity<ResponseDTO<Lease>> endLease(@RequestBody RequestDTO requestDTO) {
        logger.info("Admin requested to end lease with ID: {}", requestDTO.getLeaseId());

        try {
            adminService.endLease(requestDTO);
            logger.info("Lease ended successfully. Lease ID: {}", requestDTO.getLeaseId());

            ResponseDTO<Lease> response = new ResponseDTO<Lease>(HttpStatus.OK.value(),"Lease ended successfully.",  null);
            return ResponseEntity.ok(response);
        } catch (LeaseNotFoundException e) {
            logger.error("Error ending lease: {}", e.getMessage());
            ResponseDTO<Lease> response = new ResponseDTO<Lease>(HttpStatus.NOT_FOUND.value(),e.getMessage(),  null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    
    
    @GetMapping("/cars/history")
    public ResponseEntity<ResponseDTO<List<Lease>>> getCarLeaseHistory(@RequestBody RequestDTO requestDTO) {
        logger.info("Admin requested lease history for car ID: {}", requestDTO.getCarId());

        try {
            List<Lease> leaseHistory = adminService.getCarLeaseHistory(requestDTO);
            logger.info("Lease history fetched for car ID: {}. Total records: {}", requestDTO.getCarId(), leaseHistory.size());

            ResponseDTO<List<Lease>> response = new ResponseDTO<List<Lease>>(HttpStatus.OK.value(),"Lease history fetched successfully.",  leaseHistory);
            return ResponseEntity.ok(response);
        } catch (CarNotFoundException e) {
            logger.error("Error fetching lease history: {}", e.getMessage());
            ResponseDTO<List<Lease>> response = new ResponseDTO<List<Lease>>(HttpStatus.NOT_FOUND.value(),e.getMessage(),  null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}

