package maze;

public class TieBreakingComparison {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int Height=101;
		int Width=101;
		Maze[] sample=new Maze[50];
		for(int i=0;i<50;i++){
			Maze maze=new Maze(Height,Width);
			maze.generateMaze();
			maze.generateStartGoal();
			sample[i]=maze;
			System.out.println("Maze "+i);
			System.out.print("Larger G ");
			maze.fAStarSearch(false);
			System.out.print("Smaller G ");
			maze.fAStarSearch(true);
		}
	}

}
