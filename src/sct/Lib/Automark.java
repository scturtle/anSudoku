package sct.Lib;

public class Automark {
	/*
	 * function: automark a cell
	 */
	public static void automarkCell(Sudoku sudoku,int oi,int oj){
		for(int k=1;k<=9;k++)
			sudoku.cell[oi][oj].setMark(k);
		
		for(int i=0;i<9;i++) for(int j=0;j<9;j++)
			if(i==oj || j==oj || (i<=oi && oi<i+3 && j<=oj && oj<j+3))
				if(sudoku.cell[i][j].isOnNum())
					sudoku.cell[oi][oj].clearMark(sudoku.cell[i][j].getNum());
	}
	/*
	 * function: automark all cell ,called by push automark button
	 */
	public static void automarkAll(Sudoku sudoku){
		for(int i=0;i<9;i++) for(int j=0;j<9;j++)
			if(!sudoku.cell[i][j].isOnNum()){
				sudoku.cell[i][j].setMarkType();
				for(int k=1;k<=9;k++)
					sudoku.cell[i][j].setMark(k);
			}
		for(int i=0;i<9;i++) for(int j=0;j<9;j++)
			if(sudoku.cell[i][j].isOnNum()){
				int num=sudoku.cell[i][j].getNum();
				
				for(int t=0;t<9;t++){//row and column
					if(t!=j) sudoku.cell[i][t].clearMark(num);
					if(t!=i) sudoku.cell[t][j].clearMark(num);
				}
				//house
				int bi=i/3*3,bj=j/3*3;
				for(int ti=0;ti<3;ti++) for(int tj=0;tj<3;tj++)
					if(ti!=i || tj!=j)
						sudoku.cell[bi+ti][bj+tj].clearMark(num);
			}
	}
	/*
	 * function:(After set guess,) auto unmark the around
	 */
	public static void automarkSet(Sudoku sudoku,int oi,int oj,int num){
		int bi=oi/3*3,bj=oj/3*3;
		for(int i=0;i<3;i++) for(int j=0;j<3;j++)
			sudoku.cell[bi+i][bj+j].clearMark(num);
		for(int t=0;t<9;t++){ 
			sudoku.cell[oi][t].clearMark(num);
			sudoku.cell[t][oj].clearMark(num);
		}
	}
	/*
	 * function:(After clear the origin guess,) auto ununmark the around
	 */
	public static void automarkClear(Sudoku sudoku,int oi,int oj,int num){
		int bi=oi/3*3,bj=oj/3*3;
		for(int i=0;i<9;i++) for(int j=0;j<9;j++)
			if(oi==i || oj==j || (bi<=i && i<bi+3 && bj<=j && j<bj+3)){ //row or column or house
				if(sudoku.cell[i][j].isMarkType()){ //is mark and check num can be marked
					int tbi=i/3*3,tbj=j/3*3;
					boolean foundNum=false;
					for(int ti=0;ti<9;ti++) for(int tj=0;tj<9;tj++)
						if(i==ti || j==tj || (tbi<=ti && ti<tbi+3 && tbj<=tj && tj<tbj+3)) //row or column or house
						if( sudoku.cell[ti][tj].isOnNum() && sudoku.cell[ti][tj].getNum()==num )
							foundNum=true;
					if(!foundNum) sudoku.cell[i][j].setMark(num);
				}
			}
	}
	/*
	 * function: clear all mark
	 */
	public static void clearAllMark(Sudoku sudoku){
		for(int i=0;i<9;i++) for(int j=0;j<9;j++)
			for(int k=1;k<=9;k++)
			sudoku.cell[i][j].clearMark(k);
	}
}
