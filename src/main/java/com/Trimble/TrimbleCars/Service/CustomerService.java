package com.Trimble.TrimbleCars.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Trimble.TrimbleCars.Exception.CarNotAvailableException;
import com.Trimble.TrimbleCars.Exception.CustomerNotFoundException;
import com.Trimble.TrimbleCars.Exception.LeaseLimitExceededException;
import com.Trimble.TrimbleCars.Model.Car;
import com.Trimble.TrimbleCars.Model.CarStatus;
import com.Trimble.TrimbleCars.Model.Customer;
import com.Trimble.TrimbleCars.Model.Lease;
import com.Trimble.TrimbleCars.Repository.CarRepository;
import com.Trimble.TrimbleCars.Repository.CustomerRepository;
import com.Trimble.TrimbleCars.Repository.LeaseRepository;
import com.Trimble.TrimbleCars.Request.RequestDTO;

import java.util.List;




@Service
public class CustomerService {

	@Autowired
    private  CustomerRepository customerRepository;
	
	@Autowired
    private  CarRepository carRepository;
	
	@Autowired
    private  LeaseRepository leaseRepository;
	
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    
    


    public Customer registerCustomer(RequestDTO requestDTO) {
        logger.info("Registering new customer: {}", requestDTO.getName());
        
        Customer customer = new Customer();
        customer.setCustomerId(requestDTO.getCustomerId());
        customer.setName(requestDTO.getName());
        customer.setEmail(requestDTO.getEmail());
        customer.setPhone(requestDTO.getPhone());
        
        return customerRepository.save(customer);
    }

    public List<Car> getAvailableCars() {
        logger.info("Fetching all available cars for lease.");
        
        return carRepository.findByStatus(CarStatus.IDEAL);
    }
    
    
    public Lease startLease(RequestDTO requestDTO) {
        logger.info("Starting lease for customer ID: {} and car ID: {}", requestDTO.getCustomerId(), requestDTO.getCarId());
        
        Long customerId = requestDTO.getCustomerId();
        Long carId = requestDTO.getCarId();

        // Check if the customer has already leased 2 cars
        if (leaseRepository.countByCustomer_CustomerIdAndActiveTrue(customerId) >= 2) {
            logger.error("Customer ID: {} exceeded lease limit.", customerId);
            throw new LeaseLimitExceededException("Customer cannot lease more than 2 cars at a time.");
        }

        // Check if the car exists
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> {
                    logger.error("Cannot find car with Car ID: {}", carId);
                    return new CarNotAvailableException("Car not available ");
                });

        // Check if the car is in an 'ideal' state (not already leased)
        if (!car.getStatus().equals(CarStatus.IDEAL)) {
            logger.error("Car ID: {} is not in an ideal state for leasing. Current status: {}", carId, car.getStatus());
            throw new CarNotAvailableException("Car is not available for leasing. Current status: " + car.getStatus());
        }

        // Update car status to ON_LEASE
        car.setStatus(CarStatus.ON_LEASE);
        carRepository.save(car);

        // Create and save the lease
        Lease lease = new Lease();
        lease.setCar(car);
        lease.setCustomer(customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    logger.error("Customer not found. Customer ID: {}", customerId);
                    return new CustomerNotFoundException("Customer not found.");
                }));
        lease.setLeaseStartDate(requestDTO.getLeaseStartDate());
        lease.setLeaseEndDate(requestDTO.getLeaseEndDate());
        lease.setActive(true);


        Lease savedLease = leaseRepository.save(lease);
        logger.info("Lease started successfully. Lease ID: {}", savedLease.getLeaseId());
        return savedLease;
    }

    
    
 // End a lease
    public void endLease(RequestDTO requestDTO) {
        logger.info("Ending lease for customer ID: {} and Lease ID: {}", requestDTO.getCustomerId(), requestDTO.getLeaseId());

        Long customerId=requestDTO.getCustomerId();
        Long leaseId=requestDTO.getLeaseId();

        Lease lease = leaseRepository.findByLeaseIdAndCustomer_CustomerIdAndActiveTrue(leaseId, customerId)
                .orElseThrow(() -> new RuntimeException("Active lease not found for this customer."));

        lease.setActive(false);
        leaseRepository.save(lease);
        logger.info("Lease ended. Lease ID: {}", lease.getLeaseId());


        Car car = lease.getCar();
        car.setStatus(CarStatus.IDEAL);
        carRepository.save(car);
    }
    

    // View lease history for a customer
    public List<Lease> getLeaseHistory(RequestDTO requestDTO) {
        logger.info("getching Lease History for customer ID: {}", requestDTO.getCustomerId());

    	Long customerId=requestDTO.getCustomerId();
    	
    	// Check if the customer exists
        boolean customerExists = customerRepository.existsById(customerId);
        if (!customerExists) {
            logger.error("Customer not found with ID: {}", customerId);
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found.");
        }

    	List<Lease> history = leaseRepository.findByCustomer_CustomerId(customerId);
        logger.info("Lease history fetched successfully for customer ID: {}. Records count: {}", customerId, history.size());
        
        return history;

    }

	public List<Car> viewAllCarsStatus() {
		logger.info("Fetching all cars and their status.");
        
        return carRepository.findAll();
	}

    


}




