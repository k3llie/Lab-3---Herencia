import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Agrupa envios y controla la preparacion, salida y resultados del reparto.
public class Despacho {
    private final int numero;
    private final Vehiculo vehiculo;
    private final ArrayList<Envio> envios;
    private final Map<Envio, ResultadoEntrega> resultados;
    private EstadoDespacho estado;

    public Despacho(int numero, Vehiculo vehiculo) {
        if (numero <= 0) {
            throw new IllegalArgumentException("El numero de despacho debe ser positivo.");
        }
        this.numero = numero;
        this.vehiculo = Validaciones.noNulo(vehiculo, "El vehiculo");
        envios = new ArrayList<>();
        resultados = new LinkedHashMap<>();
        estado = EstadoDespacho.PREPARACION;
    }

    public void asignar(Envio envio) {
        Validaciones.noNulo(envio, "El envio");
        exigirEstado(EstadoDespacho.PREPARACION);
        if (envios.contains(envio)) {
            throw new IllegalStateException("El envio ya pertenece a este despacho.");
        }
        if (envio.getEstado() != EstadoEnvio.BODEGA
                || envio.getIntentos() >= envio.obtenerMaxIntentos()) {
            throw new IllegalStateException("El envio no esta disponible en bodega para otro intento.");
        }
        if (!envio.admiteVehiculo(vehiculo)) {
            throw new IllegalArgumentException("El vehiculo no es compatible con " + envio.obtenerCategoria() + ".");
        }
        BigDecimal nuevoPeso = pesoExacto().add(BigDecimal.valueOf(envio.getPesoReal()));
        if (nuevoPeso.compareTo(BigDecimal.valueOf(vehiculo.getCapacidadKg())) > 0) {
            throw new IllegalStateException("La asignacion excede la capacidad del vehiculo.");
        }
        // Todas las condiciones se verifican antes de modificar el envio o la lista.
        envio.asignar();
        envios.add(envio);
    }

// Retira un envio del despacho, asegurando que el despacho esté en preparación y que el envio pertenezca a este despacho.
    public void retirar(Envio envio) {
        Validaciones.noNulo(envio, "El envio");
        exigirEstado(EstadoDespacho.PREPARACION);
        exigirPertenencia(envio);
        envio.retirar();
        envios.remove(envio);
    }

    public void salir() {
        exigirEstado(EstadoDespacho.PREPARACION);
        if (envios.isEmpty()) {
            throw new IllegalStateException("No puede salir un despacho vacio.");
        }
        // Se valida el grupo completo antes de cambiar el primer envio.
        for (Envio envio : envios) {
            if (envio.getEstado() != EstadoEnvio.ASIGNADO
                    || envio.getIntentos() >= envio.obtenerMaxIntentos()) {
                throw new IllegalStateException("El envio " + envio.getCodigo() + " no puede salir.");
            }
        }
        for (Envio envio : envios) {
            envio.marcarEnRuta();
        }
        estado = EstadoDespacho.EN_RUTA;
    }

// Registra el resultado de un envio, asegurando que el despacho esté en ruta, que el envio pertenezca a este despacho y que no se haya registrado un resultado previamente.
    public void registrarResultado(Envio envio, ResultadoEntrega resultado) {
        Validaciones.noNulo(envio, "El envio");
        Validaciones.noNulo(resultado, "El resultado");
        exigirEstado(EstadoDespacho.EN_RUTA);
        exigirPertenencia(envio);
        if (resultados.containsKey(envio)) {
            throw new IllegalStateException("Este envio ya tiene resultado en este despacho.");
        }
        envio.registrarResultado(resultado);
        resultados.put(envio, resultado);
        if (resultados.size() == envios.size()) {
            estado = EstadoDespacho.CERRADO;
        }
    }

    private void exigirEstado(EstadoDespacho esperado) {
        if (estado != esperado) {
            throw new IllegalStateException("El despacho " + numero + " esta en " + estado
                    + "; esta operacion requiere " + esperado + ".");
        }
    }

    private void exigirPertenencia(Envio envio) {
        if (!envios.contains(envio)) {
            throw new IllegalArgumentException("El envio no pertenece a este despacho.");
        }
    }

    private BigDecimal pesoExacto() {
        BigDecimal peso = BigDecimal.ZERO;
        for (Envio envio : envios) {
            peso = peso.add(BigDecimal.valueOf(envio.getPesoReal()));
        }
        return peso;
    }

    public double getPesoCargado() { return pesoExacto().doubleValue(); }
    public double getCapacidadDisponible() {
        return BigDecimal.valueOf(vehiculo.getCapacidadKg()).subtract(pesoExacto()).doubleValue();
    }
    public EstadoDespacho getEstado() { return estado; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public int getNumero() { return numero; }
    public List<Envio> getEnvios() {
        return Collections.unmodifiableList(new ArrayList<>(envios));
    }

    // Consulta adicional para mostrar resultados pendientes sin exponer el mapa interno.
        return Collections.unmodifiableMap(new LinkedHashMap<>(resultados));
    }
}
