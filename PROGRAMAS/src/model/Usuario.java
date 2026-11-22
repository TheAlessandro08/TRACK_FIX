package model;

// Representa os usuários e técnicos que operam o sistema.
public class Usuario {

    private int id;
    private String login;
    private String senha;
    private String tipo;         // "A" = Administrador | "U" = Usuário/Técnico
    private String ativo;        // "T" = Ativo | "F" = Inativo
    private String email;
    private String dataCriacao;

    public Usuario() {
    }

    public Usuario(int id, String login, String senha, String tipo, String ativo, String email, String dataCriacao) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.tipo = tipo;
        this.ativo = ativo;
        this.email = email;
        this.dataCriacao = dataCriacao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAtivo() {
        return ativo;
    }

    public void setAtivo(String ativo) {
        this.ativo = ativo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    // Facilita a exibição do nome do técnico caso seja usado em um JComboBox<Usuario>
    @Override
    public String toString() {
        return login;
    }
}