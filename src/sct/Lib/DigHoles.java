package sct.Lib;
import java.lang.reflect.Array;
import java.util.Random;


public class DigHoles{
	
	/*
	 * function: get a sudoku with fixed number holes and unique answer
	 * input: holes
	 * output: sudoku with holes, answer(, times to get it)
	 */
	public static Object tryDigLoop(int holes){
		Object arr= Array.newInstance(Object.class, 3);//make a array with 3 place
		int[][] m=RandomFullSudoku.getSudoku();
		int[][] res=m.clone();
        for (int i = 0; i  < m.length; i ++){
        	res[i] = m[i].clone();  
        }
		int times=0;
		//keep trying until we get what we want
		while(!tryDig(res,holes)) {
			if(times>30){//trick: no answer?
				m=RandomFullSudoku.getSudoku();
				times=0;
			}
			for (int i = 0; i  < m.length; i ++)
				res[i] = m[i].clone();  
			times++;
		}
		Array.set(arr, 0, m); // answer
		Array.set(arr, 1, res); // sudoku
		Array.set(arr, 2, times);
		return arr;
	}
	
	/*
	 * function: 
	 */
	public static boolean tryDig(int[][] m,int holes){
		// shuffle all 81 positions
		int[] arr=new int[81];
		int c=0;// len of arr
		for(int i=1;i<=9;i++)
			for(int j=1;j<=9;j++)
				arr[c++]=i*10+j;
		
		Random r=new Random();
		for(int i=0;i<81;i++)
		{
			int a=r.nextInt(81),b=r.nextInt(81);
			int t=arr[a];arr[a]=arr[b];arr[b]=t;//random
		}
		// trick: remove some holes first, very useful !
		for (int k=0;k<35;k++){
			--c; int i=arr[c]/10,j=arr[c]%10;
			m[i][j]=0;--holes;
		}
		if(Dlx.getCount(m)!=1) return false;
		// check unique answer while digging more holes
		while(holes>0 && c>=holes)
		{
			--c;
			int i=arr[c]/10,j=arr[c]%10;
			int t=m[i][j];
			m[i][j]=0;
			if(Dlx.getCount(m)==1)
				holes--;
			else
				m[i][j]=t;
		}
		return holes==0;
	}
	// test function
    static void print(int[][] m){
        for(int i=1;i<=9;i++)
        {
            for(int j=1;j<=9;j++)
                System.out.print((m[i][j]==0?" ":m[i][j])+" ");
            System.out.println();
        }
    }
	// test function
	public static void main(String args[]){
		int[][] m=RandomFullSudoku.getSudoku();
		int[][] mc=m.clone();
        for (int i = 0; i  < m.length; i ++){
        	mc[i] = m[i].clone();  
        }
		int times=0;
		long start=System.currentTimeMillis(); 
		while(!tryDig(m,55)) {
			for (int i = 0; i  < mc.length; i ++){
				m[i] = mc[i].clone();  
			}
			times++;
		}
		long end=System.currentTimeMillis(); 
		print(m);
		System.out.printf("Try:%dtimes using:%dms\n",times,end-start);
	}
}