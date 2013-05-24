package creators

import tools.Tools

object DbPatterns {

  def createSql(entityName: String, attributs: Map[String, String]): String = {
    var colonnes = Tools.getSql(attributs).mkString(",\n\t")
    
    Patterns.sqlPattern.format(entityName.toLowerCase(), entityName.toUpperCase(), entityName.toLowerCase(), 
        colonnes, entityName.toUpperCase(), entityName.toLowerCase())
  }

  def createEntity(entityName: String, attributs: Map[String, String]): String = {
    var nomEntite = entityName(0).toUpper + entityName.substring(1)
    var listParam = attributs.keys.map(cle => cle + " : " + attributs(cle))
    "\tcase class %s (%s)".format(nomEntite, listParam.mkString(", "))
  }

  def createParser(entityName: String, attributs: Map[String, String]): String = {
    var nomParser = entityName(0).toLower + entityName.substring(1)
    var nomEntite = entityName(0).toUpper + entityName.substring(1)
    var listParam = attributs.keys.map(cle => cle + " : " + attributs(cle))
    var rowParser = attributs.keys.map(cle => Patterns.rowParserPattern.format(attributs(cle), cle))

    val parametres = "(" + listParam.mkString(", ") + ")"
    val methode = "\tval %s = { %s map { case %s => %s(%s) } }".format(nomParser,
      rowParser.mkString(" ~ "),
      attributs.keys.mkString(" ~ "),
      nomEntite,
      (Map("Some(id)" -> "Long") ++ attributs).filterKeys(s => !"id".equals(s)).keys.mkString(", "))
    methode
  }

  def createMethod(entityName: String, attributs: Map[String, String]): String = {
    val paramSignature: String = Tools.toSignature(attributs)
    val listeVarNames = Tools.getVarNames(attributs)
    val parameterizedValues = "{" + listeVarNames.mkString("}, {") + "}"
    val mappingValues = listeVarNames.map(s => "'" + s + " -> " + s).mkString(", ")
    """
    def create(%s) {
    DB.withConnection { implicit c =>
      Sql.sql("insert into %s (%s) values (%s)").on(
        %s).executeUpdate()
    }}
    """.format(paramSignature, entityName.toUpperCase(), listeVarNames.mkString(", "), parameterizedValues, mappingValues)
  }

  def createDeleteMethod(entityName: String): String = {
    "\tdef delete(id: Long) { DB.withConnection { implicit c => SQL(\"delete from %s where id = {id}\").on( 'id -> id).executeUpdate() } }".format(entityName.toUpperCase())
  }

  def creategetAllMethod(entityName: String, attributs: Map[String, String]): String = {
    var nomParser = entityName(0).toLower + entityName.substring(1)
    var nomEntite = entityName(0).toUpper + entityName.substring(1)
    "\tdef all(): List[%s] = DB.withConnection { implicit c => Sql.sql(\"select * from %s\").as(%s *) }".format(nomEntite, nomEntite.toUpperCase(), nomParser)
  }

}