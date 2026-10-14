import java.math.BigDecimal;
import java.util.Locale;

//Clase de paquete que hereda de la clase Envio, representando un tipo de envío que tiene dimensiones físicas (largo, ancho y alto) y calcula su costo basado en el peso facturable, que es el mayor entre el peso real y el peso volumétrico. Esta clase también implementa métodos para estimar días hábiles de entrega y obtener la descripción del paquete.
public class Paquete extends Envio {
    private final double largoCm;
    private final double anchoCm;
    private final double altoCm;

    public Paquete(String codigo, String remitente, String destinatario, double pesoReal,
                   Destino destino, double largoCm, double anchoCm, double altoCm) {
        super(codigo, remitente, destinatario, pesoReal, destino);
        this.largoCm = Validaciones.positivo(largoCm, "El largo");
        this.anchoCm = Validaciones.positivo(anchoCm, "El ancho");
        this.altoCm = Validaciones.positivo(altoCm, "El alto");
        // Se comprueba el rango sin llamar a un metodo sobrescrito en el constructor.
        Validaciones.positivo(costoPaquete(), "El costo del paquete");
    }

    public double calcularPesoVolumetrico() {
        return BigDecimal.valueOf(largoCm).multiply(BigDecimal.valueOf(anchoCm))
                .multiply(BigDecimal.valueOf(altoCm)).divide(BigDecimal.valueOf(5000)).doubleValue();
    }

    public double calcularPesoFacturable() {
        return Math.ceil(Math.max(getPesoReal(), calcularPesoVolumetrico()));
    }

    private double costoPaquete() {
        return (getDestino() == Destino.URBANO ? 25 : 40) + 8 * calcularPesoFacturable();
    }

// Implementación de los métodos abstractos heredados de la clase Envio, adaptados a las características específicas del paquete.
    @Override
    public double calcularCosto() { return Validaciones.dinero(costoPaquete()); }
    @Override
    public int estimarDiasHabiles() { return getDestino() == Destino.URBANO ? 2 : 4; }
    @Override
    public int obtenerMaxIntentos() { return 3; }
    @Override
    public String obtenerCategoria() { return "Paquete"; }

    @Override
    public String obtenerDescripcion() {
        return super.obtenerDescripcion() + String.format(Locale.US,
                " | Dimensiones: %.2f x %.2f x %.2f cm | Peso volumetrico: %.3f kg | Facturable: %.0f kg",
                largoCm, anchoCm, altoCm, calcularPesoVolumetrico(), calcularPesoFacturable());
    }
}
