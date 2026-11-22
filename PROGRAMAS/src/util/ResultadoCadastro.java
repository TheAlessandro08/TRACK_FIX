package util;

// Enumeração que padroniza os possíveis retornos das operações de cadastro
// entre as camadas DAO, Controller e View.
public enum ResultadoCadastro {
    SUCESSO,
    JA_EXISTE,
    ERRO_BANCO
}