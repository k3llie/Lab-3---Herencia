//Cobro inmutable: identifica su envio, motivo e importe efectivo.

public class MovimientoIngreso {
    private final Envio envio;
    private final TipoIngreso tipo;
    private final double monto;

// Constructor que inicializa un movimiento de ingreso con un envío, tipo de ingreso y monto, asegurando que ninguno de los parámetros sea nulo y que el monto sea positivo.
    public MovimientoIngreso(Envio envio, TipoIngreso tipo, double monto) {
        this.envio = Validaciones.noNulo(envio, "El envio");
        this.tipo = Validaciones.noNulo(tipo, "El tipo de ingreso");
        this.monto = Validaciones.dinero(monto);
        Validaciones.positivo(this.monto, "El monto redondeado");
    }

//Regresa el envio, tipo de ingreso y monto del movimiento, proporcionando métodos de acceso para cada uno de estos atributos.
    public Envio getEnvio() { return envio; }
    public TipoIngreso getTipo() { return tipo; }
    public double getMonto() { return monto; }
}
