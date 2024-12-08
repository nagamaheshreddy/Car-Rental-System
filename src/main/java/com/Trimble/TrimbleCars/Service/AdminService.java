package com.Trimble.TrimbleCars.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Trimble.TrimbleCars.Exception.CarNotFoundException;
import com.Trimble.TrimbleCars.Exception.CustomerNotFoundException;
import com.Trimble.TrimbleCars.Exception.LeaseNotFoundException;
import com.Trimble.TrimbleCars.Model.Car;
import com.Trimble.TrimbleCars.Model.CarStatus;
import com.Trimble.TrimbleCars.Model.Customer;
import com.Trimble.TrimbleCars.Model.Lease;
import com.Trimble.TrimbleCars.Repository.CarRepository;
import com.Trimble.TrimbleCars.Repository.CustomerRepository;
import com.Trimble.TrimbleCars.Repository.LeaseRepository;
import com.Trimble.TrimbleCars.Request.RequestDTO;

import java.util.List;
import java.util.Optional;




@Service
@SuppressWarnings("unused")
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private  CarRepository carRepository;
    
    @Autowired
    private  CustomerRepository customerRepository;
    
    @Autowired
    private  LeaseRepository leaseRepository;

    
    // Operations for Car Owner
    public Car registerCar(RequestDTO requestDTO) {
        logger.info("Admin registering a new car with details: {}", requestDTO.getCarId());
        
        Car car = new Car();
        car.setModel(requestDTO.getModel());
        car.setMake(requestDTO.getMake());
        car.setColor(requestDTO.getColor());
        car.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : CarStatus.IDEAL);

        Car savedCar = carRepository.save(car);
        logger.info("Car registered successfully with ID: {}", savedCar.getCarId());
        return savedCar;
    }

    public Car updateCarStatus(RequestDTO requestDTO) {
        logger.info("Admin updating status for car ID: {} to {}", requestDTO.getCarId(), requestDTO.getStatus());
        
        Long carId=requestDTO.getCarId();
        CarStatus status=requestDTO.getStatus();
        
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> {
                    logger.error("Car not found with ID: {}", carId);
                    return new CarNotFoundException("Car not found with ID: " + carId);
                });
        car.setStatus(status);
        Car updatedCar = carRepository.save(car);
        logger.info("Car status updated successfully for ID: {}", carId);
        return updatedCar;
    }

    public List<Lease> getCarLeaseHistory(RequestDTO requestDTO) {
        logger.info("Admin fetching lease history for car ID: {}", requestDTO.getCarId());
        Long carId=requestDTO.getCarId();
        
        
		Car car = carRepository.findById(carId)
                .orElseThrow(() -> {
                    logger.error("Car not found with ID: {}", carId);
                    return new CarNotFoundException("Car not found with ID: " + carId);
                });
        List<Lease> leases = leaseRepository.findByCar_CarId(carId);
        logger.info("Lease history fetched for car ID: {}. Total records: {}", carId, leases.size());
        return leases;
    }

    // Operations for End Customer
    public Customer registerCustomer(RequestDTO requestDTO) {
        logger.info("Admin registering a new customer: {}", requestDTO.getName());
        
        Customer customer = new Customer();
        customer.setCustomerId(requestDTO.getCustomerId());
        customer.setName(requestDTO.getName());
        customer.setEmail(requestDTO.getEmail());
        customer.setPhone(requestDTO.getPhone());
        
        Customer savedCustomer = customerRepository.save(customer);
        logger.info("Customer registered successfully with ID: {}", savedCustomer.getCustomerId());
        return savedCustomer;
    }

    public List<Customer> getAllCustomers() {
        logger.info("Admin fetching all customers");
        List<Customer> customers = customerRepository.findAll();
        logger.info("Total customers fetched: {}", customers.size());
        return customers;
    }

    public Lease startLease(RequestDTO requestDTO) {
        logger.info("Admin starting lease for customer ID: {} and car ID: {}", requestDTO.getCustomerId(), requestDTO.getCarId());
        
        Long customerId=requestDTO.getCustomerId();
        Long carId=requestDTO.getCarId();

        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            logger.error("Customer not found with ID: {}", customerId);
            throw new CustomerNotFoundException("Customer not found with ID: " + customerId);
        }

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> {
                    logger.error("Car not found with ID: {}", carId);
                    return new CarNotFoundException("Car not found with ID: " + carId);
                });

        if (car.getStatus() != CarStatus.IDEAL) {
            logger.error("Car with ID: {} is not available for leasing", carId);
            throw new IllegalStateException("Car is not available for leasing");
        }

        car.setStatus(CarStatus.ON_LEASE);
        carRepository.save(car);

        Lease lease = new Lease();
        lease.setCar(car);
        lease.setCustomer(customerOpt.get());
        lease.setActive(true);
        lease.setLeaseStartDate(requestDTO.getLeaseStartDate());
        lease.setLeaseEndDate(requestDTO.getLeaseEndDate());
        
        Lease savedLease = leaseRepository.save(lease);

        logger.info("Lease started successfully. Lease ID: {}", savedLease.getLeaseId());
        return savedLease;
    }

    public void endLease(RequestDTO requestDTO) {
        logger.info("Admin ending lease with ID: {}", requestDTO.getLeaseId());
        
        Long leaseId=requestDTO.getLeaseId();

        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> {
                    logger.error("Lease not found with ID: {}", leaseId);
                    return new LeaseNotFoundException("Lease not found with ID: " + leaseId);
                });

        lease.setActive(false);
        leaseRepository.save(lease);

        Car car = lease.getCar();
        car.setStatus(CarStatus.IDEAL);
        carRepository.save(car);

        logger.info("Lease ended successfully. Lease ID: {}", leaseId);
    }
}

