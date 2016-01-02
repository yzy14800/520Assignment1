package maze;

public class Cell implements Comparable<Cell>{
	
	public int x;
	public int y;
	public int g;
	public int f;
	public int h;
	public int hn;
	//fasle->Larger-G,true->Smaller-G
	public boolean TIE_BREAKING=false;
	public Cell parent;
	
	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int compareTo(Cell o) {
		// TODO Auto-generated method stub
//		return f-o.f;
		if(!TIE_BREAKING)
			return 101*101*(f-o.f)-(g-o.g);
		else
			return 101*101*(f-o.f)+(g-o.g);
	}
}
