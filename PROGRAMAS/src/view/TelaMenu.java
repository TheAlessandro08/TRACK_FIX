package view;

import controller.AssistenciaFacade;
import controller.SistemaObserver;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import model.OrdemServico;
import model.Peca;
import model.Usuario;
import util.Mensagem;

// Menu principal: implementa SistemaObserver para refletir indicadores em tempo real.
public class TelaMenu extends JFrame implements SistemaObserver {

    private final Usuario usuario;
    private final AssistenciaFacade facade = AssistenciaFacade.getInstance();
    private final JLabel lblResumoOS;
    private final JLabel lblResumoEstoque;
    private final JPanel cardEstoque;

    // Paleta de cores TRACK FIX (alinhada com a TelaLogin)
    private static final Color COR_HEADER_BG = new Color(30, 41, 59);     // #1E293B
    private static final Color COR_PRIMARIA = new Color(37, 99, 235);     // #2563EB
    private static final Color COR_FUNDO = new Color(248, 250, 252);      // #F8FAFC
    private static final Color COR_CARD = Color.WHITE;
    private static final Color COR_TEXTO = new Color(30, 41, 59);
    private static final Color COR_SUBTEXTO = new Color(100, 116, 139);
    private static final Color COR_BORDA = new Color(203, 213, 225);

    // Cores dinâmicas para o indicador de estoque
    private static final Color COR_OK_BG = new Color(240, 253, 244);
    private static final Color COR_OK_BORDA = new Color(134, 239, 172);
    private static final Color COR_OK_TEXTO = new Color(21, 128, 61);

    private static final Color COR_ALERTA_BG = new Color(254, 242, 242);
    private static final Color COR_ALERTA_BORDA = new Color(252, 165, 165);
    private static final Color COR_ALERTA_TEXTO = new Color(185, 28, 28);

