import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Solo maneja entrada y salida; las reglas del negocio viven en el modelo. */
public class Vista {
    private final Scanner scanner;

    public Vista() {
        scanner = new Scanner(System.in, "UTF-8");
    }

    public int mostrarMenu() {
        System.out.println();
        System.out.println("███████╗███╗░░██╗██╗░░░██╗██╗░█████╗░  ░██████╗░████████╗");
        System.out.println("██╔════╝████╗░██║██║░░░██║██║██╔══██╗  ██╔════╝░╚══██╔══╝");
        System.out.println("█████╗░░██╔██╗██║╚██╗░██╔╝██║███████║  ██║░░██╗░░░░██║░░░");
        System.out.println("██╔══╝░░██║╚████║░╚████╔╝░██║██╔══██║  ██║░░╚██╗░░░██║░░░");
        System.out.println("███████╗██║░╚███║░░╚██╔╝░░██║██║░░██║  ╚██████╔╝░░░██║░░░");
        System.out.println("╚══════╝╚═╝░░╚══╝░░░╚═╝░░░╚═╝╚═╝░░╚═╝  ░╚═════╝░░░░╚═╝░░░");
        System.out.println();
        System.out.println("1. Cotizar un envio y, si se acepta, registrarlo");
        System.out.println("2. Registrar vehiculo");
        System.out.println("3. Crear despacho");
        System.out.println("4. Asignar envio a despacho");
        System.out.println("5. Retirar envio de despacho");
        System.out.println("6. Hacer salir un despacho");
        System.out.println("7. Registrar resultado de entrega");
        System.out.println("8. Consultar envios, vehiculos y despachos activos");
        System.out.println("9. Generar reportes");
        System.out.println("0. Salir");
        return leerOpcion("Opcion: ", 0, 9);
    }

