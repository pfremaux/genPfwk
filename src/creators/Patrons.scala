package creators

import java.io.FileInputStream
import java.util.Properties
import java.io.File

object Crud {

  val prop = new Properties()
  prop.load(new FileInputStream("./resources/lang_FR.properties"))
  val baseDir = "/Users/Pierre/Documents/workspace/frameworkPlay/crudScala"
  
  
  def usage(): Unit = {
    println(prop.getProperty("usage"));
  }

  def main(args: Array[String]): Unit = {
    var ent = "Personne"
    var membres = Map[String, String]("nom" -> "String", "age" -> "Int")
    
    var inc:Int = 1
    while (new File(baseDir+"/conf/evolutions/default/"+inc+".sql").exists()) {
      inc = inc + 1
    }
    Patterns.writeFile(baseDir+"/conf/evolutions/default/"+inc+".sql", DbPatterns.createSql(ent, membres), false)
    
    
    var fc: ScalaFileConstructor = new ScalaFileConstructor(ent+".scala")
    //var s = fc.packageIn("mon.pack").imports("play.data.Entity").imports("play.data.Model").newClass("MaClass").furthermore.newClass("MaSecondeClass").withListFromDB("Personne").furthermore.getFileContent
    var s = fc.packageIn("models").withDBAccess(ent, membres)
    //println(s.getFileContent);
    Patterns.writeFile(baseDir+"/app/models/"+ent+".scala", s.getFileContent, false)
    
    var t = new ScalaFileConstructor(ent+"Controller.scala")
    t.packageIn("controllers").asCrudController(ent, membres)
    //println(t.getFileContent)
    
    Patterns.writeFile(baseDir+"/app/controllers/"+ent+"Controller.scala", t.getFileContent, false)
    
    
    Patterns.writeFile(baseDir+"/app/views/"+ent.toLowerCase()+".scala.html", WebPattern.createWebCrud(ent, membres), false)
    //println(WebPattern.createWebCrud(ent))
    
    
    Patterns.writeFile(baseDir+"/conf/routes", WebPattern.createRouteCrud(ent), true)
    //println(WebPattern.createRouteCrud(ent))
    
  /*  
    var w = new WebFileConstructor("toto")
    w.withInput("test", "String")
    var f = ""
    f = DbPatterns.createEntity("nomClasse", Map("label" -> "String", "date" -> "Date", "taille" -> "Int"))
    println(f)
    f = DbPatterns.createMethod("nomClasse", Map("label" -> "String", "date" -> "Date", "taille" -> "Int"))
    println(f)
    f = DbPatterns.createDeleteMethod("nomClasse")
    println(f)
    f = DbPatterns.creategetAllMethod("nomClasse", Map("label" -> "String", "date" -> "Date", "taille" -> "Int"))
    println(f)*/
  }
}

