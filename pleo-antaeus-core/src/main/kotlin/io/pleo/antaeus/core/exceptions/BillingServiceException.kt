package io.pleo.antaeus.core.exceptions

import java.lang.Exception
import java.util.*

class BillingServiceException(message: String) :Exception("Exception at scheduled billing service with optional message: $message")