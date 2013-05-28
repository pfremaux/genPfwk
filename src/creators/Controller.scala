package creators

import creators.entities.Model

object Controller {

  def generateController(baseDir: String, aModel: Model) = {
    var t = new ScalaFileConstructor(aModel.nameFirstUpperCase + "Controller.scala")
    t.packageIn("controllers").asCrudController(aModel.nameLowerCase, aModel.attribs)
    Patterns.writeFile(baseDir + "/app/controllers/" + t.fileName, t.getFileContent, false)

  }

}