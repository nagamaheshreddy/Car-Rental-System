package com.Trimble.TrimbleCars.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.Trimble.TrimbleCars.Exception.*;
import com.Trimble.TrimbleCars.Model.*;
import com.Trimble.TrimbleCars.Repository.*;
import com.Trimble.TrimbleCars.Request.RequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;



class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private LeaseRepository leaseRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    

    @Test
    void testRegisterCustomer_Success() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setCustomerId(1L);
        requestDTO.setName("John Doe");
        requestDTO.setEmail("john.doe@example.com");
        requestDTO.setPhone("1234567890");

        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhone("1234567890");

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer result = customerService.registerCustomer(requestDTO);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testGetAvailableCars_Success() {
        List<Car> cars = Arrays.asList(new Car(), new Car());

        when(carRepository.findByStatus(CarStatus.IDEAL)).thenReturn(cars);

        List<Car> result = customerService.getAvailableCars();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(carRepository, times(1)).findByStatus(CarStatus.IDEAL);
    }

    @Test
    void testStartLease_Success() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setCustomerId(1L);
        requestDTO.setCarId(1L);

        Customer customer = new Customer();
        customer.setCustomerId(1L);

        Car car = new Car();
        car.setCarId(1L);
        car.setStatus(CarStatus.IDEAL);

        Lease lease = new Lease();
        lease.setLeaseId(1L);
        lease.setCar(car);
        lease.setCustomer(customer);
        lease.setActive(true);

        when(leaseRepository.countByCustomer_CustomerIdAndActiveTrue(1L)).thenReturn(0);
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(leaseRepository.save(any(Lease.class))).thenReturn(lease);

        Lease result = customerService.startLease(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getLeaseId());
        assertTrue(result.isActive());
        verify(carRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).findById(1L);
        verify(leaseRepository, times(1)).save(any(Lease.class));
    }

    @Test
    void testStartLease_Failure_LeaseLimitExceeded() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setCustomerId(1L);

        when(leaseRepository.countByCustomer_CustomerIdAndActiveTrue(1L)).thenReturn(2);

        assertThrows(LeaseLimitExceededException.class, () -> customerService.startLease(requestDTO));

        verify(leaseRepository, times(1)).countByCustomer_CustomerIdAndActiveTrue(1L);
        verify(carRepository, never()).findById(anyLong());
    }

    @Test
    void testStartLease_Failure_CarNotAvailable() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setCarId(1L);

        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CarNotAvailableException.class, () -> customerService.startLease(requestDTO));

        verify(carRepository, times(1)).findById(1L);
        verify(leaseRepository, never()).save(any(Lease.class));
    }

    @Test
    void testEndLease_Success() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setCustomerId(1L);
        requestDTO.setLeaseId(1L);

        Car car = new Car();
        car.setCarId(1L);
        car.setStatus(CarStatus.ON_LEASE);

        Lease lease = new Lease();
        lease.setLeaseId(1L);
        lease.setCar(car);
        lease.setActive(true);

        when(leaseRepository.findByLeaseIdAndCustomer_CustomerIdAndActiveTrue(1L, 1L)).thenReturn(Optional.of(lease));

        customerService.endLease(requestDTO);

        verify(leaseRepository, times(1)).findByLeaseIdAndCustomer_CustomerIdAndActiveTrue(1L, 1L);
        verify(leaseRepository, times(1)).save(any(Lease.class));
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void testEndLease_Failure_ActiveLeaseNotFound() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setLeaseId(1L);
        requestDTO.setCustomerId(1L);

        when(leaseRepository.findByLeaseIdAndCustomer_CustomerIdAndActiveTrue(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> customerService.endLease(requestDTO));

        verify(leaseRepository, times(1)).findByLeaseIdAndCustomer_CustomerIdAndActiveTrue(1L, 1L);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void testGetLeaseHistory_Success() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setCustomerId(1L);

        List<Lease> leases = Arrays.asList(new Lease(), new Lease());

        when(customerRepository.existsById(1L)).thenReturn(true);
        when(leaseRepository.findByCustomer_CustomerId(1L)).thenReturn(leases);

        List<Lease> result = customerService.getLeaseHistory(requestDTO);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(customerRepository, times(1)).existsById(1L);
        verify(leaseRepository, times(1)).findByCustomer_CustomerId(1L);
    }

    @Test
    void testGetLeaseHistory_Failure_CustomerNotFound() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setCustomerId(1L);

        when(customerRepository.existsById(1L)).thenReturn(false);

        assertThrows(CustomerNotFoundException.class, () -> customerService.getLeaseHistory(requestDTO));

        verify(customerRepository, times(1)).existsById(1L);
        verify(leaseRepository, never()).findByCustomer_CustomerId(anyLong());
    }
}
