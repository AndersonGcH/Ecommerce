package model;

import java.time.LocalDate;
import java.util.List;

public class Orden {
    private final String codigo;
    private final String clienteId;
    private final LocalDate fecha;
    private final List<ProductoOrden> productos;
    private final double totalFinal;

    public Orden(String codigo, String clienteId, LocalDate fecha, List<ProductoOrden> productos, double totalFinal) {
        this.codigo = codigo;
        this.clienteId = clienteId;
        this.fecha = fecha;
        this.productos = productos;
        this.totalFinal = totalFinal;
    }

    public String getCodigo() { return codigo; }
    public String getClienteId() { return clienteId; }
    public LocalDate getFecha() { return fecha; }
    public List<ProductoOrden> getProductos() { return productos; }
    public double getTotalFinal() { return totalFinal; }
}
