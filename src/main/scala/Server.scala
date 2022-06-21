import cats.effect.{Blocker, ConcurrentEffect, ContextShift, ExitCode, IO, IOApp, Resource, Timer}
import doobie.util.ExecutionContexts
import io.circe.config.parser
import org.http4s.server.{Router, Server => H4Server}
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import tsec.authentication.SecuredRequestHandler
import tsec.mac.jca.HMACSHA256
import tsec.passwordhashers.jca.BCrypt
import config._
import config.psDec
import domain.Auth
import domain.user.UserValidationInterpreter
import domain.item._
import domain.rent.{RentService, RentValidationInterpreter}
import domain.users.UserService
import infrastructure.endpoint.{ItemEndpoints, RentEndpoints, UserEndpoints}
import infrastructure.repository.{DoobieAuthRepositoryInterpreter, DoobieItemRepositoryInterpreter, DoobieRentRepositoryInterpreter, DoobieUserRepositoryInterpreter}

object Server extends IOApp {
  def createServer[F[_]: ContextShift: ConcurrentEffect: Timer]: Resource[F, H4Server[F]] =
    for {
      conf <- Resource.eval(parser.decodePathF[F, HeySharingConfig]("heysharing"))
      serverEc <- ExecutionContexts.cachedThreadPool[F]
      connEc <- ExecutionContexts.fixedThreadPool[F](conf.db.connections.poolSize)
      txnEc <- ExecutionContexts.cachedThreadPool[F]
      xa <- DatabaseConfig.dbTransactor(conf.db, connEc, Blocker.liftExecutionContext(txnEc))
      key <- Resource.eval(HMACSHA256.generateKey[F])
      authRepo = DoobieAuthRepositoryInterpreter[F, HMACSHA256](key, xa)
      userRepo = DoobieUserRepositoryInterpreter[F](xa)
      userValidation = UserValidationInterpreter[F](userRepo)
      userService = UserService[F](userRepo, userValidation)
      itemRepo = DoobieItemRepositoryInterpreter[F](xa)
      itemValidation = ItemValidationInterpreter(itemRepo)
      itemService = ItemService(itemRepo, itemValidation)
      rentRepo = DoobieRentRepositoryInterpreter[F](xa)
      rentValidation = RentValidationInterpreter(itemRepo)
      rentService = RentService(rentRepo, rentValidation, itemService)
      authenticator = Auth.jwtAuthenticator[F, HMACSHA256](key, authRepo, userRepo)
      routeAuth = SecuredRequestHandler(authenticator)
      httpApp = Router(
        "/rent" -> RentEndpoints.endpoints[F, HMACSHA256](rentService, routeAuth),
        "/items" -> ItemEndpoints.endpoints[F, HMACSHA256](itemService, routeAuth),
        "/users" -> UserEndpoints
          .endpoints[F, BCrypt, HMACSHA256](userService, BCrypt.syncPasswordHasher[F], routeAuth),
      ).orNotFound
      _ <- Resource.eval(DatabaseConfig.initializeDb(conf.db))
      server <- BlazeServerBuilder[F](serverEc)
        .bindHttp(conf.serverConfig.port, conf.serverConfig.host)
        .withHttpApp(httpApp)
        .resource
    } yield server

  def run(args: List[String]): IO[ExitCode] = createServer.use(_ => IO.never).as(ExitCode.Success)
}
