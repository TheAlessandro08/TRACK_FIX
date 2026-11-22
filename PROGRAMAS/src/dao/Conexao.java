package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Classe utilitária centralizada para abertura de conexões JDBC com o MySQL.
public class Conexao {

    // Parâmetros de conexão apontando para o banco bd_assistencia
    private static final String URL = "jdbc:mysql://localhost:3306/bd_assistencia?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String SENHA = "0485269"; // Ajuste caso seu MySQL utilize senha

    // Retorna uma nova conexão ativa; o fechamento automático é feito via try-with-resources nos DAOs
    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}