package domain.item

trait ItemValidationError extends Product with Serializable
case object ItemNotFoundError extends ItemValidationError
case object UpdateNotAllowed extends ItemValidationError

