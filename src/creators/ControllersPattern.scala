package creators
import tools.Tools._
import tools.Tools

object ControllersPattern {

  def formValidatorCreator(entityName: String, params: Map[String, String]): String = {
    var entityClass = firstCharToUpper(entityName)
    var entityBean = firstCharToLower(entityName)
    var paramsConstraints = "\"id\" -> optional(longNumber),\n\t" + params.keys.map(s => "\"" + s + "\" -> "+Tools.getBasicConstraint(params(s))).mkString("\t,\n")
    "\tval %sForm = Form[%s](mapping(\n%s)(%s.apply)(%s.unapply))".format(entityBean, entityClass, paramsConstraints, entityClass, entityClass)
  }

  def indexCreator(entityName: String): String = {
    var entityClass = firstCharToUpper(entityName)
    var entityBean = firstCharToLower(entityName)
    "def index = Action { Ok(views.html.%s(%s.all(), %sForm)) }".format(entityName.toLowerCase(), entityClass, entityBean)
  }

  def supprCreator(entityName: String): String = {
    var entityClass = firstCharToUpper(entityName)
    var entityBean = firstCharToLower(entityName)
    """
	  def suppr(id: Long) = Action {
	  %s.delete(id)
	  Ok(views.html.%s(%s.all(), %sForm))
	  }
	  """.format(entityClass, entityBean, entityClass, entityBean)
  }

  /*def listCreator(entityName:String) : String = {
	"def taches = Action { Ok(views.html.%s.index(Tache.all(), taskForm)) }"
  }*/

  def newEntityCreator(entityName: String, params: Map[String, String]): String = {
    var entityClass = firstCharToUpper(entityName)
    var entityBean = firstCharToLower(entityName)
    var paramsCreation = Tools.getVarNames(params).map(p => "obj." + p).mkString(", ")

    """
  def create = Action { implicit request =>
    %sForm.bindFromRequest.fold(
        errors => BadRequest(views.html.%s(%s.all(), %sForm)),
        obj => %s.create(%s)
        )
    
    Ok(views.html.%s(%s.all(), %sForm))
  }
  """.format(entityBean, entityBean, entityClass, entityBean, entityClass, paramsCreation, entityBean, entityClass, entityBean)
  }

}