# LiterAlura - Challenge Alura Latam 

¡Bienvenido al proyecto LiterAlura! Una herramienta de consola desarrollada en Java que permite gestionar un catálogo de libros y autores conectándose a la API de Gutendex.

## Funcionalidades
- **Buscar libro por título:** Obtiene datos directamente desde la API y los persiste en una base de datos PostgreSQL.
- **Listar libros registrados:** Muestra todos los libros almacenados localmente.
- **Listar autores registrados:** Presenta una lista de los escritores guardados.
- **Autores vivos en determinado año:** Filtra autores según su periodo de vida.
- **Estadísticas por idioma:** Consulta cuántos libros hay registrados en un idioma específico (ES, EN, FR, PT).

## Tecnologías utilizadas
- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA**
- **PostgreSQL**
- **Jackson (Manejo de JSON)**
- **API Gutendex**

## Cómo ejecutar el proyecto
1. Clona el repositorio.
2. Configura tus credenciales de base de datos en `application.properties`.
3. Ejecuta la clase `LiteraluraApplication`.