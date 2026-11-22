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
import model.Peca;
import util.Mensagem;

// Tela de controle de peças: implementa SistemaObserver para atualizar os saldos
// automaticamente sempre que uma Ordem de Serviço consumir ou estornar itens.
public class CRUDPeca extends JFrame implements SistemaObserver {

    private final JTable tabelaPecas;
    private final DefaultTableModel model;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField txtBusca;
    private final JButton btnNovo;
    private final JButton btnEditar;
    private final JButton btnExcluir;

    private final AssistenciaFacade facade = AssistenciaFacade.getInstance();

    public CRUDPeca() {
        facade.adicionarObserver(this);

        setTitle("Controle de Peças e Estoque");
        setSize(800, 430);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(painelPrincipal);

        // === TOPO: Busca dinâmica ===
        JPanel painelTopo = new JPanel(new BorderLayout(8, 0));
        painelTopo.add(new JLabel("Buscar peça (Nome ou Marca):"), BorderLayout.WEST);
        txtBusca = new JTextField();
        painelTopo.add(txtBusca, BorderLayout.CENTER);
        painelPrincipal.add(painelTopo, BorderLayout.NORTH);

        // === CENTRO: Tabela de Peças ===
        model = new DefaultTableModel(
            new String[]{"ID", "Nome", "Marca", "Preço Unit.", "Estoque", "Mínimo", "Situação"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaPecas = new JTable(model);
        tabelaPecas.setRowHeight(24);
        sorter = new TableRowSorter<>(model);
        tabelaPecas.setRowSorter(sorter);
        painelPrincipal.add(new JScrollPane(tabelaPecas), BorderLayout.CENTER);

        // === RODAPÉ: Botões de ação ===
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnNovo = new JButton("Nova Peça");
        btnEditar = new JButton("Editar / Repor Estoque");
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
        btnEditar.addActionListener(e -> editarPeca());
        btnExcluir.addActionListener(e -> excluirPeca());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                facade.removerObserver(CRUDPeca.this);
            }
        });

        carregarPecas();
        setVisible(true);
    }

    private void filtrar() {
        String texto = txtBusca.getText().trim();
        sorter.setRowFilter(texto.isEmpty() ? null : RowFilter.regexFilter("(?i)" + texto));
    }

    private void carregarPecas() {
        model.setRowCount(0);
        List<Peca> pecas = facade.listarPecas();
        for (Peca p : pecas) {
            String situacao = p.getQuantidadeEstoque() <= p.getEstoqueMinimo() ? "ESTOQUE BAIXO" : "NORMAL";
            model.addRow(new Object[]{
                p.getId(),
                p.getNome(),
                p.getMarca(),
                String.format("R$ %.2f", p.getPreco()),
                p.getQuantidadeEstoque(),
                p.getEstoqueMinimo(),
                situacao
            });
        }
    }

    private void abrirFormulario(Peca peca) {
        FormularioPeca form = new FormularioPeca(this, peca);
        form.setVisible(true);
    }

    private void editarPeca() {
        int linhaView = tabelaPecas.getSelectedRow();
        if (linhaView >= 0) {
            int linhaModel = tabelaPecas.convertRowIndexToModel(linhaView);
            int id = (int) model.getValueAt(linhaModel, 0);
            Peca peca = facade.buscarPecaPorId(id);
            abrirFormulario(peca);
        } else {
            Mensagem.aviso("Selecione uma peça para editar!");
        }
    }

    private void excluirPeca() {
        int linhaView = tabelaPecas.getSelectedRow();
        if (linhaView >= 0) {
            int linhaModel = tabelaPecas.convertRowIndexToModel(linhaView);
            int id = (int) model.getValueAt(linhaModel, 0);

            if (Mensagem.confirmar("Deseja realmente excluir esta peça do estoque?")) {
                if (facade.excluirPeca(id)) {
                    Mensagem.info("Peça excluída com sucesso!");
                } else {
                    Mensagem.erro("Não foi possível excluir a peça (verifique se ela já foi usada em alguma OS).");
                }
            }
        } else {
            Mensagem.aviso("Selecione uma peça para excluir!");
        }
    }

    // === MÉTODOS DO PADRÃO OBSERVER ===

    @Override
    public void onOrdemServicoAtualizada() {
        // Atualização de saldo é tratada por onEstoqueAtualizado
    }

    @Override
    public void onEstoqueAtualizado(String alertaEstoque) {
        carregarPecas();
    }

    @Override
    public void onClienteAtualizado() {
    }
}