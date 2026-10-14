import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/** Utilidades compartidas; no contiene decisiones propias de las categorias. */
final class Validaciones {
    private Validaciones() { }

    static String texto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede estar vacio.");
        }
        return valor.trim();
    }

    static String identificador(String valor, String campo) {
        return texto(valor, campo).toUpperCase(Locale.ROOT);
    }

    static double positivo(double valor, String campo) {
        if (!Double.isFinite(valor) || valor <= 0) {
            throw new IllegalArgumentException(campo + " debe ser un numero finito mayor que cero.");
        }
        return valor;
    }

    static <T> T noNulo(T valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
        return valor;
    }

    // Los atributos y firmas siguen usando double como en el UML.
    // BigDecimal evita errores acumulados al redondear y sumar centavos.
    static double dinero(double valor) {
        positivo(valor, "El monto");
        return BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    static double sumarDinero(double primero, double segundo) {
        double total = BigDecimal.valueOf(primero).add(BigDecimal.valueOf(segundo)).doubleValue();
        if (!Double.isFinite(total)) {
            throw new IllegalArgumentException("El ingreso acumulado excede el rango numerico.");
        }
        return total;
    }
}
