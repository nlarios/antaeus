package io.pleo.antaeus.core.exceptions

import java.lang.Exception

class BillingServiceException(message: String) :Exception("Exception at scheduled billing service with optional message: $message")