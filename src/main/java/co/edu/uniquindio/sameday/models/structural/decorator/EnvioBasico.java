package co.edu.uniquindio.sameday.models.structural.decorator;

/**
 * PATRÓN ESTRUCTURAL: DECORATOR
 * Componente concreto que representa un envío básico sin servicios adicionales
 */
public class EnvioBasico implements EnvioComponent {
    private double tarifaBase;
    private double peso;

    public EnvioBasico(double peso) {
        this.peso = peso;
        this.tarifaBase = calcularTarifaPorPeso(peso);
    }

    /**
     * Calcula la tarifa base según el peso del paquete
     * Tarifa base: $11,000
     */
    private double calcularTarifaPorPeso(double peso) {
        double tarifa = 11000.0; // Tarifa base

        if (peso > 30) {
            tarifa += 15000.0;
        } else if (peso > 20) {
            tarifa += 10000.0;
        } else if (peso > 5) {
            tarifa += 5000.0;
        }

        return tarifa;
    }

    @Override
    public double calcularCosto() {
        return tarifaBase;
    }

    @Override
    public String getDescripcion() {
        return String.format("Envío básico (%.2f kg) - Tarifa base: $%,.0f", peso, tarifaBase);
    }

    public double getPeso() {
        return peso;
    }
}