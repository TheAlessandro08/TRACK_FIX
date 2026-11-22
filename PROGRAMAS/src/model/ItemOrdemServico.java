package model;

// Representa o vínculo associativo entre uma Ordem de Serviço e cada Peça utilizada nela.
public class ItemOrdemServico {

    private int id;
    private int idOrdemServico;
    private int idPeca;
    private int quantidade;
    private double valorUnitario; // Armazena o preço praticado no momento da inclusão na OS

    public ItemOrdemServico() {
    }

    public ItemOrdemServico(int id, int idOrdemServico, int idPeca, int quantidade, double valorUnitario) {
        this.id = id;
        this.idOrdemServico = idOrdemServico;
        this.idPeca = idPeca;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdOrdemServico() {
        return idOrdemServico;
    }

    public void setIdOrdemServico(int idOrdemServico) {
        this.idOrdemServico = idOrdemServico;
    }

    public int getIdPeca() {
        return idPeca;
    }

    public void setIdPeca(int idPeca) {
        this.idPeca = idPeca;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    // Calcula o subtotal do item para exibição nas tabelas de detalhes da OS
    public double getSubtotal() {
        return quantidade * valorUnitario;
    }
}