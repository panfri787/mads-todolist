#Esquema de usuarios.

# --- !Ups

CREATE TABLE Usuario(
	email varchar(255) NOT NULL,
	password varchar(255) NOT NULL,
	PRIMARY KEY(email)
	
);



# --- !Downs

DROP TABLE Usuario;
