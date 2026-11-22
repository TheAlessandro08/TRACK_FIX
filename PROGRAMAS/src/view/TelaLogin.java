package view;

import controller.AssistenciaFacade;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JPanel;
import util.Mensagem;

// Tela de entrada do sistema: delega autenticação e sessão para o Singleton AssistenciaFacade.
public class TelaLogin extends JFrame {

    private static final long serialVersionUID = 1L;

    private final JTextField txtLogin;
    private final JPasswordField txtSenha;
    private final JButton btnEntrar;
    private final JButton btnCadastrar;

    // Obtém a instância única da Fachada (Padrão Singleton)
    private final AssistenciaFacade facade = AssistenciaFacade.getInstance();

    public TelaLogin() {
        setTitle("Login - Assistência Técnica");
        setSize(320, 210);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painel = new JPanel(new GridLayout(3, 2, 10, 10));
        painel.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(painel);

        painel.add(new JLabel("Usuário:"));
        txtLogin = new JTextField();
        painel.add(txtLogin);

        painel.add(new JLabel("Senha:"));
        txtSenha = new JPasswordField();
        painel.add(txtSenha);

        btnEntrar = new JButton("Entrar");
        btnCadastrar = new JButton("Cadastrar");
        painel.add(btnEntrar);
        painel.add(btnCadastrar);

        // Permite pressionar Enter para acionar o login
        getRootPane().setDefaultButton(btnEntrar);

        // === AÇÃO ENTRAR ===
        btnEntrar.addActionListener(e -> {
            String login = txtLogin.getText().trim();
            String senha = new String(txtSenha.getPassword());

            if (facade.autenticar(login, senha)) {
                Mensagem.info("Login realizado com sucesso!");
                dispose();
                // Abre o menu principal usando o usuário mantido na sessão da Facade
                new TelaMenu(facade.getUsuarioLogado()).setVisible(true);
            } else {
                Mensagem.erro("Usuário ou senha inválidos (ou conta inativa)!");
            }
        });

        // === AÇÃO CADASTRAR ===
        btnCadastrar.addActionListener(e -> new TelaCadastroUsuario().setVisible(true));
    }
}