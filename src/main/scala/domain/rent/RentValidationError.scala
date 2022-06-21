package domain.rent

trait RentValidationError extends Product with Serializable
case object CannotRentItem extends RentValidationError
