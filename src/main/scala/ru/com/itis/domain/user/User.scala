package ru.com.itis.domain.user

case class User(
  id: Long,
  username: String,
  hash: String,
  role: Role,
  products: List[Product],
  favoriteProducts: List[Product]
)

sealed trait Role {
  case object Admin extends Role
  case object Moder extends Role
  case object Default extends Role
}
