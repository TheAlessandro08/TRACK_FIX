package controller;

import dao.OrdemServicoDAO;
import java.util.List;
import model.ItemOrdemServico;
import model.OrdemServico;
import model.Peca;

public class OrdemServicoController {

    private final OrdemServicoDAO osDAO;

    public OrdemServicoController() {
        this.osDAO = new OrdemServicoDAO();
    }

    // Valida cliente, equipamento e defeito; inicializa status como "ABERTA" se não informado.
    public boolean abrirOS(OrdemServico os) {
        if (os == null || os.getIdCliente() <= 0
                || os.getEquipamento() == null || os.getEquipamento().trim().isEmpty()
                || os.getDefeitoRelatado() == null || os.getDefeitoRelatado().trim().isEmpty()) {
            return false;
        }
        os.setEquipamento(os.getEquipamento().trim());
        os.setDefeitoRelatado(os.getDefeitoRelatado().trim());
        if (os.getStatus() == null || os.getStatus().trim().isEmpty()) {
            os.setStatus("ABERTA");
        }
        recalcularValorTotal(os);
        return osDAO.inserir(os);
    }

    // Recalcula os valores antes de salvar qualquer alteração na OS.
    public boolean atualizarOS(OrdemServico os) {
        if (os == null || os.getId() <= 0) {
            return false;
        }
        recalcularValorTotal(os);
        return osDAO.gravar(os);
    }

    // Cria o vínculo da peça com a OS (congelando o preço unitário do momento) e atualiza o total.
    public boolean registrarItemPeca(int idOs, Peca peca, int quantidade) {
        if (idOs <= 0 || peca == null || quantidade <= 0) {
            return false;
        }

        ItemOrdemServico item = new ItemOrdemServico();
        item.setIdOrdemServico(idOs);
        item.setIdPeca(peca.getId());
        item.setQuantidade(quantidade);
        item.setValorUnitario(peca.getPreco());

        boolean inseriuItem = osDAO.inserirItem(item);
        if (inseriuItem) {
            OrdemServico os = osDAO.buscarPorId(idOs);
            if (os != null) {
                recalcularValorTotal(os);
                osDAO.gravar(os);
            }
        }
        return inseriuItem;
    }

    // Soma o valor de todas as peças vinculadas e adiciona ao valor da mão de obra.
    public void recalcularValorTotal(OrdemServico os) {
        double totalPecas = 0.0;
        if (os.getId() > 0) {
            List<ItemOrdemServico> itens = osDAO.listarItensPorOS(os.getId());
            for (ItemOrdemServico item : itens) {
                totalPecas += item.getQuantidade() * item.getValorUnitario();
            }
        }
        os.setValorPecas(totalPecas);
        os.setValorTotal(os.getValorServico() + totalPecas);
    }

    public List<ItemOrdemServico> listarItensPorOS(int idOs) {
        return osDAO.listarItensPorOS(idOs);
    }

    public OrdemServico buscarPorId(int id) {
        return osDAO.buscarPorId(id);
    }

    public List<OrdemServico> listarTodas() {
        return osDAO.listarTodas();
    }

    public boolean excluirOS(int id) {
        return osDAO.excluir(id);
    }
}