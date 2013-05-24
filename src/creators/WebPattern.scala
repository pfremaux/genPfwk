package creators

import tools.Tools

object WebPattern {

  def createWebCrud(entity: String, params: Map[String, String]): String = {
    val entityBean = Tools.firstCharToLower(entity)
    val entityClass = Tools.firstCharToUpper(entity)
    var nomsChamps = Tools.getEnteteColonnes(params).mkString("\n")
    var champsSaisie = Tools.getHtmlInput(params).mkString("\n")
    var champsReadOnly = Tools.getVarNames(params).map(v => "<td>@%s.%s</td>".format(entityBean, v)).mkString("\n")

    Patterns.crudPattern.format(entityClass, entityClass, nomsChamps, entityClass, champsSaisie, entityBean, champsReadOnly, entityClass, entityBean)
  }

  def createRouteCrud(entity: String): String = {
    Patterns.getRouteLine("GET", "index", entity, Map[String, String]()) + "\n" +
      Patterns.getRouteLine("GET", "create", entity, Map[String, String]()) + "\n" +
      Patterns.getRouteLine("POST", "suppr", entity, Map[String, String]("id" -> "Long")) + "\n"
  }

}