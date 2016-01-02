package maze;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Maze extends JFrame{
	private int X;
	private int Y;
	//store block status of whole maze
	//0->unblock,1->block,2->start,3->goal
	private int[][] maze;
	//store g,h,f value and coordinates of each cell
	private Cell[][] state;
	private Cell start=null;
	private Cell goal=null;
	//record each cell a search goes
	private boolean[][] findPath;
	//record num of expanded node of forward(0),backward(1),adaptive(2)
	public int[] numExpand;
	
	public JPanel grid;


	public Maze(int x, int y) {
		super();
		X = x;
		Y = y;
		state=new Cell[X][Y];
		for(int i=0;i<X;i++){
			for(int j=0;j<Y;j++){
				state[i][j]=new Cell(i,j);
			}
		}
		numExpand=new int[3];
	}

	public boolean isValid(int x,int y){
		if(x<0||y<0||x>X-1||y>Y-1)
			return false;
		else
			return true;
	}
	
	public Cell move(Cell cur,int dir){
		int nX=cur.x;
		int nY=cur.y;
		switch (dir){
			case 0: //0->left;
				nX--;
				break;
			case 1: //1->right;
				nX++;
				break;			
			case 2: //2->up;
				nY--;
				break;
			default: //3->down;
				nY++;
				break;
		}
		return isValid(nX,nY)? state[nX][nY]:null;
	}
	
	public Integer[] randomizeDirs(){
		Integer[] dirs=new Integer[]{0,1,2,3};
		Collections.shuffle(Arrays.asList(dirs));
		return dirs;
	}
	
	public int randomizeBlock(Random seed){
		return seed.nextInt(10)<3? 1:0;
		
	}
	
	public int[][] generateMaze(){
		maze=new int[Y][X];
		int[][] visited=new int[Y][X];
		Random seed=new Random();
		Stack<Cell> stack=new Stack<Cell>();
		int cnt=0;
		Cell cur=null;
		int cX,cY;
		while(cnt<X*Y){
			//initialize start state
			if(cnt==0){
				cX=seed.nextInt(X);
				cY=seed.nextInt(Y);
				cur=new Cell(cX,cY);
				visited[cX][cY]=1;
				cnt++;
				stack.push(cur);
			}
			
			//check unvisited neighbours
			int check=0;
			for(Integer dir:randomizeDirs()){
				//neighboor
				Cell nb=move(cur,dir);
				if(nb==null){
					check++;
					continue;
				}
				int nX=nb.x;
				int nY=nb.y;
				if(visited[nX][nY]==1){
					check++;
					continue;
				}
				//randomly generate block
				maze[nX][nY]=randomizeBlock(seed);
				if(maze[nX][nY]==1){
					visited[nX][nY]=1;
					cnt++;
					check++;
					continue;
				}
				stack.push(nb);
				cur=nb;
				visited[nX][nY]=1;
				cnt++;
				break;
			}
			
			//check deadend
			if(check==4){
				if(!stack.empty()){
					cur=stack.pop();
				}else{
					//find new unvisited cell
					for(int i=0;i<Y;i++){
						for(int j=0;j<X;j++){
							if(visited[i][j]==0){
								cur=new Cell(i,j);
								cnt++;
								visited[i][j]=1;
								i=Y;
								break;
							}
						}
					}
					//
				}
			}
			
		}
		return maze;
	}
	public void generateStartGoal(){
		if(start==null||goal==null){
			//randomly create start state and goal state
			Random seed=new Random();
			int sX,sY,gX,gY;
			do{
				sX=seed.nextInt(X);
				sY=seed.nextInt(Y);
				gX=seed.nextInt(X);
				gY=seed.nextInt(Y);
			}while((sX==gX&&sY==gY)||maze[sX][sY]==1||maze[gX][gY]==1);
			
			start=state[sX][sY];
			maze[sX][sY]=2;
			goal=state[gX][gY];
			maze[gX][gY]=3;
		}
	}
	
	public void fAStarSearch(boolean tie){
		System.out.println("Forward:");
		numExpand[0]=0;
		int cnt=0;
		int search[][]=new int[X][Y];
		//store current knowledge of maze
		boolean knowledge[][]=new boolean[X][Y];
		//difine tie breaking policy
		for(int i=0;i<X;i++){
			for(int j=0;j<Y;j++){
				state[i][j].TIE_BREAKING=tie;
			}
		}
		//record path
		findPath=new boolean[X][Y];
		findPath[start.x][start.y]=findPath[goal.x][goal.y]=true;
		Cell cur=start;
		explore(cur,knowledge);
		BinaryMinHeap open=new BinaryMinHeap(X*Y);
		LinkedList<Cell> closed=new LinkedList<Cell>();
		while(cur!=goal){
			cnt++;
			cur.g=0;
			search[cur.x][cur.y]=cnt;
			goal.g=Integer.MAX_VALUE;
			search[goal.x][goal.y]=cnt;
			cur.f=f(cur,goal);
			open.clear();
			closed.clear();
			open.insert(cur);
			numExpand[0]=computePath(open,closed,search,cnt,knowledge,numExpand[0]);
			if(open.isEmpty()){
				System.out.println("I cannot reach the target.");
				System.out.println("Node Expanded: "+numExpand[0]);
				return;
			}
			LinkedList<Cell> path=new LinkedList<Cell>();
			for(Cell i=goal;i!=cur;i=i.parent)
				path.add(i);
			Iterator<Cell> pi=path.descendingIterator();
			while(pi.hasNext()){
				Cell p=pi.next();
				if(maze[p.x][p.y]==1)
					break;
				explore(p,knowledge);
				cur=p;
				findPath[cur.x][cur.y]=true;
				
			}
		}
		System.out.println("I reached the target.");
		System.out.println("Node Expanded: "+numExpand[0]);
	}
	
	public int computePath(BinaryMinHeap open,LinkedList<Cell> closed,int[][] search,int cnt,boolean[][] knowledge,int numE){
		while(!open.isEmpty()){
			Cell c=open.pop();
			if(c==goal){
				break;
			}
			closed.add(c);
			numE++;
			
			for(Cell tmp:generateSuccessors(c,knowledge)){
				if(search[tmp.x][tmp.y]<cnt){
					tmp.g=Integer.MAX_VALUE;
					search[tmp.x][tmp.y]=cnt;
				}
				//if neighbor in CLOSED and cost less than g(neighbor)
				if(closed.contains(tmp)){
					continue;
				}
				int curIndex=open.find(tmp);
				if(tmp.g>c.g+1){
					tmp.g=c.g+1;
					tmp.f=f(tmp,goal);
					tmp.parent=c;
					if(curIndex!=0)
						//if neighbor in OPEN and cost less than g(neighbor)
						open.rearrange(curIndex);
					else
						//if neighbor not in OPEN and neighbor not in CLOSED
						open.insert(tmp);
				}
			}
		}
		return numE;
	}
	
	public void bAStarSearch(){
		System.out.println("Backward:");
		numExpand[1]=0;
		int cnt=0;
		int search[][]=new int[X][Y];
		boolean knowledge[][]=new boolean[X][Y];
		//record path
		findPath=new boolean[X][Y];
		findPath[start.x][start.y]=findPath[goal.x][goal.y]=true;
		Cell cur=start;
		explore(cur,knowledge);
		BinaryMinHeap open=new BinaryMinHeap(X*Y);
		LinkedList<Cell> closed=new LinkedList<Cell>();
		while(cur!=goal){
			cnt++;
			cur.g=Integer.MAX_VALUE;
			search[cur.x][cur.y]=cnt;
			goal.g=0;
			search[goal.x][goal.y]=cnt;
			goal.f=f(cur,start);
			open.clear();
			closed.clear();
			open.insert(goal);
			bComputePath(cur,open,closed,search,cnt,knowledge);
			if(open.isEmpty()){
				while(cur.parent!=null){
					Cell p=cur.parent;
					if(maze[p.x][p.y]==1)
						break;
					explore(p,knowledge);
					cur=p;
					findPath[cur.x][cur.y]=true;
				}
				System.out.println("I cannot reach the target.");		
				System.out.println("Node Expanded: "+numExpand[1]);
				return;
			}
			while(cur!=goal){
				Cell p=cur.parent;
				if(maze[p.x][p.y]==1)
					break;
				explore(p,knowledge);
				cur=p;
				findPath[cur.x][cur.y]=true;
			}
		}
		System.out.println("I reached the target.");
		System.out.println("Node Expanded: "+numExpand[1]);
	}
	
	public void bComputePath(Cell cur,BinaryMinHeap open,LinkedList<Cell> closed,int[][] search,int cnt,boolean[][] knowledge){
		while(!open.isEmpty()){
			Cell c=open.pop();
			if(c==cur){
				break;
			}
			closed.add(c);
			numExpand[1]++;
			
			for(Cell tmp:generateSuccessors(c,knowledge)){
				if(search[tmp.x][tmp.y]<cnt){
					tmp.g=Integer.MAX_VALUE;
					search[tmp.x][tmp.y]=cnt;
				}
				//if neighbor in CLOSED and cost less than g(neighbor)
				if(closed.contains(tmp)){
					continue;
				}
				int curIndex=open.find(tmp);
				if(tmp.g>c.g+1){
					tmp.g=c.g+1;
					tmp.f=f(tmp,start);
					tmp.parent=c;
					if(curIndex!=0)
						//if neighbor in OPEN and cost less than g(neighbor)
						open.rearrange(curIndex);
					else
						//if neighbor not in OPEN and neighbor not in CLOSED
						open.insert(tmp);
				}
			}
		}
	}
	
	public void adaptiveAStarSearch(){
		System.out.println("Adaptive:");
		numExpand[2]=0;
		int cnt=0;
		int search[][]=new int[X][Y];
		boolean knowledge[][]=new boolean[X][Y];
		//record path
		findPath=new boolean[X][Y];
		findPath[start.x][start.y]=findPath[goal.x][goal.y]=true;
		Cell cur=start;
		explore(cur,knowledge);
		BinaryMinHeap open=new BinaryMinHeap(X*Y);
		LinkedList<Cell> closed=new LinkedList<Cell>();
		while(cur!=goal){
			cnt++;
			cur.g=0;
			search[cur.x][cur.y]=cnt;
			goal.g=Integer.MAX_VALUE;
			search[goal.x][goal.y]=cnt;
			cur.f=f(cur,goal);
			open.clear();
			closed.clear();
			open.insert(cur);
			numExpand[2]=computePath(open,closed,search,cnt,knowledge,numExpand[2]);
			//update h value
			for(Cell c:closed){
				c.h=c.hn;
				c.hn=goal.g-c.g;
			}
			if(open.isEmpty()){
				System.out.println("I cannot reach the target.");
				System.out.println("Node Expanded: "+numExpand[2]);
				return;
			}
			LinkedList<Cell> path=new LinkedList<Cell>();
			for(Cell i=goal;i!=cur;i=i.parent)
				path.add(i);
			Iterator<Cell> pi=path.descendingIterator();
			while(pi.hasNext()){
				Cell p=pi.next();
				if(maze[p.x][p.y]==1)
					break;
				explore(p,knowledge);
				cur=p;
				findPath[cur.x][cur.y]=true;
				
			}
		}
		System.out.println("I reached the target.");
		System.out.println("Node Expanded: "+numExpand[2]);
	}
	
	//gain the knowledge of maze
	public void explore(Cell cur,boolean[][] knowledge){
		for(int i=0;i<4;i++){
			Cell tmp=move(cur,i);
			if(tmp!=null&&maze[tmp.x][tmp.y]==1)
				knowledge[tmp.x][tmp.y]=true;
		}
	}
	
	public List<Cell> generateSuccessors(Cell p,boolean[][] knowledge){
		List<Cell> succ=new LinkedList<Cell>();
		for(int i=0;i<4;i++){
			Cell tmp=move(p,i);
			if(tmp!=null&&!knowledge[tmp.x][tmp.y])
				succ.add(tmp);
		}
		return succ;
	}
	
	public void displayMaze(){
		if(grid!=null)
			grid.removeAll();
		else
			grid=new JPanel();
		grid.setLayout(new GridLayout(X,Y));
		for(int i=0;i<X;i++){
			for(int j=0;j<Y;j++){
				JPanel cell=new JPanel();
				cell.setBorder(BorderFactory.createLineBorder(Color.black));
				switch(maze[i][j]){
				case 0:
					cell.setBackground(Color.white);
					break;
				case 1:
					cell.setBackground(Color.black);
					break;
				case 2:
					cell.setBackground(Color.red);
					break;
				default:
					cell.setBackground(Color.green);
					break;
				}
				grid.add(cell);
				System.out.print(maze[i][j]+"  ");
				if(j==Y-1)
					System.out.println();
			}
		}
		grid.repaint();
		this.setContentPane(grid);
		this.setVisible(true);
		this.setSize(707, 707);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
		
	}
	public void displayPath(){
		if(grid!=null)
			grid.removeAll();
		else
			grid=new JPanel();
		grid=new JPanel();
		grid.setLayout(new GridLayout(X,Y));
		for(int i=0;i<Y;i++){
			for(int j=0;j<X;j++){
				JPanel cell=new JPanel();
				cell.setBorder(BorderFactory.createLineBorder(Color.black));
				switch(maze[i][j]){
					case 0:
						if(findPath[i][j])
							cell.setBackground(Color.red);
						else
							cell.setBackground(Color.white);
						break;
					case 1:
						cell.setBackground(Color.black);
						break;
					case 2:
						cell.setBackground(Color.red);
						break;
					default:
						cell.setBackground(Color.green);
						break;
				}
				grid.add(cell);
				if(findPath[i][j]&&maze[i][j]==0)
					System.out.print("*  ");
				else
					System.out.print(maze[i][j]+"  ");
				if(j==X-1)
					System.out.println();
			}
		}
		grid.repaint();
		this.setContentPane(grid);
		this.setVisible(true);
		this.setSize(707, 707);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  
	}
	
	public int h(Cell cur,Cell goal){
		if(cur.hn==0)
			return Math.abs(cur.x-goal.x)+Math.abs(cur.y-goal.y);
		else
			return cur.hn;
	}
	
	public int f(Cell cur, Cell goal){
		return cur.g+h(cur,goal);
	}
	
	public int[][] getMaze() {
		return maze;
	}

	public void setMaze(int[][] maze) {
		this.maze = maze;
	}

	public Cell getStart() {
		return start;
	}

	public void setStart(Cell start) {
		this.start = start;
	}

	public Cell getGoal() {
		return goal;
	}

	public void setGoal(Cell goal) {
		this.goal = goal;
	}
	
}

