## Corrección de la práctica

VERDU ROMERO, PABLO

[Bitbucket](https://bitbucket.org/pvr3/mads-todolist-p4)

### Documentación

- Algo escasa la documentación

### Código e implementación

- Bien los tests de los controladores
- Los tests de ordenación de tareas deberían implementarse con un bucle que compruebe que las fechas (o el Id) estén ordenadas
- El test de borrar estaría mejor comprobarlo haciendo un `find` y no encontrándola


### Calificación

Algo mejorable la documentación y los tests del modelo

Calificación: **0,45**

7 de enero de 2014  
Domingo Gallardo

------

#Practica 4 - Pruebas en Play Framework
##Alumno: Pablo Verdú Romero

###1. Objetivo de la practica.

El objetivo de esta practica es el de familiarizarse con la suite de pruebas que tiene Play Framework, para ello se debera:

1. Crear una base de datos de prueba.

2. Testear los metodos del modelo de la aplicacion.

3. Testear los metodos del controlador de la aplicacion.

###2. Aspectos tecnicos mas importante de los tests realizados.

Para realizar los test en *Play Framework* hay que situarlos en la carpeta `test` de la jerarquia de directorios. El propio framework te provee de unos ficheros donde realizar los test: `ModelSpec.scala` para los tests del modelo y `ApplicationSpec.scala` para los del controlador.

Dichos test se deben introducir dentro de una clase que herede de la clase `Specification`. Dentro de la misma para declarar una *Suite de test* debes de hacerlo con la siguiente estructura:

	"Nombre de la suite" should {

		//Tests

	}  

De la misma forma, para codificar un test dentro de la suite, debes de seguir la siguiente estructura:

	"Nombre del test" in {
		//Contenido del test
	}

Para realizar el paso de test, hay que lanzar la aplicacion con el comando `play test` con el cual los test se compilaran y se iran pasando sucesivamente mostrando el resultado por consola.

Los test, deben realizarse sobre un entorno de ejecucion ficticio que especificas dentro de cada test, para ello debes declararlo asi:

	running (FakeApplication()){
		//Contenido del test
	}

Adicionalmente, se le pueden pasar parametros a este entorno de pruebas como una *Base de datos en memoria*:

	running (FakeApplication(additionalConfiguration = inMemoryDatabase())){
		//Contenido del test
	}

La codificiacion de test en *Specs* es similar a otras liberias de test de otros lenguajes en la cual invocas el metodo o la accion a probar y luego compruebas el resultado esperado. Para realizar la comprobacion *Specs* utiliza la palabra reservada `must` para comprobar el resultado esperado con el obtenido.

Ejemplos de comprobaciones que he realizado en la practica son las siguientes:

	//Comprobacion de igualdades

	/*Un objeto, lista o cadena*/ must equalTo(/*Un objeto, lista o cadena esperados*/)

	/*Un objeto, lista o cadena*/ must not be equalTo(/*Un objeto, lista o cadena no esperados*/)

	//Comprobacion de booleanos

	/*Un metodo que devuelve cierto*/ must beTrue7

	/*Un metodo que devuelve falso*/ must beFalse

	//Comprobaciones con cadenas.

	"una cadena" must contain "una subcadena"

	//Control de excepciones.

	/*Un metodo que lanza una excepcion*/ must throwA[Excepcion esperada]

Como estamos realizando el testeo de una aplicacion web, el framework nos debe proporcionar herramientas para realizar peticiones HTTP ficticias, para ello *Play framework* nos proporciona la herramienta `FakeRequest()` la cual se utiliza de la siguiente manera, obteniendo el `Result` correspondiente pasandosela como parametro al metodo `route()`:

	val request = route(FakeRequest(Tipo de peticion, URL de la peticion))

Dicha llamada devuelve un objeto de tipo `Option[Result]` cuyo contenido se utiliza para testear si el codigo de estado de la peticion es el esperado, las cabeceras o incluso el contenido de la respuesta.

###3. Lista de metodos probados.

1. Modelo

	1.1 Usuario

		User.add(User)
		User.getUser(String)

	1.2 Task

		Task.create(Task)
		Task.findById(Long)
		Task.all(Option[Long])
		Task.delete(Long)
		Task.listByUser(String, Option[Long])
		Task.update(Long, Task)
		Task.isOwner(Long, String)

2. Controlador

	2.1 Application

		index()
		tasks(Option[Int])
		newTask()
		deleteTask(Long)
		showNewTaskForm()
		editTask(Long)
		updateTask(Long)
		showSiginForm()
		signNewUser()
		showLoginForm()
		doLogin()
		logout()
		onUnauthorized(RequestHeader)
		getLoggedUser(f: => String => Request[AnyContent] => Result)
		isOwnerOf(Long)(f: => String => Request[AnyContent] => Result)












