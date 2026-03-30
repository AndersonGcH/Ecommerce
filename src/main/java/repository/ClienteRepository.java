package repository;

public interface ClienteRepository {
    boolean existeCliente(String clienteId);
    boolean clienteActivo(String clienteId);
}
