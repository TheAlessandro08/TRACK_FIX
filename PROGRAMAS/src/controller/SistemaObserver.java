package controller;

// Interface do padrão Observer: qualquer tela (View) que precisar atualizar
// suas tabelas automaticamente deve implementar estes métodos.
public interface SistemaObserver {
    void onOrdemServicoAtualizada();
    void onEstoqueAtualizado(String alertaEstoque);
    void onClienteAtualizado();
}