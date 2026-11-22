package view;

import controller.AssistenciaFacade;
import controller.SistemaObserver;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
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

    public TelaMenu(Usuario usuario) {
        this.usuario = usuario != null ? usuario : facade.getUsuarioLogado();

        // Registra esta janela na lista de observadores da Facade
        facade.adicionarObserver(this);

        String nomeExibicao = this.usuario != null ? this.usuario.getLogin() : "Operador";
        setTitle("AssistOS - Menu Principal (" + nomeExibicao + ")");
        setSize(520, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel painelPrincipal = new JPanel(new BorderLayout(12, 12));
        painelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(painelPrincipal);

        // === BARRA DE MENU ===
        JMenuBar menuBar = new JMenuBar();

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
        boolean isAdmin = this.usuario != null && "A".equalsIgnoreCase(this.usuario.getTipo());
        itemUsuarios.setEnabled(isAdmin);

        JMenu menuSair = new JMenu("Sair");
        JMenuItem itemLogout = new JMenuItem("Logout");
        menuSair.add(itemLogout);

        menuBar.add(menuOperacoes);
        menuBar.add(menuCadastro);
        menuBar.add(menuSair);
        setJMenuBar(menuBar);

        // === TOPO: Saudação ===
        JLabel lblSaudacao = new JLabel("Bem-vindo, " + nomeExibicao + "!", SwingConstants.CENTER);
        lblSaudacao.setFont(new Font("Arial", Font.BOLD, 18));
        painelPrincipal.add(lblSaudacao, BorderLayout.NORTH);

        // === CENTRO: Atalhos rápidos e indicadores ao vivo ===
        JPanel painelCentro = new JPanel(new GridLayout(3, 1, 10, 10));

        JPanel painelAtalhos = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton btnAtalhoOS = new JButton("Ordens de Serviço");
        JButton btnAtalhoPecas = new JButton("Estoque de Peças");
        JButton btnAtalhoClientes = new JButton("Clientes");
        painelAtalhos.add(btnAtalhoOS);
        painelAtalhos.add(btnAtalhoPecas);
        painelAtalhos.add(btnAtalhoClientes);

        lblResumoOS = new JLabel("", SwingConstants.CENTER);
        lblResumoOS.setFont(new Font("Arial", Font.PLAIN, 14));

        lblResumoEstoque = new JLabel("", SwingConstants.CENTER);
        lblResumoEstoque.setFont(new Font("Arial", Font.BOLD, 13));

        painelCentro.add(painelAtalhos);
        painelCentro.add(lblResumoOS);
        painelCentro.add(lblResumoEstoque);
        painelPrincipal.add(painelCentro, BorderLayout.CENTER);

        // === EVENTOS DE NAVEGAÇÃO ===
        itemOS.addActionListener(e -> new CRUDOrdemServico());
        btnAtalhoOS.addActionListener(e -> new CRUDOrdemServico());

        itemPecas.addActionListener(e -> new CRUDPeca());
        btnAtalhoPecas.addActionListener(e -> new CRUDPeca());

        itemClientes.addActionListener(e -> new CRUDCliente());
        btnAtalhoClientes.addActionListener(e -> new CRUDCliente());

        itemUsuarios.addActionListener(e -> new CRUDUsuario());

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

    // Recalcula contadores de OSs ativas e peças em estoque crítico
    private void atualizarIndicadores() {
        List<OrdemServico> listaOS = facade.listarOrdensServico();
        long osAbertas = listaOS.stream()
                .filter(os -> !"CONCLUIDA".equalsIgnoreCase(os.getStatus())
                           && !"ENTREGUE".equalsIgnoreCase(os.getStatus())
                           && !"CANCELADA".equalsIgnoreCase(os.getStatus()))
                .count();
        lblResumoOS.setText("Ordens de Serviço em andamento: " + osAbertas + " (Total: " + listaOS.size() + ")");

        List<Peca> pecas = facade.listarPecas();
        long estoqueBaixo = pecas.stream()
                .filter(p -> p.getQuantidadeEstoque() <= p.getEstoqueMinimo())
                .count();
        lblResumoEstoque.setText(estoqueBaixo > 0
                ? "Alerta: " + estoqueBaixo + " peça(s) com estoque mínimo ou zerado!"
                : "Estoque de peças regular (" + pecas.size() + " itens cadastrados).");
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