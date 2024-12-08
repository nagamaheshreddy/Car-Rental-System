package com.Trimble.TrimbleCars.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import com.Trimble.TrimbleCars.Exception.CarOwnerNotFoundException;
import com.Trimble.TrimbleCars.Model.Car;
import com.Trimble.TrimbleCars.Model.CarOwner;
import com.Trimble.TrimbleCars.Model.CarStatus;
import com.Trimble.TrimbleCars.Model.Lease;
import com.Trimble.TrimbleCars.Repository.CarOwnerRepository;
import com.Trimble.TrimbleCars.Repository.CarRepository;
import com.Trimble.TrimbleCars.Repository.LeaseRepository;
import com.Trimble.TrimbleCars.Request.RequestDTO;

import java.util.*;


class CarOwnerServiceTest {

    @InjectMocks
    private CarOwnerService carOwnerService;

    @Mock
    private CarOwnerRepository carOwnerRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private LeaseRepository leaseRepository;
    

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    

    @Test
    void testRegisterOwner_Success() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setOwnerId(1L);
        requestDTO.setName("John Doe");
        requestDTO.setEmail("john.doe@example.com");

        CarOwner carOwner = new CarOwner();
        carOwner.setOwnerId(1L);
        carOwner.setName("John Doe");
        carOwner.setEmail("john.doe@example.com");

        when(carOwnerRepository.save(any(CarOwner.class))).thenReturn(carOwner);

        CarOwner result = carOwnerService.registerOwner(requestDTO);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(carOwnerRepository, times(1)).save(any(CarOwner.class));
    }

    @Test
    void testRegisterCar_Success() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setOwnerId(1L);
        requestDTO.setModel("Model X");
        requestDTO.setMake("Tesla");
        requestDTO.setColor("Red");

        CarOwner owner = new CarOwner();
        owner.setOwnerId(1L);
        owner.setName("John Doe");

        Car car = new Car();
        car.setCarId(1L);
        car.setModel("Model X");
        car.setMake("Tesla");
        car.setColor("Red");
        car.setStatus(CarStatus.IDEAL);
        car.setOwner(owner);

        when(carOwnerRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car result = carOwnerService.registerCar(requestDTO);

        assertNotNull(result);
        assertEquals("Model X", result.getModel());
        verify(carOwnerRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void testRegisterCar_Failure_OwnerNotFound() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setOwnerId(99L);

        when(carOwnerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CarOwnerNotFoundException.class, () -> carOwnerService.registerCar(requestDTO));

        verify(carOwnerRepository, times(1)).findById(99L);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void testGetCarStatus_Success() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setCarId(1L);
        requestDTO.setOwnerId(1L);

        Car car = new Car();
        car.setCarId(1L);
        car.setModel("Model X");
        car.setStatus(CarStatus.IDEAL);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        Car result = carOwnerService.getCarStatus(requestDTO);

        assertNotNull(result);
        assertEquals(CarStatus.IDEAL, result.getStatus());
        verify(carRepository, times(1)).findById(1L);
    }

    @Test
    void testGetLeaseHistory_Success() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setOwnerId(1L);

        List<Lease> leases = Arrays.asList(new Lease(), new Lease());

        when(carOwnerRepository.existsById(1L)).thenReturn(true);
        when(leaseRepository.findByCar_Owner_OwnerId(1L)).thenReturn(leases);

        List<Lease> result = carOwnerService.getLeaseHistory(requestDTO);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(carOwnerRepository, times(1)).existsById(1L);
        verify(leaseRepository, times(1)).findByCar_Owner_OwnerId(1L);
    }
}
