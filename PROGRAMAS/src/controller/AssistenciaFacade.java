package controller;

import java.util.ArrayList;
import java.util.List;
import model.Cliente;
import model.ItemOrdemServico;
import model.OrdemServico;
import model.Peca;
import model.Usuario;
import util.ResultadoCadastro;

// Classe central que combina três padrões de projeto:
// 1. Singleton: mantém uma única instância ativa com o usuário logado e observadores.
// 2. Facade: oferece uma interface simplificada para a View, coordenando os 4 controllers.
// 3. Observer (Subject): notifica as telas abertas sempre que dados são alterados.
public class AssistenciaFacade {

    // Instância única do Singleton
    private static AssistenciaFacade instance;

    // Subsistemas encapsulados pela Facade
    private final UsuarioController usuarioController;
    private final ClienteController clienteController;
    private final PecaController pecaController;
    private final OrdemServicoController osController;

    // Lista de telas inscritas no padrão Observer
    private final List<SistemaObserver> observadores;

    // Estado de sessão compartilhado entre as telas
    private Usuario usuarioLogado;

    // Construtor privado impede instanciação externa direta (new AssistenciaFacade())
    private AssistenciaFacade() {
        this.usuarioController = new UsuarioController();
        this.clienteController = new ClienteController();
        this.pecaController = new PecaController();
        this.osController = new OrdemServicoController();
        this.observadores = new ArrayList<>();
    }

    // Ponto de acesso global da instância única (thread-safe via synchronized)
    public static synchronized AssistenciaFacade getInstance() {
        if (instance == null) {
            instance = new AssistenciaFacade();
        }
        return instance;
    }

    // === MÉTODOS DO PADRÃO OBSERVER ===

    public void adicionarObserver(SistemaObserver observer) {
        if (observer != null && !observadores.contains(observer)) {
            observadores.add(observer);
        }
    }

    public void removerObserver(SistemaObserver observer) {
        observadores.remove(observer);
    }

    // Itera sobre uma cópia da lista para evitar ConcurrentModificationException caso uma tela feche
    private void notificarOSAtualizada() {
        for (SistemaObserver obs : new ArrayList<>(observadores)) {
            obs.onOrdemServicoAtualizada();
        }
    }

    private void notificarEstoqueAtualizado(String alerta) {
        for (SistemaObserver obs : new ArrayList<>(observadores)) {
            obs.onEstoqueAtualizado(alerta);
        }
    }

    private void notificarClienteAtualizado() {
        for (SistemaObserver obs : new ArrayList<>(observadores)) {
            obs.onClienteAtualizado();
        }
    }

    // === AUTENTICAÇÃO E USUÁRIOS ===

    public boolean autenticar(String login, String senha) {
        Usuario u = usuarioController.buscarPorLoginESenha(login, senha);
        if (u != null) {
            this.usuarioLogado = u;
            return true;
        }
        return false;
    }

