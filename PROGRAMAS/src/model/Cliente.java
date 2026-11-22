package model;

// Representa o cliente proprietário dos equipamentos enviados para manutenção.
public class Cliente {

    private int id;
    private String nome;
    private String cpfCnpj;
    private String telefone;
    private String email;

    public Cliente() {
    }

    public Cliente(int id, String nome, String cpfCnpj, String telefone, String email) {
        this.id = id;
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.telefone = telefone;
        this.email = email;
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

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Permite carregar objetos Cliente diretamente em JComboBox<Cliente> na tela de abertura de OS
    @Override
    public String toString() {
        if (cpfCnpj != null && !cpfCnpj.isEmpty()) {
            return nome + " (" + cpfCnpj + ")";
        }
        return nome;
    }
}