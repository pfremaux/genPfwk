package creators

import java.io.FileInputStream
import java.util.Properties
import creators.entities.Model

object Crud {

  val prop = new Properties()
  prop.load(new FileInputStream("./resources/lang_FR.properties"))
  val baseDir = "/Users/Pierre/Documents/workspace/frameworkPlay/crudScala"
  
  
  def usage(): Unit = {
    println(prop.getProperty("usage"));
  }

  def main(args: Array[String]): Unit = {
    var aModel = new Model("Person", Map("name" -> "String", "age" -> "Int"))
    Database.generateModels(baseDir, aModel)
    Database.generateSqlTables(baseDir, aModel)
    Controller.generateController(baseDir, aModel)
    Web.generateScalaHtml(baseDir, aModel)
    Web.appendRouteFile(baseDir, aModel)
  }
}

