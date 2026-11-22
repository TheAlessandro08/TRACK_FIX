package model;

// Representa os componentes e insumos controlados no estoque da assistência técnica.
public class Peca {

    private int id;
    private String nome;
    private String marca;
    private double preco;
    private int quantidadeEstoque;
    private int estoqueMinimo;

    public Peca() {
    }

    public Peca(int id, String nome, String marca, double preco, int quantidadeEstoque, int estoqueMinimo) {
        this.id = id;
        this.nome = nome;
        this.marca = marca;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.estoqueMinimo = estoqueMinimo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public int getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(int estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    // Exibe nome, marca, preço e saldo atual diretamente ao selecionar a peça na OS
    @Override
    public String toString() {
        return String.format("%s (%s) - R$ %.2f [Estoque: %d]", nome, marca, preco, quantidadeEstoque);
    }
}