# Software Engineering 2 project
Politecnico di Milano. Antonini Paolo, 858242; Corneo Andrea, 849793.

## myTaxiService
>The government of a large city aims at optimizing its taxi service. In particular, it  wants  to:  (1)  simplify  the  access  of  passengers  to  the  service,  and  (2)  guarantee  a  fair management of taxi queues. 
Passengers  can  request  a  taxi  either  through  a  web  application  or  a  mobile  app.  The system answers to the request by informing the passenger about the code of  the incoming taxi and the waiting time. 
Taxi  drivers  use  a  mobile  application  to  inform  the  system  about  their  availability and to confirm that they are going to take care of a certain call.   The system guarantees a fair management of taxi queues. In particular, the city is  divided  in  taxi  zones  (approximately  2  km^2  each).  Each  zone  is  associated  to  a  queue of taxis. The system automatically computes the distribution of taxis in the  various  zones  based  on  the  GPS  information  it  receives  from  each  taxi.  When  a  taxi is available, its identifier is stored in the queue of taxis in the corresponding  zone.   
When  a  request  arrives  from  a  certain  zone,  the  system  forwards  it  to  the  first  taxi  queuing  in  that  zone.  If  the  taxi  confirms,  then  the  system  will  send  a  confirmation to the passenger. If not, then the system will forward the request to  the  second  in  the  queue  and  will,  at  the  same  time,  move  the  first  taxi  in  the  last  position in the queue.  
Besides  the  specific  user  interfaces  for  passengers  and  taxi  drivers,  the  system  offers  also  programmatic  interfaces  to  enable  the  development  of  additional  services (e.g., taxi sharing) on top of the basic one. 
A user can reserve a taxi by specifying the origin and the destination of the ride.  The reservation has to occur at least two hours before the ride. In this case, the  system confirms the reservation to the user and allocates a taxi to the request 10  minutes before the meeting time with the user. 

+ **Requirements Analysis and Specification Document** submission deadline 06/11/2015 - [link](./Deliveries/1_RASD.pdf)
+ **Design Document** submission deadline 04/12/2015 - [link](./Deliveries/2_DD.pdf)


## GlassFish 4.1.1
>Code inspection (e.g., code analysis, visual inspection, reverse engineering, etc.) is systematic examination (often known as peer review) of computer source code. It is intended to find mistakes overlooked in the initial development phase, improving both the overall quality of software and the developers' skills. Reviews are done in various forms such as pair programming, informal walkthroughs, and formal inspections. You are to apply Code Inspection techniques (supported by the review checklist) for the purpose of evaluating the general quality of selected code extracts from a release of the Glassfish 4.1 application server.   
In the scope of this assignment, you will be given a selected number of classes extracted from said software release. Said selection is done systematically, and will assign different sets of classes to different groups. The systematic selection makes sure that the difficulty of the assignment is homogeneous per every group and takes into account the fact that we have groups of different sizes. We have allocated about 80 lines of code per person.

+ **Inspection document** submission deadline 05/01/2016 - [link](./Deliveries/3_CI.pdf)


## Future assignments
+ **Testing document** submission deadline 21/01/2016
+ **Function points document** submission deadline 30/01/2016
