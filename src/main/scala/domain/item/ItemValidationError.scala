package domain.item

sealed trait ItemValidationError extends Product with Serializable
case object ItemNotFoundError extends ItemValidationError

