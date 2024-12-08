package com.Trimble.TrimbleCars.Request;


public class LeaseRequestDTO {
    private Long customerId;
    private Long carId;
    private Long leaseId;
    

    
	// Getters and Setters

    public Long getLeaseId() {
		return leaseId;
	}

	public void setLeaseId(Long leaseId) {
		this.leaseId = leaseId;
	}
	
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }
}

