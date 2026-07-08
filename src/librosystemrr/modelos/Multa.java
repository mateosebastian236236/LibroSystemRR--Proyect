package librosystemrr.modelos;

import java.util.Date;

/**
 * Representa una multa generada por la devolución tardía de un préstamo.
 * El monto se calcula automáticamente a razón de {@link #MONTO_POR_DIA} por día de retraso.
 */
import java.io.Serializable;

public class Multa implements Serializable {

    /** Monto cobrado por cada día de retraso (en dólares). */
    public static final double MONTO_POR_DIA = 0.50;

    /** Monto total de la multa. */
    private double monto;

    /** Indica si la multa ya fue pagada. */
    private boolean pagada;

    /** Fecha en que se realizó el pago. {@code null} si aún no se ha pagado. */
    private Date fechaPago;

    /**
     * Construye una multa con el monto calculado según los días de retraso.
     *
     * @param diasRetraso Número de días de retraso en la devolución.
     */
    public Multa(int diasRetraso) {
        this.monto = diasRetraso * MONTO_POR_DIA;
        this.pagada = false;
        this.fechaPago = null;
    }

    // ══════════════════════════════════════════
    // MÉTODOS DE NEGOCIO
    // ══════════════════════════════════════════

    /**
     * Registra el pago de esta multa. Si ya estaba pagada, no hace nada.
     */
    public void pagar() {
        if (!pagada) {
            this.pagada = true;
            this.fechaPago = new Date();
        }
    }

    // ══════════════════════════════════════════
    // GETTERS
    // ══════════════════════════════════════════

    /** @return Monto total de la multa. */
    public double getMonto() { return monto; }

    /** @return {@code true} si la multa fue pagada. */
    public boolean isPagada() { return pagada; }

    /** @return Fecha de pago, o {@code null} si aún no se pagó. */
    public Date getFechaPago() { return fechaPago; }

    @Override
    public String toString() {
        return "Multa{monto=$" + String.format("%.2f", monto) +
                ", pagada=" + pagada +
                (fechaPago != null ? ", fechaPago=" + fechaPago : "") + "}";
    }
}