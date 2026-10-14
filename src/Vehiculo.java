import java.util.Locale;

//Clase de vehículo que representa un medio de transporte con una placa, capacidad de carga y si es refrigerado o no. Esta clase proporciona métodos para obtener la descripción del vehículo y sus atributos.
public class Vehiculo {
    private final String placa;
    private final double capacidadKg;
    private final boolean refrigerado;

//Constructor para inicializar un vehículo con su placa, capacidad de carga y si es refrigerado o no, asegurando que la placa sea un identificador válido y que la capacidad sea positiva.
    public Vehiculo(String placa, double capacidadKg, boolean refrigerado) {
        this.placa = Validaciones.identificador(placa, "La placa");
        this.capacidadKg = Validaciones.positivo(capacidadKg, "La capacidad");
        this.refrigerado = refrigerado;
    }

// Métodos de acceso para obtener la placa, capacidad de carga y si es refrigerado o no, proporcionando información sobre el vehículo.
    public String getPlaca() { return placa; }
    public double getCapacidadKg() { return capacidadKg; }
    public boolean isRefrigerado() { return refrigerado; }
    public String obtenerDescripcion() {
        return String.format(Locale.US, "%s | Capacidad: %.3f kg | Refrigerado: %s",
                placa, capacidadKg, refrigerado ? "Si" : "No");
    }
}
