import java.util.Locale;

//Clase abstracta que representa un envio. Contiene los atributos y metodos comunes a todas las categorias de envio, asi como la logica de transicion
// Datos comunes y transiciones de estado de cualquier categoria de envio. 
public abstract class Envio {
    private final String codigo;
    private final String remitente;
    private final String destinatario;
    private final double pesoReal;
    private final Destino destino;
    private EstadoEnvio estado;
    private int intentos;

    protected Envio(String codigo, String remitente, String destinatario,
                    double pesoReal, Destino destino) {
        this.codigo = Validaciones.identificador(codigo, "El codigo");
        this.remitente = Validaciones.texto(remitente, "El remitente");
        this.destinatario = Validaciones.texto(destinatario, "El destinatario");
        this.pesoReal = Validaciones.positivo(pesoReal, "El peso real");
        this.destino = Validaciones.noNulo(destino, "El destino");
        estado = EstadoEnvio.BODEGA;
        intentos = 0;
    }

    public abstract double calcularCosto();
    public abstract int estimarDiasHabiles();
    public abstract int obtenerMaxIntentos();
    public abstract String obtenerCategoria();

    public boolean admiteVehiculo(Vehiculo vehiculo) {
        Validaciones.noNulo(vehiculo, "El vehiculo");
        return true;
    }

    public void asignar() {
        exigirEstado(EstadoEnvio.BODEGA);
        if (intentos >= obtenerMaxIntentos()) {
            throw new IllegalStateException("El envio ya agoto sus intentos de entrega.");
        }
        estado = EstadoEnvio.ASIGNADO;
    }

    public void retirar() {
        exigirEstado(EstadoEnvio.ASIGNADO);
        estado = EstadoEnvio.BODEGA;
    }

    public void marcarEnRuta() {
        exigirEstado(EstadoEnvio.ASIGNADO);
        if (intentos >= obtenerMaxIntentos()) {
            throw new IllegalStateException("El envio ya agoto sus intentos de entrega.");
        }
        intentos++;
        estado = EstadoEnvio.EN_RUTA;
    }

    public void registrarResultado(ResultadoEntrega resultado) {
        Validaciones.noNulo(resultado, "El resultado");
        exigirEstado(EstadoEnvio.EN_RUTA);
        if (resultado == ResultadoEntrega.ENTREGADO) {
            estado = EstadoEnvio.ENTREGADO;
        } else if (intentos >= obtenerMaxIntentos()) {
            estado = EstadoEnvio.DEVUELTO_REMITENTE;
        } else {
            estado = EstadoEnvio.BODEGA;
        }
    }

    private void exigirEstado(EstadoEnvio esperado) {
        if (estado != esperado) {
            throw new IllegalStateException("El envio " + codigo + " esta en " + estado
                    + "; esta operacion requiere " + esperado + ".");
        }
    }

    public String getCodigo() { return codigo; }
    public String getRemitente() { return remitente; }
    public String getDestinatario() { return destinatario; }
    public double getPesoReal() { return pesoReal; }
    public Destino getDestino() { return destino; }
    public EstadoEnvio getEstado() { return estado; }
    public int getIntentos() { return intentos; }

    public String obtenerDescripcion() {
        return String.format(Locale.US,
                "%s | %s | %s -> %s | %.3f kg | %s | %s | Intentos: %d/%d | Costo: Q%.2f | %d dia(s) habil(es)",
                codigo, obtenerCategoria(), remitente, destinatario, pesoReal, destino,
                estado, intentos, obtenerMaxIntentos(), calcularCosto(), estimarDiasHabiles());
    }
}
