package view;

import controller.UsuarioController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import model.Usuario;
import util.Mensagem;

// Tela administrativa para listar, criar, editar e inativar/excluir usuários e técnicos.
public class CRUDUsuario extends JFrame {

    private final JTable tabelaUsuarios;
    private final DefaultTableModel model;
    private final JButton btnNovo;
    private final JButton btnEditar;
    private final JButton btnExcluir;

    private final UsuarioController usuarioController = new UsuarioController();

    public CRUDUsuario() {
        setTitle("Gerenciamento de Usuários e Técnicos");
        setSize(740, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(painelPrincipal);

        model = new DefaultTableModel(
            new String[]{"ID", "Login", "E-mail", "Perfil", "Situação", "Data Criação"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaUsuarios = new JTable(model);
        tabelaUsuarios.setRowHeight(24);
        painelPrincipal.add(new JScrollPane(tabelaUsuarios), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnNovo = new JButton("Novo");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        btnNovo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> editarUsuario());
        btnExcluir.addActionListener(e -> excluirUsuario());

        carregarUsuarios();
        setVisible(true);
    }

    private void carregarUsuarios() {
        model.setRowCount(0);
        List<Usuario> usuarios = usuarioController.listarTodos();
        for (Usuario u : usuarios) {
            String tipoLegivel = "A".equalsIgnoreCase(u.getTipo()) ? "Administrador" : "Técnico / Usuário";
            String ativoLegivel = "T".equalsIgnoreCase(u.getAtivo()) ? "Ativo" : "Inativo";

            model.addRow(new Object[]{
                u.getId(),
                u.getLogin(),
                u.getEmail(),
                tipoLegivel,
                ativoLegivel,
                u.getDataCriacao()
            });
        }
    }

    private void abrirFormulario(Usuario usuario) {
        FormularioUsuario form = new FormularioUsuario(this, usuario);
        form.setVisible(true);
    }

    private void editarUsuario() {
        int linha = tabelaUsuarios.getSelectedRow();
        if (linha >= 0) {
            int id = (int) model.getValueAt(linha, 0);
            Usuario usuario = usuarioController.buscarPorId(id);
            abrirFormulario(usuario);
        } else {
            Mensagem.aviso("Selecione um usuário para editar!");
        }
    }

    private void excluirUsuario() {
        int linha = tabelaUsuarios.getSelectedRow();
        if (linha >= 0) {
            int id = (int) model.getValueAt(linha, 0);
            if (Mensagem.confirmar("Deseja realmente excluir este usuário?")) {
                if (usuarioController.excluir(id)) {
                    carregarUsuarios();
                    Mensagem.info("Usuário excluído com sucesso!");
                } else {
                    Mensagem.erro("Erro ao excluir usuário!");
                }
            }
        } else {
            Mensagem.aviso("Selecione um usuário para excluir!");
        }
    }

    public void atualizarTabela() {
        carregarUsuarios();
    }
}