package pe.edu.upeu.service;

import pe.edu.upeu.model.Cliente;

public interface IClienteService extends ICrudGenericoService<Cliente, Long> {
    // Puedes agregar métodos específicos para buscar por DNI o RUC si lo requieres
    Cliente buscarPorDniRuc(String dniRuc);
}