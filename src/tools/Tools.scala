package tools

object Tools {

  def firstCharToUpper(str: String): String = {
    str(0).toUpper + str.substring(1)
  }

  def firstCharToLower(str: String): String = {
    str(0).toLower + str.substring(1)
  }

  def toSignature(params: Map[String, String]): String = {
    params.keys.map(cle => cle + " : " + params(cle)).mkString(", ")
  }

  def getVarNames(params: Map[String, String]): List[String] = {
    params.keys.map(s => s).toList
  }

  def getHtmlInput(params: Map[String, String]): List[String] = {
    var listInput = List[String]()
    var component = "sinputText"
    listInput = params.keys.map(k => {
      component = params(k) match {
        case "String"|"Int"|"Long" => "sinputText"
        case "Date" => "sinputDate"
        case "Boolean" => "sinputCheck"
      }
      "<td>@%s(\"%s\")</td>".format(component, k)
    }).toList
    listInput
  }
  
  def getSql(params: Map[String, String]): List[String] = {
    var listInput = List[String]()
    var typeColumn = "INTEGER NOT NULL"
    listInput = params.keys.map(k => {
      typeColumn = params(k) match {
        case "String" => "VARCHAR(255) NOT NULL"
        case "Date" => "DATETIME NOT NULL"
        case "Long"|"Int" => "INTEGER NOT NULL"
          
      }
      "\t%s %s".format(k, typeColumn)
    }).toList
    listInput.reverse
  }

  def getBasicConstraint(typeParam:String): String = {
    typeParam match {
      case "String" => "nonEmptyText"
      case "Int" => "number"
      case "Long" => "longNumber"
      case "Date" => "date"
      case _ => "optionnal(text)"
    }
  }
  
  def getEnteteColonnes(params:Map[String, String]):List[String] = {
    params.keys.map(s => "<th>%s</th>".format(s)).toList
  }
  
}