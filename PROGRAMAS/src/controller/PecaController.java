package controller;

import dao.PecaDAO;
import java.util.List;
import model.Peca;
import util.ResultadoCadastro;

public class PecaController {

    private final PecaDAO pecaDAO;

    public PecaController() {
        this.pecaDAO = new PecaDAO();
    }

    // Impede valores negativos de preço ou quantidade e define 0 como piso do estoque mínimo.
    public ResultadoCadastro cadastrar(String nome, String marca, double preco, int quantidade, int estoqueMinimo) {
        if (nome == null || nome.trim().isEmpty() || preco < 0 || quantidade < 0) {
            return ResultadoCadastro.ERRO_BANCO;
        }

        Peca peca = new Peca();
        peca.setNome(nome.trim());
        peca.setMarca(marca != null ? marca.trim() : "");
        peca.setPreco(preco);
        peca.setQuantidadeEstoque(quantidade);
        peca.setEstoqueMinimo(Math.max(estoqueMinimo, 0));

        return pecaDAO.inserir(peca);
    }

    public boolean atualizar(Peca peca) {
        if (peca == null || peca.getId() <= 0
                || peca.getNome() == null || peca.getNome().trim().isEmpty()
                || peca.getPreco() < 0 || peca.getQuantidadeEstoque() < 0) {
            return false;
        }
        peca.setNome(peca.getNome().trim());
        return pecaDAO.gravar(peca);
    }

    public boolean excluir(int id) {
        if (id <= 0) {
            return false;
        }
        return pecaDAO.excluir(id);
    }

    // Verifica se o saldo atual comporta a quantidade solicitada pela Ordem de Serviço.
    public boolean temEstoqueSuficiente(int idPeca, int quantidadeSolicitada) {
        if (quantidadeSolicitada <= 0) {
            return false;
        }
        Peca peca = pecaDAO.buscarPorId(idPeca);
        return peca != null && peca.getQuantidadeEstoque() >= quantidadeSolicitada;
    }

    // Subtrai a quantidade utilizada do saldo da peça no banco.
    public boolean baixarEstoque(int idPeca, int quantidade) {
        Peca peca = pecaDAO.buscarPorId(idPeca);
        if (peca == null || quantidade <= 0 || peca.getQuantidadeEstoque() < quantidade) {
            return false;
        }
        peca.setQuantidadeEstoque(peca.getQuantidadeEstoque() - quantidade);
        return pecaDAO.gravar(peca);
    }

    // Devolve peças ao almoxarifado (usado em cancelamentos ou estorno de falha).
    public boolean reporEstoque(int idPeca, int quantidade) {
        Peca peca = pecaDAO.buscarPorId(idPeca);
        if (peca == null || quantidade <= 0) {
            return false;
        }
        peca.setQuantidadeEstoque(peca.getQuantidadeEstoque() + quantidade);
        return pecaDAO.gravar(peca);
    }

    // Retorna mensagem de alerta caso o saldo atinja ou fique abaixo do estoque mínimo.
    public String verificarAlertaEstoqueMinimo(int idPeca) {
        Peca peca = pecaDAO.buscarPorId(idPeca);
        if (peca != null && peca.getQuantidadeEstoque() <= peca.getEstoqueMinimo()) {
            return "Atenção: Estoque baixo para a peça '" + peca.getNome()
                    + "' (Restam " + peca.getQuantidadeEstoque() + " un.).";
        }
        return null;
    }

    public Peca buscarPorId(int id) {
        return pecaDAO.buscarPorId(id);
    }

    public List<Peca> listarTodos() {
        return pecaDAO.listarTodos();
    }
}