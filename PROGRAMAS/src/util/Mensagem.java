package util;

import javax.swing.JOptionPane;

// Classe utilitária para padronizar a exibição de alertas e confirmações na camada View.
public class Mensagem {

    // Exibe mensagem informativa (ex.: operação concluída com sucesso)
    public static void info(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, "Informação", JOptionPane.INFORMATION_MESSAGE);
    }

    // Exibe mensagem de erro (ex.: falha de autenticação, validação ou erro de banco)
    public static void erro(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    // Exibe mensagem de alerta (ex.: aviso de estoque mínimo disparado pelo Observer)
    public static void aviso(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    // Solicita confirmação (Sim/Não) antes de ações críticas como exclusão ou cancelamento de OS
    public static boolean confirmar(String mensagem) {
        int resposta = JOptionPane.showConfirmDialog(
            null,
            mensagem,
            "Confirmação",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return resposta == JOptionPane.YES_OPTION;
    }
}