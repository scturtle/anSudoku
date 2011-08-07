package sct.Lib;

/*
 * Dancing links alogrithm
 */
public class Dlx{
	static int MAXR=9*9*9+10;
	static int MAXC=9*9*4+10;
	static int SIZE=MAXR*MAXC;
	static int[] 
			L=new int[SIZE], R=new int[SIZE],
			U=new int[SIZE], D=new int[SIZE],
			Col=new int[SIZE], Row=new int[SIZE],
			S=new int[MAXC], sel=new int[MAXR];
	static int seln,count;

	static void  insCol(int c,int p) {
		U[D[c]]=p; U[p]=c; D[p]=D[c];
		D[c]=p;
		S[c]++; Col[p]=c;
	}

	static void init(int[][] m)
	{
		//0~8 => 1~9
		int[][] su=new int[10][10];
		for(int i=0;i<9;i++) for(int j=0;j<9;j++)
			su[i+1][j+1]=m[i][j];
		
		int i,j,k;
		for (i=1;i<=9*9*4;i++)
		{
			L[i]=i-1; R[i]=i+1;
			U[i]=D[i]=i;
			S[i]=0;
		}
		int p=9*9*4;

		for(i=1;i<=9;i++)
			for(j=1;j<=9;j++)
			{
				int b=(i-1)/3*3+(j-1)/3+1;
				if(su[i][j]==0)
				{
					for(k=1;k<=9;k++)
					{
						for(int l=1;l<=4;l++)
						{
							L[p+l]=p+l-1;
							R[p+l]=p+l+1;
							Row[p+l]=10*10*i+10*j+k;
						}
						L[p+1]=p+4;R[p+4]=p+1;
						insCol(0*9*9 + 9*(i-1)+j ,p+1);
						insCol(1*9*9 + 9*(i-1)+k ,p+2);
						insCol(2*9*9 + 9*(j-1)+k ,p+3);
						insCol(3*9*9 + 9*(b-1)+k ,p+4);
						p+=4;
					}
				}
				else
				{
					k=su[i][j];
					for(int l=1;l<=4;l++)
					{
						L[p+l]=p+l-1;
						R[p+l]=p+l+1;
						Row[p+l]=10*10*i+10*j+k;
					}
					L[p+1]=p+4;R[p+4]=p+1;
					insCol(0*9*9 + 9*(i-1)+j ,p+1);
					insCol(1*9*9 + 9*(i-1)+k ,p+2);
					insCol(2*9*9 + 9*(j-1)+k ,p+3);
					insCol(3*9*9 + 9*(b-1)+k ,p+4);
					p+=4;
				}
			}
		R[9*9*4] = 0; L[0] = 9*9*4; R[0] = 1;
		S[0] = 0x7fffffff;
		count=0;seln=0;
	}

	static void remove(int c) 
	{
		L[R[c]] = L[c]; R[L[c]] = R[c];
		for (int i = D[c]; i != c; i = D[i])
			for (int j = R[i]; j != i; j = R[j])
			{
				U[D[j]] = U[j];
				D[U[j]] = D[j];
				--S[Col[j]];
			}
	}

	static void resume(int c) 
	{
		for (int i = U[c]; i != c; i = U[i]) 
			for (int j = L[i]; j != i; j = L[j]) 
			{
				U[D[j]] = j;
				D[U[j]] = j;
				++S[Col[j]];
			}
		L[R[c]] = c; R[L[c]] = c;
	}

	//ref to getAnswer()
	static boolean dance(){
		if (R[0] == 0) {
//			printSolution(getSolution());
			return true;
		}
		int c=0, i, j;
		for (i = R[0]; i != 0; i = R[i])
			if (S[i] < S[c]) c = i;
		remove(c);
		for (i = D[c]; i != c; i = D[i])
		{
			for (j = R[i]; j != i; j = R[j])
				remove(Col[j]);
			sel[seln]=Row[i];
			seln++;
			if(dance()) return true;
			seln--;
			for (j = L[i]; j != i; j = L[j])
				resume(Col[j]);
		}
		resume(c); return false;
	}

	//ref to getCount()
	static void danceCheck() {
		if (R[0] == 0) {
			count++; return;
		}
		int c=0, i, j;
		for (i = R[0]; i != 0; i = R[i])
			if (S[i] < S[c]) c = i;
		remove(c);
		for (i = D[c]; i != c; i = D[i])
		{
			for (j = R[i]; j != i; j = R[j])
				remove(Col[j]);
			sel[seln]=Row[i];
			seln++;
			danceCheck();
			if(count>1) return;
			seln--;
			for (j = L[i]; j != i; j = L[j])
				resume(Col[j]);
		}
		resume(c);
	}

	// change the input sudoku to the answer
	static int[][] getSolution(int m[][]){
		for(int h=0;h<seln;h++)
		{
			int t=sel[h];
			int k=t%10; t/=10;
			int j=t%10; t/=10;
			int i=t%10;
			m[i-1][j-1]=k;
		}
		return m;
	}

	static void printSolution(int[][] m){
		for(int i=0;i<9;i++)
		{
			for(int j=0;j<9;j++)
				System.out.print(m[i][j]+" ");
			System.out.println();
		}
	}

	/*
	 * function: solve a sudoku with holes
	 * input: sudoku with holes
	 * output: boolean the if the sudoku is solvable
	 * side effect: run getSolution () and m will be the answer
	 */
	public static boolean getAnswer(int[][] m){
		init(m);
		boolean ans=dance();
		if(ans)
			getSolution(m);
		return ans;
	}

	/*
	 * function: check if the sudoku is of unique answer
	 * input: sudoku with holes
	 * output: 0 or 1 or 2(means 2+)
	 * side effect: then getSolution () is callable()
	 */
	public static int getCount(int[][] m){
		init(m);
		danceCheck();
		return count;
	}
	
	public static void main(String args[]){
		String s="..1..4.......6.3.5...9.....8.....7.3.......285...7.6..3...8...6..92......4...1...";
		int m[][]=new int[9][9];
		for(int i=0;i<9;i++) 
			for(int j=0;j<9;j++)
				if(s.charAt(i*9+j)=='.')
					m[i][j]=0;
				else
					m[i][j]=s.charAt(i*9+j)-'0';

		long start=System.currentTimeMillis(); 
		init(m);
		dance();
		long end=System.currentTimeMillis(); 
		System.out.printf("count:%d %d ms\n",count,end-start);
		int[][] ans=new int[9][9];
		getSolution(ans);
		printSolution(ans);
	}
}