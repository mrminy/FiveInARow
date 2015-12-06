package Game;// Game.Five.java - main program for Game.Five-In-A-Row Program
import javax.swing.JFrame;

////////////////////////////////////////////////////// class Game.Five
/** Game.Five.java - Winner is able to put 5 pieces in a row.
 The Game.Five program consists of three files:
 Game.Five.java      - this file with main to create window.
 Game.FiveGUI.java   - implements the GUI interface.
 Game.FiveLogic.java - the logical functioning.
 @author Fred Swartz
 @version 2004-05-02
 */
class Five {
	//================================================ method main
	public static void main(String[] args) {
		JFrame window = new JFrame("Game.Five In A Row");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setContentPane(new FiveGUI());
		window.pack();  // finalize layout
		window.setResizable(false);
		window.show();  // make window visible
	}//end main
}//endclass Game.Five