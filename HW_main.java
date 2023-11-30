import java.io.IOException;
import java.util.ArrayList;

public class HW_main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		GameBoard gb = new GameBoard();
		gb.readInput(args[0]);
		ArrayList<Pair> path = gb.getPlan();
		for (int i = 0; i < path.size(); i++) {
			System.out.println(path.get(i).getId() + " " +  path.get(i).getDirection());
		}
		System.out.println(gb.getNumOfPaths());

	}

}
