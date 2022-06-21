package infrastructure.endpoint

import cats.effect.Sync
import domain.rent.{CannotRentItem, Rent, RentService}
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import tsec.jwt.algorithms.JWTMacAlgo
import tsec.authentication.{AugmentedJWT, SecuredRequestHandler, asAuthed}
import io.circe.generic.auto._
import org.http4s.circe.jsonOf
import cats.syntax.all._
import domain.Auth
import domain.user.User
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder

class RentEndpoints[F[_]: Sync, Auth: JWTMacAlgo] extends Http4sDsl[F] {

  implicit val rentDecoder: EntityDecoder[F, Rent] = jsonOf

  private def createRentEndpoint(
                                  rentService: RentService[F]
                                ): AuthEndpoint[F, Auth] = {
    case req @ POST -> Root / "toTenant" / LongVar(itemId) asAuthed user =>
      user.id match {
        case Some(id) =>
          val result = for {
            rent <- req.request.as[Rent]
            result <- rentService.createRent(rent, itemId, id).value
          } yield result
          result.flatMap {
            case Right(item) => Ok(item)
            case Left(_) => Forbidden()
          }
        case None => NotFound(CannotRentItem)
      }
  }

  def endpoints(
                 itemService: RentService[F],
                 auth: SecuredRequestHandler[F, Long, User, AugmentedJWT[Auth, Long]],
               ): HttpRoutes[F] = {
    val authEndpoints: AuthService[F, Auth] = {
      val allRoles =
        createRentEndpoint(itemService)

      Auth.allRoles { allRoles }
    }

    auth.liftService(authEndpoints)
  }
}

object RentEndpoints {
  def endpoints[F[_]: Sync, Auth: JWTMacAlgo](
                                               rentService: RentService[F],
                                               auth: SecuredRequestHandler[F, Long, User, AugmentedJWT[Auth, Long]],
                                             ): HttpRoutes[F] =
    new RentEndpoints[F, Auth].endpoints(rentService, auth)
}
