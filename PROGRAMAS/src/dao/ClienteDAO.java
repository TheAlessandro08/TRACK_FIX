package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Cliente;
import util.ResultadoCadastro;

// Responsável pelas operações CRUD da tabela clientes.
public class ClienteDAO {

    // Evita duplicidade caso o CPF/CNPJ tenha sido preenchido
    public boolean existeClientePorDocumento(String cpfCnpj) {
        if (cpfCnpj == null || cpfCnpj.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT 1 FROM clientes WHERE cpf_cnpj = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpfCnpj.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar cliente: " + e.getMessage());
            return false;
        }
    }

    public ResultadoCadastro inserir(Cliente cliente) {
        if (existeClientePorDocumento(cliente.getCpfCnpj())) {
            return ResultadoCadastro.JA_EXISTE;
        }

        String sql = "INSERT INTO clientes (nome, cpf_cnpj, telefone, email) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpfCnpj());
            stmt.setString(3, cliente.getTelefone());
            stmt.setString(4, cliente.getEmail());

            stmt.executeUpdate();
            return ResultadoCadastro.SUCESSO;

        } catch (SQLException e) {
            System.out.println("Erro ao inserir cliente: " + e.getMessage());
            return ResultadoCadastro.ERRO_BANCO;
        }
    }

    public boolean gravar(Cliente cliente) {
        String sql = "UPDATE clientes SET nome = ?, cpf_cnpj = ?, telefone = ?, email = ? WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpfCnpj());
            stmt.setString(3, cliente.getTelefone());
            stmt.setString(4, cliente.getEmail());
            stmt.setInt(5, cliente.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar cliente: " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM clientes WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao excluir cliente: " + e.getMessage());
            return false;
        }
    }

    public Cliente buscarPorId(int id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar cliente por ID: " + e.getMessage());
        }
        return null;
    }

    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY nome";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar clientes: " + e.getMessage());
        }
        return lista;
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setNome(rs.getString("nome"));
        c.setCpfCnpj(rs.getString("cpf_cnpj"));
        c.setTelefone(rs.getString("telefone"));
        c.setEmail(rs.getString("email"));
        return c;
    }
}