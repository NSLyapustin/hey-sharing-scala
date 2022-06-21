package domain.rent.Validation

trait RentValidationError extends Product with Serializable
case object CannotRentItem extends RentValidationError
