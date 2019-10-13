package io.pleo.antaeus.core.exceptions

abstract class EntityListIsEmptyException(entity: String) : Exception("$entity is empty")