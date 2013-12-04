package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends Specification {
  
  "Application" should {
    
    "send 404 on a bad request" in {
      running(FakeApplication()) {
        route(FakeRequest(GET, "/boum")) must beNone        
      }
    }
    
    "render the login page without authentication" in {
      running(FakeApplication()) {
        val home = route(FakeRequest(GET, "/")).get
        
        status(home) must equalTo(BAD_REQUEST)
        contentType(home) must beSome.which(_ == "text/html")
        contentAsString(home) must contain ("Login")
      }
    }

    "render the tasks list with authentication in GET /" in {
      running(FakeApplication()) {
        val home = route(FakeRequest(GET, "/").withSession("email" -> "pepe@ua.es")).get

        status(home) must equalTo(SEE_OTHER)
        redirectLocation(home).get must equalTo("/tasks")
      }
    }

    "render the tasks list with authentication" in {
      running(FakeApplication()) {
        val tasks = route(FakeRequest(GET, "/tasks").withSession("email" -> "pepe@ua.es")).get

        status(tasks) must equalTo(OK)
        contentType(tasks) must beSome.which(_ == "text/html")
        contentAsString(tasks) must contain ("3 task(s)")
        contentAsString(tasks) must contain ("Hello, pepe@ua.es")
        contentAsString(tasks) must contain ("Comprar pan")
        contentAsString(tasks) must contain ("12/12/2013")
      }
    }

    "not render the tasks list without authentication" in {
      running(FakeApplication()) {
        val tasks = route(FakeRequest(GET, "/tasks")).get

        status(tasks) must equalTo(BAD_REQUEST)
        contentType(tasks) must beSome.which(_ == "text/html")
        contentAsString(tasks) must contain ("Login")
      }
    }

    "login an existent user" in {
      running(FakeApplication()){
        val login = route(FakeRequest(POST, "/login").withFormUrlEncodedBody(("email","pepe@ua.es"),("password","pepe"))).get

        status(login) must equalTo(SEE_OTHER)
        redirectLocation(login).get must equalTo("/tasks")
      }
    }

    "not login an not existent user" in {
      running(FakeApplication()){
        val login = route(FakeRequest(POST, "/login").withFormUrlEncodedBody(("email","pepo@ua.es"),("password","pepe"))).get

        status(login) must equalTo(BAD_REQUEST)
        contentType(login) must beSome.which(_ == "text/html")
        contentAsString(login) must contain ("The user must exist") 
      }
    }

    "not login with empty inputs" in {
      running(FakeApplication()){
        val login = route(FakeRequest(POST, "/login").withFormUrlEncodedBody(("email",""),("password",""))).get

        status(login) must equalTo(BAD_REQUEST)
        contentType(login) must beSome.which(_ == "text/html")
        contentAsString(login) must contain ("Valid email required") 
        contentAsString(login) must contain ("This field is required") 
      }
    }

    "not login with wrong password" in {
      running(FakeApplication()){
        val login = route(FakeRequest(POST, "/login").withFormUrlEncodedBody(("email","pepe@ua.es"),("password","papa"))).get

        status(login) must equalTo(BAD_REQUEST)
        contentType(login) must beSome.which(_ == "text/html")
        //Lo compruebo asi porque me parsea mal "isn't" al pasarlo a String
        contentAsString(login) must contain ("The password isn")
        contentAsString(login) must contain ("correct") 
      }
    }

    "render the register page" in {
      running(FakeApplication()) {
        val signin = route(FakeRequest(GET, "/signin")).get
        
        status(signin) must equalTo(OK)
        contentType(signin) must beSome.which(_ == "text/html")
        contentAsString(signin) must contain ("Sign new user")
      }
    }

    "not signin with empty inputs" in {
      running(FakeApplication()){
        val signin = route(FakeRequest(POST, "/signin").withFormUrlEncodedBody(("email",""),("password",""))).get

        status(signin) must equalTo(BAD_REQUEST)
        contentType(signin) must beSome.which(_ == "text/html")
        contentAsString(signin) must contain ("Valid email required") 
        contentAsString(signin) must contain ("This field is required") 
      }
    }

    "sign in an new user" in {
      running(FakeApplication()){
        val login = route(FakeRequest(POST, "/signin").withFormUrlEncodedBody(("email","pablo@ua.es"),("password","pablo"))).get

        status(login) must equalTo(SEE_OTHER)
        redirectLocation(login).get must equalTo("/tasks")
      }
    }

    "logout a logged user" in {
      running(FakeApplication()){
        val logout = route(FakeRequest(GET, "/logout")).get

        headers(logout).get("Set-Cookie").getOrElse("") contains "Expires"
      }
    }

    "render the create new task page" in {
      running(FakeApplication()) {
        val create = route(FakeRequest(GET, "/tasks/newTask").withSession("email" -> "pepe@ua.es")).get
        
        status(create) must equalTo(OK)
        contentType(create) must beSome.which(_ == "text/html")
        contentAsString(create) must contain ("Add a new task")
      }
    }

    "not render the create task page without authentication" in {
      running(FakeApplication()) {
        val create = route(FakeRequest(GET, "/tasks/newTask")).get
        
        status(create) must equalTo(BAD_REQUEST)
        contentType(create) must beSome.which(_ == "text/html")
        contentAsString(create) must contain ("Login")
      }
    }

    "not create a new task with label input empty" in {
      running(FakeApplication()) {
        val create = route(FakeRequest(POST, "/tasks/newTask").withSession("email" -> "pepe@ua.es").withFormUrlEncodedBody(("label",""),("endDate",""))).get

        status(create) must equalTo(BAD_REQUEST)
        contentType(create) must beSome.which(_ == "text/html") 
        contentAsString(create) must contain ("This field is required")
      }
    }

    "create a new task" in {
      running(FakeApplication()) {
        val create = route(FakeRequest(POST, "/tasks/newTask").withSession("email" -> "pepe@ua.es").withFormUrlEncodedBody(("label","Tarea de prueba"),("endDate",""),("user", "pepe@ua.es"))).get

        status(create) must equalTo(SEE_OTHER)
        redirectLocation(create).get must equalTo("/tasks")

        val lista = route(FakeRequest(GET, redirectLocation(create).get).withSession("email" -> "pepe@ua.es")).get
        contentType(lista) must beSome.which(_ == "text/html")
        contentAsString(lista) must contain("Tarea de prueba")
      }
    }

    "delete an user task" in {
      running(FakeApplication()){
        val delete = route(FakeRequest(POST, "/tasks/1/delete").withSession("email" -> "pepe@ua.es")).get
        status(delete) must equalTo(SEE_OTHER)
        redirectLocation(delete).get must equalTo("/tasks")

        val lista = route(FakeRequest(GET, redirectLocation(delete).get).withSession("email" -> "pepe@ua.es")).get
        contentType(lista) must beSome.which(_ == "text/html")
        contentAsString(lista) must not contain("Comprar pan")
      }
    }

    "not delete a task if the logged user is not the creator" in {
      running(FakeApplication()){
        val delete = route(FakeRequest(POST, "/tasks/1/delete").withSession("email" -> "luis@ua.es")).get

        status(delete) must equalTo(FORBIDDEN)
        contentAsString(delete) must contain("You aren't authorized")
      }
    }

    "render the update task form if you are the owner" in {
      running(FakeApplication()){
        val update = route(FakeRequest(GET, "/tasks/1").withSession("email" -> "pepe@ua.es")).get

        status(update) must equalTo(OK)
        contentAsString(update) must contain("Edit task")
        contentAsString(update) must contain("Comprar pan")
      }
    }

    "not render the update task form if you are not the owner" in {
      running(FakeApplication()){
        val update = route(FakeRequest(GET, "/tasks/1").withSession("email" -> "luis@ua.es")).get

        status(update) must equalTo(FORBIDDEN)
        contentAsString(update) must contain("You aren't authorized")
      }
    }

    "not render the update task form if you are not logged" in {
      running(FakeApplication()){
        val update = route(FakeRequest(GET, "/tasks/1")).get

        status(update) must equalTo(BAD_REQUEST)
        contentType(update) must beSome.which(_ == "text/html")
        contentAsString(update) must contain ("Login")        
      }
    }

    "update a task if you're are the owner" in {
      running(FakeApplication()){
        val update = route(FakeRequest(POST, "/tasks/1").withSession("email" -> "pepe@ua.es").withFormUrlEncodedBody(("label","Tarea de prueba"),("endDate",""),("user", "pepe@ua.es"))).get

        status(update) must equalTo(SEE_OTHER)
        redirectLocation(update).get must equalTo("/tasks")

        val lista = route(FakeRequest(GET, redirectLocation(update).get).withSession("email" -> "pepe@ua.es")).get
        contentType(lista) must beSome.which(_ == "text/html")
        contentAsString(lista) must contain("Tarea de prueba")
      } 
    }

    "not update a task if the logged user is not the creator" in {
      running(FakeApplication()){
        val update = route(FakeRequest(POST, "/tasks/1").withSession("email" -> "luis@ua.es").withFormUrlEncodedBody(("label","Tarea de prueba"),("endDate",""),("user", "pepe@ua.es"))).get


        status(update) must equalTo(FORBIDDEN)
        contentAsString(update) must contain("You aren't authorized")
      }
    }
  }
}