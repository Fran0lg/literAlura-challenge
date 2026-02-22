package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.AutorRepository;
import com.aluracursos.literalura.repository.LibroRepository;
import com.aluracursos.literalura.service.ConsumoAPI;
import com.aluracursos.literalura.service.ConvierteDatos;

import java.util.Optional;
import java.util.Scanner;
import java.util.List;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos conversor = new ConvierteDatos();
    private LibroRepository libroRepositorio;
    private AutorRepository autorRepositorio;

    public Principal(LibroRepository libroRepo, AutorRepository autorRepo) {
        this.libroRepositorio = libroRepo;
        this.autorRepositorio = autorRepo;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar libro por título 
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                    0 - Salir
                    """;
            System.out.println(menu);
            try {
                opcion = teclado.nextInt();
                teclado.nextLine();
            } catch (Exception e) {
                System.out.println("Opción inválida. Por favor, ingrese un número.");
                teclado.nextLine();
                continue;
            }

            switch (opcion) {
                case 1:
                    buscarLibroWeb();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosEnAnio();
                    break;
                case 5:
                    mostrarEstadisticasPorIdioma();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    private DatosLibro getDatosLibro() {
        System.out.println("Escribe el nombre del libro que deseas buscar");
        var nombreLibro = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + "?search=" + nombreLibro.replace(" ", "+"));
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        return datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(nombreLibro.toUpperCase()))
                .findFirst()
                .orElse(null);
    }

    private void buscarLibroWeb() {
        DatosLibro datos = getDatosLibro();

        if (datos != null) {
            Optional<Libro> libroExistente = libroRepositorio.findByTituloContainsIgnoreCase(datos.titulo());

            if (libroExistente.isPresent()) {
                System.out.println("AVISO: Este libro ya está registrado.");
                System.out.println(libroExistente.get());
            } else {
                // 1. Creamos el objeto Libro desde los datos de la API
                Libro libro = new Libro(datos);

                // 2. Extraemos el primer autor de la lista de autores que viene en el JSON
                if (!datos.autor().isEmpty()) {
                    DatosAutor datosAutor = datos.autor().get(0); // Tomamos el primero según el Trello

                    // 3. Verificamos si el autor ya existe en la BD para no duplicar autores
                    Autor autor = autorRepositorio.findByNombreContainsIgnoreCase(datosAutor.nombre())
                            .orElseGet(() -> autorRepositorio.save(new Autor(datosAutor)));

                    // 4. ¡Aquí está la magia! Vinculamos el autor al libro
                    libro.setAutor(autor);
                }

                libroRepositorio.save(libro);
                System.out.println(libro);
                System.out.println("¡Libro guardado con éxito!");
            }
        } else {
            System.out.println("Libro no encontrado");
        }
    }

    private void listarLibrosRegistrados() {
        List<Libro> libros = libroRepositorio.findAll();

        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados en tu catálogo.");
        } else {
            libros.forEach(System.out::println);
        }
    }

    private void listarAutoresRegistrados() {
        // En el challenge avanzado, esto se hace desde el autorRepositorio
        List<Autor> autores = autorRepositorio.findAll();

        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
        } else {
            autores.forEach(System.out::println);
        }
    }

    private void listarAutoresVivosEnAnio() {
        System.out.println("Ingresa el año que deseas consultar:");
        try {
            var anio = teclado.nextInt();
            teclado.nextLine();
            List<Autor> autores = autorRepositorio.autoresVivosEnDeterminadoAnio(anio);
            if (autores.isEmpty()) {
                System.out.println("No se encontraron autores vivos en el año " + anio);
            } else {
                autores.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Año inválido.");
            teclado.nextLine();
        }
    }

    private void mostrarEstadisticasPorIdioma() {
        System.out.println("""
            Ingrese el idioma para buscar los libros:
            es - español
            en - inglés
            fr - francés
            pt - portugués
            """);
        var idioma = teclado.nextLine();
        List<Libro> libros = libroRepositorio.findByIdioma(idioma);
        System.out.println("Cantidad de libros encontrados en [" + idioma + "]: " + libros.size());
        libros.forEach(System.out::println);
    }
}