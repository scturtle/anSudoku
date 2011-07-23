package sct.Lib;
import java.io.Serializable;

public class Sudoku implements Serializable{
	private static final long serialVersionUID = -4893465166415304143L;
	public Unit unit[][]=new Unit[9][9];
	
	public Sudoku(){
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++)
				unit[i][j]=new Unit();
	}
}