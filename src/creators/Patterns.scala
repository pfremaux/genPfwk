package creators

import scala.io.Source
import tools.Tools
import java.io.FileWriter
import creators.entities.Model

/**
 * Class statique contenant les constantes nécessaires à la création des fichiers.
 * @author PFX
 *
 */
object Patterns {
  val pathResources = "./resources/"
  val importPattern = getFile("import.patt")
  val packagePattern = getFile("package.patt")
  val filePattern = getFile("file.patt")
  val classPattern = getFile("class.patt")
  val modelDBPattern = getFile("db/modelDB.patt")
  val rowParserPattern = getFile("db/rowParser.patt")
  val sqlPattern = getFile("db/sql.patt")
  val getListPattern = getFile("getList.patt")
  val inputHTMLPattern = getFile("inputHTML.patt")
  val websocketJSPattern = getFile("ws/websocketJS.patt")
  val crudPattern = getFile("web/crud.patt")

  def getFile(path: String): String = {
    Source.fromFile(pathResources + path).getLines().mkString("\n")
  }

  def writeFile(path: String, data: String, append: Boolean) = {
    val fw = new FileWriter(path, append)
    try {
      fw.write(data)
    } finally fw.close()
  }

  def getRouteLine(mode: String, operation: String, entity: String, params: Map[String, String]): String = {
    var varUrl = params.keys.map(cle => ":" + cle)
    var supplement = ""
    if (!varUrl.isEmpty) supplement = "/"
    var parametres = Tools.toSignature(params)
    var entityClass = Tools.firstCharToUpper(entity)
    "%s    /%s/%s%s       controllers.%sController.%s(%s)".format(mode, entity, varUrl.mkString("/") + supplement, operation, entityClass, operation, parametres, entityClass)
  }
}

/**
 * Générateur dédié à la génération d'une classe scala
 * @author PFX
 *
 */
class ClassCreator(name: String, hostingFile: ScalaFileConstructor) {
  var className = name
  val host: ScalaFileConstructor = hostingFile
  var attributs: List[String] = List[String]()
  var methodes: List[String] = List[String]()
  var defaultConstructor = ""
  var isSingleton: Boolean = false
  var isCaseClass: Boolean = false
  var extendClass = ""

  def furthermore: ScalaFileConstructor = host


  def extending(extClassName: String): ClassCreator = {
    extendClass = extClassName
    this
  }

  def asCaseClass(): ClassCreator = {
    isCaseClass = true
    this
  }

  def asSingleton(): ClassCreator = {
    isSingleton = true
    this
  }

  def getClassContent(): String = {
    var typeC: String = "class"
    if (isSingleton) {
      typeC = "object"
    } else if (isCaseClass) {
      typeC = "case class"
    }
    var classNameAndInheritence = ""
    if (extendClass.isEmpty())
      classNameAndInheritence = className + defaultConstructor
    else
      classNameAndInheritence = className + defaultConstructor + " extends " + extendClass
    Patterns.classPattern.format(typeC, classNameAndInheritence, methodes.mkString("\n") + attributs.mkString("\n"))
  }

  def withDefaultConstructor(params: Map[String, String]): ClassCreator = {
    defaultConstructor = "(" + Tools.toSignature(params) + ")"
    this
  }

  def asCrudController(entityName: String, attributs: Map[String, String]): ClassCreator = {
    methodes = ControllersPattern.formValidatorCreator(entityName, attributs) ::
      ControllersPattern.indexCreator(entityName) ::
      ControllersPattern.newEntityCreator(entityName, attributs) :: 
      ControllersPattern.supprCreator(entityName) :: methodes
    this
  }

  def withDBAccess(entityName: String, attributs: Map[String, String]): ClassCreator = {
    methodes = DbPatterns.createDeleteMethod(entityName) ::
      DbPatterns.creategetAllMethod(entityName, attributs) ::
      DbPatterns.createMethod(entityName, attributs) ::
      DbPatterns.createParser(new Model(entityName, Map("id" -> "Long") ++ attributs)) :: methodes
    this
  }

  def withAttribut(attribut: String): ClassCreator = {
    attributs = attribut :: attributs
    this
  }

}


class WebFileConstructor(name: String) {
  val fileName = name
  val typeVarToInput = Map("String" -> "text", "Date" -> "date")
  var webSocketSupport = false
  var html = ""

  def withInput(name: String, inputType: String) = {
    println(typeVarToInput.get(inputType).get)
  }

  def withWebsocket() {
    webSocketSupport = true
  }

}

//////////////////////////////////////////////

class ScalaFileConstructor(name: String) {

  val classToImport = Map("Actor" -> "import akka.actor.Actor" /*, */ )
  val fileName = name

  var pack: String = ""
  var classes: List[ClassCreator] = List[ClassCreator]()
  var imports: List[String] = List[String]()

  def getFileContent(): String = {
    imports = imports.map(s => Patterns.importPattern.format(s))
    var strClasses: List[String] = classes.map(c => c.getClassContent)
    val imp = imports.mkString("\n")

    Patterns.filePattern.format(pack, imp, strClasses.mkString("\n"))
  }

  def packageIn(path: String): ScalaFileConstructor = {
    pack = Patterns.packagePattern.format(path)
    ScalaFileConstructor.this
  }

  def imports(path: String): ScalaFileConstructor = {
    imports = path :: imports
    ScalaFileConstructor.this
  }

  def withDBAccess(entityName: String, attributs: Map[String, String]): ScalaFileConstructor = {
    //methodes = Patterns.getListPattern.format(entityName, entityName, entityName) :: methodes
    imports("play.api.db.DB").imports("anorm._").imports("play.api.Play.current").imports("anorm.SqlParser._")
    newClass(entityName).asSingleton.withDBAccess(entityName, attributs)
    newClass(entityName).asCaseClass.withDefaultConstructor(Map("id" -> "Option[Long]") ++ attributs)
    this
  }

  def asCrudController(entityName: String, attributs: Map[String, String]): ScalaFileConstructor = {
    imports("play.api.mvc.Action").imports("play.api.mvc.Controller").imports("play.api.data._").imports("play.api.data.Forms._")
    var entityClass = Tools.firstCharToUpper(entityName)
    imports("models." + entityClass)
    newClass(entityClass + "Controller").asSingleton.extending("Controller").asCrudController(entityName, attributs)

    this
  }

  def newClass(className: String): ClassCreator = {
    var nvelleClasse = new ClassCreator(className, ScalaFileConstructor.this)
    classes = nvelleClasse :: classes
    nvelleClasse
  }

}