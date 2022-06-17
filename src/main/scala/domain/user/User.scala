package domain.user

case class User(
  id: Long,
  username: String,
  email: String,
  role: Role,
  products: List[Product],
  favoriteProducts: List[Product]
)

sealed trait Role {
  case object Admin extends Role
  case object Moder extends Role
  case object Default extends Role
}
