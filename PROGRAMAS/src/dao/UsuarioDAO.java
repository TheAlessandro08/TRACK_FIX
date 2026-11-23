package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Usuario;
import util.ResultadoCadastro;

// Responsável pela persistência e autenticação de usuários e técnicos no banco.
public class UsuarioDAO {

    // Busca o usuário combinando login, hash SHA-256 da senha e status ativo ('T')
    public Usuario autenticar(String login, String senhaHash) {
        String sql = "SELECT * FROM usuarios WHERE login = ? AND senha = ? AND ativo = 'T'";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, senhaHash);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao autenticar usuário: " + e.getMessage());
        }
        return null;
    }

    // Verifica duplicidade de login antes de realizar um novo cadastro
    public boolean existeUsuario(String login) {
        String sql = "SELECT 1 FROM usuarios WHERE login = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar usuário: " + e.getMessage());
            return false;
        }
    }

    // Insere um novo usuário retornando o status padronizado (SUCESSO, JA_EXISTE ou ERRO_BANCO)
    public ResultadoCadastro inserir(Usuario usuario) {
        if (existeUsuario(usuario.getLogin())) {
            return ResultadoCadastro.JA_EXISTE;
        }

        String sql = "INSERT INTO usuarios (login, senha, tipo, ativo, email) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getLogin());
            stmt.setString(2, usuario.getSenha());
            stmt.setString(3, usuario.getTipo() != null ? usuario.getTipo() : "U");
            stmt.setString(4, usuario.getAtivo() != null ? usuario.getAtivo() : "T");
            stmt.setString(5, usuario.getEmail());

            stmt.executeUpdate();
            return ResultadoCadastro.SUCESSO;

        } catch (SQLException e) {
            System.out.println("Erro ao inserir usuário: " + e.getMessage());
            return ResultadoCadastro.ERRO_BANCO;
        }
    }

    // Atualiza os dados cadastrais sem sobrescrever a senha já armazenada
    public boolean gravar(Usuario usuario) {
        String sql = "UPDATE usuarios SET login = ?, tipo = ?, ativo = ?, email = ? WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getLogin());
            stmt.setString(2, usuario.getTipo());
            stmt.setString(3, usuario.getAtivo());
            stmt.setString(4, usuario.getEmail());
            stmt.setInt(5, usuario.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar usuário: " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao excluir usuário: " + e.getMessage());
            return false;
        }
    }

    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuário por ID: " + e.getMessage());
        }
        return null;
    }

    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY id";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar usuários: " + e.getMessage());
        }
        return lista;
    }

    // Método auxiliar para evitar repetição na conversão de ResultSet para objeto Usuario
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setLogin(rs.getString("login"));
        u.setSenha(rs.getString("senha"));
        u.setTipo(rs.getString("tipo"));
        u.setAtivo(rs.getString("ativo"));
        u.setEmail(rs.getString("email"));
        u.setDataCriacao(rs.getString("data_criacao"));
        return u;
    }
}