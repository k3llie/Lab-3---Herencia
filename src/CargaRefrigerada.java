import java.math.BigDecimal;
import java.util.Locale;


//Clase que representa un tipo de envío de carga refrigerada, que hereda de la clase Envio. Esta clase incluye atributos y métodos específicos para manejar envíos que requieren control de temperatura, asegurando que solo se puedan enviar a destinos urbanos y dentro de un rango de temperatura permitido.
public class CargaRefrigerada extends Envio {
    private final double temperaturaC;

    public CargaRefrigerada(String codigo, String remitente, String destinatario,
                            double pesoReal, Destino destino, double temperaturaC) {
        super(codigo, remitente, destinatario, pesoReal, destino);
        if (destino != Destino.URBANO) {
            throw new IllegalArgumentException("La carga refrigerada solo admite destino urbano.");
        }
        if (!Double.isFinite(temperaturaC) || temperaturaC < -25 || temperaturaC > 8) {
            throw new IllegalArgumentException("La temperatura debe estar entre -25 y 8 grados Celsius.");
        }
        this.temperaturaC = temperaturaC;
        Validaciones.positivo(costoRefrigerado(), "El costo de la carga refrigerada");
    }

    private double costoRefrigerado() {
        return BigDecimal.valueOf(getPesoReal()).multiply(BigDecimal.valueOf(12))
                .add(BigDecimal.valueOf(85)).doubleValue();
    }

// Implementación de los métodos abstractos heredados de la clase Envio, adaptados a las características específicas de la carga refrigerada.
    @Override
    public double calcularCosto() { return Validaciones.dinero(costoRefrigerado()); }
    @Override
    public int estimarDiasHabiles() { return 1; }
    @Override
    public int obtenerMaxIntentos() { return 1; }
    @Override
    public String obtenerCategoria() { return "Carga refrigerada"; }
    @Override
    public boolean admiteVehiculo(Vehiculo vehiculo) {
        return Validaciones.noNulo(vehiculo, "El vehiculo").isRefrigerado();
    }
    @Override
    public String obtenerDescripcion() {
        return super.obtenerDescripcion()
                + String.format(Locale.US, " | Temperatura: %.2f C", temperaturaC);
    }
}