    // Encerra a sessão e limpa os observadores registrados das telas fechadas
    public void logout() {
        this.usuarioLogado = null;
        this.observadores.clear();
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public ResultadoCadastro solicitarAcesso(String login, String senha) {
        return usuarioController.solicitarAcesso(login, senha);
    }

    public ResultadoCadastro cadastrarUsuario(String login, String senha) {
        return usuarioController.cadastrar(login, senha);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioController.listarTodos();
    }

    // === CLIENTES ===

    public ResultadoCadastro cadastrarCliente(Cliente cliente) {
        ResultadoCadastro res = clienteController.cadastrar(cliente);
        if (res == ResultadoCadastro.SUCESSO) {
            notificarClienteAtualizado();
        }
        return res;
    }

    public boolean atualizarCliente(Cliente cliente) {
        boolean ok = clienteController.atualizar(cliente);
        if (ok) {
            notificarClienteAtualizado();
        }
        return ok;
    }

    public boolean excluirCliente(int idCliente) {
        boolean ok = clienteController.excluir(idCliente);
        if (ok) {
            notificarClienteAtualizado();
        }
        return ok;
    }

    public Cliente buscarClientePorId(int id) {
        return clienteController.buscarPorId(id);
    }

    public List<Cliente> listarClientes() {
        return clienteController.listarTodos();
    }

    // === PEÇAS / ALMOXARIFADO ===

    public ResultadoCadastro cadastrarPeca(String nome, String marca, double preco, int qtd, int estoqueMin) {
        ResultadoCadastro res = pecaController.cadastrar(nome, marca, preco, qtd, estoqueMin);
        if (res == ResultadoCadastro.SUCESSO) {
            notificarEstoqueAtualizado(null);
        }
        return res;
    }

    // Ao editar uma peça, verifica se o saldo caiu abaixo do mínimo e envia o alerta aos Observers
    public boolean atualizarPeca(Peca peca) {
        boolean ok = pecaController.atualizar(peca);
        if (ok) {
            String alerta = pecaController.verificarAlertaEstoqueMinimo(peca.getId());
            notificarEstoqueAtualizado(alerta);
        }
        return ok;
    }

    public boolean excluirPeca(int idPeca) {
        boolean ok = pecaController.excluir(idPeca);
        if (ok) {
            notificarEstoqueAtualizado(null);
        }
        return ok;
    }

    public Peca buscarPecaPorId(int id) {
        return pecaController.buscarPorId(id);
    }

    public List<Peca> listarPecas() {
        return pecaController.listarTodos();
    }

    // === ORDENS DE SERVIÇO (OPERAÇÕES INTEGRADAS DA FACADE) ===

    // Associa automaticamente o técnico logado caso nenhum técnico tenha sido informado
    public boolean abrirOrdemServico(OrdemServico os) {
        if (usuarioLogado != null && os.getIdTecnico() <= 0) {
            os.setIdTecnico(usuarioLogado.getId());
        }
        boolean ok = osController.abrirOS(os);
        if (ok) {
            notificarOSAtualizada();
        }
        return ok;
    }

    // Orquestra a adição de peça em 4 etapas:
    // 1. Checa saldo; 2. Baixa estoque; 3. Vincula na OS (com estorno se falhar); 4. Notifica telas.
    public String adicionarPecaNaOrdemServico(int idOs, int idPeca, int quantidade) {
        if (!pecaController.temEstoqueSuficiente(idPeca, quantidade)) {
            return "Estoque insuficiente para a peça selecionada.";
        }

        Peca peca = pecaController.buscarPorId(idPeca);
        if (!pecaController.baixarEstoque(idPeca, quantidade)) {
            return "Não foi possível realizar a baixa no estoque.";
        }

        if (!osController.registrarItemPeca(idOs, peca, quantidade)) {
            pecaController.reporEstoque(idPeca, quantidade); // Desfaz a baixa em caso de erro
            return "Erro ao vincular peça na Ordem de Serviço.";
        }

        String alertaEstoque = pecaController.verificarAlertaEstoqueMinimo(idPeca);
        notificarEstoqueAtualizado(alertaEstoque);
        notificarOSAtualizada();

        return "SUCESSO";
    }

    // Registra o laudo técnico, aplica o valor de mão de obra e encerra o reparo
    public boolean concluirOrdemServico(int idOs, String diagnostico, double valorMaoDeObra) {
        OrdemServico os = osController.buscarPorId(idOs);
        if (os == null) {
            return false;
        }
        os.setDiagnosticoTecnico(diagnostico);
        os.setValorServico(valorMaoDeObra);
        os.setStatus("CONCLUIDA");

        boolean ok = osController.atualizarOS(os);
        if (ok) {
            notificarOSAtualizada();
        }
        return ok;
    }

    // Cancela a OS e devolve automaticamente ao estoque todas as peças vinculadas a ela
    public boolean cancelarOrdemServico(int idOs) {
        OrdemServico os = osController.buscarPorId(idOs);
        if (os == null || "CANCELADA".equalsIgnoreCase(os.getStatus())) {
            return false;
        }

        List<ItemOrdemServico> itens = osController.listarItensPorOS(idOs);
        for (ItemOrdemServico item : itens) {
            pecaController.reporEstoque(item.getIdPeca(), item.getQuantidade());
        }

        os.setStatus("CANCELADA");
        boolean ok = osController.atualizarOS(os);
        if (ok) {
            notificarEstoqueAtualizado(null);
            notificarOSAtualizada();
        }
        return ok;
    }

    public OrdemServico buscarOSPorId(int id) {
        return osController.buscarPorId(id);
    }

    public List<OrdemServico> listarOrdensServico() {
        return osController.listarTodas();
    }

    public List<ItemOrdemServico> listarItensDaOS(int idOs) {
        return osController.listarItensPorOS(idOs);
    }
}