    public TelaMenu(Usuario usuario) {
        this.usuario = usuario != null ? usuario : facade.getUsuarioLogado();

        // Registra esta janela na lista de observadores da Facade
        facade.adicionarObserver(this);

        String nomeExibicao = this.usuario != null ? this.usuario.getLogin() : "Operador";
        boolean isAdmin = this.usuario != null && "A".equalsIgnoreCase(this.usuario.getTipo());
        String perfilTexto = isAdmin ? "Administrador" : "Técnico / Operador";

        setTitle("TRACK FIX - Menu Principal (" + nomeExibicao + ")");
        setSize(760, 470);
        setMinimumSize(new Dimension(680, 430));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel painelRaiz = new JPanel(new BorderLayout());
        painelRaiz.setBackground(COR_FUNDO);
        setContentPane(painelRaiz);

        // === BARRA DE MENU SUPERIOR ===
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COR_BORDA));

        JMenu menuOperacoes = new JMenu("Operações");
        JMenuItem itemOS = new JMenuItem("Ordens de Serviço");
        menuOperacoes.add(itemOS);

        JMenu menuCadastro = new JMenu("Cadastros");
        JMenuItem itemClientes = new JMenuItem("Clientes");
        JMenuItem itemPecas = new JMenuItem("Peças / Estoque");
        JMenuItem itemUsuarios = new JMenuItem("Usuários / Técnicos");
        menuCadastro.add(itemClientes);
        menuCadastro.add(itemPecas);
        menuCadastro.add(itemUsuarios);

        // Restringe cadastro de usuários apenas para administradores ("A")
        itemUsuarios.setEnabled(isAdmin);

        JMenu menuSair = new JMenu("Sair");
        JMenuItem itemLogout = new JMenuItem("Logout");
        menuSair.add(itemLogout);

        menuBar.add(menuOperacoes);
        menuBar.add(menuCadastro);
        menuBar.add(menuSair);
        setJMenuBar(menuBar);

        // === CABEÇALHO TRACK FIX ===
        JPanel painelHeader = new JPanel(new BorderLayout(15, 0));
        painelHeader.setBackground(COR_HEADER_BG);
        painelHeader.setBorder(new EmptyBorder(18, 28, 18, 28));

        JPanel painelMarca = new JPanel(new GridLayout(2, 1, 0, 2));
        painelMarca.setOpaque(false);

        JLabel lblLogo = new JLabel(
            "<html><span style='color:#FFFFFF; font-size:20px; font-weight:bold; letter-spacing:1px;'>TRACK </span>" +
            "<span style='color:#38BDF8; font-size:20px; font-weight:bold; letter-spacing:1px;'>FIX</span></html>"
        );
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel lblSubtitulo = new JLabel("Painel de Controle da Assistência Técnica");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(203, 213, 225));

        painelMarca.add(lblLogo);
        painelMarca.add(lblSubtitulo);

        JPanel painelUsuario = new JPanel(new GridLayout(2, 1, 0, 2));
        painelUsuario.setOpaque(false);

        JLabel lblSaudacao = new JLabel("Bem-vindo, " + nomeExibicao, SwingConstants.RIGHT);
        lblSaudacao.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSaudacao.setForeground(Color.WHITE);

        JLabel lblPerfil = new JLabel("Perfil: " + perfilTexto, SwingConstants.RIGHT);
        lblPerfil.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPerfil.setForeground(new Color(56, 189, 248));

        painelUsuario.add(lblSaudacao);
        painelUsuario.add(lblPerfil);

        painelHeader.add(painelMarca, BorderLayout.WEST);
        painelHeader.add(painelUsuario, BorderLayout.EAST);
        painelRaiz.add(painelHeader, BorderLayout.NORTH);

        // === ÁREA CENTRAL: ATALHOS RÁPIDOS E INDICADORES AO VIVO ===
        JPanel painelConteudo = new JPanel(new GridBagLayout());
        painelConteudo.setBackground(COR_FUNDO);
        painelConteudo.setBorder(new EmptyBorder(24, 28, 28, 28));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Título da seção de atalhos
        JLabel lblSecaoAtalhos = new JLabel("Acesso Rápido");
        lblSecaoAtalhos.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSecaoAtalhos.setForeground(COR_TEXTO);

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 12, 0);
        painelConteudo.add(lblSecaoAtalhos, gbc);

        // Grade de botões de atalho
        JPanel painelAtalhos = new JPanel(new GridLayout(1, 4, 12, 0));
        painelAtalhos.setBackground(COR_FUNDO);

        JButton btnAtalhoOS = new JButton("Ordens de Serviço");
        estilizarBotaoPrimario(btnAtalhoOS);

        JButton btnAtalhoPecas = new JButton("Estoque de Peças");
        estilizarBotaoCard(btnAtalhoPecas);

        JButton btnAtalhoClientes = new JButton("Clientes");
        estilizarBotaoCard(btnAtalhoClientes);

        JButton btnAtalhoUsuarios = new JButton("Usuários");
        estilizarBotaoCard(btnAtalhoUsuarios);
        btnAtalhoUsuarios.setEnabled(isAdmin);

        painelAtalhos.add(btnAtalhoOS);
        painelAtalhos.add(btnAtalhoPecas);
        painelAtalhos.add(btnAtalhoClientes);
        painelAtalhos.add(btnAtalhoUsuarios);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 26, 0);
        painelConteudo.add(painelAtalhos, gbc);

        // Título da seção de indicadores
        JLabel lblSecaoIndicadores = new JLabel("Indicadores em Tempo Real");
        lblSecaoIndicadores.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSecaoIndicadores.setForeground(COR_TEXTO);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 12, 0);
        painelConteudo.add(lblSecaoIndicadores, gbc);

        // Painel de cartões de resumo (Observer)
        JPanel painelIndicadores = new JPanel(new GridLayout(2, 1, 0, 12));
        painelIndicadores.setBackground(COR_FUNDO);

        JPanel cardOS = new JPanel(new BorderLayout());
        cardOS.setBackground(COR_CARD);
        cardOS.setBorder(new CompoundBorder(
            new LineBorder(COR_BORDA, 1, true),
            new EmptyBorder(14, 18, 14, 18)
        ));

        lblResumoOS = new JLabel("", SwingConstants.LEFT);
        lblResumoOS.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblResumoOS.setForeground(COR_TEXTO);
        cardOS.add(lblResumoOS, BorderLayout.CENTER);

        cardEstoque = new JPanel(new BorderLayout());
        cardEstoque.setBackground(COR_OK_BG);
        cardEstoque.setBorder(new CompoundBorder(
            new LineBorder(COR_OK_BORDA, 1, true),
            new EmptyBorder(14, 18, 14, 18)
        ));

        lblResumoEstoque = new JLabel("", SwingConstants.LEFT);
        lblResumoEstoque.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblResumoEstoque.setForeground(COR_OK_TEXTO);
        cardEstoque.add(lblResumoEstoque, BorderLayout.CENTER);

        painelIndicadores.add(cardOS);
        painelIndicadores.add(cardEstoque);

        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        painelConteudo.add(painelIndicadores, gbc);

        painelRaiz.add(painelConteudo, BorderLayout.CENTER);

        // === EVENTOS DE NAVEGAÇÃO ===
        itemOS.addActionListener(e -> new CRUDOrdemServico());
        btnAtalhoOS.addActionListener(e -> new CRUDOrdemServico());

        itemPecas.addActionListener(e -> new CRUDPeca());
        btnAtalhoPecas.addActionListener(e -> new CRUDPeca());

        itemClientes.addActionListener(e -> new CRUDCliente());
        btnAtalhoClientes.addActionListener(e -> new CRUDCliente());

        itemUsuarios.addActionListener(e -> new CRUDUsuario());
        btnAtalhoUsuarios.addActionListener(e -> new CRUDUsuario());

        itemLogout.addActionListener(e -> {
            facade.logout(); // Limpa sessão e observadores
            dispose();
            new TelaLogin().setVisible(true);
        });

        // Remove observer se a janela for fechada
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                facade.removerObserver(TelaMenu.this);
            }
        });

        atualizarIndicadores();
    }

    private void estilizarBotaoPrimario(JButton botao) {
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setBackground(COR_PRIMARIA);
        botao.setForeground(Color.WHITE);
        botao.setPreferredSize(new Dimension(150, 52));
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(true);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void estilizarBotaoCard(JButton botao) {
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setBackground(COR_CARD);
        botao.setForeground(COR_TEXTO);
        botao.setPreferredSize(new Dimension(150, 52));
        botao.setFocusPainted(false);
        botao.setBorder(new LineBorder(COR_BORDA, 1, true));
        botao.setOpaque(true);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Recalcula contadores de OSs ativas e peças em estoque crítico
    private void atualizarIndicadores() {
        List<OrdemServico> listaOS = facade.listarOrdensServico();
        long osAbertas = listaOS.stream()
                .filter(os -> !"CONCLUIDA".equalsIgnoreCase(os.getStatus())
                           && !"ENTREGUE".equalsIgnoreCase(os.getStatus())
                           && !"CANCELADA".equalsIgnoreCase(os.getStatus()))
                .count();
        lblResumoOS.setText("Ordens de Serviço em andamento: " + osAbertas + "  (Total registradas: " + listaOS.size() + ")");

        List<Peca> pecas = facade.listarPecas();
        long estoqueBaixo = pecas.stream()
                .filter(p -> p.getQuantidadeEstoque() <= p.getEstoqueMinimo())
                .count();

        if (estoqueBaixo > 0) {
            cardEstoque.setBackground(COR_ALERTA_BG);
            cardEstoque.setBorder(new CompoundBorder(
                new LineBorder(COR_ALERTA_BORDA, 1, true),
                new EmptyBorder(14, 18, 14, 18)
            ));
            lblResumoEstoque.setForeground(COR_ALERTA_TEXTO);
            lblResumoEstoque.setText("Alerta de Estoque: " + estoqueBaixo + " peça(s) no nível mínimo ou zerado!");
        } else {
            cardEstoque.setBackground(COR_OK_BG);
            cardEstoque.setBorder(new CompoundBorder(
                new LineBorder(COR_OK_BORDA, 1, true),
                new EmptyBorder(14, 18, 14, 18)
            ));
            lblResumoEstoque.setForeground(COR_OK_TEXTO);
            lblResumoEstoque.setText("Estoque de peças regular  (" + pecas.size() + " itens cadastrados)");
        }
    }

    // === MÉTODOS DO PADRÃO OBSERVER ===

    @Override
    public void onOrdemServicoAtualizada() {
        atualizarIndicadores();
    }

    @Override
    public void onEstoqueAtualizado(String alertaEstoque) {
        atualizarIndicadores();
        if (alertaEstoque != null && !alertaEstoque.isEmpty()) {
            Mensagem.aviso(alertaEstoque);
        }
    }

    @Override
    public void onClienteAtualizado() {
        // Nenhuma métrica de cliente exibida no painel inicial
    }
}