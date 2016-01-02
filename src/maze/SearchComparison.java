package maze;

public class SearchComparison {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int Height=101;
		int Width=101;
		Maze[] sample=new Maze[50];
		for(int i=0;i<50;i++){
			Maze maze=new Maze(Height,Width);
			maze.generateMaze();
			maze.generateStartGoal();
			System.out.println("Maze "+i);
			maze.fAStarSearch(false);
			maze.bAStarSearch();
			maze.adaptiveAStarSearch();
			sample[i]=maze;
		}
		
		System.out.println("Maze     For.     Back.     Adaptive");
		for(int i=0;i<50;i++){
			if(i<10)
				System.out.println("  0"+i+"     "+sample[i].numExpand[0]+"     "+sample[i].numExpand[1]+"     "+sample[i].numExpand[2]);
			else
				System.out.println("  "+i+"     "+sample[i].numExpand[0]+"     "+sample[i].numExpand[1]+"     "+sample[i].numExpand[2]);
			
		}

	}

}
