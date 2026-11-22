package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Peca;
import util.ResultadoCadastro;

// Responsável pela persistência de peças e saldos do almoxarifado.
public class PecaDAO {

    public boolean existePeca(String nomePeca) {
        String sql = "SELECT 1 FROM pecas WHERE nome = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomePeca);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar peça: " + e.getMessage());
            return false;
        }
    }

    public ResultadoCadastro inserir(Peca peca) {
        if (existePeca(peca.getNome())) {
            return ResultadoCadastro.JA_EXISTE;
        }

        String sql = "INSERT INTO pecas (nome, marca, preco, quantidade_estoque, estoque_minimo) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, peca.getNome());
            stmt.setString(2, peca.getMarca());
            stmt.setDouble(3, peca.getPreco());
            stmt.setInt(4, peca.getQuantidadeEstoque());
            stmt.setInt(5, peca.getEstoqueMinimo());

            stmt.executeUpdate();
            return ResultadoCadastro.SUCESSO;

        } catch (SQLException e) {
            System.out.println("Erro ao inserir peça: " + e.getMessage());
            return ResultadoCadastro.ERRO_BANCO;
        }
    }

    // Atualiza tanto dados cadastrais quanto movimentações de saldo (baixa/reposição)
    public boolean gravar(Peca peca) {
        String sql = "UPDATE pecas SET nome = ?, marca = ?, preco = ?, quantidade_estoque = ?, estoque_minimo = ? WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, peca.getNome());
            stmt.setString(2, peca.getMarca());
            stmt.setDouble(3, peca.getPreco());
            stmt.setInt(4, peca.getQuantidadeEstoque());
            stmt.setInt(5, peca.getEstoqueMinimo());
            stmt.setInt(6, peca.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar peça: " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM pecas WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao excluir peça: " + e.getMessage());
            return false;
        }
    }

    public Peca buscarPorId(int id) {
        String sql = "SELECT * FROM pecas WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearPeca(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar peça por ID: " + e.getMessage());
        }
        return null;
    }

    public List<Peca> listarTodos() {
        List<Peca> lista = new ArrayList<>();
        String sql = "SELECT * FROM pecas ORDER BY nome";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPeca(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar peças: " + e.getMessage());
        }
        return lista;
    }

    private Peca mapearPeca(ResultSet rs) throws SQLException {
        Peca p = new Peca();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setMarca(rs.getString("marca"));
        p.setPreco(rs.getDouble("preco"));
        p.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
        p.setEstoqueMinimo(rs.getInt("estoque_minimo"));
        return p;
    }
}