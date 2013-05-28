package creators

import tools.Tools
import creators.entities.Model

object DbPatterns {

  def createSql(entityName: String, attributs: Map[String, String]): String = {
    var colonnes = Tools.getSql(attributs).mkString(",\n\t")
    
    Patterns.sqlPattern.format(entityName.toLowerCase(), entityName.toUpperCase(), entityName.toLowerCase(), 
        colonnes, entityName.toUpperCase(), entityName.toLowerCase())
  }

  def createEntityContent(entity:Model): String = {
    var listParam = entity.attribs.keys.map(cle => cle + " : " + entity.attribs(cle))
    "\tcase class %s (%s)".format(entity.nameFirstUpperCase, listParam.mkString(", "))
  }

  def createParser(entity:Model): String = {
    var parserName = entity.nameFirstLowerCase
    var entityName = entity.nameFirstUpperCase
    var listParam = entity.attribs.keys.map(cle => cle + " : " + entity.attribs(cle))
    var rowParser = entity.attribs.keys.map(cle => Patterns.rowParserPattern.format(entity.attribs(cle), cle))

    val parametres = "(" + listParam.mkString(", ") + ")"
    val methode = "\tval %s = { %s map { case %s => %s(%s) } }".format(parserName,
      rowParser.mkString(" ~ "),
      entity.attribs.keys.mkString(" ~ "),
      entityName,
      (Map("Some(id)" -> "Long") ++ entity.attribs).filterKeys(s => !"id".equals(s)).keys.mkString(", "))
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