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
		
	public void p(){
		char[][] c=new char[27][27];
		for(int i=0;i<27;i++) for(int j=0;j<27;j++)c[i][j]=' ';
		for(int i=0;i<9;i++){
			for(int j=0;j<9;j++){
				int num=cell[i][j].getNum();
				int cbi=i*3,cbj=j*3;
				if(cell[i][j].isMarkType()){
					for(int n=0,ci=cbi;ci<cbi+3;ci++)
						for(int cj=cbj;cj<cbj+3;cj++){
							++n; if(cell[i][j].isMark(n)) c[ci][cj]=(char) (n+'0');
						}
				}
				else if (num>0){
					for(int ci=cbi;ci<cbi+3;ci++)
						for(int cj=cbj;cj<cbj+3;cj++)
							if(ci%3==1 && cj%3==1)
								c[ci][cj]=(char) (num+'0');
							else if(ci%3==1) c[ci][cj]='|';
							else if(cj%3==1) c[ci][cj]='-';
							else c[ci][cj]='+';
				}
			}
		}
		for(int i=0;i<27;i++){
			for(int j=0;j<27;j++){
				System.out.print(c[i][j]);
				System.out.print(' ');
				if(j==8||j==17) System.out.print("| ");
			}
			System.out.print('\n');
			if(i==8||i==17) {
				for(int t=0;t<57;t++) System.out.print('-');
				System.out.print('\n');
			}
		}
		System.out.println("==================");
	}
}