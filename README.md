# Trimble Cars - Car Lease Management Application

## Project Overview 
**Trimble Cars** is a comprehensive car lease management application designed to streamline operations for car owners, end customers, and administrators. It provides an interface for car owners to lease their vehicles, allows customers to manage car rentals, and empowers administrators with full operational control.


## Key Features  
1. **Car Owners**:
   - Register cars for leasing.
   - View the car's current status (`Idle`, `On Lease`, `On Service`).
   - Access lease history.
2. **End Customers**:
   - Register and view available cars for lease.
   - Start a lease (max two cars at a time).
   - End a lease without any time restrictions.
   - View their own lease history.
3. **Admins**:
   - Perform all operations available to car owners and end customers for all users and cars.



————-


## Tech Stack 
- **Programming Language**: Java
- **Framework**: Spring Boot
- **Build Tool**: Maven
- **Database**: H2 (In-memory database)



————



## Prerequisites 
Before running the application, ensure the following are installed:
- **JDK 11** or higher
- **Maven 3.8** or higher
- **Postman** 



————




Application Structure


TrimbleCars
├── src/main/java
│   ├── com.trimblecars
│   │   ├── controller        # REST controllers
│   │   ├── service           # Business logic
│   │   ├── repository        # Data access layer
│   │   ├── model             # Entity and DTO classes
│   │   ├── config            # Application configuration
│   │   └── TrimbleCarsApp    # Main application class
├── src/main/resources
│   ├── static/images         # Application assets (e.g., logo)
│   ├── application.properties # Configuration file
│   └── data.sql              # Sample data for testing
├── src/test/java             # Unit and integration tests
├── target                    # Compiled application files
├── logs                      # Application logs
├── Postman                   # Postman collection and documentation
├── README.md                 # Project documentation
└── Lease_History_Report.pdf  # Sample report PDF



—————



Database Configuration
The application uses an H2 in-memory database. By default, the database console is accessible at:
* URL: http://localhost:8080/h2-console
* Username: sa
* Password: (leave blank)
You can update the configuration in the application.properties file.



—————-



Logs and Reports

* Logs: Application logs are stored in the logs/ directory.
* Test Reports: Test execution reports are available in the target/surefire-reports/ directory.



——————



Optional Features

1. Authentication: Integrate with a third-party authentication provider (e.g., OAuth).
2. Car Models Integration: Fetch car models from portals like AutoCar or Overdrive.
3. Lease History Export: Generate printable reports in PDF format.


———————

Contact
Name: Naga Maheswara Reddy Email: naga.maheswara.57@gmail.com Phone: 7989285169




