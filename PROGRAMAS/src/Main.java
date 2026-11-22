import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import view.TelaLogin;

// Ponto de entrada principal da aplicação (fica diretamente na raiz da pasta src).
public class Main {

    public static void main(String[] args) {
        // Aplica o tema visual Nimbus (nativo do Java) para deixar tabelas e botões mais modernos
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Se falhar, utiliza o visual padrão do sistema operacional sem interromper o programa
        }

        // Inicia a interface gráfica na Event Dispatch Thread (EDT) do Swing
        SwingUtilities.invokeLater(() -> {
            new TelaLogin().setVisible(true);
        });
    }
}