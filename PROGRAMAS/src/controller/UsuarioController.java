package controller;

import dao.UsuarioDAO;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import model.Usuario;
import util.ResultadoCadastro;

public class UsuarioController {

    private final UsuarioDAO usuarioDAO;

    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    // Valida credenciais convertendo a senha digitada em hash SHA-256
    // e garante que apenas usuários ativos ("T") consigam acessar o sistema.
    public boolean login(String login, String senha) {
        if (login == null || login.trim().isEmpty() || senha == null || senha.isEmpty()) {
            return false;
        }
        String senhaHash = gerarHashSHA256(senha.trim());
        Usuario usuario = usuarioDAO.autenticar(login.trim(), senhaHash);
        return usuario != null && "T".equalsIgnoreCase(usuario.getAtivo());
    }

    // Retorna o objeto Usuario completo para manter a sessão ativa na Facade.
    public Usuario buscarPorLoginESenha(String login, String senha) {
        if (login == null || login.trim().isEmpty() || senha == null || senha.isEmpty()) {
            return null;
        }
        String senhaHash = gerarHashSHA256(senha.trim());
        Usuario usuario = usuarioDAO.autenticar(login.trim(), senhaHash);
        if (usuario != null && "T".equalsIgnoreCase(usuario.getAtivo())) {
            return usuario;
        }
        return null;
    }

    // Cadastro simplificado (usado pela TelaLogin): define perfil padrão "U" e ativo "T".
    public ResultadoCadastro cadastrar(String login, String senha) {
        if (login == null || login.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            return ResultadoCadastro.ERRO_BANCO;
        }

        Usuario novo = new Usuario();
        novo.setLogin(login.trim());
        novo.setSenha(gerarHashSHA256(senha.trim()));
        novo.setTipo("U");
        novo.setAtivo("T");

        return usuarioDAO.inserir(novo);
    }

    // Cadastro completo (usado pelo FormularioUsuario administrativo).
    public ResultadoCadastro inserirUsuarioCompleto(Usuario usuario, String senhaPura) {
        if (usuario.getLogin() == null || usuario.getLogin().trim().isEmpty()
                || senhaPura == null || senhaPura.trim().isEmpty()) {
            return ResultadoCadastro.ERRO_BANCO;
        }
        usuario.setLogin(usuario.getLogin().trim());
        usuario.setSenha(gerarHashSHA256(senhaPura.trim()));
        return usuarioDAO.inserir(usuario);
    }

    public boolean atualizar(Usuario usuario) {
        if (usuario == null || usuario.getLogin() == null || usuario.getLogin().trim().isEmpty()) {
            return false;
        }
        return usuarioDAO.gravar(usuario);
    }

    public boolean excluir(int id) {
        return usuarioDAO.excluir(id);
    }

    public Usuario buscarPorId(int id) {
        return usuarioDAO.buscarPorId(id);
    }

    public List<Usuario> listarTodos() {
        return usuarioDAO.listarTodos();
    }

    // Converte a senha em texto plano para hexadecimal SHA-256 (64 caracteres).
    public String gerarHashSHA256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar hash SHA-256", e);
        }
    }
}