package maze;

public class BinaryMinHeap {
	private Cell[ ] heap;
	private int size;
	
	public BinaryMinHeap(int capacity) {
		size=0;
		heap=new Cell[capacity+1];
	}
	
	public boolean isEmpty(){
		return size==0;
	}
	
	public boolean isFull(){
		return size==heap.length-1;
	}
	
	public void clear(){
		size=0;
	}
	
	public void insert(Cell x){
		int cur=++size;
		while(cur>1&&x.compareTo(heap[cur/2])<0){
			heap[cur]=heap[cur/2];
			cur/=2;
		}
		heap[cur]=x;
	}
	
	public Cell peek(){
		if(!isEmpty()){
			return heap[1];
		}
		return null;
	}
	
	public Cell pop(){
		if(!isEmpty()){
			Cell min=heap[1];
			heap[1]=heap[size--];
			percolateDown(1);
			return min;
		}
		return null;
	}
	
	public int find(Cell cell){
		for(int i=1;i<size;i++){
			if(heap[i].x==cell.x&&heap[i].y==cell.y)
				return i;
		}
		return 0;
	}
	
	public void rearrange(int cur){
		Cell x=heap[cur];
		while(cur>1&&x.compareTo(heap[cur/2])<0){
			heap[cur]=heap[cur/2];
			cur/=2;
		}
		heap[cur]=x;
	}
	
	private void percolateDown(int i){
		int cur=i*2;
		Cell tmp=heap[i];
		if(cur<size){
			if(heap[cur+1].compareTo(heap[cur])<0)
				cur++;
			if(heap[cur].compareTo(tmp)<0){
				heap[i]=heap[cur];
				heap[cur]=tmp;
				percolateDown(cur);
			}
		}
	}
	
}
