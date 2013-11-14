package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class User(email: String, password: String)

object User {

	var user = {
		get[String]("email") ~
		get[String]("password") map {
			case email~password => User(email,password)
		}
	}

	def add(user: User) = {
		DB.withConnection { implicit conn =>
			SQL( "insert into Usuario (email, password) values ({email}, {password})" ).on(
				'email -> user.email,
				'password -> user.password 
			).executeUpdate()				    
		}		
	}

	def getUser(email: String) = {
		DB.withConnection { implicit conn =>
		    SQL( "select * from Usuario where email = {email}" ).on(
				'email -> email
			).as(user.singleOpt)
		}
	}

}

