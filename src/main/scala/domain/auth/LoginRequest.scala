package domain.auth

import domain.user.Models.{Role, User}
import tsec.passwordhashers.PasswordHash

final case class LoginRequest(
                               username: String,
                               password: String,
                             )

final case class SignupRequest(
                                username: String,
                                email: String,
                                password: String
                              ) {
  def asUser[A](hashedPassword: PasswordHash[A]): User = User(
    username,
    email,
    hashedPassword.toString,
    role = Role.Default
  )
}
