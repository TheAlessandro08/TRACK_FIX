package view;

import controller.AssistenciaFacade;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import model.Cliente;
import model.ItemOrdemServico;
import model.OrdemServico;
import model.Peca;
import util.Mensagem;

// Formulário modal para abrir novas OSs ou gerenciar peças, diagnóstico e conclusão.
public class FormularioOrdemServico extends JDialog {

    private final AssistenciaFacade facade = AssistenciaFacade.getInstance();
    private OrdemServico os;

    // Campos principais da OS
    private final JComboBox<Cliente> cbCliente;
    private final JTextField txtEquipamento;
    private final JTextField txtDefeito;
    private final JTextField txtDiagnostico;
    private final JTextField txtValorServico;
    private final JLabel lblTotais;

    // Campos da seção de peças (visíveis quando a OS já existe)
    private JComboBox<Peca> cbPeca;
    private JSpinner spnQuantidade;
    private DefaultTableModel modelItens;

    public FormularioOrdemServico(JFrame parent, OrdemServico os) {
        super(parent, true);
        this.os = os;

        boolean isNova = (os == null);
        setTitle(isNova ? "Abrir Nova Ordem de Serviço" : "Gerenciar Ordem de Serviço #" + os.getId());
        setSize(680, isNova ? 340 : 560);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel painelPrincipal = new JPanel(new BorderLayout(12, 12));
        painelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(painelPrincipal);

        // === TOPO: Dados do Equipamento e Atendimento ===
        JPanel painelDados = new JPanel(new GridLayout(isNova ? 4 : 6, 2, 10, 8));
        painelDados.setBorder(BorderFactory.createTitledBorder("Dados da Ordem de Serviço"));

        painelDados.add(new JLabel("Cliente:"));
        cbCliente = new JComboBox<>();
        for (Cliente c : facade.listarClientes()) {
            cbCliente.addItem(c);
        }
        painelDados.add(cbCliente);

        painelDados.add(new JLabel("Equipamento:"));
        txtEquipamento = new JTextField();
        painelDados.add(txtEquipamento);

        painelDados.add(new JLabel("Defeito Relatado:"));
        txtDefeito = new JTextField();
        painelDados.add(txtDefeito);

        painelDados.add(new JLabel("Valor Mão de Obra (R$):"));
        txtValorServico = new JTextField("0.00");
        painelDados.add(txtValorServico);

        if (!isNova) {
            painelDados.add(new JLabel("Diagnóstico Técnico:"));
            txtDiagnostico = new JTextField();
            painelDados.add(txtDiagnostico);

            painelDados.add(new JLabel("Resumo Financeiro:"));
            lblTotais = new JLabel();
            painelDados.add(lblTotais);
        } else {
            txtDiagnostico = null;
            lblTotais = null;
        }

        painelPrincipal.add(painelDados, BorderLayout.NORTH);

        // === CENTRO: Lançamento de Peças (apenas para OS já aberta/salva) ===
        if (!isNova) {
            JPanel painelPecas = new JPanel(new BorderLayout(8, 8));
            painelPecas.setBorder(BorderFactory.createTitledBorder("Peças Utilizadas (Baixa Automática no Estoque)"));

            JPanel painelAddPeca = new JPanel(new BorderLayout(8, 0));
            cbPeca = new JComboBox<>();
            carregarComboPecas();

            spnQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
            JButton btnAdicionarPeca = new JButton("Adicionar Peça");

            JPanel painelDireitaPeca = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            painelDireitaPeca.add(new JLabel("Qtd:"));
            painelDireitaPeca.add(spnQuantidade);
            painelDireitaPeca.add(btnAdicionarPeca);

            painelAddPeca.add(cbPeca, BorderLayout.CENTER);
            painelAddPeca.add(painelDireitaPeca, BorderLayout.EAST);
            painelPecas.add(painelAddPeca, BorderLayout.NORTH);

            modelItens = new DefaultTableModel(
                new String[]{"Peça", "Qtd", "Valor Unit.", "Subtotal"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            JTable tabelaItens = new JTable(modelItens);
            tabelaItens.setRowHeight(22);
            painelPecas.add(new JScrollPane(tabelaItens), BorderLayout.CENTER);

            painelPrincipal.add(painelPecas, BorderLayout.CENTER);

            // Desabilita lançamento de peças se a OS já foi finalizada ou cancelada
            boolean bloqueada = "CONCLUIDA".equalsIgnoreCase(os.getStatus())
                             || "CANCELADA".equalsIgnoreCase(os.getStatus());
            btnAdicionarPeca.setEnabled(!bloqueada);

            btnAdicionarPeca.addActionListener(e -> adicionarPeca());

            preencherCamposEdicao();
        }

        // === RODAPÉ: Botões ===
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        if (isNova) {
            JButton btnSalvar = new JButton("Abrir OS");
            btnSalvar.addActionListener(e -> salvarNovaOS());
            painelBotoes.add(btnSalvar);
        } else {
            boolean bloqueada = "CONCLUIDA".equalsIgnoreCase(os.getStatus())
                             || "CANCELADA".equalsIgnoreCase(os.getStatus());
            JButton btnConcluir = new JButton("Salvar Diagnóstico e Concluir OS");
            btnConcluir.setEnabled(!bloqueada);
            btnConcluir.addActionListener(e -> concluirOS());
            painelBotoes.add(btnConcluir);
        }

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        painelBotoes.add(btnFechar);

        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);
    }

    private void carregarComboPecas() {
        cbPeca.removeAllItems();
        for (Peca p : facade.listarPecas()) {
            cbPeca.addItem(p);
        }
    }

    private void preencherCamposEdicao() {
        // Seleciona o cliente correspondente no JComboBox
        for (int i = 0; i < cbCliente.getItemCount(); i++) {
            if (cbCliente.getItemAt(i).getId() == os.getIdCliente()) {
                cbCliente.setSelectedIndex(i);
                break;
            }
        }
        cbCliente.setEnabled(false); // Não troca o cliente após abertura da OS
        txtEquipamento.setText(os.getEquipamento());
        txtDefeito.setText(os.getDefeitoRelatado());
        txtValorServico.setText(String.format("%.2f", os.getValorServico()).replace(",", "."));
        txtDiagnostico.setText(os.getDiagnosticoTecnico() != null ? os.getDiagnosticoTecnico() : "");

        atualizarTabelaItensETotais();
    }

    private void atualizarTabelaItensETotais() {
        // Recarrega a OS atualizada do banco para exibir valores consolidados
        this.os = facade.buscarOSPorId(os.getId());
        modelItens.setRowCount(0);

        Map<Integer, String> mapaPecas = new HashMap<>();
        for (Peca p : facade.listarPecas()) {
            mapaPecas.put(p.getId(), p.getNome());
        }

        List<ItemOrdemServico> itens = facade.listarItensDaOS(os.getId());
        for (ItemOrdemServico item : itens) {
            String nomePeca = mapaPecas.getOrDefault(item.getIdPeca(), "Peça #" + item.getIdPeca());
            modelItens.addRow(new Object[]{
                nomePeca,
                item.getQuantidade(),
                String.format("R$ %.2f", item.getValorUnitario()),
                String.format("R$ %.2f", item.getSubtotal())
            });
        }

        lblTotais.setText(String.format(
            "Status: %s | Peças: R$ %.2f | Total: R$ %.2f",
            os.getStatus(), os.getValorPecas(), os.getValorTotal()
        ));
    }

    private void salvarNovaOS() {
        try {
            Cliente cliente = (Cliente) cbCliente.getSelectedItem();
            if (cliente == null) {
                Mensagem.aviso("Cadastre ao menos um cliente antes de abrir uma OS!");
                return;
            }

            String equipamento = txtEquipamento.getText().trim();
            String defeito = txtDefeito.getText().trim();
            double valorServico = Double.parseDouble(txtValorServico.getText().trim().replace(",", "."));

            if (equipamento.isEmpty() || defeito.isEmpty()) {
                Mensagem.aviso("Informe o equipamento e o defeito relatado!");
                return;
            }

            OrdemServico novaOS = new OrdemServico();
            novaOS.setIdCliente(cliente.getId());
            novaOS.setEquipamento(equipamento);
            novaOS.setDefeitoRelatado(defeito);
            novaOS.setValorServico(valorServico);

            if (facade.abrirOrdemServico(novaOS)) {
                Mensagem.info("Ordem de Serviço #" + novaOS.getId() + " aberta com sucesso!");
                dispose();
            } else {
                Mensagem.erro("Erro ao abrir Ordem de Serviço!");
            }
        } catch (NumberFormatException ex) {
            Mensagem.erro("Informe um valor numérico válido para a mão de obra!");
        }
    }

    private void adicionarPeca() {
        Peca pecaSelecionada = (Peca) cbPeca.getSelectedItem();
        if (pecaSelecionada == null) {
            Mensagem.aviso("Nenhuma peça disponível no estoque.");
            return;
        }

        int quantidade = (int) spnQuantidade.getValue();

        // Delega toda a transação (validação + baixa de estoque + vínculo + Observer) à Facade
        String resultado = facade.adicionarPecaNaOrdemServico(os.getId(), pecaSelecionada.getId(), quantidade);

        if ("SUCESSO".equals(resultado)) {
            carregarComboPecas(); // Atualiza o saldo exibido no combo
            atualizarTabelaItensETotais();
        } else {
            Mensagem.erro(resultado);
        }
    }

    private void concluirOS() {
        try {
            String diagnostico = txtDiagnostico.getText().trim();
            double maoDeObra = Double.parseDouble(txtValorServico.getText().trim().replace(",", "."));

            if (diagnostico.isEmpty()) {
                Mensagem.aviso("Preencha o diagnóstico técnico antes de concluir a OS!");
                return;
            }

            if (facade.concluirOrdemServico(os.getId(), diagnostico, maoDeObra)) {
                Mensagem.info("Ordem de Serviço #" + os.getId() + " concluída com sucesso!");
                dispose();
            } else {
                Mensagem.erro("Erro ao concluir Ordem de Serviço!");
            }
        } catch (NumberFormatException ex) {
            Mensagem.erro("Informe um valor numérico válido para a mão de obra!");
        }
    }
}