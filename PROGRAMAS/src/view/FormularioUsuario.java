package view;

import controller.UsuarioController;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import model.Usuario;
import util.Mensagem;
import util.ResultadoCadastro;

// Formulário modal administrativo para criação e edição de usuários/técnicos.
public class FormularioUsuario extends JDialog {

    private final JTextField txtLogin;
    private final JTextField txtEmail;
    private JPasswordField txtSenha;
    private final JComboBox<String> cbTipo;
    private final JComboBox<String> cbAtivo;
    private final JButton btnSalvar;

    private final UsuarioController usuarioController = new UsuarioController();
    private final Usuario usuario;
    private final CRUDUsuario parent;

    public FormularioUsuario(CRUDUsuario parent, Usuario usuario) {
        super(parent, true);
        this.parent = parent;
        this.usuario = usuario;

        boolean isNovo = (usuario == null);
        setTitle(isNovo ? "Novo Usuário / Técnico" : "Editar Usuário");
        setSize(370, isNovo ? 310 : 270);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel painel = new JPanel(new GridLayout(isNovo ? 6 : 5, 2, 10, 10));
        painel.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(painel);

        painel.add(new JLabel("Login:"));
        txtLogin = new JTextField();
        painel.add(txtLogin);

        painel.add(new JLabel("E-mail:"));
        txtEmail = new JTextField();
        painel.add(txtEmail);

        if (isNovo) {
            painel.add(new JLabel("Senha:"));
            txtSenha = new JPasswordField();
            painel.add(txtSenha);
        }

        painel.add(new JLabel("Perfil:"));
        cbTipo = new JComboBox<>(new String[]{"Técnico / Usuário", "Administrador"});
        painel.add(cbTipo);

        painel.add(new JLabel("Situação:"));
        cbAtivo = new JComboBox<>(new String[]{"Ativo", "Inativo"});
        painel.add(cbAtivo);

        btnSalvar = new JButton("Salvar");
        painel.add(new JLabel());
        painel.add(btnSalvar);

        if (!isNovo) {
            txtLogin.setText(usuario.getLogin());
            txtEmail.setText(usuario.getEmail() != null ? usuario.getEmail() : "");
            cbTipo.setSelectedItem("A".equalsIgnoreCase(usuario.getTipo()) ? "Administrador" : "Técnico / Usuário");
            cbAtivo.setSelectedItem("T".equalsIgnoreCase(usuario.getAtivo()) ? "Ativo" : "Inativo");
        }

        btnSalvar.addActionListener(e -> salvarUsuario());
    }

    private void salvarUsuario() {
        String login = txtLogin.getText().trim();
        String email = txtEmail.getText().trim();
        String tipo = "Administrador".equals(cbTipo.getSelectedItem()) ? "A" : "U";
        String ativo = "Ativo".equals(cbAtivo.getSelectedItem()) ? "T" : "F";

        if (login.isEmpty()) {
            Mensagem.aviso("O login é obrigatório!");
            return;
        }

        if (usuario == null) {
            String senha = new String(txtSenha.getPassword()).trim();
            if (senha.isEmpty()) {
                Mensagem.aviso("A senha é obrigatória para novos usuários!");
                return;
            }

            Usuario novo = new Usuario();
            novo.setLogin(login);
            novo.setEmail(email);
            novo.setTipo(tipo);
            novo.setAtivo(ativo);

            ResultadoCadastro resultado = usuarioController.inserirUsuarioCompleto(novo, senha);
            switch (resultado) {
                case SUCESSO -> {
                    Mensagem.info("Usuário cadastrado com sucesso!");
                    parent.atualizarTabela();
                    dispose();
                }
                case JA_EXISTE -> Mensagem.erro("Já existe um usuário com esse login!");
                case ERRO_BANCO -> Mensagem.erro("Erro ao salvar usuário no banco de dados!");
            }
        } else {
            usuario.setLogin(login);
            usuario.setEmail(email);
            usuario.setTipo(tipo);
            usuario.setAtivo(ativo);

            if (usuarioController.atualizar(usuario)) {
                Mensagem.info("Usuário atualizado com sucesso!");
                parent.atualizarTabela();
                dispose();
            } else {
                Mensagem.erro("Erro ao atualizar usuário!");
            }
        }
    }
}