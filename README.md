This project is to test the potential of Java as an Object-Oriented Programming language.
I pushed my understanding of Java further by using multiple classes, inheriting them, and connecting certain classes to PostgreSQL.

Basically, this repo contains code that (crudely) simulates a travel booking system with two actors, namely the client/user/traveller, and the admin.
The former's functionalities can all be accessed through the Main.java class, while the Admin's functionality can be completed through the AdminApprovalTest.java (apologies for
poor choice of nomenclature).
The user can create a new account with their username, password and email ID, but can't login with these credentials unless they have been seperately approved of by the admin.
The admin can approve users by first viewing all available user account creation requests, and also choose the available trip combinations (locations, dates, modes of transport, etc.) from which the user must choose.
The user can choose to book a new trip, view existing booked trips, and cancel a trip.
There is a payment section here, but it is hard-coded as implementing actual payment gateways is beyond the scope of this project.
All transactions, viz. trip booking, user account creation/approval, payment, are recorded in the appropriate databases.
