package model;

// Representa o chamado técnico, diagnóstico, status de execução e valores consolidados.
public class OrdemServico {

    private int id;
    private int idCliente;
    private int idTecnico;
    private String equipamento;
    private String defeitoRelatado;
    private String diagnosticoTecnico;
    private String status;           // ABERTA, EM_ANALISE, AGUARDANDO_PECA, CONCLUIDA, ENTREGUE, CANCELADA
    private double valorServico;     // Mão de obra
    private double valorPecas;       // Soma calculada dos itens aplicados
    private double valorTotal;       // valorServico + valorPecas
    private String dataAbertura;

    public OrdemServico() {
    }

    public OrdemServico(int id, int idCliente, int idTecnico, String equipamento, String defeitoRelatado,
                        String diagnosticoTecnico, String status, double valorServico, double valorPecas,
                        double valorTotal, String dataAbertura) {
        this.id = id;
        this.idCliente = idCliente;
        this.idTecnico = idTecnico;
        this.equipamento = equipamento;
        this.defeitoRelatado = defeitoRelatado;
        this.diagnosticoTecnico = diagnosticoTecnico;
        this.status = status;
        this.valorServico = valorServico;
        this.valorPecas = valorPecas;
        this.valorTotal = valorTotal;
        this.dataAbertura = dataAbertura;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdTecnico() {
        return idTecnico;
    }

    public void setIdTecnico(int idTecnico) {
        this.idTecnico = idTecnico;
    }

    public String getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(String equipamento) {
        this.equipamento = equipamento;
    }

    public String getDefeitoRelatado() {
        return defeitoRelatado;
    }

    public void setDefeitoRelatado(String defeitoRelatado) {
        this.defeitoRelatado = defeitoRelatado;
    }

    public String getDiagnosticoTecnico() {
        return diagnosticoTecnico;
    }

    public void setDiagnosticoTecnico(String diagnosticoTecnico) {
        this.diagnosticoTecnico = diagnosticoTecnico;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getValorServico() {
        return valorServico;
    }

    public void setValorServico(double valorServico) {
        this.valorServico = valorServico;
    }

    public double getValorPecas() {
        return valorPecas;
    }

    public void setValorPecas(double valorPecas) {
        this.valorPecas = valorPecas;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(String dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    @Override
    public String toString() {
        return "OS #" + id + " - " + equipamento + " [" + status + "]";
    }
}