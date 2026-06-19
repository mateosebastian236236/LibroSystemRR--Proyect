package librosystemrr;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

public class Main {

    static class Libro {
        String isbn;
        String titulo;
        String autor;
        boolean disponible;

        Libro(String isbn, String titulo, String autor, boolean disponible) {
            this.isbn = isbn;
            this.titulo = titulo;
            this.autor = autor;
            this.disponible = disponible;
        }
    }

    static class NodoBST {
        Libro libro;
        NodoBST izquierdo;
        NodoBST derecho;

        NodoBST(Libro libro) {
            this.libro = libro;
        }
    }

    static class CatalogoBST {
        NodoBST raiz;

        void insertar(Libro libro) {
            raiz = insertarRec(raiz, libro);
        }

        private NodoBST insertarRec(NodoBST nodo, Libro libro) {
            if (nodo == null) return new NodoBST(libro);
            if (libro.isbn.compareTo(nodo.libro.isbn) < 0) {
                nodo.izquierdo = insertarRec(nodo.izquierdo, libro);
            } else {
                nodo.derecho = insertarRec(nodo.derecho, libro);
            }
            return nodo;
        }
    }

    static class Multa {
        static final double MONTO_POR_DIA = 0.50;
        double monto;
        boolean pagada;
        LocalDate fechaPago;

        Multa(int diasRetraso) {
            this.monto = diasRetraso * MONTO_POR_DIA;
            this.pagada = false;
        }

        void pagar() {
            this.pagada = true;
            this.fechaPago = LocalDate.now();
        }
    }

    static class Prestamo {
        static int contador = 1;
        int id;
        Libro libro;
        String usuario;
        LocalDate fechaPrestamo;
        LocalDate fechaDevolucion;
        boolean devuelto;
        Multa multa;

        Prestamo(Libro libro, String usuario, LocalDate fechaPrestamo, LocalDate fechaDevolucion) {
            this.id = contador++;
            this.libro = libro;
            this.usuario = usuario;
            this.fechaPrestamo = fechaPrestamo;
            this.fechaDevolucion = fechaDevolucion;
            this.devuelto = false;
        }

        boolean isVencido() {
            return !devuelto && LocalDate.now().isAfter(fechaDevolucion);
        }

        long getDiasRetraso() {
            if (!isVencido()) return 0;
            return ChronoUnit.DAYS.between(fechaDevolucion, LocalDate.now());
        }

        void devolver() {
            long retraso = getDiasRetraso();
            this.devuelto = true;
            this.libro.disponible = true;
            if (retraso > 0) {
                this.multa = new Multa((int) retraso);
            }
        }
    }

    static abstract class Empleado {
        String codigoEmpleado;

        Empleado(String codigoEmpleado) {
            this.codigoEmpleado = codigoEmpleado;
        }

        abstract String getTipo();
    }

    static class Bibliotecario extends Empleado {
        Bibliotecario(String codigoEmpleado) {
            super(codigoEmpleado);
        }

        String getTipo() {
            return "Bibliotecario";
        }

        void registrarLibro(CatalogoBST catalogo, Libro libro) {
            catalogo.insertar(libro);
        }

        void procesarDevolucion(Prestamo prestamo) {
            prestamo.devolver();
        }
    }

    static class AyudanteBibliotecario extends Empleado {
        AyudanteBibliotecario(String codigoEmpleado) {
            super(codigoEmpleado);
        }

        String getTipo() {
            return "AyudanteBibliotecario";
        }

        Prestamo registrarPrestamo(Libro libro, String usuario, LocalDate inicio, LocalDate vencimiento) {
            libro.disponible = false;
            return new Prestamo(libro, usuario, inicio, vencimiento);
        }
    }

    static class AlgoritmosBusqueda {
        static int busquedaLineal(Libro[] libros, String isbn) {
            for (int i = 0; i < libros.length; i++) {
                if (libros[i].isbn.equals(isbn)) return i;
            }
            return -1;
        }

