package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import anorm._

import models.User
import models.Task

class ModelSpec() extends Specification {

	def dateIs(date: java.util.Date, str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date) == str
	def strToDate(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)

	"Users" should {
		"create and get users" in {
			running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
				User.add(new User("pablito@ua.es", "1234"))


				val Some(user) = User.getUser("pablito@ua.es")
				user.email must equalTo("pablito@ua.es")
				user.password must equalTo("1234")
			}
		}

		//Test algo absurdo, porque la excepcion no esta controlada en la aplicacion, pero bueno.
		"fails if you try to create a duplicate user" in {
			running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
				User.add(new User("pablito@ua.es", "1234"))

				User.add(new User("pablito@ua.es", "1234")) must throwA[org.h2.jdbc.JdbcSQLException]
			}
		}
	}

	"Task" should {
		"create and get task" in {
			running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
				Task.create(new Task(anorm.NotAssigned,"Comprar huevos", Option(strToDate("2013-11-14")), "pepe@ua.es"))

				//Puesto que el metodo create, pone como id el siguiente en la serie. Recupero el sucesivo a las tareas
				//insertadas en la evolucion de la BD.
				val Some(task) = Task.findById(7)
				task.label must equalTo("Comprar huevos")
				dateIs(task.endDate.get, "2013-11-14") must beTrue
				task.user must equalTo("pepe@ua.es")
			}
		}

		"list all available tasks default ordered" in {
			running(FakeApplication()){
				val lista = List(new Task(anorm.Id(1), "Comprar pan", Option(strToDate("2013-12-12")), "pepe@ua.es"),
								 new Task(anorm.Id(2), "Comprar leche", Option(strToDate("2014-10-20")), "pepe@ua.es"),
								 new Task(anorm.Id(3), "Comprar pan", Option(strToDate("2015-11-21")), "paco@ua.es"),
								 new Task(anorm.Id(4), "Comprar leche", Option(strToDate("2012-09-01")), "paco@ua.es"),
								 new Task(anorm.Id(5), "Comprar arroz", None, "pepe@ua.es"),
								 new Task(anorm.Id(6), "Comprar arroz", None, "paco@ua.es"))

				lista must equalTo (Task.all(None))
			}
		}

		"list all available tasks date ordered" in {
			running(FakeApplication()){
				val lista = List(new Task(anorm.Id(5), "Comprar arroz", None, "pepe@ua.es"),
								 new Task(anorm.Id(6), "Comprar arroz", None, "paco@ua.es"),
								 new Task(anorm.Id(4), "Comprar leche", Option(strToDate("2012-09-01")), "paco@ua.es"),
								 new Task(anorm.Id(1), "Comprar pan", Option(strToDate("2013-12-12")), "pepe@ua.es"),
								 new Task(anorm.Id(2), "Comprar leche", Option(strToDate("2014-10-20")), "pepe@ua.es"),
								 new Task(anorm.Id(3), "Comprar pan", Option(strToDate("2015-11-21")), "paco@ua.es"))

				lista must equalTo (Task.all(Some(1)))
			}
		}

		"delete tasks" in {
			running(FakeApplication(additionalConfiguration = inMemoryDatabase())){
				Task.create(new Task(anorm.NotAssigned,"Comprar huevos", Option(strToDate("2013-11-14")), "pepe@ua.es"))
				val list1: List[Task] = Task.all(None)

				Task.delete(7)
				val list2: List[Task] = Task.all(None)

				list1 must not be equalTo (list2)
			}
		}

		"list all tasks of one single user" in {
			running(FakeApplication()){
				val correct1 = List(new Task(anorm.Id(1), "Comprar pan", Option(strToDate("2013-12-12")), "pepe@ua.es"),
								 new Task(anorm.Id(2), "Comprar leche", Option(strToDate("2014-10-20")), "pepe@ua.es"),
								 new Task(anorm.Id(5), "Comprar arroz", None, "pepe@ua.es"))

				correct1 must be equalTo (Task.listByUser("pepe@ua.es", None))

				val notCorrect1 = List(new Task(anorm.Id(1), "Comprar pan", Option(strToDate("2013-12-12")), "pepe@ua.es"),
								 new Task(anorm.Id(2), "Comprar leche", Option(strToDate("2014-10-20")), "pepe@ua.es"),
								 new Task(anorm.Id(3), "Comprar pan", Option(strToDate("2015-11-21")), "paco@ua.es"),
								 new Task(anorm.Id(5), "Comprar arroz", None, "pepe@ua.es"))

				notCorrect1 must not be equalTo (Task.listByUser("pepe@ua.es", None))

				val correct2 = List(new Task(anorm.Id(5), "Comprar arroz", None, "pepe@ua.es"),
								 new Task(anorm.Id(1), "Comprar pan", Option(strToDate("2013-12-12")), "pepe@ua.es"),
								 new Task(anorm.Id(2), "Comprar leche", Option(strToDate("2014-10-20")), "pepe@ua.es"))

				correct2 must be equalTo (Task.listByUser("pepe@ua.es", Some(1)))

				val notCorrect2 = List(new Task(anorm.Id(5), "Comprar arroz", None, "pepe@ua.es"),
								 new Task(anorm.Id(1), "Comprar pan", Option(strToDate("2013-12-12")), "pepe@ua.es"),
								 new Task(anorm.Id(2), "Comprar leche", Option(strToDate("2014-10-20")), "pepe@ua.es"),
								 new Task(anorm.Id(3), "Comprar pan", Option(strToDate("2015-11-21")), "paco@ua.es"))

				notCorrect2 must not be equalTo (Task.listByUser("pepe@ua.es", Some(1)))
			}
		}
	}
	
}