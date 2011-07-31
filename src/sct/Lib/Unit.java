package sct.Lib;

import java.io.Serializable;

/*
 * store the info of per unit (81 in total)
 */
public class Unit implements Serializable{
	private static final long serialVersionUID = -3770040309571770996L;

	public enum Type { fix,guess,mark };
	public enum Bg { white,blue,gray };
	Type type;
	Bg bg;
	int num;
	int mark;
	
	public Unit(){
		type=Type.guess;
		bg=Bg.white; 
		num=0;
		mark=0;
	}
	
	public boolean isFixType(){return type==Type.fix;}
	public boolean isGuessType(){return type==Type.guess;}
	public boolean isMarkType(){return type==Type.mark;}
	public void setFixType(){type=Type.fix;}
	public void setGuessType(){type=Type.guess;}
	public void setMarkType(){type=Type.mark;}

	public Bg getbg() { return bg; }
	public void setbg(Bg bg) { this.bg = bg; }
	
	public int getNum() { return num; }
	public Type getType() {
		return type;
	}

	public void setNum(int num) { // side effect: clear all mark
		this.num = num; 
		mark=0;
	}

	public boolean isOnNum(){ // there is a number on it, useful
		return type==Type.fix || (type==Type.guess && num!=0);
	}
	public boolean isMark(int n) {
		if(n>=1 && n<=9)
			return ( mark & (1<<n) ) > 0;
		else 
			return false;
	}

	public void setMark(int n) {
		mark=mark | (1<<n);
	}
	
	public void clearMark(int n){
		mark=mark & (0xfff ^ (1<<n));
	}
	
}