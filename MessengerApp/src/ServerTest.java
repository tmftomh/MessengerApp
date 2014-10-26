import javax.swing.*;

public class ServerTest {
	public static void main(String[] args) {
		Server serverTest = new Server();
		serverTest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		serverTest.startRunning();
	}
}
