// Clase de sobre que hereda de la clase Envio, representando un tipo de envío que tiene un peso máximo permitido de 0.5 kg. Esta clase implementa métodos para calcular el costo del envío, estimar los días hábiles de entrega, obtener el número máximo de intentos de entrega y obtener la categoría del envío.

public class Sobre extends Envio {
    public Sobre(String codigo, String remitente, String destinatario,
                 double pesoReal, Destino destino) {
        super(codigo, remitente, destinatario, pesoReal, destino);
        if (pesoReal > 0.5) {
            throw new IllegalArgumentException("Un sobre no puede pesar mas de 0.5 kg.");
        }
    }

// Implementación de los métodos abstractos heredados de la clase Envio, adaptados a las características específicas del sobre.
    @Override
    public double calcularCosto() { return getDestino() == Destino.URBANO ? 20 : 35; }
    @Override
    public int estimarDiasHabiles() { return getDestino() == Destino.URBANO ? 1 : 2; }
    @Override
    public int obtenerMaxIntentos() { return 3; }
    @Override
    public String obtenerCategoria() { return "Sobre"; }
}
