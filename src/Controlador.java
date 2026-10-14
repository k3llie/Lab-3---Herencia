import java.util.Locale;
import java.util.NoSuchElementException;

/** Conecta el menu con la empresa y controla los errores de cada operacion.
 *  Gestiona la entrada y salida de datos en consola. */

public class Controlador {
    private final Vista vista;
    private final EnviaGT empresa;

    public Controlador() {
        vista = new Vista();
        empresa = new EnviaGT();
    }

// Inicia el bucle principal (Usando switch) del programa, mostrando el menú y gestionando las operaciones seleccionadas por el usuario. Maneja excepciones para errores de entrada y operaciones inválidas, mostrando mensajes apropiados en la consola.
    public void iniciar() {
        vista.mostrarMensaje("Bienvenido a EnviaGT. Se cargaron 8 envios y 3 vehiculos sin ingresos iniciales.");
        boolean continuar = true;
        while (continuar) {
            try {
                switch (vista.mostrarMenu()) {
                    case 1: gestionarCotizacion(); break;
                    case 2: gestionarRegistroVehiculo(); break;
                    case 3: gestionarCreacionDespacho(); break;
                    case 4: gestionarAsignacion(); break;
                    case 5: gestionarRetiro(); break;
                    case 6: gestionarSalida(); break;
                    case 7: gestionarResultado(); break;
                    case 8: gestionarConsultas(); break;
                    case 9: gestionarReportes(); break;
                    case 0: continuar = false; break;
                    default: vista.mostrarError("Opcion no disponible.");
                }
            } catch (Vista.FinEntradaException ex) {
                vista.mostrarMensaje("\nFin de la entrada de consola.");
                continuar = false;
            } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException ex) {
                vista.mostrarError(ex.getMessage());
            }
        }
        vista.mostrarMensaje("Programa finalizado. Los datos solo se conservaron en memoria.");
    }

    private void gestionarCotizacion() {
        Cotizacion cotizacion = empresa.cotizar(vista.leerEnvio());
        vista.mostrarCotizacion(cotizacion);
        if (vista.confirmarRegistro()) {
            empresa.registrarEnvio(cotizacion);
            vista.mostrarMensaje(String.format(Locale.US,
                    "Envio registrado en bodega. Cobro registrado: Q%.2f.", cotizacion.getCosto()));
        } else {
            vista.mostrarMensaje("Cotizacion terminada sin registrar el envio ni cobrar.");
        }
    }

    private void gestionarRegistroVehiculo() {
        empresa.registrarVehiculo(vista.leerVehiculo());
        vista.mostrarMensaje("Vehiculo registrado.");
    }

    private void gestionarCreacionDespacho() {
        vista.mostrarVehiculos(empresa.obtenerVehiculos());
        vista.mostrarDespachos(empresa.obtenerDespachosActivos());
        Despacho despacho = empresa.crearDespacho(vista.leerTexto("Placa del vehiculo: "));
        vista.mostrarMensaje("Despacho " + despacho.getNumero() + " creado en preparacion.");
    }

    private void gestionarAsignacion() {
        int numero = vista.leerEntero("Numero de despacho: ");
        String codigo = vista.leerTexto("Codigo del envio: ");
        empresa.asignarEnvio(numero, codigo);
        vista.mostrarMensaje("Envio asignado. No se han generado cargos por esta asignacion.");
    }

    private void gestionarRetiro() {
        int numero = vista.leerEntero("Numero de despacho: ");
        String codigo = vista.leerTexto("Codigo del envio: ");
        empresa.retirarEnvio(numero, codigo);
        vista.mostrarMensaje("Envio retirado y devuelto a bodega. No se generaron cargos.");
    }

    private void gestionarSalida() {
        int numero = vista.leerEntero("Numero de despacho: ");
        double antes = empresa.obtenerIngresoPorTipo(TipoIngreso.REINTENTO);
        empresa.salirDespacho(numero);
        double cargo = empresa.obtenerIngresoPorTipo(TipoIngreso.REINTENTO) - antes;
        vista.mostrarMensaje(String.format(Locale.US,
                "Despacho en ruta. Cargo por reintentos de esta salida: Q%.2f.", cargo));
    }

    private void gestionarResultado() {
        int numero = vista.leerEntero("Numero de despacho: ");
        String codigo = vista.leerTexto("Codigo del envio: ");
        ResultadoEntrega resultado = vista.leerResultado();
        empresa.registrarResultado(numero, codigo, resultado);
        vista.mostrarMensaje("Resultado guardado. Envio: " + empresa.buscarEnvio(codigo).getEstado()
                + ". Despacho: " + empresa.buscarDespacho(numero).getEstado() + ".");
    }

    private void gestionarConsultas() {
        vista.mostrarEnvios(empresa.obtenerEnvios());
        vista.mostrarVehiculos(empresa.obtenerVehiculos());
        vista.mostrarDespachos(empresa.obtenerDespachosActivos());
    }

    private void gestionarReportes() {
        vista.mostrarReportes(empresa.generarResumenes(),
                empresa.obtenerIngresoPorTipo(TipoIngreso.REGISTRO),
                empresa.obtenerIngresoPorTipo(TipoIngreso.REINTENTO));
        vista.mostrarDespachos(empresa.obtenerDespachosActivos());
    }
}
