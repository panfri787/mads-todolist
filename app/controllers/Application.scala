package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Task
import models.User
import anorm._

object Application extends Controller {

  var order : Option[Int] = None

  val userForm = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(User.apply)(User.unapply)
  )      

  val taskForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "label" -> nonEmptyText,
      "endDate" -> optional(date("yyyy-MM-dd")),
      "user" -> text
    )(Task.apply)(Task.unapply)
  )
  
  def index = getLoggedUser { username => implicit request =>
    Redirect(routes.Application.tasks(Application.order))
  }

  def tasks(orderParam: Option[Int]) = getLoggedUser { userEmail => implicit request =>
    order = orderParam
    Ok(views.html.index(Task.listByUser(userEmail,order), userEmail))
  }

  def newTask = getLoggedUser { userEmail => implicit request =>
    taskForm.bindFromRequest.fold (
      formWithErrors => BadRequest( views.html.newTask(formWithErrors, userEmail) ),
      task => {
          Task.create(task)
          Redirect(routes.Application.tasks(Application.order))
      }
    )
  }

  //Action que borra la tarea pasada por parametro y te rederige a la lista de tareas.
  def deleteTask(id: Long) = IsOwnerOf(id){ userEmail => implicit request =>
    Task.delete(id)
    Redirect(routes.Application.tasks(Application.order))    
  }

  //Action que responde enviandote a la pagina para crear una nueva tarea.
  def showNewTaskForm = getLoggedUser { userEmail => implicit request =>
    Ok(views.html.newTask(taskForm, userEmail))
  }

  //Action que responde enviandote a la pagina para editar una nueva tarea.
  def editTask(id: Long) = IsOwnerOf(id) { userEmail => implicit request =>
    Task.findById(id).map { task =>
      Ok(views.html.editTask(id, taskForm.fill(task), userEmail))
    }.getOrElse(NotFound("Task not found"))
  }
  
  //Action que actualiza la informacion de una tarea.
  def updateTask(id: Long) = IsOwnerOf(id) { userEmail => implicit request =>
    taskForm.bindFromRequest.fold (
      formWithErrors => BadRequest(views.html.editTask(id, formWithErrors, userEmail)),
      task => {
          Task.update(id, task)
          Redirect(routes.Application.tasks(Application.order))
      }
    )
  }

  //Muestra el formulario para registrar un nuevo usuario.
  def showSigninForm() = Action { implicit request =>
    Ok(views.html.signin(userForm))  
  }

  //Action que registra un nuevo usuario.
  def signNewUser() = Action { implicit request =>
    userForm.bindFromRequest.fold (
         formWithErrors => BadRequest(views.html.signin(formWithErrors)),
         user => {
            User.add(user)
            Redirect(routes.Application.tasks(order)).withSession("email" -> user.email)
         }
     )     
  }

  //Muestra el formulario para hacer login.
  def showLoginForm() = Action { implicit request =>
    Ok(views.html.login(userForm, ""))  
  }

  //Realiza el login en la aplicacion comprobando los datos.
  def doLogin() = Action { implicit request =>
    userForm.bindFromRequest.fold (
      formWithErrors => BadRequest( views.html.login(formWithErrors, "") ),
      user => {
        User.getUser(user.email).map{
          userGet => {
            if(userGet.password == user.password){
              Redirect(routes.Application.tasks(order)).withSession("email" -> user.email)
            } else {
              BadRequest(views.html.login(userForm, "The password isn't correct"))
            }
          }
        }.getOrElse(BadRequest(views.html.login(userForm, "The user must exist")))
      }
    )      
  }

  //"Desloguea" al usuario actual.
  def logout() = Action {
    Redirect(routes.Application.showLoginForm).withNewSession
  }

  //Funcion que extrae el valor de la variable de sesion "email"
  private def username(request: RequestHeader) = request.session.get("email")

  //Funcion que te redirige a la pagina de login (Para utilizar como callback cuando la autentificacion falla)
  private def onUnauthorized(request: RequestHeader) = BadRequest(views.html.login(userForm, ""))

  //Funcion para añadir la restriccion de que haya un usuario conectado para obtener una action.
  def getLoggedUser(f: => String => Request[AnyContent] => Result) = 
    Security.Authenticated(username, onUnauthorized) {
      user => Action(request => f(user)(request))
    }

  //Funcion para añadir la restriccion de que un recurso sea propiedad de un usuario.
  def IsOwnerOf(task: Long)(f: => String => Request[AnyContent] => Result) = 
   getLoggedUser { user => request =>
      if(Task.isOwner(task, user)) {
         f(user)(request)
      } else {
         Results.Forbidden("You aren't authorized")
      }
   }

}