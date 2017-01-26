package Main;

import java.applet.Applet;

/*
 * Note: The main engine of this code was borrowed from a set of tutorials online.
 * The link : https://www.youtube.com/watch?v=9dzhgsVaiSo
 * 
 * The original engine was created by ForeignGuyMike
 * Most of the code has been modified to meet our requirements for specific modes, characters and gameplay elements.
 * 
 */
import javax.swing.JFrame;

public class GradePoint {

	/**
	* 
	*/
	private static final long serialVersionUID = 275830433244911894L;

	/**
	* 
	*/

	public static void main(String[] args) {

		JFrame window = new JFrame("GRADE POINT");
		window.setContentPane(new GamePanel());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.pack();
		window.setVisible(true);
	}
}
