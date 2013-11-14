package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

import java.util.{Date}

case class Task(id: Pk[Long], label: String, endDate: Option[Date], user: String)

object Task {

	//Parseador de tareas al obtenerlas de la BD
	val task = {
		get[Pk[Long]]("id") ~ 
		get[String]("label") ~
		get[Option[Date]]("endDate") ~
		get[String]("userEmail") map {
			case id~label~endDate~user => Task(id,label,endDate,user)
		}
	}

	//Obtiene todas las tareas de la BD
	def all(order: Option[Int]): List[Task] = order match{
		case Some(1) => DB.withConnection { implicit c =>
			SQL("select * from task order by endDate").as(task *)

		}

		case _ => DB.withConnection { implicit c => 
			SQL("select * from task order by id").as(task *) 
		}
	}

	//Inserta una tarea en la BD asignandole el parametro label como dicho valor.
	def create(task: Task) {
		DB.withConnection { implicit c =>
			SQL("insert into task (label, endDate, userEmail) values ({label}, {endDate}, {userEmail})").on(
				'label -> task.label,
				'endDate -> task.endDate,
				'userEmail -> task.user
			).executeUpdate()
		}
	}

	//Borra de la BD la tarea con el id pasado por parametro.
	def delete(id: Long) {
		DB.withConnection{ implicit c =>
			SQL("delete from task where id = {id}").on(
				'id -> id
			).executeUpdate()
		} 
	}

	//Busca una tarea concreta por su id
	def findById(id: Long) : Option[Task] = {
		DB.withConnection { implicit c =>
			SQL("select * from task where id = {id}").on(
				'id -> id).as(task.singleOpt)
		}
	}

	//Lista las tareas de un usuario
	def listByUser(user: String, order: Option[Int]) : List[Task] = order match {
		case Some(1) => DB.withConnection { implicit c =>
			SQL("""select * from task 
				where userEmail = {userEmail}
				order by endDate""").on(
				'userEmail -> user).as(task *)
		}

		case _ => DB.withConnection { implicit c => 
			SQL("""select * from task 
				where userEmail = {userEmail}
				order by id""").on(
				'userEmail -> user).as(task *) 
		}	
	}

	//Actualiza una tarea en la BD
	def update(id: Long, task: Task) = {
		DB.withConnection { implicit c =>
			SQL("update task set label={label}, endDate={endDate} where id={id}").on(
				'label -> task.label,
				'id -> id,
				'endDate -> task.endDate).executeUpdate()
		}
	}

	//Devuelve true si la tarea con id pasado por parametro corresponde con el usuario pasado por parametro,
	//y false si no.
	def isOwner(id: Long, email: String) : Boolean = {
		val result = DB.withConnection { implicit conn =>
	    	SQL( "select userEmail from task where id={id}" ).on(
		    	'id -> id).as(scalar[String].singleOpt)
		}
		if(result == None || result.get != email){
			return false
		} else {
			return true
		}
	}
	
}