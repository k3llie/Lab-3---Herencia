# Lab-3 - Herencia

**Nombre completo:** Kellie Sophia López Torres

**Carné:** 261551

**Nombre completo:** Santiago Aguilón Muñoz

**Carné:** 26199

## Descripción

EnviaGT permite administrar envíos, vehículos y despachos de una empresa de mensajería. El programa cotiza y registra envíos, registra vehículos, crea despachos, asigna envíos, registra los resultados de entrega y presenta reportes de ingresos.

El programa está organizado con MVC: la Vista muestra el menú y pide los datos, el Controlador coordina las acciones y el modelo gestiona los envíos, vehículos y despachos.

La clase abstracta Envio reúne los datos comunes. Sus clases hijas (Sobre, Paquete, PaqueteFragil y CargaRefrigerada) aplican las reglas de cada categoría mediante polimorfismo.

Main inicia el programa y EnviaGT carga al comenzar dos envíos por categoría y al menos tres vehículos, con los ingresos en cero.

## Cómo ejecutar
```bash
javac -d bin src/*.java
java -cp bin Main
```

## Propósito de las clases

**Main**

Punto de entrada del programa. Crea el Controlador e inicia la ejecución.

**Controlador**

Coordina el flujo del programa. Mantiene el menú disponible, pide los datos a la Vista, delega las operaciones del negocio a EnviaGT y controla los errores de cada opción para que el programa no se detenga.

**Vista**

Se encarga de toda la interacción por consola. Presenta el menú, lee y valida los datos ingresados, construye los envíos y vehículos que el usuario describe y muestra cotizaciones, listas, reportes, mensajes y errores.

**EnviaGT**

Es el administrador del sistema y la clase principal del modelo. Conserva los envíos, vehículos, despachos e ingresos, valida que los códigos y las placas no se repitan, genera las cotizaciones, coordina los despachos, cobra los reintentos y calcula los resúmenes por categoría.

**Envio**

Clase abstracta que representa cualquier envío. Guarda los datos comunes (código, remitente, destinatario, peso, destino, estado e intentos) y controla su ciclo de vida: bodega, asignado, en ruta, entregado o devuelto al remitente. Cada categoría define su propio costo, plazo e intentos permitidos.

**Sobre**

Envío de documentos con peso máximo de 0.5 kg. Tiene tarifa fija según el destino, plazo de uno o dos días hábiles y hasta tres intentos de entrega.

**Paquete**

Envío de mercadería con dimensiones. Calcula el peso volumétrico, factura el mayor entre el peso real y el volumétrico y cobra una tarifa base más un monto por kilogramo facturable.

**PaqueteFragil**

Paquete que requiere cuidado especial. Hereda de Paquete y suma el costo de embalaje y un seguro sobre el valor declarado. Su plazo es un día mayor y solo permite dos intentos.

**CargaRefrigerada**

Envío que debe mantener la cadena de frío. Solo se acepta para destinos urbanos, exige una temperatura entre −25 °C y 8 °C, se entrega en un día hábil, permite un solo intento y solo puede viajar en vehículos refrigerados.

**Vehiculo**

Representa un vehículo de reparto. Guarda la placa, la capacidad máxima en kilogramos y si cuenta con refrigeración.

**Despacho**

Agrupa los envíos que salen juntos en un mismo vehículo. Verifica la capacidad y la compatibilidad antes de aceptar un envío, registra un resultado por cada envío entregado y se cierra cuando todos tienen respuesta, dejando libre al vehículo.

**Cotizacion**

Conserva la propuesta de un envío con su costo y plazo estimado sin registrarlo ni cobrarlo, para que el usuario decida si la acepta.

**MovimientoIngreso**

Registra un cobro efectivamente realizado, ya sea el pago inicial de un envío o el cargo por un reintento. Con estos movimientos se calculan los ingresos.

**ResumenCategoria**

Acumula, para una categoría de envío, la cantidad de envíos por estado y los ingresos por registro y por reintento. Es la base de los reportes.

### Enumeraciones

**Destino**

Indica si la entrega es urbana (dentro de la Ciudad de Guatemala) o departamental.

**EstadoEnvio**

Define las etapas de un envío: bodega, asignado, en ruta, entregado o devuelto al remitente.

**EstadoDespacho**

Define las etapas de un despacho: preparación, en ruta o cerrado.

**ResultadoEntrega**

Indica si un intento de entrega fue exitoso o fallido.

**TipoIngreso**

Distingue el cobro inicial de registro del cargo por reintento.