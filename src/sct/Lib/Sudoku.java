package sct.Lib;

public class Sudoku{
	public Unit unit[][]=new Unit[9][9];
	
	public Sudoku(){
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++)
				unit[i][j]=new Unit();
	}
}