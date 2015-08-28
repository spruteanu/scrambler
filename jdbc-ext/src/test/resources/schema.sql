/*adopted example from http://www.telerik.com/help/openaccess-classic/quickstart-carrental-sample-database.html*/
CREATE TABLE Cars(
  CarID INT IDENTITY(1,1) NOT NULL,
  TagNumber varchar(20) NULL,
  Make varchar(50) NULL,
  Model varchar(50) NOT NULL,
  CarYear SMALLINT NULL,
  Category varchar(50) NULL,
  mp3layer BIT NULL,
  DVDPlayer BIT NULL,
  AirConditioner BIT NULL,
  ABS BIT NULL,
  ASR BIT NULL,
  Navigation BIT NULL,
  Available BIT NULL
)
/

CREATE TABLE RentalRates(
  RentalRateID INT IDENTITY(1,1) NOT NULL,
  Category varchar(50) NULL,
  Daily DECIMAL(20, 2) NULL,
  Weekly DECIMAL(20, 2) NULL,
  Monthly DECIMAL(20, 2) NULL
)
/

CREATE TABLE Employees(
  EmployeeID INT IDENTITY(1,1) NOT NULL,
  EmployeeNumber NCHAR(5) NULL,
  FirstName varchar(32) NULL,
  LastName varchar(32) NOT NULL,
  Title varchar(80) NULL,
  HourlySalary DECIMAL(20, 2) NULL
)
/

CREATE TABLE Customers(
  CustomerID INT IDENTITY(1,1) NOT NULL,
  DrvLicNumber varchar(50) NULL,
  FullName varchar(80) NULL,
  Address varchar(100) NOT NULL,
  Country varchar(100) NOT NULL,
  City varchar(50) NULL,
  State varchar(50) NULL,
  ZIPCode varchar(20) NULL
)
/

CREATE TABLE RentalOrders(
  RentalOrderID INT IDENTITY(1,1) NOT NULL,
  DateProcessed datetime NULL,
  EmployeeID INT NOT NULL,
  CustomerID INT NOT NULL,
  CarID INT NOT NULL,
  TankLevel varchar(40) NULL,
  MileageStart INT NULL,
  MileageEnd INT NULL,
  RentStartDate datetime NULL,
  RentEndDate datetime NULL,
  Days  INT NULL,
  RateApplied DECIMAL(20, 2) NULL,
  OrderTotal  DECIMAL(20, 2) NULL,
  OrderStatus varchar(50) NULL
)
/

ALTER TABLE Cars ADD CONSTRAINT PK_RentalRates PRIMARY KEY (CarID)
/

ALTER TABLE RentalRates ADD CONSTRAINT PK_RentalRates PRIMARY KEY (RentalRateID)
/

ALTER TABLE Employees ADD CONSTRAINT PK_Employees PRIMARY KEY (EmployeeID)
/

ALTER TABLE Customers ADD CONSTRAINT PK_Customer PRIMARY KEY (CustomerID)
/

ALTER TABLE RentalOrders ADD CONSTRAINT PK_RentalOrder PRIMARY KEY (RentalOrderID)
/

ALTER TABLE RentalOrders ADD CONSTRAINT FK_Cars FOREIGN KEY(CarID) REFERENCES Cars (CarID)
/

ALTER TABLE RentalOrders ADD CONSTRAINT FK_Customers FOREIGN KEY(CustomerID) REFERENCES Customers (CustomerID)
/

ALTER TABLE RentalOrders ADD CONSTRAINT FK_Employees FOREIGN KEY(EmployeeID) REFERENCES Employees (EmployeeID)
/
