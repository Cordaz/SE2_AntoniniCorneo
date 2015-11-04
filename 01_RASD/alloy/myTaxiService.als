module myTaxiService


-----  SIGNATURES  ------------------------------------------------------------

abstract sig Person {
--	name: one String,
--	surname: one String,
--	phoneNumber: one String,
}

sig Customer extends Person {}

sig Operator extends Person {}

sig RegisteredCustomer extends Customer {
	email: one String,
	addressRecord: set Address,
}

sig TaxiDriver extends Person {
	driverLicenceNumber: one String,
}

sig Taxi {
--	taxiID: one String,
--	licencePlate : one String,
	onDuty: one Boolean,
	gpsLocation: one GPS,
	driver: one TaxiDriver,
}

sig Boolean {}

sig GPS{}

sig Address {
--	town: one String,
--	street: one String,
--	number: one String,
	isIn: one Area,
} 

sig Date {}

sig TaxiRequest {
	date: one Date,
	origin: one Address,
	destination: one Address,
	allocatedTaxi: some Taxi, -- there may be more than 4 passengers
	customer: one Customer,
	numOfPassengers: one Int
} {
	origin != destination
}

sig TaxiReservation {
	reservationDate: one Date,
	requestDate: one Date,
	origin: one Address,
	destination: one Address,
	taxiRequest: one TaxiRequest,
	customer: one RegisteredCustomer,
	numOfPassengers: one Int
} {
	requestDate = taxiRequest.date 
	origin = taxiRequest.origin
	destination = taxiRequest.destination
	customer = taxiRequest.customer
	reservationDate != requestDate  -- In Java, Date class contains time as well.
	numOfPassengers = taxiRequest.numOfPassengers
}

sig Intersection {}

sig Area {
	adjAreas: some Area, 
	taxiQueue: set Taxi,
	borders: some Intersection,
	contains: some Address,
} 

sig FaultReport {
	operator: one Operator,
	taxiInvolved: one Taxi,
}



-----  FACTS  -----------------------------------------------------------------
--Facts are constraints that restrict the model. RESTR on PROPERTIES
--Facts are part of our specification of our system.
--Any configuration that is an instance of the specification has to satisfy all the facts.


--A taxi driver is registered only once
fact singleTaxiDriverProprieties {
	no disj td1, td2: TaxiDriver |
		((td1.driverLicenceNumber & td2.driverLicenceNumber) = none)
}

--A user can register only once
fact registerOnlyOnce {
	no disj rc1, rc2: RegisteredCustomer |
		((rc1.email & rc2.email) = none)
}


-- All TaxiDrivers have one and only one taxi.
fact oneTaxiPropriety {
	no disj t1, t2: Taxi | t1.driver = t2.driver
}

-- Adjacency is a symmetric, non reflexive property.
fact adjacentProprieties {
	all a, b: Area | (a in b.adjAreas) iff (b in a.adjAreas)  -- symmetry
	all a: Area | a not in a.adjAreas  -- non reflexivity
}

-- A taxi belongs to one and only one taxi queue.
fact noTaxiUbiquity {
	all disj a1, a2: Area | (a1.taxiQueue & a2.taxiQueue) = none
}

-- An address is located in a specific area.
fact noAddressesUbiquity {
	all disj a1, a2: Area | (a1.contains & a2.contains) = none	
}

-- If an address is in an area, then that area contains that address.
fact inverseFunction1 {
	 isIn = ~ contains
}

-- A customer can have only one active request.
fact oneActiveRequest {
	all disj r1, r2: TaxiRequest |
		r1.date = r2.date implies ((r1.customer & r2.customer) = none)
}

-- A taxi is assigned at most one request at the same time.
fact requestAssignation {
	all disj tr1, tr2: TaxiRequest |
		tr1.allocatedTaxi = tr2.allocatedTaxi implies ((tr1.date & tr2.date) = none)
}

-- A request can correspond to at most one reservation.
fact oneReservationOneRequest {
	no disj t1, t2: TaxiReservation | (t1.taxiRequest & t2.taxiRequest) != none
}

--To each request there are a sufficient number of taxi and no more
fact numberOfTaxi {
	no r: TaxiRequest |
		#allocatedTaxi = rem[r.numOfPassengers, 4]
}


-----  ASSERTIONS  ------------------------------------------------------------
--Constraints that were intended to follow from facts of the model
--Assertions state the properties that we expect to hold

-- No multiple allocations of the same taxi to the same request.
assert noMultipleAllocations {
	all disj t1, t2: TaxiRequest |
		(t1.date = t2.date) implies ((t1.allocatedTaxi & t2.allocatedTaxi) = none)
}

check noMultipleAllocations

--A customer can have only one reservation at the same moment 
assert oneActiveReservation {
	all disj r1, r2: TaxiReservation |
		(r1.requestDate = r2.requestDate) implies ((r1.customer & r2.customer) = none)
}

check oneActiveReservation

/*

-----  PREDICATES  ------------------------------------------------------------
--A predicate is a named constraint with zero or more arguments
--When it is used, the arguments are replaced with the instantiating expressions

-- Show a world where a request is due to a reservation.
pred showReservationRequest() {
	all res: TaxiReservation | some req: TaxiRequest | res.taxiRequest = req
} 
run showReservationRequest for 2

*/


pred show() {
	#Customer > 0
	#RegisteredCustomer = 0
--	#TaxiDriver  > 0
--	#Taxi  > 0
--	#Boolean  = 0
--	#GPS > 0
--	#Address  > 0
--	#Date  > 0 
	#TaxiRequest  > 0
--	#TaxiReservation  =0 
--	#Intersection  > 0
--	#Area > 0

	#Operator  = 0
	#FaultReport  = 0
}
run show
