package view;

import controller.AssistenciaFacade;
import util.ResultadoCadastro;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class TelaLogin extends JFrame {

    private final AssistenciaFacade facade;
    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JButton btnSolicitarAcesso;

    // Paleta de cores do TRACK FIX
    private static final Color COR_BRAND_BG = new Color(30, 41, 59);      // Azul-marinho escuro (#1E293B)
    private static final Color COR_DESTAQUE = new Color(56, 189, 248);    // Azul ciano para o "FIX" (#38BDF8)
    private static final Color COR_PRIMARIA = new Color(37, 99, 235);     // Azul botão principal (#2563EB)
    private static final Color COR_FUNDO_FORM = Color.WHITE;
    private static final Color COR_TEXTO = new Color(30, 41, 59);
    private static final Color COR_SUBTEXTO = new Color(100, 116, 139);
    private static final Color COR_BORDA = new Color(203, 213, 225);

    public TelaLogin() {
        this.facade = AssistenciaFacade.getInstance();
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setTitle("TRACK FIX - Acesso ao Sistema");
        setSize(740, 430);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // ================= PAINEL ESQUERDO (IDENTIDADE TRACK FIX) =================
        JPanel painelMarca = new JPanel();
        painelMarca.setBackground(COR_BRAND_BG);
        painelMarca.setPreferredSize(new Dimension(300, 430));
        painelMarca.setLayout(new GridBagLayout());
        painelMarca.setBorder(new EmptyBorder(30, 25, 30, 25));

        GridBagConstraints gbcMarca = new GridBagConstraints();
        gbcMarca.gridx = 0;
        gbcMarca.fill = GridBagConstraints.HORIZONTAL;
        gbcMarca.anchor = GridBagConstraints.CENTER;

        // Logo tipográfico "TRACK FIX"
        JLabel lblLogo = new JLabel(
            "<html><div style='text-align: center;'>" +
            "<span style='color: #FFFFFF; font-size: 28px; font-weight: bold; letter-spacing: 2px;'>TRACK </span>" +
            "<span style='color: #38BDF8; font-size: 28px; font-weight: bold; letter-spacing: 2px;'>FIX</span>" +
            "</div></html>",
            SwingConstants.CENTER
        );
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 32));

        // Linha divisória sutil
        JSeparator separador = new JSeparator();
        separador.setForeground(COR_DESTAQUE);
        separador.setBackground(COR_DESTAQUE);

        JLabel lblSubtitulo = new JLabel(
            "<html><div style='text-align: center; color: #CBD5E1; font-size: 11px;'>" +
            "Sistema de Gestão de Assistência Técnica<br>e Controle de Ordens de Serviço" +
            "</div></html>",
            SwingConstants.CENTER
        );
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        gbcMarca.gridy = 0;
        gbcMarca.insets = new Insets(0, 0, 12, 0);
        painelMarca.add(lblLogo, gbcMarca);

        gbcMarca.gridy = 1;
        gbcMarca.insets = new Insets(0, 40, 16, 40);
        painelMarca.add(separador, gbcMarca);

        gbcMarca.gridy = 2;
        gbcMarca.insets = new Insets(0, 0, 0, 0);
        painelMarca.add(lblSubtitulo, gbcMarca);

        // ================= PAINEL DIREITO (FORMULÁRIO DE LOGIN) =================
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBackground(COR_FUNDO_FORM);
        painelForm.setBorder(new EmptyBorder(35, 45, 35, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblBemVindo = new JLabel("Bem-vindo de volta");
        lblBemVindo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBemVindo.setForeground(COR_TEXTO);

        JLabel lblInstrucao = new JLabel("Insira suas credenciais para acessar o painel");
        lblInstrucao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInstrucao.setForeground(COR_SUBTEXTO);

        JLabel lblUsuario = new JLabel("Usuário");
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUsuario.setForeground(COR_TEXTO);

        txtUsuario = new JTextField();
        estilizarCampoTexto(txtUsuario);

        JLabel lblSenha = new JLabel("Senha");
        lblSenha.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSenha.setForeground(COR_TEXTO);

        txtSenha = new JPasswordField();
        estilizarCampoTexto(txtSenha);

        // Painel com os dois botões lado a lado
        JPanel painelBotoes = new JPanel(new GridLayout(1, 2, 12, 0));
        painelBotoes.setBackground(COR_FUNDO_FORM);

        btnEntrar = new JButton("Entrar");
        estilizarBotaoPrimario(btnEntrar);

        btnSolicitarAcesso = new JButton("Solicitar Acesso");
        estilizarBotaoSecundario(btnSolicitarAcesso);

        painelBotoes.add(btnEntrar);
        painelBotoes.add(btnSolicitarAcesso);

        // Montagem vertical do formulário
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 4, 0);
        painelForm.add(lblBemVindo, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 24, 0);
        painelForm.add(lblInstrucao, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 6, 0);
        painelForm.add(lblUsuario, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 16, 0);
        painelForm.add(txtUsuario, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 6, 0);
        painelForm.add(lblSenha, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 26, 0);
        painelForm.add(txtSenha, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 0, 0);
        painelForm.add(painelBotoes, gbc);

        add(painelMarca, BorderLayout.WEST);
        add(painelForm, BorderLayout.CENTER);

        // Permite pressionar ENTER para fazer login
        getRootPane().setDefaultButton(btnEntrar);

        configurarEventos();
    }

    private void estilizarCampoTexto(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setForeground(COR_TEXTO);
        campo.setPreferredSize(new Dimension(280, 38));
        campo.setBorder(new CompoundBorder(
            new LineBorder(COR_BORDA, 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
    }

    private void estilizarBotaoPrimario(JButton botao) {
        botao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botao.setBackground(COR_PRIMARIA);
        botao.setForeground(Color.WHITE);
        botao.setPreferredSize(new Dimension(130, 40));
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(true);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void estilizarBotaoSecundario(JButton botao) {
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setBackground(new Color(241, 245, 249));
        botao.setForeground(COR_TEXTO);
        botao.setPreferredSize(new Dimension(145, 40));
        botao.setFocusPainted(false);
        botao.setBorder(new LineBorder(COR_BORDA, 1, true));
        botao.setOpaque(true);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void configurarEventos() {
        btnEntrar.addActionListener(e -> realizarLogin());
        btnSolicitarAcesso.addActionListener(e -> solicitarAcesso());
    }

    private void realizarLogin() {
        String login = txtUsuario.getText().trim();
        String senha = new String(txtSenha.getPassword());

        if (login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha usuário e senha.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (facade.autenticar(login, senha)) {
            dispose();
            new TelaMenu(facade.getUsuarioLogado()).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Usuário ou senha inválidos, ou cadastro ainda aguardando aprovação do administrador.",
                "Acesso Negado",
                JOptionPane.ERROR_MESSAGE
            );
            txtSenha.setText("");
            txtSenha.requestFocus();
        }
    }

    private void solicitarAcesso() {
        String login = txtUsuario.getText().trim();
        String senha = new String(txtSenha.getPassword());

        if (login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Preencha o usuário e a senha desejados nos campos acima antes de solicitar acesso.",
                "Solicitar Acesso",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        ResultadoCadastro resultado = facade.solicitarAcesso(login, senha);
        if (resultado == ResultadoCadastro.SUCESSO) {
            JOptionPane.showMessageDialog(
                this,
                "Solicitação enviada com sucesso!\nAguarde um administrador ativar o seu usuário para entrar no sistema.",
                "Aguardando Aprovação",
                JOptionPane.INFORMATION_MESSAGE
            );
            txtUsuario.setText("");
            txtSenha.setText("");
        } else if (resultado == ResultadoCadastro.JA_EXISTE) {
            JOptionPane.showMessageDialog(
                this,
                "Este nome de usuário já está cadastrado ou possui uma solicitação pendente.",
                "Usuário Indisponível",
                JOptionPane.WARNING_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Erro ao registrar solicitação no banco de dados.",
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}