        static int buscarPorTituloLineal(Libro[] libros, String titulo) {
            for (int i = 0; i < libros.length; i++) {
                if (libros[i].titulo.equalsIgnoreCase(titulo)) return i;
            }
            return -1;
        }

        static Prestamo[] buscarVencidos(Prestamo[] prestamos) {
            int count = 0;
            for (Prestamo p : prestamos) {
                if (p.isVencido()) count++;
            }
            Prestamo[] vencidos = new Prestamo[count];
            int idx = 0;
            for (Prestamo p : prestamos) {
                if (p.isVencido()) vencidos[idx++] = p;
            }
            return vencidos;
        }

        static int busquedaBinaria(Libro[] libros, String isbn) {
            int izq = 0;
            int der = libros.length - 1;
            while (izq <= der) {
                int medio = (izq + der) / 2;
                int cmp = libros[medio].isbn.compareTo(isbn);
                if (cmp == 0) return medio;
                if (cmp < 0) izq = medio + 1;
                else der = medio - 1;
            }
            return -1;
        }

        static Libro buscarEnBST(NodoBST nodo, String isbn) {
            if (nodo == null) return null;
            int cmp = isbn.compareTo(nodo.libro.isbn);
            if (cmp == 0) return nodo.libro;
            return cmp < 0 ? buscarEnBST(nodo.izquierdo, isbn) : buscarEnBST(nodo.derecho, isbn);
        }
    }

    static class AlgoritmosOrdenamiento {
        static void mergeSort(Libro[] libros, int izq, int der) {
            if (izq < der) {
                int medio = (izq + der) / 2;
                mergeSort(libros, izq, medio);
                mergeSort(libros, medio + 1, der);
                merge(libros, izq, medio, der);
            }
        }

        private static void merge(Libro[] libros, int izq, int medio, int der) {
            Libro[] temp = new Libro[der - izq + 1];
            int i = izq, j = medio + 1, k = 0;
            while (i <= medio && j <= der) {
                if (libros[i].titulo.compareTo(libros[j].titulo) <= 0) {
                    temp[k++] = libros[i++];
                } else {
                    temp[k++] = libros[j++];
                }
            }
            while (i <= medio) temp[k++] = libros[i++];
            while (j <= der) temp[k++] = libros[j++];
            for (int m = 0; m < temp.length; m++) {
                libros[izq + m] = temp[m];
            }
        }

        static void ordenarPorTitulo(Libro[] libros) {
            mergeSort(libros, 0, libros.length - 1);
        }

        static void ordenarPorAutor(Libro[] libros) {
            quickSort(libros, 0, libros.length - 1);
        }

        static void quickSort(Libro[] libros, int izq, int der) {
            if (izq < der) {
                int p = partition(libros, izq, der);
                quickSort(libros, izq, p - 1);
                quickSort(libros, p + 1, der);
            }
        }

        private static int partition(Libro[] libros, int izq, int der) {
            String pivote = libros[der].autor;
            int i = izq - 1;
            for (int j = izq; j < der; j++) {
                if (libros[j].autor.compareTo(pivote) <= 0) {
                    i++;
                    Libro tempL = libros[i];
                    libros[i] = libros[j];
                    libros[j] = tempL;
                }
            }
            Libro tempL = libros[i + 1];
            libros[i + 1] = libros[der];
            libros[der] = tempL;
            return i + 1;
        }

        static void ordenarPorFechaDevolucion(Prestamo[] prestamos) {
            Arrays.sort(prestamos, (a, b) -> a.fechaDevolucion.compareTo(b.fechaDevolucion));
        }
    }

