package creators.entities

import scala.collection.mutable.HashMap
import tools.Tools

class Model(name:String, attributs:Map[String, String]) {
  require(name != null && attributs != null)
  val nameLowerCase:String = name.toLowerCase()
  val nameFirstLowerCase:String = Tools.firstCharToLower(name)
  val nameFirstUpperCase:String = Tools.firstCharToUpper(name)
  val attribs:Map[String, String] = attributs
}