//la cotizacion no modifica los registros ni los ingresos de la empresa.
public class Cotizacion {
    private final Envio envio;

    public Cotizacion(Envio envio) {
        this.envio = Validaciones.noNulo(envio, "El envio");
    }

    public Envio getEnvio() { return envio; }
    public double getCosto() { return envio.calcularCosto(); }
    public int getDiasHabiles() { return envio.estimarDiasHabiles(); }
}
