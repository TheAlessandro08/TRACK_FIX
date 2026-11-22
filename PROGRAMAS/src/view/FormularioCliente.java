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
import model.Cliente;
import util.Mensagem;
import util.ResultadoCadastro;

// Formulário modal para cadastro e edição de clientes.
public class FormularioCliente extends JDialog {

    private final JTextField txtNome;
    private final JTextField txtCpfCnpj;
    private final JTextField txtTelefone;
    private final JTextField txtEmail;
    private final JButton btnSalvar;

    private final AssistenciaFacade facade = AssistenciaFacade.getInstance();
    private final Cliente cliente;

    public FormularioCliente(JFrame parent, Cliente cliente) {
        super(parent, true);
        this.cliente = cliente;

        setTitle(cliente == null ? "Novo Cliente" : "Editar Cliente");
        setSize(380, 270);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel painel = new JPanel(new GridLayout(5, 2, 10, 10));
        painel.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(painel);

        painel.add(new JLabel("Nome Completo:"));
        txtNome = new JTextField();
        painel.add(txtNome);

        painel.add(new JLabel("CPF / CNPJ:"));
        txtCpfCnpj = new JTextField();
        painel.add(txtCpfCnpj);

        painel.add(new JLabel("Telefone:"));
        txtTelefone = new JTextField();
        painel.add(txtTelefone);

        painel.add(new JLabel("E-mail:"));
        txtEmail = new JTextField();
        painel.add(txtEmail);

        btnSalvar = new JButton("Salvar");
        painel.add(new JLabel());
        painel.add(btnSalvar);

        if (cliente != null) {
            txtNome.setText(cliente.getNome());
            txtCpfCnpj.setText(cliente.getCpfCnpj());
            txtTelefone.setText(cliente.getTelefone());
            txtEmail.setText(cliente.getEmail());
        }

        btnSalvar.addActionListener(e -> salvarCliente());
    }

    private void salvarCliente() {
        String nome = txtNome.getText().trim();
        String cpfCnpj = txtCpfCnpj.getText().trim();
        String telefone = txtTelefone.getText().trim();
        String email = txtEmail.getText().trim();

        if (nome.isEmpty()) {
            Mensagem.aviso("O nome do cliente é obrigatório!");
            return;
        }

        if (cliente == null) {
            Cliente novo = new Cliente();
            novo.setNome(nome);
            novo.setCpfCnpj(cpfCnpj);
            novo.setTelefone(telefone);
            novo.setEmail(email);

            ResultadoCadastro resultado = facade.cadastrarCliente(novo);
            switch (resultado) {
                case SUCESSO -> {
                    Mensagem.info("Cliente cadastrado com sucesso!");
                    dispose();
                }
                case JA_EXISTE -> Mensagem.erro("Já existe um cliente cadastrado com esse CPF/CNPJ!");
                case ERRO_BANCO -> Mensagem.erro("Erro ao salvar cliente no banco de dados!");
            }
        } else {
            cliente.setNome(nome);
            cliente.setCpfCnpj(cpfCnpj);
            cliente.setTelefone(telefone);
            cliente.setEmail(email);

            if (facade.atualizarCliente(cliente)) {
                Mensagem.info("Cliente atualizado com sucesso!");
                dispose();
            } else {
                Mensagem.erro("Erro ao atualizar cliente!");
            }
        }
    }
}