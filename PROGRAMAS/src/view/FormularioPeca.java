package view;

import controller.AssistenciaFacade;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import model.Peca;
import util.Mensagem;
import util.ResultadoCadastro;

// Formulário modal para cadastro de novas peças ou reposição/edição de itens existentes.
public class FormularioPeca extends JDialog {

    private final JTextField txtNome;
    private final JTextField txtMarca;
    private final JTextField txtPreco;
    private final JTextField txtQuantidade;
    private final JTextField txtEstoqueMinimo;
    private final JButton btnSalvar;

    private final AssistenciaFacade facade = AssistenciaFacade.getInstance();
    private final Peca peca;

    public FormularioPeca(JFrame parent, Peca peca) {
        super(parent, true);
        this.peca = peca;

        setTitle(peca == null ? "Nova Peça" : "Editar Peça");
        setSize(380, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel painel = new JPanel(new GridLayout(6, 2, 10, 10));
        painel.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(painel);

        painel.add(new JLabel("Nome da Peça:"));
        txtNome = new JTextField();
        painel.add(txtNome);

        painel.add(new JLabel("Marca:"));
        txtMarca = new JTextField();
        painel.add(txtMarca);

        painel.add(new JLabel("Preço Unitário (R$):"));
        txtPreco = new JTextField();
        painel.add(txtPreco);

        painel.add(new JLabel("Qtd. em Estoque:"));
        txtQuantidade = new JTextField("0");
        painel.add(txtQuantidade);

        painel.add(new JLabel("Estoque Mínimo:"));
        txtEstoqueMinimo = new JTextField("2");
        painel.add(txtEstoqueMinimo);

        btnSalvar = new JButton("Salvar");
        painel.add(new JLabel());
        painel.add(btnSalvar);

        if (peca != null) {
            txtNome.setText(peca.getNome());
            txtMarca.setText(peca.getMarca());
            txtPreco.setText(String.valueOf(peca.getPreco()));
            txtQuantidade.setText(String.valueOf(peca.getQuantidadeEstoque()));
            txtEstoqueMinimo.setText(String.valueOf(peca.getEstoqueMinimo()));
        }

        btnSalvar.addActionListener(e -> salvarPeca());
    }

    private void salvarPeca() {
        try {
            String nome = txtNome.getText().trim();
            String marca = txtMarca.getText().trim();
            double preco = Double.parseDouble(txtPreco.getText().trim().replace(",", "."));
            int quantidade = Integer.parseInt(txtQuantidade.getText().trim());
            int estoqueMinimo = Integer.parseInt(txtEstoqueMinimo.getText().trim());

            if (nome.isEmpty()) {
                Mensagem.aviso("O nome da peça é obrigatório!");
                return;
            }

            if (preco < 0 || quantidade < 0 || estoqueMinimo < 0) {
                Mensagem.aviso("Preço e quantidades não podem ser negativos!");
                return;
            }

            if (peca == null) {
                ResultadoCadastro resultado = facade.cadastrarPeca(nome, marca, preco, quantidade, estoqueMinimo);
                switch (resultado) {
                    case SUCESSO -> {
                        Mensagem.info("Peça cadastrada com sucesso!");
                        dispose();
                    }
                    case JA_EXISTE -> Mensagem.erro("Já existe uma peça cadastrada com esse nome!");
                    case ERRO_BANCO -> Mensagem.erro("Erro ao salvar peça no banco de dados!");
                }
            } else {
                peca.setNome(nome);
                peca.setMarca(marca);
                peca.setPreco(preco);
                peca.setQuantidadeEstoque(quantidade);
                peca.setEstoqueMinimo(estoqueMinimo);

                if (facade.atualizarPeca(peca)) {
                    Mensagem.info("Peça atualizada com sucesso!");
                    dispose();
                } else {
                    Mensagem.erro("Erro ao atualizar peça!");
                }
            }
        } catch (NumberFormatException ex) {
            Mensagem.erro("Verifique os valores numéricos de Preço, Estoque e Estoque Mínimo!");
        }
    }
}