import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/** Resultado calculado para un reporte, sin modificar los datos originales. */
public class ResumenCategoria {
    private final String categoria;
    private final EnumMap<EstadoEnvio, Integer> cantidadesPorEstado;
    private double ingresosRegistro;
    private double ingresosReintento;

// Constructor que inicializa el resumen para una categoría específica, estableciendo las cantidades por estado en cero y los ingresos en cero.
    public ResumenCategoria(String categoria) {
        this.categoria = Validaciones.texto(categoria, "La categoria");
        cantidadesPorEstado = new EnumMap<>(EstadoEnvio.class);
        for (EstadoEnvio estado : EstadoEnvio.values()) {
            cantidadesPorEstado.put(estado, 0);
        }
    }

// Métodos para contar envíos y acumular movimientos de ingresos, asegurando que los envíos y movimientos correspondan a la categoría del resumen y actualizando las cantidades por estado e ingresos totales.
    public void contarEnvio(Envio envio) {
        Validaciones.noNulo(envio, "El envio");
        exigirCategoria(envio);
        EstadoEnvio estado = envio.getEstado();
        cantidadesPorEstado.put(estado, cantidadesPorEstado.get(estado) + 1);
    }

// Acumula un movimiento de ingreso, verificando que el movimiento no sea nulo y que corresponda a la categoría del resumen. Dependiendo del tipo de ingreso (registro o reintento), se actualizan los ingresos correspondientes.
    public void acumularMovimiento(MovimientoIngreso movimiento) {
        Validaciones.noNulo(movimiento, "El movimiento");
        exigirCategoria(movimiento.getEnvio());
        if (movimiento.getTipo() == TipoIngreso.REGISTRO) {
            ingresosRegistro = Validaciones.sumarDinero(ingresosRegistro, movimiento.getMonto());
        } else {
            ingresosReintento = Validaciones.sumarDinero(ingresosReintento, movimiento.getMonto());
        }
    }

//Metodo privado que verifica si el envío pertenece a la categoría del resumen, lanzando una excepción si no es así.
    private void exigirCategoria(Envio envio) {
        if (!categoria.equals(envio.obtenerCategoria())) {
            throw new IllegalArgumentException("El envio no corresponde a la categoria del resumen.");
        }
    }

// Métodos de acceso para obtener la categoría, las cantidades por estado y los ingresos totales, proporcionando una vista inmutable de las cantidades por estado.
    public String getCategoria() { return categoria; }
    public Map<EstadoEnvio, Integer> getCantidadesPorEstado() {
        return Collections.unmodifiableMap(new EnumMap<>(cantidadesPorEstado));
    }
    public double getIngresosRegistro() { return ingresosRegistro; }
    public double getIngresosReintento() { return ingresosReintento; }
    public double getIngresoTotal() {
        return Validaciones.sumarDinero(ingresosRegistro, ingresosReintento);
    }
}
