package sct.Lib;

import java.io.Serializable;

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
	
//	public boolean isFix(){return type==Type.fix;}
//	public boolean isGuess(){return type==Type.guess;}
//	public boolean isMark(){return type==Type.mark;}
//	public void setFix(){type=Type.fix;}
//	public void setGuess(){type=Type.guess;}
//	public void setMark(){type=Type.mark;}
//	
//	public boolean isWhite(){return bg==Bg.white;}
//	public boolean isBlue(){return bg==Bg.blue;}
//	public boolean isGray(){return bg==Bg.gray;}
//	public void setWhite(){bg=Bg.white;}
//	public void setBlue(){bg=Bg.blue;}
//	public void setGray(){bg=Bg.gray;}

	public Type gettype() { return type; }
	public void settype(Type type) { this.type = type; }
	public Bg getbg() { return bg; }
	public void setbg(Bg bg) { this.bg = bg; }
	
	public int getNum() { return num; }
	public Type getType() {
		return type;
	}

	public void setNum(int num) { this.num = num; }

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