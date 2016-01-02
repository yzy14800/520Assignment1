package maze;

import java.util.Scanner;

class PathDisplay {

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
		}
		int one=-1;
		int method=-1;
		boolean stop=false;
		while(!stop){
			System.out.println("You have generated 50 mazes, please select one for display:(0-49), -2~stop");
			Scanner input=new Scanner(System.in);
			if(input.hasNextInt()){
				one=input.nextInt();
				if(one>49)
					continue;
				if(one==-2)
					break;
			}else{
				System.out.println("Error:please input an integer! ");
				continue;
			}
			
			System.out.println("Maze "+one+" (0~unblock,1~block,2~start,3~goal)");
			sample[one].displayMaze();
			
			while(method==-1){
				System.out.println("Maze"+one+", please select search method:(0~Forward,1~Backward,2~Adaptive,-1~Rechoose maze)");
				if(input.hasNextInt()){
					method=input.nextInt();
					if(method==-1){
						break;
					}
					if(method==-2){
						stop=true;
						break;
					}
				}
				else{
					System.out.println("Error:please input an integer! ");
					continue;
				}
				switch(method){
					case 0:
						sample[one].fAStarSearch(false);
						sample[one].displayPath();
						method=-1;
						break;
					case 1:
						sample[one].bAStarSearch();
						sample[one].displayPath();
						method=-1;
						break;
					case 2:
						sample[one].adaptiveAStarSearch();
						sample[one].displayPath();
						method=-1;
						break;
					default:
						method=-1;
						break;
				}
			}
		}
	}

}
