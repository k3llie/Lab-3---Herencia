import java.math.BigDecimal;
import java.util.Locale;

//Es un paquete: reutiliza su peso facturable, tarifa base y plazo.
//Clase de paquete frágil que hereda de la clase Paquete, representando un tipo de envío que requiere un manejo especial debido a su fragilidad. Esta clase incluye un atributo adicional para el valor declarado del paquete y calcula su costo considerando un recargo por fragilidad y un porcentaje del valor declarado. Además, ajusta los días hábiles estimados y el número máximo de intentos de entrega.
public class PaqueteFragil extends Paquete {
    private final double valorDeclarado;

    public PaqueteFragil(String codigo, String remitente, String destinatario, double pesoReal,
                         Destino destino, double largoCm, double anchoCm, double altoCm,
                         double valorDeclarado) {
        super(codigo, remitente, destinatario, pesoReal, destino, largoCm, anchoCm, altoCm);
        this.valorDeclarado = Validaciones.positivo(valorDeclarado, "El valor declarado");
        Validaciones.positivo(costoFragil(), "El costo del paquete fragil");
    }

    private double costoFragil() {
        return BigDecimal.valueOf(super.calcularCosto()).add(BigDecimal.valueOf(15))
                .add(BigDecimal.valueOf(valorDeclarado).multiply(new BigDecimal("0.03"))).doubleValue();
    }

// Implementación de los métodos abstractos heredados de la clase Envio, adaptados a las características específicas del paquete frágil.
    @Override
    public double calcularCosto() { return Validaciones.dinero(costoFragil()); }
    @Override
    public int estimarDiasHabiles() { return super.estimarDiasHabiles() + 1; }
    @Override
    public int obtenerMaxIntentos() { return 2; }
    @Override
    public String obtenerCategoria() { return "Paquete fragil"; }
    @Override
    public String obtenerDescripcion() {
        return super.obtenerDescripcion()
                + String.format(Locale.US, " | Valor declarado: Q%.2f", valorDeclarado);
    }
}
