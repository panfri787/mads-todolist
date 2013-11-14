# Realacionar una tarea con un usuario.

# --- !Ups
ALTER TABLE task ADD COLUMN userEmail varchar(255);

ALTER TABLE task
	ADD CONSTRAINT fk_task_user
	FOREIGN KEY (userEmail)
	REFERENCES Usuario;  

# --- !Downs

ALTER TABLE task
	DROP COLUMN userEmail;