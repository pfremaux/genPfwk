package creators

import java.io.File
import creators.entities.Model

object Database {

  def generateSqlTables(baseDir: String, aModel: Model) = {
    var inc: Int = 1
    while (new File(baseDir + "/conf/evolutions/default/" + inc + ".sql").exists()) {
      inc = inc + 1
    }
    Patterns.writeFile(baseDir + "/conf/evolutions/default/" + inc + ".sql", DbPatterns.createSql(aModel.nameLowerCase, aModel.attribs), false)
  }

  def generateModels(baseDir: String, aModel: Model) = {
    var fc: ScalaFileConstructor = new ScalaFileConstructor(aModel.nameFirstUpperCase + ".scala")
    var s = fc.packageIn("models").withDBAccess(aModel.nameFirstUpperCase, aModel.attribs)
    Patterns.writeFile(baseDir + "/app/models/" + fc.fileName, s.getFileContent, false)
  }

}