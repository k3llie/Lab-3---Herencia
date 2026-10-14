import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/** Administra registros, disponibilidad de vehiculos e ingresos en memoria.
 *  Proporciona operaciones de cotizacion, registro de envios y vehiculos,
 *  creacion de despachos, asignacion y retiro de envios, salida de despachos
 *  y registro de resultados. */

public class EnviaGT {
    private final Map<String, Envio> envios;
    private final Map<String, Vehiculo> vehiculos;
    private final Map<Integer, Despacho> despachos;
    private final ArrayList<MovimientoIngreso> movimientos;
    private int siguienteNumeroDespacho;

//Se define el constructor de la clase EnviaGT, que inicializa las estructuras de datos para almacenar envíos, vehículos, despachos y movimientos de ingresos. Además, se establece el número del siguiente despacho y se cargan datos iniciales para demostración.
    public EnviaGT() {
        envios = new LinkedHashMap<>();
        vehiculos = new LinkedHashMap<>();
        despachos = new LinkedHashMap<>();
        movimientos = new ArrayList<>();
        siguienteNumeroDespacho = 1;
        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        Envio[] ejemplos = {
            new Sobre("S001", "Ana", "Luis", 0.25, Destino.URBANO),
            new Sobre("S002", "Carlos", "Maria", 0.5, Destino.DEPARTAMENTAL),
            new Paquete("P001", "Elena", "Pedro", 3.2, Destino.URBANO, 40, 30, 20),
            new Paquete("P002", "Luis", "Sara", 5.1, Destino.DEPARTAMENTAL, 20, 20, 20),
            new PaqueteFragil("F001", "Tienda Uno", "Diana", 3.2, Destino.URBANO, 40, 30, 20, 1200),
            new PaqueteFragil("F002", "Tienda Dos", "Pablo", 2, Destino.DEPARTAMENTAL, 20, 20, 20, 500),
            new CargaRefrigerada("R001", "Farmacia", "Clinica", 4.5, Destino.URBANO, -10),
            new CargaRefrigerada("R002", "Mercado", "Restaurante", 7.25, Destino.URBANO, 4)
        };
// Son datos de demostracion: no se llama a registrarEnvio ni se crean cobros.
        for (Envio envio : ejemplos) {
            envios.put(envio.getCodigo(), envio);
        }
        registrarVehiculo(new Vehiculo("C001AAA", 10, false));
        registrarVehiculo(new Vehiculo("C002BBB", 25, true));
        registrarVehiculo(new Vehiculo("C003CCC", 5, false));
    }

    public Cotizacion cotizar(Envio envio) { return new Cotizacion(envio); }

