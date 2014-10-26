import javax.swing.JFrame;

public class ClientTest {
	public static void main(String[] args) {
		Client clientTest = new Client("127.0.0.1");
		clientTest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		clientTest.startRunning();
	}
}