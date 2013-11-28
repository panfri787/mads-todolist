# Datos para pruebas.

# --- !Ups

insert into Usuario (email,password) values ('pepe@ua.es', 'pepe');
insert into Usuario (email,password) values ('paco@ua.es', '1234');
insert into Usuario (email,password) values ('luis@ua.es', 'luis1234');

insert into task (label, endDate, userEmail) values ('Comprar pan', '2013-12-12', 'pepe@ua.es');
insert into task (label, endDate, userEmail) values ('Comprar leche', '2014-10-20', 'pepe@ua.es');
insert into task (label, endDate, userEmail) values ('Comprar pan', '2015-11-21', 'paco@ua.es');
insert into task (label, endDate, userEmail) values ('Comprar leche', '2012-09-01', 'paco@ua.es');
insert into task (label, userEmail) values ('Comprar arroz', 'pepe@ua.es');
insert into task (label, userEmail) values ('Comprar arroz', 'paco@ua.es');


# --- !Downs

delete from task;
delete from Usuario;