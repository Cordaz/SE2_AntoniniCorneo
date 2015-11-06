module myTaxiService


-----  SIGNATURES  ------------------------------------------------------------

abstract sig Person {
}

sig Customer extends Person {}

sig Operator extends Person {}

sig RegisteredCustomer extends Customer {
}

sig TaxiDriver extends Person {
}

sig Taxi {
	gpsLocation: one GPS,
	driver: one TaxiDriver,
}

sig GPS{}

sig Address {
	isIn: one Area,
} 

sig Date {}

sig TaxiRequest {
	date: one Date,
	origin: one Address,
	destination: one Address,
	allocatedTaxi: some Taxi, -- there may be more than 4 passengers
	customer: one Customer,
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
} {
	requestDate = taxiRequest.date 
	origin = taxiRequest.origin
	destination = taxiRequest.destination
	customer = taxiRequest.customer
	reservationDate != requestDate
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


-----  ASSERTIONS  ------------------------------------------------------------

-- No multiple allocations of the same taxi to the same request.
assert noMultipleAllocations {
	all disj t1, t2: TaxiRequest |
		(t1.date = t2.date) implies ((t1.allocatedTaxi & t2.allocatedTaxi) = none)
}
check noMultipleAllocations

--A customer can have only one reservation at the same moment. 
assert oneActiveReservation {
	all disj r1, r2: TaxiReservation |
		(r1.requestDate = r2.requestDate) implies ((r1.customer & r2.customer) = none)
}
check oneActiveReservation


-----  PREDICATES  ------------------------------------------------------------

pred showNoReservation() {
	#Customer > 0
	#RegisteredCustomer = 0
	#TaxiReservation = 0
	#FaultReport = 0
}
run showNoReservation

pred showFaultReport() {
	#Customer = 1
	#FaultReport = 1
}
run showFaultReport

pred show() {
	#Customer > 0
	#RegisteredCustomer > 0
	#TaxiRequest  > 0
	#Operator  = 0
	#FaultReport  = 0
}
run show
