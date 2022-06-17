package config

final case class ServerConfig(host: String, port: Int)
final case class HeySharingConfig(db: DatabaseConfig, serverConfig: ServerConfig)