    public void registrarEnvio(Cotizacion cotizacion) {
        Validaciones.noNulo(cotizacion, "La cotizacion");
        Envio envio = cotizacion.getEnvio();
        if (envios.containsKey(envio.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un envio con el codigo " + envio.getCodigo() + ".");
        }
        if (envio.getEstado() != EstadoEnvio.BODEGA || envio.getIntentos() != 0) {
            throw new IllegalStateException("Solo se puede registrar un envio nuevo, en bodega y sin intentos.");
        }
        MovimientoIngreso cobro = new MovimientoIngreso(envio, TipoIngreso.REGISTRO, cotizacion.getCosto());
        Validaciones.sumarDinero(obtenerIngresoTotal(), cobro.getMonto());
        envios.put(envio.getCodigo(), envio);
        movimientos.add(cobro);
    }

    public void registrarVehiculo(Vehiculo vehiculo) {
        Validaciones.noNulo(vehiculo, "El vehiculo");
        if (vehiculos.containsKey(vehiculo.getPlaca())) {
            throw new IllegalArgumentException("Ya existe un vehiculo con la placa " + vehiculo.getPlaca() + ".");
        }
        vehiculos.put(vehiculo.getPlaca(), vehiculo);
    }

    public Despacho crearDespacho(String placa) {
        Vehiculo vehiculo = buscarVehiculo(placa);
        for (Despacho despacho : despachos.values()) {
            if (despacho.getVehiculo() == vehiculo && despacho.getEstado() != EstadoDespacho.CERRADO) {
                throw new IllegalStateException("El vehiculo ya tiene un despacho sin cerrar.");
            }
        }
        Despacho despacho = new Despacho(siguienteNumeroDespacho, vehiculo);
        despachos.put(siguienteNumeroDespacho, despacho);
        siguienteNumeroDespacho++;
        return despacho;
    }

    public void asignarEnvio(int numero, String codigo) {
        Despacho despacho = buscarDespacho(numero);
        Envio envio = buscarEnvio(codigo);
        // Una referencia historica con resultado no constituye una asignacion activa.
        for (Despacho otro : despachos.values()) {
            if (otro.getEstado() != EstadoDespacho.CERRADO && otro.getEnvios().contains(envio)
                    && !otro.getResultados().containsKey(envio)) {
                throw new IllegalStateException("El envio ya esta asignado a un despacho.");
            }
        }
        despacho.asignar(envio);
    }

// Retira un envio del despacho, asegurando que el despacho esté en preparación y que el envio pertenezca a este despacho.
    public void retirarEnvio(int numero, String codigo) {
        buscarDespacho(numero).retirar(buscarEnvio(codigo));
    }

    public void salirDespacho(int numero) {
        Despacho despacho = buscarDespacho(numero);
        ArrayList<MovimientoIngreso> nuevosCobros = new ArrayList<>();
        double totalPrevisto = obtenerIngresoTotal();
        for (Envio envio : despacho.getEnvios()) {
            if (envio.getIntentos() >= 1) {
                MovimientoIngreso cobro = new MovimientoIngreso(envio, TipoIngreso.REINTENTO, 15);
                totalPrevisto = Validaciones.sumarDinero(totalPrevisto, cobro.getMonto());
                nuevosCobros.add(cobro);
            }
        }

// Si salir() rechaza la operacion, los cobros preparados nunca se guardan.
        despacho.salir();
        movimientos.addAll(nuevosCobros);
    }

    public void registrarResultado(int numero, String codigo, ResultadoEntrega resultado) {
        buscarDespacho(numero).registrarResultado(buscarEnvio(codigo), resultado);
    }

    public Envio buscarEnvio(String codigo) {
        String clave = Validaciones.identificador(codigo, "El codigo");
        Envio envio = envios.get(clave);
        if (envio == null) {
            throw new NoSuchElementException("No existe el envio " + clave + ".");
        }
        return envio;
    }

    public Vehiculo buscarVehiculo(String placa) {
        String clave = Validaciones.identificador(placa, "La placa");
        Vehiculo vehiculo = vehiculos.get(clave);
        if (vehiculo == null) {
            throw new NoSuchElementException("No existe el vehiculo " + clave + ".");
        }
        return vehiculo;
    }

    public Despacho buscarDespacho(int numero) {
        Despacho despacho = despachos.get(numero);
        if (despacho == null) {
            throw new NoSuchElementException("No existe el despacho " + numero + ".");
        }
        return despacho;
    }

    public List<Envio> obtenerEnvios() {
        return Collections.unmodifiableList(new ArrayList<>(envios.values()));
    }
    public List<Vehiculo> obtenerVehiculos() {
        return Collections.unmodifiableList(new ArrayList<>(vehiculos.values()));
    }
    public List<Despacho> obtenerDespachosActivos() {
        ArrayList<Despacho> activos = new ArrayList<>();
        for (Despacho despacho : despachos.values()) {
            if (despacho.getEstado() != EstadoDespacho.CERRADO) {
                activos.add(despacho);
            }
        }
        return Collections.unmodifiableList(activos);
    }

    public Map<String, ResumenCategoria> generarResumenes() {
        Map<String, ResumenCategoria> resumenes = new LinkedHashMap<>();
        for (Envio envio : envios.values()) {
            String categoria = envio.obtenerCategoria();
            resumenes.computeIfAbsent(categoria, ResumenCategoria::new).contarEnvio(envio);
        }
        for (MovimientoIngreso movimiento : movimientos) {
            String categoria = movimiento.getEnvio().obtenerCategoria();
            resumenes.computeIfAbsent(categoria, ResumenCategoria::new).acumularMovimiento(movimiento);
        }
        return Collections.unmodifiableMap(resumenes);
    }

    public double obtenerIngresoPorTipo(TipoIngreso tipo) {
        Validaciones.noNulo(tipo, "El tipo de ingreso");
        double total = 0;
        for (MovimientoIngreso movimiento : movimientos) {
            if (movimiento.getTipo() == tipo) {
                total = Validaciones.sumarDinero(total, movimiento.getMonto());
            }
        }
        return total;
    }

    public double obtenerIngresoTotal() {
        return Validaciones.sumarDinero(obtenerIngresoPorTipo(TipoIngreso.REGISTRO),
                obtenerIngresoPorTipo(TipoIngreso.REINTENTO));
    }
}
