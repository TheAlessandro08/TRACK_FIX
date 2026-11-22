package view;

import controller.AssistenciaFacade;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import util.Mensagem;
import util.ResultadoCadastro;

// Tela de cadastro rápido acessível pelo botão "Cadastrar" da TelaLogin.
public class TelaCadastroUsuario extends JFrame {

    private final JTextField txtLogin;
    private final JPasswordField txtSenha;
    private final JButton btnSalvar;

    private final AssistenciaFacade facade = AssistenciaFacade.getInstance();

    public TelaCadastroUsuario() {
        setTitle("Cadastrar Usuário");
        setSize(320, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel painel = new JPanel(new GridLayout(3, 2, 10, 10));
        painel.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(painel);

        painel.add(new JLabel("Novo Usuário:"));
        txtLogin = new JTextField();
        painel.add(txtLogin);

        painel.add(new JLabel("Senha:"));
        txtSenha = new JPasswordField();
        painel.add(txtSenha);

        btnSalvar = new JButton("Salvar");
        painel.add(new JLabel());
        painel.add(btnSalvar);

        btnSalvar.addActionListener(e -> {
            String login = txtLogin.getText().trim();
            String senha = new String(txtSenha.getPassword()).trim();

            if (login.isEmpty() || senha.isEmpty()) {
                Mensagem.aviso("Preencha usuário e senha!");
                return;
            }

            ResultadoCadastro resultado = facade.cadastrarUsuario(login, senha);
            switch (resultado) {
                case SUCESSO -> {
                    Mensagem.info("Usuário cadastrado com sucesso!");
                    dispose();
                }
                case JA_EXISTE -> Mensagem.erro("Já existe um usuário com esse nome. Escolha outro.");
                case ERRO_BANCO -> Mensagem.erro("Erro ao salvar no banco de dados.");
            }
        });
    }
}