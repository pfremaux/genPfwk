package creators

import creators.entities.Model

object Web {

  // TODO simplifier les appels en retirant les class intermédiaires.
  
  def generateScalaHtml(baseDir: String, aModel: Model) = {
    Patterns.writeFile(baseDir + "/app/views/" + aModel.nameLowerCase + ".scala.html", WebPattern.createWebCrud(aModel), false)
  }

  def appendRouteFile(baseDir: String, aModel: Model) = {
    Patterns.writeFile(baseDir + "/conf/routes", WebPattern.createRouteCrud(aModel.nameFirstUpperCase), true)
  }

}