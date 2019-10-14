## Antaeus

Antaeus (/ænˈtiːəs/), in Greek mythology, a giant of Libya, the son of the sea god Poseidon and the Earth goddess Gaia. He compelled all strangers who were passing through the country to wrestle with him. Whenever Antaeus touched the Earth (his mother), his strength was renewed, so that even if thrown to the ground, he was invincible. Heracles, in combat with him, discovered the source of his strength and, lifting him up from Earth, crushed him to death.

Welcome to our challenge.

## The challenge

As most "Software as a Service" (SaaS) companies, Pleo needs to charge a subscription fee every month. Our database contains a few invoices for the different markets in which we operate. Your task is to build the logic that will schedule payment of those invoices on the first of the month. While this may seem simple, there is space for some decisions to be taken and you will be expected to justify them.

## Developing

Requirements:
- \>= Java 11 environment

### Building

```
./gradlew build
```

### Running

There are 2 options for running Anteus. You either need libsqlite3 or docker. Docker is easier but requires some docker knowledge. We do recommend docker though.


*Running through docker*

Install docker for your platform

```
make docker-run
```

And run script `./docker-start.sh` 

*Running Natively*

Native java with sqlite (requires libsqlite3):

If you use homebrew on MacOS `brew install sqlite`.

```
./gradlew run
```


### App Structure
The code given is structured as follows. Feel free however to modify the structure to fit your needs.
```
├── pleo-antaeus-app
|       main() & initialization
|
├── pleo-antaeus-core
|       This is probably where you will introduce most of your new code.
|       Pay attention to the PaymentProvider and BillingService class.
|
|
├── pleo-antaeus-data
|       Module interfacing with the database. Contains the database models, mappings and access layer.
|
├── pleo-antaeus-models
|       Definition of the "rest api" models used throughout the application.
|
├── pleo-antaeus-rest
|        Entry point for REST API. This is where the routes are defined.
└──
```


# Solution

##Components
 
### Models
*Changes in models:*

In the Model Layer the Billing model class was added. 
The Billing class corresponds to the billing that are created by the payment service for each customer.

A designated table Billings was created. 
In this table the Billing of each customer is stored for every month, even when a customer have zero expenses a billing is created for logging and archiving purposes.

In the Customer class the balance property was added. 
(Of course Antaeus knows how much money each customer has in it's account.)
Each Customer have a Balance. In order for a successful payment of an invoice, the balance of the customer should be greater than the invoice amount.

ExchangeRate model class was added

The Model Layer changes: 
```
* Customer
    * id
    * balance       //Each customer have balance
        * value
        * currency

* Billing
    * id
    * customerId
    * totalAmount    //the total amount of every invoice that have to pay in the specific payment
        * value
        * currency

 * ExcangeRate
    *currency       //the base currency in which all the conversions are made
    *rates (Map)    // the rates corresponding to the base currency
```

### Database 
The EER diagram of the Antaeus Database:

![alt text](./AntaeusDB.png)

### Data Access Layer
The AntaeusDal is divided into three different DAL files, each for every db table.
#### CustomerDal
For transactions with the Customer Table:
* fetchCustomer()
* fetchAll()

#### InvoiceDal
For transactions with the Invoice Table. New functions implemented:

 * `fetchInvoicesByCostumerAndStatus(id: Int, status: InvoiceStatus)` :
    * Fetch all Invoices of customer with  id with the specified status 
     
* `fetchInvoicesByCostumer(id:Int)` :
    * Fetch all Invoices of customer with  id
#### BillingDal
For transactions with the Billing Table.  New functions implemented:
    
 * `fetchInvoicesByCostumerAndStatus(id: Int, status: InvoiceStatus)` :
    * Fetch all Invoices of customer with  id with the specified status 
     
* `fetchInvoicesByCostumer(id:Int)` :
    * Fetch all Invoices of customer with  id


### BillingService

`billAllCustomers()`
* Basically a for loop for all customer that calls billCustomer method

`billCustomer(id: Int)`
* All the business logic for the billing is inside this method. The charging, the calculation of the total amount of the billing and the update of the database

### Scheduler
The scheduler i
For the scheduling of the payment service the `Timer().schedule()` function is used  from the java.util.timer package.
This function Schedules an action to be executed at the specified time.
The method that was implemented is: 
```
scheduleNextPayment(billingAction: ((String) -> List<Billing?>?), date: Date = calculateNextBillingDate())
```
This method has as a parameter the billing action with is the main Billing method from BillingService.
and the date, where it calculates the next payment day.

Having the date as a parameter is also helpful for the testing of the sceduler

Steps:

* Firstly the scheduler calculates the next payment date (1st of next month). 
* When the time comes, the scheduler is triggered and the BillingAction method that was defined
in the BillingServices is initialized. 
* It fetches all the pending invoices for each customer, check if the customer is able to pay for them, calculate the totalAmount for payment and creates the Billing for each Customer
* Finally, using recursion it reschedules the BillingService for the next month. Recalculate the 1st of next month and set the scheduler.

### CurrencyConverter
In case that the invoices are in different currency than the customer's balance a currencyConverter is used.
The base currency in which the currencies are  converted is DKK. After the calculations the totalAmount is converted back to the customer's currency 

For the conversion of currencies ExchangeRateProvider is used. As an immutable object it contains all the different rates corresponding to the base currency (DKK)

### BillingServicePaymentProvider
Functionality of `charge` method:
 * On payment day for every customer all the pending invoices are checked by charge method. 
 
 Steps of charge method:
  * If balance of customer is different than the one of the invoice, both are converted to the base currency
  * If the customer have enough balance, he is charged. T
  * The invoice status is changed to PAID and the value of the invoice is removed from his balance.
  * Customer balance and the status of the invoice are 
 The method returns true if the invoice was successfully paid and the customer was charged


### REST API
The second version (v2) of the api was implemented. The new api calls are:

* **POST:**
    ```
    /v2/payments       //iniate monthly payment(billing) service for all customers
    /v2/payments/{id}  //iniate monthly payment(billing) service for customer with specific id
  ```
* **GET:**
    ```
    /v2/billings        //get all billings that have been printed
    /v2/billings/{id}   //get billing with specific id
  ```



## Testing
Both unit and integration tests are implemented
Almost every method of the core module has its own unit test.

*Most important test classes:*

* `BillingServiceTest`
Unit tests for BillingService, billing of all customer, billing of specific customer
* `BillingPaymentProviderTest`
Unit test for BillingPaymentProvider charge
* `PaymentSchedulerIntegrationTest`
Integration test for scheduler. It runs the scheduler for specific date, one second later. It waits one second and check if the billing table has new records.
 

## Conclusion
Antaeus was pretty mighty but as he couldn't beat Hercules, he couldn't run away from the even mightier Pleo's payment service. 
At the first of every month he has to pay his pending invoices!
