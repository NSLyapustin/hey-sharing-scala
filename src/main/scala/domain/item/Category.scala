package domain.item

final case class Category(categoryRepl: String)

object Category {
  val Vehicle: Category = Category("Vehicle")
  val Appliances: Category = Category("Appliances")
  val Electronics: Category = Category("Electronics")
  val Furniture: Category = Category("Furniture")
  val HobbiesAndLeisure: Category = Category("HobbiesAndLeisure")
  val Clothes: Category = Category("Clothes")
  val Other: Category = Category("Other")
}