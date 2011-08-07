package sct.Lib;
import java.util.Random;

public class RandomFullSudoku{
	static int randomTimes=9;
	/*
	 * function: get a random fullsudoku
	 */
	public static int[][] getSudoku(){
		Random r=new Random();
		int[][] m=new int[9][9];
		int[] n={1,2,3,4,5,6,7,8,9};
		int[] s={0,3,6};
		// random the diagonal houses first
		for(int c=0;c<3;c++)
		{
			for(int i=0;i<randomTimes;i++)//shuffle
			{
				int a=r.nextInt(9),b=r.nextInt(9);
				int t=n[a];n[a]=n[b];n[b]=t;
			}
			for(int i=0;i<9;i++)//set
				m[s[c]+i/3][s[c]+i%3]=n[i];
		}
//		print(m);
		Dlx.getAnswer(m);// then get full sudoku by dlxing it
//		print(m);
		return m;
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
    
	//test function
	public static void main(String args[]){
		int[][] m=getSudoku();
		print(m);
	}
}