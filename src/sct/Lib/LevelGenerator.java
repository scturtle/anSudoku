package sct.Lib;

import java.util.Random;

public class LevelGenerator {
	
	static int[][] m,ans;
	public static int[][] getSudoku(){return m;}
	public static int[][] getAns(){return ans;}

	public static boolean getSudokuAtLevel(int level){// 1<=level<=4
		int[] arr=new int[81];
		do
		{
			m=RandomFullSudoku.getSudoku();
			ans=m.clone(); for(int i=0;i<m.length;i++) ans[i]=m[i].clone();
			int c=0;// len of arr
			for(int i=0;i<9;i++) //get cell list
				for(int j=0;j<9;j++)
					arr[c++]=i*10+j;

			//shuttle the list
			Random r=new Random();
			for(int i=0;i<81;i++)
			{
				int a=r.nextInt(81),b=r.nextInt(81);
				int t=arr[a];arr[a]=arr[b];arr[b]=t;//random
			}
			int n=30;//assume remove number
			for(c=0;c<n;c++){
				int i=arr[c]/10,j=arr[c]%10;
				m[i][j]=0;
			}
			if(Dlx.getCount(m)!=1) continue;

			for(c=n;c<81;c++){
				int i=arr[c]/10,j=arr[c]%10;
				int t=m[i][j];
				m[i][j]=0;
				if(Dlx.getCount(m)!=1)
					m[i][j]=t;
			}
		}while(Solver.isLevel(m,level)==false);
		return true;
	}
	
	//test function
    static void print(int[][] m){
        for(int i=0;i<9;i++)
        {
            for(int j=0;j<9;j++)
                System.out.print(m[i][j]+" ");
            System.out.println();
        }
    }
	public static void main(String[] args) {
		long start=System.currentTimeMillis(); 
		getSudokuAtLevel(4);
		long timeused=System.currentTimeMillis()-start; 
		print(getSudoku());
		System.out.println("=================");
		print(getAns());
		System.out.println("time used: "+timeused);
	}
}
