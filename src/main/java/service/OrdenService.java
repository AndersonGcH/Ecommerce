package service;

import model.Orden;
import model.ProductoOrden;
import repository.ClienteRepository;
import repository.CodigoOrdenGenerator;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrdenService {

    private final ClienteRepository clienteRepository;
    private final StockService stockService;
    private final CodigoOrdenGenerator codigoOrdenGenerator;

    public OrdenService(ClienteRepository clienteRepository,
                        StockService stockService,
                        CodigoOrdenGenerator codigoOrdenGenerator) {
        this.clienteRepository = clienteRepository;
        this.stockService = stockService;
        this.codigoOrdenGenerator = codigoOrdenGenerator;
    }

    public Orden registrarOrden(String clienteId, List<ProductoOrden> productos) {

        validarCliente(clienteId);
        validarProductos(productos);

        double total = calcularTotal(productos);

        total = aplicarDescuento(total);

        return new Orden(
                codigoOrdenGenerator.siguienteCodigo(),
                clienteId,
                LocalDate.now(),
                productos,
                total
        );
    }

    private void validarCliente(String clienteId) {
        if (!clienteRepository.existeCliente(clienteId)) {
            throw new IllegalArgumentException("Cliente no existe");
        }
        if (!clienteRepository.clienteActivo(clienteId)) {
            throw new IllegalArgumentException("Cliente inactivo");
        }
    }

    private void validarProductos(List<ProductoOrden> productos) {
        if (productos == null || productos.isEmpty()) {
            throw new IllegalArgumentException("La orden debe tener productos");
        }
    }
    private double calcularTotal(List<ProductoOrden> productos) {
        Set<String> ids = new HashSet<>();
        double total = 0;

        for (ProductoOrden p : productos) {

            validarProducto(p, ids);

            total += p.cantidad() * p.precioUnitario();
        }

        return total;
    }

    private void validarProducto(ProductoOrden p, Set<String> ids) {

        if (p.cantidad() <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }

        if (!ids.add(p.productoId())) {
            throw new IllegalArgumentException("No se permiten productos duplicados");
        }

        if (!stockService.hayStock(p.productoId(), p.cantidad())) {
            throw new IllegalStateException("Sin stock, orden cancelada");
        }
    }

    private double aplicarDescuento(double total) {
        if (total > 500) {
            return total * 0.9;
        }
        return total;
    }
}
