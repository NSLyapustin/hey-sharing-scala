package infrastructure.endpoint

import cats.effect.Sync
import cats.syntax.all._
import domain.Auth
import domain.item.{Item, ItemService}
import domain.user.{User, UserNotFoundError}
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import tsec.authentication.{AugmentedJWT, SecuredRequestHandler, asAuthed}
import tsec.jwt.algorithms.JWTMacAlgo

class ItemEndpoints[F[_]: Sync, Auth: JWTMacAlgo] extends Http4sDsl[F] {
  implicit val itemDecoder: EntityDecoder[F, Item] = jsonOf

  private def createItemEndpoint(
                            itemService: ItemService[F]
                            ): AuthEndpoint[F, Auth] = {
    case req @ POST -> Root asAuthed user =>
      user.id match {
        case Some(id) => Ok(
          for {
            item <- req.request.as[Item]
            result <- itemService.createItem(item, id)
          } yield result
        )
        case None => NotFound(UserNotFoundError)
      }
  }

  def endpoints(
                 itemService: ItemService[F],
                 auth: SecuredRequestHandler[F, Long, User, AugmentedJWT[Auth, Long]],
               ): HttpRoutes[F] = {
    val authEndpoints: AuthService[F, Auth] = {
      val allRoles =
        createItemEndpoint(itemService)

      Auth.allRoles { allRoles }
    }

    auth.liftService(authEndpoints)
  }
}

object ItemEndpoints {
  def endpoints[F[_]: Sync, Auth: JWTMacAlgo](
                                               itemService: ItemService[F],
                                               auth: SecuredRequestHandler[F, Long, User, AugmentedJWT[Auth, Long]],
                                             ): HttpRoutes[F] =
    new ItemEndpoints[F, Auth].endpoints(itemService, auth)
}

