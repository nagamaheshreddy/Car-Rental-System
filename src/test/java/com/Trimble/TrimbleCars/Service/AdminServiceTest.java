package com.Trimble.TrimbleCars.Service;


import com.Trimble.TrimbleCars.Exception.CarNotFoundException;
import com.Trimble.TrimbleCars.Model.Car;
import com.Trimble.TrimbleCars.Model.CarStatus;
import com.Trimble.TrimbleCars.Model.Customer;
import com.Trimble.TrimbleCars.Model.Lease;
import com.Trimble.TrimbleCars.Repository.CarRepository;
import com.Trimble.TrimbleCars.Repository.CustomerRepository;
import com.Trimble.TrimbleCars.Repository.LeaseRepository;
import com.Trimble.TrimbleCars.Request.RequestDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private CarRepository carRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LeaseRepository leaseRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for registerCar
    @Test
    void testRegisterCar_Success() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setModel("Tesla Model S");
        requestDTO.setMake("Tesla");
        requestDTO.setColor("White");
        requestDTO.setStatus(CarStatus.IDEAL);

        Car car = new Car();
        car.setCarId(1L);
        car.setModel("Tesla Model S");
        car.setMake("Tesla");
        car.setColor("White");
        car.setStatus(CarStatus.IDEAL);

        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car result = adminService.registerCar(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getCarId());
        verify(carRepository, times(1)).save(any(Car.class));
    }

    // Test for updateCarStatus
    @Test
    void testUpdateCarStatus_Success() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setCarId(1L);
        requestDTO.setStatus(CarStatus.ON_LEASE);

        Car car = new Car();
        car.setCarId(1L);
        car.setStatus(CarStatus.IDEAL);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car result = adminService.updateCarStatus(requestDTO);

        assertNotNull(result);
        assertEquals(CarStatus.ON_LEASE, result.getStatus());
        verify(carRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void testUpdateCarStatus_CarNotFound() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setCarId(1L);

        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CarNotFoundException.class, () -> adminService.updateCarStatus(requestDTO));
        verify(carRepository, times(1)).findById(1L);
    }

    // Test for getCarLeaseHistory
    @Test
    void testGetCarLeaseHistory_Success() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setCarId(1L);

        Car car = new Car();
        car.setCarId(1L);

        Lease lease1 = new Lease();
        Lease lease2 = new Lease();
        List<Lease> leases = Arrays.asList(lease1, lease2);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(leaseRepository.findByCar_CarId(1L)).thenReturn(leases);

        List<Lease> result = adminService.getCarLeaseHistory(requestDTO);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(carRepository, times(1)).findById(1L);
        verify(leaseRepository, times(1)).findByCar_CarId(1L);
    }

    @Test
    void testGetCarLeaseHistory_CarNotFound() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setCarId(1L);

        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CarNotFoundException.class, () -> adminService.getCarLeaseHistory(requestDTO));
        verify(carRepository, times(1)).findById(1L);
    }

    // Test for registerCustomer
    @Test
    void testRegisterCustomer_Success() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setCustomerId(1L);
        requestDTO.setName("John Doe");
        requestDTO.setEmail("johndoe@example.com");
        requestDTO.setPhone("1234567890");

        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setName("John Doe");

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer result = adminService.registerCustomer(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getCustomerId());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    // Test for startLease
    @Test
    void testStartLease_Success() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setCustomerId(1L);
        requestDTO.setCarId(1L);
        requestDTO.setLeaseStartDate(LocalDate.now());
        requestDTO.setLeaseEndDate(LocalDate.now().plusDays(10));

        Customer customer = new Customer();
        customer.setCustomerId(1L);

        Car car = new Car();
        car.setCarId(1L);
        car.setStatus(CarStatus.IDEAL);

        Lease lease = new Lease();
        lease.setLeaseId(1L);
        lease.setActive(true);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(leaseRepository.save(any(Lease.class))).thenReturn(lease);

        Lease result = adminService.startLease(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getLeaseId());
        verify(customerRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).findById(1L);
        verify(leaseRepository, times(1)).save(any(Lease.class));
    }

    // Test for endLease
    @Test
    void testEndLease_Success() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setLeaseId(1L);

        Lease lease = new Lease();
        lease.setLeaseId(1L);
        lease.setActive(true);

        Car car = new Car();
        car.setCarId(1L);
        lease.setCar(car);

        when(leaseRepository.findById(1L)).thenReturn(Optional.of(lease));
        when(carRepository.save(any(Car.class))).thenReturn(car);

        adminService.endLease(requestDTO);

        assertFalse(lease.isActive());
        assertEquals(CarStatus.IDEAL, lease.getCar().getStatus());
        verify(leaseRepository, times(1)).findById(1L);
        verify(leaseRepository, times(1)).save(any(Lease.class));
        verify(carRepository, times(1)).save(any(Car.class));
    }
}
