package view;

import controller.AssistenciaFacade;
import controller.SistemaObserver;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import model.OrdemServico;
import util.Mensagem;

// Tela de gerenciamento de Ordens de Serviço: atualiza-se automaticamente via Observer.
public class CRUDOrdemServico extends JFrame implements SistemaObserver {

    private final JTable tabelaOS;
    private final DefaultTableModel model;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField txtBusca;
    private final JButton btnNovaOS;
    private final JButton btnGerenciar;
    private final JButton btnCancelarOS;

    private final AssistenciaFacade facade = AssistenciaFacade.getInstance();

    public CRUDOrdemServico() {
        facade.adicionarObserver(this);

        setTitle("Gerenciamento de Ordens de Serviço");
        setSize(900, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(painelPrincipal);

        // === TOPO: Busca dinâmica ===
        JPanel painelTopo = new JPanel(new BorderLayout(8, 0));
        painelTopo.add(new JLabel("Filtrar OS (Cliente, Equipamento ou Status):"), BorderLayout.WEST);
        txtBusca = new JTextField();
        painelTopo.add(txtBusca, BorderLayout.CENTER);
        painelPrincipal.add(painelTopo, BorderLayout.NORTH);

        // === CENTRO: Tabela de Ordens de Serviço ===
        model = new DefaultTableModel(
            new String[]{"OS #", "Cliente", "Equipamento", "Status", "Mão de Obra", "Peças", "Total", "Abertura"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaOS = new JTable(model);
        tabelaOS.setRowHeight(24);
        sorter = new TableRowSorter<>(model);
        tabelaOS.setRowSorter(sorter);
        painelPrincipal.add(new JScrollPane(tabelaOS), BorderLayout.CENTER);

        // === RODAPÉ: Botões de ação ===
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnNovaOS = new JButton("Nova OS");
        btnGerenciar = new JButton("Gerenciar / Peças / Concluir");
        btnCancelarOS = new JButton("Cancelar OS");

        painelBotoes.add(btnNovaOS);
        painelBotoes.add(btnGerenciar);
        painelBotoes.add(btnCancelarOS);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        // Filtro enquanto digita
        txtBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });

        btnNovaOS.addActionListener(e -> abrirFormulario(null));
        btnGerenciar.addActionListener(e -> gerenciarSelecionada());
        btnCancelarOS.addActionListener(e -> cancelarSelecionada());

        // Remove o observador ao fechar a janela para evitar vazamento de memória
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                facade.removerObserver(CRUDOrdemServico.this);
            }
        });

        carregarOrdens();
        setVisible(true);
    }

    private void filtrar() {
        String texto = txtBusca.getText().trim();
        sorter.setRowFilter(texto.isEmpty() ? null : RowFilter.regexFilter("(?i)" + texto));
    }

    private void carregarOrdens() {
        model.setRowCount(0);

        // Mapeia ID -> Nome do cliente para exibição legível na JTable
        Map<Integer, String> mapaClientes = new HashMap<>();
        for (Cliente c : facade.listarClientes()) {
            mapaClientes.put(c.getId(), c.getNome());
        }

        List<OrdemServico> ordens = facade.listarOrdensServico();
        for (OrdemServico os : ordens) {
            String nomeCliente = mapaClientes.getOrDefault(os.getIdCliente(), "Cliente #" + os.getIdCliente());
            model.addRow(new Object[]{
                os.getId(),
                nomeCliente,
                os.getEquipamento(),
                os.getStatus(),
                String.format("R$ %.2f", os.getValorServico()),
                String.format("R$ %.2f", os.getValorPecas()),
                String.format("R$ %.2f", os.getValorTotal()),
                os.getDataAbertura()
            });
        }
    }

    private void abrirFormulario(OrdemServico os) {
        FormularioOrdemServico form = new FormularioOrdemServico(this, os);
        form.setVisible(true);
    }

    private void gerenciarSelecionada() {
        int linhaView = tabelaOS.getSelectedRow();
        if (linhaView >= 0) {
            int linhaModel = tabelaOS.convertRowIndexToModel(linhaView);
            int id = (int) model.getValueAt(linhaModel, 0);
            OrdemServico os = facade.buscarOSPorId(id);
            abrirFormulario(os);
        } else {
            Mensagem.aviso("Selecione uma Ordem de Serviço na tabela!");
        }
    }

    private void cancelarSelecionada() {
        int linhaView = tabelaOS.getSelectedRow();
        if (linhaView >= 0) {
            int linhaModel = tabelaOS.convertRowIndexToModel(linhaView);
            int id = (int) model.getValueAt(linhaModel, 0);

            if (Mensagem.confirmar("Deseja cancelar a OS #" + id + "? As peças utilizadas voltarão ao estoque.")) {
                if (facade.cancelarOrdemServico(id)) {
                    Mensagem.info("OS cancelada e peças estornadas ao estoque com sucesso!");
                } else {
                    Mensagem.erro("Não foi possível cancelar a OS (verifique se já está cancelada).");
                }
            }
        } else {
            Mensagem.aviso("Selecione uma Ordem de Serviço para cancelar!");
        }
    }

    // === MÉTODOS DO PADRÃO OBSERVER ===

    @Override
    public void onOrdemServicoAtualizada() {
        carregarOrdens();
    }

    @Override
    public void onEstoqueAtualizado(String alertaEstoque) {
        // Atualização de estoque já é tratada no Menu e no CRUDPeca
    }

    @Override
    public void onClienteAtualizado() {
        carregarOrdens(); // Atualiza nomes de clientes caso algum cadastro seja editado
    }
}