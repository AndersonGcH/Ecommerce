package service;
import model.Orden;
import model.ProductoOrden;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.ClienteRepository;
import repository.CodigoOrdenGenerator;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class OrdenServiceTest {

    @Mock
    ClienteRepository clienteRepository;
    @Mock StockService stockService;
    @Mock CodigoOrdenGenerator codigoGenerator;

    private OrdenService service;

    @BeforeEach
    void setUp() {
        service = new OrdenService(clienteRepository, stockService, codigoGenerator);
    }

    @Test
    void deberiaRegistrarOrdenConDescuentoCuandoTotalSupera500() {
        when(clienteRepository.existeCliente("C1")).thenReturn(true);
        when(clienteRepository.clienteActivo("C1")).thenReturn(true);
        when(stockService.hayStock("P1", 2)).thenReturn(true);
        when(stockService.hayStock("P2", 2)).thenReturn(true);
        when(codigoGenerator.siguienteCodigo()).thenReturn("OR-0001");

        List<ProductoOrden> productos = List.of(
                new ProductoOrden("P1", 2, 200.0),
                new ProductoOrden("P2", 2, 100.0)
        );

        Orden orden = service.registrarOrden("C1", productos);

        assertEquals("OR-0001", orden.getCodigo());
        assertEquals(LocalDate.now(), orden.getFecha());
        assertEquals(540.0, orden.getTotalFinal());
    }

    @Test
    void deberiaCancelarOrdenSiAlgunProductoNoTieneStock() {
        when(clienteRepository.existeCliente("C1")).thenReturn(true);
        when(clienteRepository.clienteActivo("C1")).thenReturn(true);
        when(stockService.hayStock("P1", 1)).thenReturn(true);
        when(stockService.hayStock("P2", 1)).thenReturn(false);

        List<ProductoOrden> productos = List.of(
                new ProductoOrden("P1", 1, 100.0),
                new ProductoOrden("P2", 1, 80.0)
        );

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.registrarOrden("C1", productos)
        );

        assertEquals("Sin stock, orden cancelada", ex.getMessage());
    }
}