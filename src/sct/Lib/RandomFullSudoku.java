package sct.Lib;
import java.util.Random;

public class RandomFullSudoku{
	static int randomTimes=5;
	public static int[][] getSudoku(){
		Random r=new Random();
		int[][] m=new int[10][10];
		int[] n={1,2,3,4,5,6,7,8,9};
		int[] s={1,4,7};
		for(int c=0;c<3;c++)
		{
			for(int i=0;i<randomTimes;i++)
			{
				int a=r.nextInt(9),b=r.nextInt(9);
				int t=n[a];n[a]=n[b];n[b]=t;
			}
			for(int i=0;i<9;i++)
				m[s[c]+i/3][s[c]+i%3]=n[i];
		}
//		print(m);
		Dlx.getAnswer(m);
//		print(m);
		return m;
	}
	
    static void print(int[][] m){
        for(int i=1;i<=9;i++)
        {
            for(int j=1;j<=9;j++)
                System.out.print(m[i][j]+" ");
            System.out.println();
        }
    }
    
	public static void main(String args[]){
		getSudoku();
	}
}