    public String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            if (!scanner.hasNextLine()) {
                throw new FinEntradaException();
            }
            String texto = scanner.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            mostrarError("El dato no puede estar vacio.");
        }
    }

    public double leerDouble(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje).replace(',', '.');
            try {
                double valor = Double.parseDouble(texto);
                if (!Double.isFinite(valor)) {
                    throw new NumberFormatException();
                }
                return valor;
            } catch (NumberFormatException ex) {
                mostrarError("Ingrese un numero decimal finito, por ejemplo 3.2. No use separadores de miles.");
                // Se reemplaza la coma por un punto para permitir la entrada de decimales en formato europeo.
                //Evita que el usuario ingrese valores no finitos como NaN o Infinity, que no son válidos para el peso o la capacidad.
            }
        }
    }

    public int leerEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(mensaje));
            } catch (NumberFormatException ex) {
                mostrarError("Ingrese un numero entero valido.");
            }
        }
    }

    private int leerOpcion(String mensaje, int minimo, int maximo) {
        while (true) {
            int opcion = leerEntero(mensaje);
            if (opcion >= minimo && opcion <= maximo) {
                return opcion;
            }
            mostrarError("Seleccione una opcion entre " + minimo + " y " + maximo + ".");
        }
    }

    public Destino leerDestino() {
        int opcion = leerOpcion("Destino (1. Urbano / 2. Departamental): ", 1, 2);
        return opcion == 1 ? Destino.URBANO : Destino.DEPARTAMENTAL;
    }

    public ResultadoEntrega leerResultado() {
        int opcion = leerOpcion("Resultado (1. Entregado / 2. Fallido): ", 1, 2);
        return opcion == 1 ? ResultadoEntrega.ENTREGADO : ResultadoEntrega.FALLIDO;
    }

    public Envio leerEnvio() {
        System.out.println("1. Sobre | 2. Paquete | 3. Paquete fragil | 4. Carga refrigerada");
        int categoria = leerOpcion("Categoria: ", 1, 4);
        String codigo = leerTexto("Codigo: ");
        String remitente = leerTexto("Remitente: ");
        String destinatario = leerTexto("Destinatario: ");
        double peso = leerDouble("Peso real (kg): ");
        Destino destino = leerDestino();

        // Esta seleccion solo construye objetos. Los costos, restricciones y
        // reintentos se resuelven mediante metodos polimorficos en las categorias.
        switch (categoria) {
            case 1:
                return new Sobre(codigo, remitente, destinatario, peso, destino);
            case 2:
            case 3:
                double largo = leerDouble("Largo (cm): ");
                double ancho = leerDouble("Ancho (cm): ");
                double alto = leerDouble("Alto (cm): ");
                if (categoria == 3) {
                    double valor = leerDouble("Valor declarado (Q): ");
                    return new PaqueteFragil(codigo, remitente, destinatario, peso,
                            destino, largo, ancho, alto, valor);
                }
                return new Paquete(codigo, remitente, destinatario, peso, destino, largo, ancho, alto);
            case 4:
                double temperatura = leerDouble("Temperatura requerida (-25 a 8 C): ");
                return new CargaRefrigerada(codigo, remitente, destinatario, peso, destino, temperatura);
            default:
                throw new IllegalArgumentException("Categoria no disponible.");
        }
    }

    public Vehiculo leerVehiculo() {
        String placa = leerTexto("Placa: ");
        double capacidad = leerDouble("Capacidad maxima (kg): ");
        boolean refrigerado = leerSiNo("Tiene refrigeracion? (s/n): ");
        return new Vehiculo(placa, capacidad, refrigerado);
    }

    private boolean leerSiNo(String mensaje) {
        while (true) {
            String respuesta = leerTexto(mensaje).toLowerCase(Locale.ROOT);
            if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) {
                return true;
            }
            if (respuesta.equals("n") || respuesta.equals("no")) {
                return false;
            }
            mostrarError("Responda s o n.");
        }
    }

    public boolean confirmarRegistro() {
        return leerSiNo("Acepta la cotizacion y desea registrar y cobrar el envio? (s/n): ");
    }

    public void mostrarCotizacion(Cotizacion cotizacion) {
        System.out.printf(Locale.US, "Cotizacion %s: Q%.2f | Plazo: %d dia(s) habil(es).%n",
                cotizacion.getEnvio().getCodigo(), cotizacion.getCosto(), cotizacion.getDiasHabiles());
        System.out.println("Esta cotizacion todavia no registra el envio ni genera ingresos.");
    }

    public void mostrarEnvios(List<Envio> envios) {
        System.out.println("\n--- ENVIOS ---");
        if (envios.isEmpty()) {
            System.out.println("No hay envios registrados.");
        }
        for (Envio envio : envios) {
            System.out.println(envio.obtenerDescripcion());
        }
    }

    public void mostrarVehiculos(List<Vehiculo> vehiculos) {
        System.out.println("\n--- VEHICULOS ---");
        if (vehiculos.isEmpty()) {
            System.out.println("No hay vehiculos registrados.");
        }
        for (Vehiculo vehiculo : vehiculos) {
            System.out.println(vehiculo.obtenerDescripcion());
        }
    }

    public void mostrarDespachos(List<Despacho> despachos) {
        System.out.println("\n--- DESPACHOS SIN CERRAR ---");
        if (despachos.isEmpty()) {
            System.out.println("No hay despachos activos. Todos los vehiculos estan disponibles.");
        }
        for (Despacho despacho : despachos) {
            System.out.printf(Locale.US,
                    "Despacho %d | %s | Vehiculo: %s | Peso: %.3f kg | Capacidad: %.3f kg | Disponible: %.3f kg%n",
                    despacho.getNumero(), despacho.getEstado(), despacho.getVehiculo().getPlaca(),
                    despacho.getPesoCargado(), despacho.getVehiculo().getCapacidadKg(),
                    despacho.getCapacidadDisponible());
            Map<Envio, ResultadoEntrega> resultados = despacho.getResultados();
            for (Envio envio : despacho.getEnvios()) {
                String detalle = resultados.containsKey(envio) ? resultados.get(envio).toString()
                        : (despacho.getEstado() == EstadoDespacho.PREPARACION ? "Asignado" : "Resultado pendiente");
                System.out.println("  " + envio.getCodigo() + " | " + detalle);
            }
        }
        System.out.println("Un vehiculo esta disponible si no aparece en un despacho sin cerrar.");
    }

    public void mostrarReportes(Map<String, ResumenCategoria> resumenes,
                                 double ingresoRegistro, double ingresoReintento) {
        System.out.println("█─▄▄▄─██▀▄─██▄─▀█▄─▄█─▄─▄─█▄─▄█▄─▄▄▀██▀▄─██▄─▄▄▀█▄─▄▄─█─▄▄▄▄█");
        System.out.println("█─███▀██─▀─███─█▄▀─████─████─███─██─██─▀─███─██─██─▄█▀█▄▄▄▄─█");
        System.out.println("▀▄▄▄▄▄▀▄▄▀▄▄▀▄▄▄▀▀▄▄▀▀▄▄▄▀▀▄▄▄▀▄▄▄▄▀▀▄▄▀▄▄▀▄▄▄▄▀▀▄▄▄▄▄▀▄▄▄▄▄▀");
        System.out.println();
        System.out.println("█▀▀▀▀▀██─▄▄▄─██▀▄─██─▄─▄─█▄─▄▄─█─▄▄▄▄█─▄▄─█▄─▄▄▀█▄─▄██▀▄─██");
        System.out.println("████████─███▀██─▀─████─████─▄█▀█─██▄─█─██─██─▄─▄██─███─▀─██");      
        System.out.println("▀▀▀▀▀▀▀▀▄▄▄▄▄▀▄▄▀▄▄▀▀▄▄▄▀▀▄▄▄▄▄▀▄▄▄▄▄▀▄▄▄▄▀▄▄▀▄▄▀▄▄▄▀▄▄▀▄▄▀");
        System.out.println();
        System.out.println("█▀▀▀▀▀██▄─▄▄─█─▄▄▄▄█─▄─▄─██▀▄─██▄─▄▄▀█─▄▄─█");
        System.out.println("█████████─▄█▀█▄▄▄▄─███─████─▀─███─██─█─██─█");
        System.out.println("▀▀▀▀▀▀▀▀▄▄▄▄▄▀▄▄▄▄▄▀▀▄▄▄▀▀▄▄▀▄▄▀▄▄▄▄▀▀▄▄▄▄▀");
        System.out.println();
        System.out.printf("%-20s %7s %9s %8s %10s %9s %6s%n",
                "Categoria", "Bodega", "Asignado", "En ruta", "Entregado", "Devuelto", "Total");
        int totalEnvios = 0;
        for (ResumenCategoria resumen : resumenes.values()) {
            Map<EstadoEnvio, Integer> cantidades = resumen.getCantidadesPorEstado();
            int total = 0;
            for (int cantidad : cantidades.values()) {
                total += cantidad;
            }
            totalEnvios += total;
            System.out.printf("%-20s %7d %9d %8d %10d %9d %6d%n", resumen.getCategoria(),
                    cantidades.get(EstadoEnvio.BODEGA), cantidades.get(EstadoEnvio.ASIGNADO),
                    cantidades.get(EstadoEnvio.EN_RUTA), cantidades.get(EstadoEnvio.ENTREGADO),
                    cantidades.get(EstadoEnvio.DEVUELTO_REMITENTE), total);
        }
        System.out.println("Total de envios: " + totalEnvios);
        System.out.println("█████████████████▀████████████████████████████████████████████████████████████▀██████████████████████");
        System.out.println("█▄─▄█▄─▀█▄─▄█─▄▄▄▄█▄─▄▄▀█▄─▄▄─█─▄▄▄▄█─▄▄─█─▄▄▄▄███─▄▄▄─██▀▄─██─▄─▄─█▄─▄▄─█─▄▄▄▄█─▄▄─█▄─▄▄▀█▄─▄██▀▄─██");
        System.out.println("██─███─█▄▀─██─██▄─██─▄─▄██─▄█▀█▄▄▄▄─█─██─█▄▄▄▄─███─███▀██─▀─████─████─▄█▀█─██▄─█─██─██─▄─▄██─███─▀─██");
        System.out.println("▀▄▄▄▀▄▄▄▀▀▄▄▀▄▄▄▄▄▀▄▄▀▄▄▀▄▄▄▄▄▀▄▄▄▄▄▀▄▄▄▄▀▄▄▄▄▄▀▀▀▄▄▄▄▄▀▄▄▀▄▄▀▀▄▄▄▀▀▄▄▄▄▄▀▄▄▄▄▄▀▄▄▄▄▀▄▄▀▄▄▀▄▄▄▀▄▄▀▄▄▀");
        System.out.println();
        for (ResumenCategoria resumen : resumenes.values()) {
            System.out.printf(Locale.US, "%s | Registro: Q%.2f | Reintentos: Q%.2f | Total: Q%.2f%n",
                    resumen.getCategoria(), resumen.getIngresosRegistro(),
                    resumen.getIngresosReintento(), resumen.getIngresoTotal());
        }
        System.out.printf(Locale.US, "INGRESO TOTAL: Q%.2f (registros Q%.2f + reintentos Q%.2f)%n",
                Validaciones.sumarDinero(ingresoRegistro, ingresoReintento), ingresoRegistro, ingresoReintento);
    }

// Muestra un mensaje en la consola, precedido por "Error: " para indicar que se trata de un mensaje de error.
    public void mostrarMensaje(String mensaje) { System.out.println(mensaje); }
    public void mostrarError(String mensaje) { System.out.println("Error: " + mensaje); }

    /** Distingue el fin de entrada de una busqueda sin resultados en el modelo. */
    static class FinEntradaException extends NoSuchElementException {
        private static final long serialVersionUID = 1L;
    }
}
