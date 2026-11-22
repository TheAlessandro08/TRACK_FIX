package view;

import controller.AssistenciaFacade;
import controller.SistemaObserver;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import model.Cliente;
import util.Mensagem;

// Tela de gerenciamento de clientes com busca dinâmica e atualização via Observer.
public class CRUDCliente extends JFrame implements SistemaObserver {

    private final JTable tabelaClientes;
    private final DefaultTableModel model;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField txtBusca;
    private final JButton btnNovo;
    private final JButton btnEditar;
    private final JButton btnExcluir;

    private final AssistenciaFacade facade = AssistenciaFacade.getInstance();

    public CRUDCliente() {
        facade.adicionarObserver(this);

        setTitle("Gerenciamento de Clientes");
        setSize(760, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(painelPrincipal);

        // === TOPO: Busca ===
        JPanel painelTopo = new JPanel(new BorderLayout(8, 0));
        painelTopo.add(new JLabel("Buscar cliente (Nome ou CPF/CNPJ):"), BorderLayout.WEST);
        txtBusca = new JTextField();
        painelTopo.add(txtBusca, BorderLayout.CENTER);
        painelPrincipal.add(painelTopo, BorderLayout.NORTH);

        // === CENTRO: Tabela ===
        model = new DefaultTableModel(
            new String[]{"ID", "Nome", "CPF/CNPJ", "Telefone", "E-mail"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaClientes = new JTable(model);
        tabelaClientes.setRowHeight(24);
        sorter = new TableRowSorter<>(model);
        tabelaClientes.setRowSorter(sorter);
        painelPrincipal.add(new JScrollPane(tabelaClientes), BorderLayout.CENTER);

        // === RODAPÉ: Botões ===
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnNovo = new JButton("Novo Cliente");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        txtBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });

        btnNovo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> editarCliente());
        btnExcluir.addActionListener(e -> excluirCliente());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                facade.removerObserver(CRUDCliente.this);
            }
        });

        carregarClientes();
        setVisible(true);
    }

    private void filtrar() {
        String texto = txtBusca.getText().trim();
        sorter.setRowFilter(texto.isEmpty() ? null : RowFilter.regexFilter("(?i)" + texto));
    }

    private void carregarClientes() {
        model.setRowCount(0);
        List<Cliente> clientes = facade.listarClientes();
        for (Cliente c : clientes) {
            model.addRow(new Object[]{
                c.getId(),
                c.getNome(),
                c.getCpfCnpj(),
                c.getTelefone(),
                c.getEmail()
            });
        }
    }

    private void abrirFormulario(Cliente cliente) {
        FormularioCliente form = new FormularioCliente(this, cliente);
        form.setVisible(true);
    }

    private void editarCliente() {
        int linhaView = tabelaClientes.getSelectedRow();
        if (linhaView >= 0) {
            int linhaModel = tabelaClientes.convertRowIndexToModel(linhaView);
            int id = (int) model.getValueAt(linhaModel, 0);
            Cliente cliente = facade.buscarClientePorId(id);
            abrirFormulario(cliente);
        } else {
            Mensagem.aviso("Selecione um cliente para editar!");
        }
    }

    private void excluirCliente() {
        int linhaView = tabelaClientes.getSelectedRow();
        if (linhaView >= 0) {
            int linhaModel = tabelaClientes.convertRowIndexToModel(linhaView);
            int id = (int) model.getValueAt(linhaModel, 0);

            if (Mensagem.confirmar("Deseja realmente excluir este cliente?")) {
                if (facade.excluirCliente(id)) {
                    Mensagem.info("Cliente excluído com sucesso!");
                } else {
                    Mensagem.erro("Não foi possível excluir o cliente (verifique se existem OSs vinculadas a ele).");
                }
            }
        } else {
            Mensagem.aviso("Selecione um cliente para excluir!");
        }
    }

    @Override
    public void onOrdemServicoAtualizada() {
    }

    @Override
    public void onEstoqueAtualizado(String alertaEstoque) {
    }

    @Override
    public void onClienteAtualizado() {
        carregarClientes();
    }
}