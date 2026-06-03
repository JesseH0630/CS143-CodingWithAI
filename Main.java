import javax.swing.SwingUtilities;

public class Main {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			LinkedListUI ui = new LinkedListUI();
			ui.setVisible(true);
		});
	}
}
