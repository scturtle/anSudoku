package sct.Lib;
import java.lang.reflect.Array;
import java.util.Random;


public class DigHoles{
	static int[][] randomDig(int[][] om,int holes){
		int[][] m=om.clone();
        for (int i = 0; i  < om.length; i ++){
        	m[i] = om[i].clone();  
        }
		int remains=81;
		Random r=new Random();
		for(int i=1;i<=9 && remains!=0;i++)
			for(int j=1;j<=9 && remains!=0;j++)
			{
				if(r.nextInt(remains)<holes) {
					m[i][j]=0; holes--;
				}
				remains--;
			}
		return m;
	}
	
	public static Object tryDigLoop(int holes){
		Object arr= Array.newInstance(Object.class, 3);
		int[][] m=RandomFullSudoku.getSudoku();
		int[][] res=m.clone();
        for (int i = 0; i  < m.length; i ++){
        	res[i] = m[i].clone();  
        }
		int times=0;
		while(!tryDig(res,holes)) {
			for (int i = 0; i  < m.length; i ++)
				res[i] = m[i].clone();  
			times++;
		}
		Array.set(arr, 0, m);
		Array.set(arr, 1, res);
		Array.set(arr, 2, times);
		return arr;
	}
	public static boolean tryDig(int[][] m,int holes){
		int[] arr=new int[81];
		int c=0;
		for(int i=1;i<=9;i++)
			for(int j=1;j<=9;j++)
				arr[c++]=i*10+j;
		
		Random r=new Random();
		for(int i=0;i<81;i++)
		{
			int a=r.nextInt(81),b=r.nextInt(81);
			int t=arr[a];arr[a]=arr[b];arr[b]=t;
		}
		while(holes!=0 && c>0)
		{
			int i=arr[--c]/10,j=arr[c]%10;
			int t=m[i][j];
			m[i][j]=0;
			if(Dlx.getCount(m)==1)
				holes--;
			else
				m[i][j]=t;
		}
		return holes==0;
	}
    static void print(int[][] m){
        for(int i=1;i<=9;i++)
        {
            for(int j=1;j<=9;j++)
                System.out.print((m[i][j]==0?" ":m[i][j])+" ");
            System.out.println();
        }
    }
    
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