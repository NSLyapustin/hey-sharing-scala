package domain.item.Dto

import domain.item.Models.Category

case class ItemCreateRequestDto(
                               name: String,
                               price: Double,
                               image: String,
                               description: String,
                               category: Category,
                               address: String
                               )