## Antaeus

Welcome to my solution! 

Bellow you can see my whole description. The first sections as you can see are more or less the same.

### Building

```
./gradlew build
```

### Running

*Running through docker*

Install docker for your platform

```
make docker-run
```

And run script 
```
docker-start.sh 
```
### App Structure
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

The challenge was completed and a payment service that will run at the first day of each month was implemented. The scheduling of the billing service will begin once you started the app. 
There is also an option to initiate the scheduled payment service by a Rest call.
 
 More info about the payment service and the billing process is available on the sections [BillingService](#BillingService) and [Scheduler](#Scheduler).

## Components
 
### Models
*Changes in the model layer:*

In the Model Layer the Billing model class was added. 
The Billing class corresponds to the billings that are created by the Billing service for each customer.

A designated table Billings was created. 
In this table the Billing of each customer is stored for every month.

In the Customer class the balance property was added. 
(Of course Antaeus knows how much money each customer has in it's account.)
Each Customer have a Balance. In order for a successful payment of an invoice, the balance of the customer should be greater than the invoice amount.

ExchangeRate model class was added in order to have a singleton object of this class to have stored the rates for the currency conversion

The Model Layer changes: 
```
* Customer
    * id
    * balance       //Each customer have balance, money left to spend
        * value
        * currency

* Billing
    * id
    * customerId
    * totalAmount    //the total amount of all invoices that customer have to pay in the specific payment
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

Steps of billing service:
```
* For each customer get the pending invoices from the database
* Call the charge method from the paymentProvider
* If the customer is charged successfully the amount of the invoice is added to the totalAmount of the customer's billing
* The totalAmount of the billing for the customer is calculated
* A billing instance is created and is stored in the database.
    (Even if the totalAmount of billing is zero is still stored in the database for logging and archive reasons)
```

### Scheduler
The scheduler class is:
 
`PaymentScheduler`

For the scheduling of the payment service the `Timer().schedule()` function is used from `kotlin-stdlib/kotlin.concurrent/java.util.Timer/` schedule  package.
This function schedules an action to be executed at the specified time.
The method that was implemented is: 
```
scheduleNextPayment(billingAction: ((String) -> List<Billing?>?), date: Date = calculateNextBillingDate())
```
This method has as a parameter the billing action with is the main Billing method from BillingService (BillAllCustomers).
and the date, where it calculates the next payment day.

Having the date as a parameter is also useful for testing the scheduler

Steps of scheduleNextPayment:
```
* Firstly the scheduler calculates the next payment date (1st of next month) and set the timer().schedule(). 
* When the time comes, the scheduler is triggered and the BillingAction method that was defined
in the BillingServices is initialized. 
* It fetches all the pending invoices for each customer, check if the customer is able to pay for them, calculate the totalAmount for payment and creates the Billing for each Customer
* Finally, using recursion it reschedules the BillingService for the next month. Recalculate the 1st of next month and set the scheduler.
```

* Two schedule functions were implemented. One for billing all customers and one for billing a specified customer. 
The logic behind this implementation is that possibly in the future there might exist a customer that must follow different billing service

### CurrencyConverter
In case that the invoices are in different currency than the customer's balance a currencyConverter is used.
The base currency in which the currencies are  converted is DKK. After the calculations the totalAmount is converted back to the customer's currency 

For the conversion of currencies ExchangeRateProvider is used. As an immutable object it contains all the different rates corresponding to the base currency (DKK)

### BillingServicePaymentProvider
Functionality of `charge` method:
 * On payment day for every customer all the pending invoices are checked by charge method. 
 
 Steps of charge method:
 ```
  * If the currency of the balance of the customer is different than the one of the invoice, both are converted to the base currency
  * If the customer have enough balance, he is charged. T
  * The invoice status is changed to PAID and the value of the invoice is removed from his balance.
  * Customer balance and the status of the invoice are updated in the database
  * The method returns true if the invoice was successfully paid and the customer was charged
```

### REST API
The second version (v2) of the api was implemented. The new api calls are:

* **POST:**
    ```
    /v2/payments       //initiate monthly payment(billing) service for all customers
    /v2/payments/{id}  //initiate monthly payment(billing) service for customer with specific id
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

Oh WAIT! Is Antaeus the one who is actually do the charging? Now I gοt it, scratch the last one! 
