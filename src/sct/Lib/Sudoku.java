package sct.Lib;
import java.io.Serializable;

/*
 * a collection of 81 cell for storing info of them to show in view
 */
public class Sudoku implements Serializable{
	private static final long serialVersionUID = -4893465166415304143L;
	public Cell cell[][]=new Cell[9][9];
	
	public Sudoku(){
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++)
				cell[i][j]=new Cell();
	}
}