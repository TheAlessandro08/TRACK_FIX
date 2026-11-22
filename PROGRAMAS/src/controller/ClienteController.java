package controller;

import dao.ClienteDAO;
import java.util.List;
import model.Cliente;
import util.ResultadoCadastro;

public class ClienteController {

    private final ClienteDAO clienteDAO;

    public ClienteController() {
        this.clienteDAO = new ClienteDAO();
    }

    // Valida a obrigatoriedade do nome e remove espaços extras antes de inserir.
    public ResultadoCadastro cadastrar(Cliente cliente) {
        if (cliente == null || cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            return ResultadoCadastro.ERRO_BANCO;
        }
        cliente.setNome(cliente.getNome().trim());
        return clienteDAO.inserir(cliente);
    }

    // Garante que o cliente possua um ID válido antes de atualizar no banco.
    public boolean atualizar(Cliente cliente) {
        if (cliente == null || cliente.getId() <= 0
                || cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            return false;
        }
        cliente.setNome(cliente.getNome().trim());
        return clienteDAO.gravar(cliente);
    }

    public boolean excluir(int id) {
        if (id <= 0) {
            return false;
        }
        return clienteDAO.excluir(id);
    }

    public Cliente buscarPorId(int id) {
        return clienteDAO.buscarPorId(id);
    }

    public List<Cliente> listarTodos() {
        return clienteDAO.listarTodos();
    }
}