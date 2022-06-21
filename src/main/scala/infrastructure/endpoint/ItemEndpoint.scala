package infrastructure.endpoint

import cats.effect.Sync
import cats.syntax.all._
import domain.Auth
import domain.item.Models.{Category, Item, ItemStatus}
import domain.item.Service.ItemService
import domain.item.Validation.ItemNotFoundError
import domain.rent.Models.Period
import domain.user.Models.User
import domain.user.Validation.UserNotFoundError
import infrastructure.endpoint.Pagination.{OptionalOffsetMatcher, OptionalPageSizeMatcher}
import org.http4s.{EntityDecoder, HttpRoutes, QueryParamDecoder}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import tsec.authentication.{AugmentedJWT, SecuredRequestHandler, asAuthed}
import tsec.jwt.algorithms.JWTMacAlgo
import io.circe.syntax._

class ItemEndpoints[F[_]: Sync, Auth: JWTMacAlgo] extends Http4sDsl[F] {
  implicit val itemDecoder: EntityDecoder[F, Item] = jsonOf

  implicit val statusQueryParamDecoder: QueryParamDecoder[ItemStatus] =
    QueryParamDecoder[String].map(ItemStatus.withName)

  implicit val categoryQueryParamDecoder: QueryParamDecoder[Category] =
    QueryParamDecoder[String].map(Category.withName)

  implicit val periodQueryParamDecoder: QueryParamDecoder[Period] =
    QueryParamDecoder[String].map(Period.withName)

  object StatusMatcher extends OptionalMultiQueryParamDecoderMatcher[ItemStatus]("status")
  object CategoryMatcher extends OptionalMultiQueryParamDecoderMatcher[ItemStatus]("category")
  object PeriodMatcher extends OptionalMultiQueryParamDecoderMatcher[ItemStatus]("period")

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

  private def getItemEndpoint(
                             itemService: ItemService[F]
                             ): HttpRoutes[F] = HttpRoutes.of[F] {
    case req@GET -> Root / LongVar(id) =>
      itemService.get(id).value.flatMap {
        case Right(item) => Ok(item.asJson)
        case Left(ItemNotFoundError) => NotFound(ItemNotFoundError)
      }
  }

  private def getItemsEndpoint(
                     itemService: ItemService[F]
                     ): HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ GET -> Root:? OptionalPageSizeMatcher(limit) :? OptionalOffsetMatcher(offset,
    ) =>
      for {
        retrieved <- itemService.list(limit.getOrElse(20), offset.getOrElse(0))
        resp <- Ok(retrieved.asJson)
      } yield resp
  }
  
  private def updateItemEndpoint(
                                itemService: ItemService[F]
                                ): AuthEndpoint[F, Auth] = {
    case req @ PUT -> Root asAuthed user =>
      user.id match {
        case Some(id) =>
          val result = for {
            item <- req.request.as[Item]
            result <- itemService.update(item, id).value
          } yield result
          result.flatMap {
            case Right(item) => Ok(item)
            case Left(_) => Forbidden()
          }
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
          .orElse(updateItemEndpoint(itemService))

      Auth.allRoles { allRoles }
    }

    val unauthorized = getItemEndpoint(itemService) <+> getItemsEndpoint(itemService)

    unauthorized <+> auth.liftService(authEndpoints)
  }
}

object ItemEndpoints {
  def endpoints[F[_]: Sync, Auth: JWTMacAlgo](
                                               itemService: ItemService[F],
                                               auth: SecuredRequestHandler[F, Long, User, AugmentedJWT[Auth, Long]],
                                             ): HttpRoutes[F] =
    new ItemEndpoints[F, Auth].endpoints(itemService, auth)
}