    public static void main(String[] args) {
        Libro l1 = new Libro("9780001", "Cien Anios de Soledad", "Gabriel Garcia Marquez", true);
        Libro l2 = new Libro("9780002", "El Aleph", "Jorge Luis Borges", true);
        Libro l3 = new Libro("9780003", "Rayuela", "Julio Cortazar", true);
        Libro l4 = new Libro("9780004", "Ficciones", "Jorge Luis Borges", true);
        Libro l5 = new Libro("9780005", "Pedro Paramo", "Juan Rulfo", true);

        Libro[] libros = {l1, l2, l3, l4, l5};

        CatalogoBST catalogo = new CatalogoBST();
        Bibliotecario bibliotecario = new Bibliotecario("B-001");
        AyudanteBibliotecario ayudante = new AyudanteBibliotecario("A-001");

        for (Libro l : libros) {
            bibliotecario.registrarLibro(catalogo, l);
        }

        System.out.println("=== Busqueda lineal por ISBN ===");
        int idx = AlgoritmosBusqueda.busquedaLineal(libros, "9780003");
        System.out.println(idx != -1 ? libros[idx].titulo : "No encontrado");

        System.out.println("\n=== Busqueda lineal por titulo ===");
        idx = AlgoritmosBusqueda.buscarPorTituloLineal(libros, "El Aleph");
        System.out.println(idx != -1 ? libros[idx].autor : "No encontrado");

        System.out.println("\n=== Busqueda en BST ===");
        Libro encontrado = AlgoritmosBusqueda.buscarEnBST(catalogo.raiz, "9780004");
        System.out.println(encontrado != null ? encontrado.titulo : "No encontrado");

        System.out.println("\n=== Ordenamiento por titulo (mergeSort) ===");
        Libro[] porTitulo = Arrays.copyOf(libros, libros.length);
        AlgoritmosOrdenamiento.ordenarPorTitulo(porTitulo);
        for (Libro l : porTitulo) System.out.println(l.titulo);

        System.out.println("\n=== Ordenamiento por autor (quickSort) ===");
        Libro[] porAutor = Arrays.copyOf(libros, libros.length);
        AlgoritmosOrdenamiento.ordenarPorAutor(porAutor);
        for (Libro l : porAutor) System.out.println(l.autor + " - " + l.titulo);

        System.out.println("\n=== Busqueda binaria (arreglo ordenado por isbn) ===");
        Libro[] porIsbn = Arrays.copyOf(libros, libros.length);
        Arrays.sort(porIsbn, (a, b) -> a.isbn.compareTo(b.isbn));
        idx = AlgoritmosBusqueda.busquedaBinaria(porIsbn, "9780002");
        System.out.println(idx != -1 ? porIsbn[idx].titulo : "No encontrado");

        System.out.println("\n=== Registro de prestamos ===");
        LocalDate hoy = LocalDate.now();
        Prestamo p1 = ayudante.registrarPrestamo(l1, "Maria Lopez", hoy.minusDays(20), hoy.minusDays(5));
        Prestamo p2 = ayudante.registrarPrestamo(l2, "Carlos Vega", hoy.minusDays(3), hoy.plusDays(4));
        Prestamo p3 = ayudante.registrarPrestamo(l5, "Ana Torres", hoy.minusDays(15), hoy.minusDays(1));

        Prestamo[] prestamos = {p1, p2, p3};

        System.out.println("\n=== Prestamos vencidos ===");
        Prestamo[] vencidos = AlgoritmosBusqueda.buscarVencidos(prestamos);
        for (Prestamo p : vencidos) {
            System.out.println("Prestamo #" + p.id + " - " + p.libro.titulo + " - " + p.usuario + " - dias retraso: " + p.getDiasRetraso());
        }

        System.out.println("\n=== Procesar devolucion con calculo de multa ===");
        bibliotecario.procesarDevolucion(p1);
        if (p1.multa != null) {
            System.out.println("Multa generada: $" + p1.multa.monto);
        } else {
            System.out.println("Sin multa");
        }

        System.out.println("\n=== Ordenamiento de prestamos por fecha de devolucion ===");
        AlgoritmosOrdenamiento.ordenarPorFechaDevolucion(prestamos);
        for (Prestamo p : prestamos) {
            System.out.println("Prestamo #" + p.id + " - vence: " + p.fechaDevolucion);
        }
    }
}