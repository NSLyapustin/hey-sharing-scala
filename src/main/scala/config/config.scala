package config

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

package object config {
  implicit val srDec: Decoder[ServerConfig] = deriveDecoder
  implicit val dbconnDec: Decoder[DatabaseConnectionsConfig] = deriveDecoder
  implicit val dbDec: Decoder[DatabaseConfig] = deriveDecoder
  implicit val psDec: Decoder[HeySharingConfig] = deriveDecoder
}
