package creators

import tools.Tools
import creators.entities.Model

object WebPattern {

  def createWebCrud(aModel:Model): String = {
    val entityBean = aModel.nameFirstLowerCase
    val entityClass = aModel.nameFirstUpperCase
    var nomsChamps = Tools.getHeaderColumns(aModel.attribs).mkString("\n")
    var champsSaisie = Tools.getHtmlInput(aModel.attribs).mkString("\n")
    var champsReadOnly = Tools.getVarNames(aModel.attribs).map(v => "<td>@%s.%s</td>".format(entityBean, v)).mkString("\n")

    Patterns.crudPattern.format(entityClass, entityClass, nomsChamps, entityClass, champsSaisie, entityBean, champsReadOnly, entityClass, entityBean)
  }

  def createRouteCrud(entity: String): String = {
    Patterns.getRouteLine("GET", "index", entity, Map[String, String]()) + "\n" +
      Patterns.getRouteLine("GET", "create", entity, Map[String, String]()) + "\n" +
      Patterns.getRouteLine("POST", "suppr", entity, Map[String, String]("id" -> "Long")) + "\n"
  }

}