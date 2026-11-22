package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.ItemOrdemServico;
import model.OrdemServico;

// Gerencia a persistência das Ordens de Serviço e de seus itens/peças vinculados.
public class OrdemServicoDAO {

    // Insere uma nova OS e recupera o ID auto-incremento gerado pelo MySQL
    public boolean inserir(OrdemServico os) {
        String sql = "INSERT INTO ordens_servico "
                   + "(id_cliente, id_tecnico, equipamento, defeito_relatado, diagnostico_tecnico, status, valor_servico, valor_pecas, valor_total) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, os.getIdCliente());
            if (os.getIdTecnico() > 0) {
                stmt.setInt(2, os.getIdTecnico());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, os.getEquipamento());
            stmt.setString(4, os.getDefeitoRelatado());
            stmt.setString(5, os.getDiagnosticoTecnico());
            stmt.setString(6, os.getStatus());
            stmt.setDouble(7, os.getValorServico());
            stmt.setDouble(8, os.getValorPecas());
            stmt.setDouble(9, os.getValorTotal());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        os.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao inserir Ordem de Serviço: " + e.getMessage());
        }
        return false;
    }

    public boolean gravar(OrdemServico os) {
        String sql = "UPDATE ordens_servico SET "
                   + "id_cliente = ?, id_tecnico = ?, equipamento = ?, defeito_relatado = ?, "
                   + "diagnostico_tecnico = ?, status = ?, valor_servico = ?, valor_pecas = ?, valor_total = ? "
                   + "WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, os.getIdCliente());
            if (os.getIdTecnico() > 0) {
                stmt.setInt(2, os.getIdTecnico());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, os.getEquipamento());
            stmt.setString(4, os.getDefeitoRelatado());
            stmt.setString(5, os.getDiagnosticoTecnico());
            stmt.setString(6, os.getStatus());
            stmt.setDouble(7, os.getValorServico());
            stmt.setDouble(8, os.getValorPecas());
            stmt.setDouble(9, os.getValorTotal());
            stmt.setInt(10, os.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar Ordem de Serviço: " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM ordens_servico WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao excluir Ordem de Serviço: " + e.getMessage());
            return false;
        }
    }

    public OrdemServico buscarPorId(int id) {
        String sql = "SELECT * FROM ordens_servico WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearOS(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Ordem de Serviço por ID: " + e.getMessage());
        }
        return null;
    }

    public List<OrdemServico> listarTodas() {
        List<OrdemServico> lista = new ArrayList<>();
        String sql = "SELECT * FROM ordens_servico ORDER BY id DESC";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearOS(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar Ordens de Serviço: " + e.getMessage());
        }
        return lista;
    }

    // Registra uma peça aplicada na OS na tabela associativa itens_ordem_servico
    public boolean inserirItem(ItemOrdemServico item) {
        String sql = "INSERT INTO itens_ordem_servico (id_ordem_servico, id_peca, quantidade, valor_unitario) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, item.getIdOrdemServico());
            stmt.setInt(2, item.getIdPeca());
            stmt.setInt(3, item.getQuantidade());
            stmt.setDouble(4, item.getValorUnitario());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao inserir item na Ordem de Serviço: " + e.getMessage());
            return false;
        }
    }

    // Lista todas as peças vinculadas a uma OS específica para cálculo de totais ou estorno
    public List<ItemOrdemServico> listarItensPorOS(int idOs) {
        List<ItemOrdemServico> itens = new ArrayList<>();
        String sql = "SELECT * FROM itens_ordem_servico WHERE id_ordem_servico = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idOs);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ItemOrdemServico item = new ItemOrdemServico();
                    item.setId(rs.getInt("id"));
                    item.setIdOrdemServico(rs.getInt("id_ordem_servico"));
                    item.setIdPeca(rs.getInt("id_peca"));
                    item.setQuantidade(rs.getInt("quantidade"));
                    item.setValorUnitario(rs.getDouble("valor_unitario"));
                    itens.add(item);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar itens da Ordem de Serviço: " + e.getMessage());
        }
        return itens;
    }

    private OrdemServico mapearOS(ResultSet rs) throws SQLException {
        OrdemServico os = new OrdemServico();
        os.setId(rs.getInt("id"));
        os.setIdCliente(rs.getInt("id_cliente"));
        os.setIdTecnico(rs.getInt("id_tecnico"));
        os.setEquipamento(rs.getString("equipamento"));
        os.setDefeitoRelatado(rs.getString("defeito_relatado"));
        os.setDiagnosticoTecnico(rs.getString("diagnostico_tecnico"));
        os.setStatus(rs.getString("status"));
        os.setValorServico(rs.getDouble("valor_servico"));
        os.setValorPecas(rs.getDouble("valor_pecas"));
        os.setValorTotal(rs.getDouble("valor_total"));
        os.setDataAbertura(rs.getString("data_abertura"));
        return os;
    }
}