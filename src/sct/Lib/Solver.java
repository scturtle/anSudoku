
package sct.Lib;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Solver {
	
	// row,column,block need only one
	static boolean fullHouse(Sudoku s,int[] ans){
		int[] ic=new int[9]; int[] jc=new int[9]; int[] bc=new int[9];
		for(int i=0;i<9;i++) for(int j=0;j<9;j++)
			if(s.cell[i][j].isOnNum()){
				ic[i]++; jc[j]++; bc[i/3*3+j/3]++;
			}
		boolean[] on=new boolean[10];
		for(int k=0;k<9;k++){
			if(ic[k]==8){
				ans[0]=k;
				for(int j=0;j<9;j++)
					if(s.cell[k][j].isOnNum())
						on[s.cell[k][j].getNum()]=true;
					else ans[1]=j;
				for(int n=1;n<=9;n++)
					if(on[n]==false) ans[2]=n;
				return true;
			}else if(jc[k]==8){
				ans[1]=k;
				for(int i=0;i<9;i++)
					if(s.cell[i][k].isOnNum())
						on[s.cell[i][k].getNum()]=true;
					else ans[0]=i;
				for(int n=1;n<=9;n++)
					if(on[n]==false) ans[2]=n;
				return true;
			}else if(bc[k]==8){
				int bi=k/3*3,bj=k%3*3;
				for(int i=bi;i<bi+3;i++) for(int j=bj;j<bj+3;j++)
					if(s.cell[i][j].isOnNum())
						on[s.cell[i][j].getNum()]=true;
					else {ans[0]=i;ans[1]=j;}
				for(int n=1;n<=9;n++)
					if(on[n]==false) ans[2]=n;
				return true;
			}
		}
		return false;
	}
	// at i j,its row colum block need only one
	public static boolean nakedSingle(Sudoku s,int[] ans){
		//init mark
		int[][] mark=new int[9][9];
		for(int i=0;i<9;i++) for(int j=0;j<9;j++)
			if(s.cell[i][j].isOnNum()==false)
				mark[i][j]=0x3fe;// 11 1111 1110
		//unmark all
		for(int i=0;i<9;i++) for(int j=0;j<9;j++)
			if(s.cell[i][j].isOnNum()){
				int n=s.cell[i][j].getNum();
				//row column
				for(int k=0;k<n;k++){
					mark[i][k]&=~(1<<n);
					mark[k][j]&=~(1<<n);
				}
				//block
				int bi=i/3*3,bj=j/3*3;
				for(int ti=bi;ti<bi+3;ti++) for(int tj=bj;tj<bj+3;tj++)
					mark[ti][tj]&=~(1<<n);
			}
		//check single
		for(int i=0;i<9;i++) for(int j=0;j<9;j++){
			int m=mark[i][j];
			if(m!=0 && ((m&(m-1))==0)){// there is single 1 in m
				ans[0]=i;ans[1]=j;
				for(int n=1;n<=9;n++)
					if((m&(1<<n))!=0) {ans[2]=n;break;}
				return true;
			}
		}
		return false;
	}
	
	public static boolean hiddenSingle(Sudoku s,int[] ans){
		boolean[][] fit=new boolean[9][9];
		for(int n=1;n<=9;n++){
			//init
			for(int i=0;i<9;i++) for(int j=0;j<9;j++)
				fit[i][j]=true;
			//unmark
			for(int i=0;i<9;i++) for(int j=0;j<9;j++)
				if(s.cell[i][j].isOnNum()){
					fit[i][j]=false;
					if(s.cell[i][j].getNum()==n){
						for(int k=0;k<9;k++)
							fit[i][k]=fit[k][j]=false;
						int bi=i/3*3,bj=j/3*3;
						for(int ti=bi;ti<bi+3;ti++) for(int tj=bj;tj<bj+3;tj++)
							fit[ti][tj]=false;
					}
				}
			ans[2]=n;
			int c;
			//check
			for(int i=0;i<9;i++){
				ans[0]=i; c=0;
				for(int j=0;j<9;j++)
					if(fit[i][j]) {c++;ans[1]=j;}
				if(c==1) return true;
			}
			for(int j=0;j<9;j++){
				ans[1]=j; c=0;
				for(int i=0;i<9;i++)
					if(fit[i][j]) {c++;ans[0]=i;}
				if(c==1) return true;
			}
			for(int b=0;b<9;b++){
				int bi=b/3*3,bj=b%3*3;
				c=0;
				for(int i=bi;i<bi+3;i++) for(int j=bj;j<bj+3;j++)
					if(fit[i][j]){c++;ans[0]=i;ans[1]=j;}
				if(c==1) return true;
			}
		}
		return false;
	}

	public static boolean checkMark(Sudoku s,int[] ans){
		for(int i=0;i<9;i++) for(int j=0;j<9;j++) if(s.cell[i][j].isMarkType()){
			ans[0]=i;ans[1]=j;
			int mark=s.cell[i][j].mark;
			if(mark!=0 && ((mark&(mark-1))==0)){// only one 1
				for(int n=1;n<=9;n++)
					if((mark&(1<<n))!=0)
					{ans[2]=n;return true;}
			}
		}
		for(int n=1;n<=9;n++){
			ans[2]=n;
			//column
			for(int i=0;i<9;i++){
				int c=0; ans[0]=i;
				for(int j=0;j<9;j++)
						if(s.cell[i][j].isMark(n)) {c++;ans[1]=j;}
				if(c==1) return true;
			}
			//row
			for(int j=0;j<9;j++){
				int c=0; ans[1]=j;
				for(int i=0;i<9;i++)
						if(s.cell[i][j].isMark(n)) {c++;ans[0]=i;}
				if(c==1) return true;
			}
			//block
			for(int bi=0;bi<9;bi+=3) for(int bj=0;bj<9;bj+=3){
				int c=0;
				for(int i=bi;i<bi+3;i++) for(int j=bj;j<bj+3;j++)
						if(s.cell[i][j].isMark(n)) {c++;ans[0]=i;ans[1]=j;}
				if(c==1) return true;
			}
		}
		return false;
	}
	
	static boolean nakedPair(Sudoku s){
		for(int ai=0;ai<9;ai++) for(int aj=0;aj<9;aj++){
			if(s.cell[ai][aj].isMarkType()==false) continue;
			int ca=0;// count of marks of a
			int[] ma=new int[3];
			for(int n=1;n<=9 && ca<=2;n++)
				if(s.cell[ai][aj].isMark(n)) ma[ca++]=n;
			if(ca!=2) continue;
			//found a then find b
			int cb=0;// count of marks of a
			int[] mb=new int[3];
			for(int bi=0;bi<9;bi++) if(bi!=ai){// column
				if(s.cell[bi][aj].isMarkType()==false) continue;
				cb=0; for(int n=1;n<=9 && cb<=2;n++)
					if(s.cell[bi][aj].isMark(n)) mb[cb++]=n;
				if(cb!=2) continue;
				if(mb[0]==ma[0] && mb[1]==ma[1]){//found b
					boolean unmark=false;
					for(int ti=0;ti<9;ti++) 
						if(ti!=ai && ti!=bi) if(s.cell[ti][aj].isMarkType()){
							for(int k=0;k<2;k++)
								if(s.cell[ti][aj].isMark(ma[k]))
								{s.cell[ti][aj].clearMark(ma[k]);unmark=true;}
						}
					if(unmark) return true;
				}
			}
			for(int bj=0;bj<9;bj++) if(bj!=aj){// row
				if(s.cell[ai][bj].isMarkType()==false) continue;
				cb=0; for(int n=1;n<=9 && cb<=2;n++)
					if(s.cell[ai][bj].isMark(n)) mb[cb++]=n;
				if(cb!=2) continue;
				if(mb[0]==ma[0] && mb[1]==ma[1]){//found b
					boolean unmark=false;
					for(int tj=0;tj<9;tj++) 
						if(tj!=aj && tj!=bj) if(s.cell[ai][tj].isMarkType()){
							for(int k=0;k<2;k++)
								if(s.cell[ai][tj].isMark(ma[k]))
								{s.cell[ai][tj].clearMark(ma[k]);unmark=true;}
						}
					if(unmark) return true;
				}
			}
			// block
			int basei=ai/3*3,basej=aj/3*3;
			for(int bi=basei;bi<basei+3;bi++) for(int bj=basej;bj<basej+3;bj++)
				if(bi!=ai || bj!=aj){
					if(s.cell[bi][bj].isMarkType()==false) continue;
					cb=0; for(int n=1;n<=9 && cb<=2;n++)
						if(s.cell[bi][bj].isMark(n)) mb[cb++]=n;
					if(cb!=2) continue;
					if(mb[0]==ma[0]&&mb[1]==ma[1]){//found b
						boolean unmark=false;
						for(int ti=basei;ti<basei+3;ti++) for(int tj=basej;tj<basej+3;tj++){
							if((ti==ai && tj==aj)||(ti==bi && tj==bj)) continue;
							if(s.cell[ti][tj].isMarkType()){
							for(int k=0;k<2;k++)
								if(s.cell[ti][tj].isMark(ma[k]))
								{s.cell[ti][tj].clearMark(ma[k]);unmark=true;}
							}
						}
						if(unmark) return true;
					}
				}
		}
		return false;
	}
	
	static boolean hiddenPair(Sudoku s){
		for(int a=1;a<=9;a++)
			for(int b=a+1;b<=9;b++){
				int[] ma=new int[3];
				int[] mb=new int[3];
				//row
				int T=(1<<a)|(1<<b);
				for(int i=0;i<9;i++){
					int ca=0,cb=0;
					for(int j=0;j<9 && ca<=2 && cb<=2;j++){
							if(s.cell[i][j].isMark(a)) ma[ca++]=j;
							if(s.cell[i][j].isMark(b)) mb[cb++]=j;
					}
					boolean unmarked=false;
					if(ca==2 && cb==2 && ma[0]==mb[0] && ma[1]==mb[1]){//found
						for(int t=0;t<2;t++)
							if(s.cell[i][ma[t]].mark!=T)
							{s.cell[i][ma[t]].mark=T;unmarked=true;}
					}
					if(unmarked) return true;
				}
				//column
				for(int j=0;j<9;j++){
					int ca=0,cb=0;
					for(int i=0;i<9 && ca<=2 && cb<=2;i++){
							if(s.cell[i][j].isMark(a)) ma[ca++]=i;
							if(s.cell[i][j].isMark(b)) mb[cb++]=i;
						}
					boolean unmarked=false;
					if(ca==2 && cb==2 && ma[0]==mb[0] && ma[1]==mb[1]){//found
						for(int t=0;t<2;t++)
							if(s.cell[ma[t]][j].mark!=T)
							{s.cell[ma[t]][j].mark=T;unmarked=true;}
					}
					if(unmarked) return true;
				}
				//block
				for(int bi=0;bi<9;bi+=3) for(int bj=0;bj<9;bj+=3){
					int ca=0,cb=0;
					for(int k=0;k<9 && ca<=2 && cb<=2;k++){
							if(s.cell[bi+k/3][bj+k%3].isMark(a)) ma[ca++]=k;
							if(s.cell[bi+k/3][bj+k%3].isMark(b)) mb[cb++]=k;
						}
					boolean unmarked=false;
					if(ca==2 && cb==2 && ma[0]==mb[0] && ma[1]==mb[1]){//found
						for(int t=0;t<2;t++)
							if(s.cell[bi+ma[t]/3][bj+ma[t]%3].mark!=T)
							{s.cell[bi+ma[t]/3][bj+ma[t]%3].mark=T;unmarked=true;}
					}
					if(unmarked) return true;
				}

			}
		return false;
	}

    //1.Sometimes a candidate within a box is restricted to one row or column.	
	//2.Sometimes a candidate within a row or column is restricted to one box. 
	static boolean lockedCandidates(Sudoku s){
		for(int n=1;n<=9;n++){
			//row
			for(int i=0;i<9;i++){
				boolean[] c=new boolean[3]; c[0]=c[1]=c[2]=false;
				for(int j=0;j<9;j++)
					if(s.cell[i][j].isMark(n)) c[j/3]=true;
				int bi=i/3*3,bj=-1;
				if(c[0]&&!c[1]&&!c[2]) bj=0;
				else if(!c[0]&&c[1]&&!c[2]) bj=3;
				else if(!c[0]&&!c[1]&&c[2]) bj=6;
				if(bj==-1) continue;//not found
				boolean unmark=false;
				for(int ti=0;ti<3;ti++) for(int tj=0;tj<3;tj++) if(bi+ti!=i)//unmark
					if(s.cell[bi+ti][bj+tj].isMark(n))
						{s.cell[bi+ti][bj+tj].clearMark(n);unmark=true;}
				if(unmark) return true;
			}
			//column
			for(int j=0;j<9;j++){
				boolean[] r=new boolean[3]; r[0]=r[1]=r[2]=false;
				for(int i=0;i<9;i++)
					if(s.cell[i][j].isMark(n)) r[i/3]=true;
				int bi=-1,bj=j/3*3;
				if(r[0]&&!r[1]&&!r[2]) bi=0;
				else if(!r[0]&&r[1]&&!r[2]) bi=3;
				else if(!r[0]&&!r[1]&&r[2]) bi=6;
				if(bi==-1) continue;//not found
				boolean unmark=false;
				for(int ti=0;ti<3;ti++) for(int tj=0;tj<3;tj++) if(bj+tj!=j)//unmark
					if(s.cell[bi+ti][bj+tj].isMark(n))
						{s.cell[bi+ti][bj+tj].clearMark(n);unmark=true;}
				if(unmark) return true;
			}
			//block
			for(int bi=0;bi<9;bi+=3) for(int bj=0;bj<9;bj+=3){
				boolean[] r=new boolean[3];r[0]=r[1]=r[2]=false;
				boolean[] c=new boolean[3];c[0]=c[1]=c[2]=false;
				//count
				for(int i=0;i<3;i++)for(int j=0;j<3;j++)
					if(s.cell[bi+i][bj+j].isMark(n))
					{r[i]=true;c[j]=true;}
				int ti,tj;
				//row
				ti=-1;
				if(r[0]&&!r[1]&&!r[2]) ti=bi;
				else if(!r[0]&&r[1]&&!r[2]) ti=bi+1;
				else if(!r[0]&&!r[1]&&r[2]) ti=bi+2;
				if(ti!=-1){
					boolean unmark=false;
					for(tj=0;tj<9;tj++) if(!(bj<=tj && tj<bj+3))
						if(s.cell[ti][tj].isMark(n))
						{s.cell[ti][tj].clearMark(n);unmark=true;}
					if(unmark) return true;
				}
				//column
				tj=-1;
				if(c[0]&&!c[1]&&!c[2]) tj=bj;
				else if(!c[0]&&c[1]&&!c[2]) tj=bj+1;
				else if(!c[0]&&!c[1]&&c[2]) tj=bj+2;
				if(tj!=-1){
					boolean unmark=false;
					for(ti=0;ti<9;ti++) if(!(bi<=ti && ti<bi+3))
						if(s.cell[ti][tj].isMark(n))
						{s.cell[ti][tj].clearMark(n);unmark=true;}
					if(unmark) return true;
				}
			}
		}
		return false;
	}
	
	static boolean nakedTripleQuad(int noc,Sudoku s){
		int[] k=new int[4];
		for(k[0]=1;k[0]<=9;k[0]++) for(k[1]=k[0]+1;k[1]<=9;k[1]++) for(k[2]=k[1]+1;k[2]<=9;k[2]++){
			int top; if(noc==3) top=k[2]+1; else top=9;//if Triple then do not loop
			for(k[3]=k[2]+1;k[3]<=top;k[3]++){
				//row
				int T=0x3fe;for(int i=0;i<noc;i++) T^=1<<k[i];
				for(int i=0;i<9;i++){
					int count=0;int[] m=new int[5];
					for(int j=0;j<9 && count<=noc;j++) if(s.cell[i][j].isMarkType()){
						if((s.cell[i][j].mark & T) ==0) m[count++]=j;
					}
					if(count==noc){//clear
						boolean unmark=false;
						for(int j=0;j<9;j++){
							boolean j_in_m=false;
							for(int t=0;t<noc;t++) if(j==m[t]) j_in_m=true;
							if(!j_in_m) for(int t=0;t<noc;t++)
								if(s.cell[i][j].isMark(k[t]))
								{s.cell[i][j].clearMark(k[t]);unmark=true;}//clear
						}
						if(unmark) return true;
					}
				}
				//column
				for(int j=0;j<9;j++){
					int count=0;int[] m=new int[5];
					for(int i=0;i<9 && count<=noc;i++) if(s.cell[i][j].isMarkType()){
						if((s.cell[i][j].mark & T) ==0) m[count++]=i;
					}
					if(count==noc){//clear
						boolean unmark=false;
						for(int i=0;i<9;i++){
							boolean i_in_m=false;
							for(int t=0;t<noc;t++) if(i==m[t]) i_in_m=true;
							if(!i_in_m) for(int t=0;t<noc;t++)
								if(s.cell[i][j].isMark(k[t]))
								{s.cell[i][j].clearMark(k[t]);unmark=true;}//clear
						}
						if(unmark) return true;
					}
				}
				//block
				for(int bi=0;bi<9;bi+=3) for(int bj=0;bj<9;bj+=3){
					int count=0;int[] m=new int[5];
					for(int b=0;b<9 && count<=noc;b++) if(s.cell[bi+b/3][bj+b%3].isMarkType()){
						if((s.cell[bi+b/3][bj+b%3].mark & T) ==0) m[count++]=b;
					}
					if(count==noc){
						boolean unmark=false;
						for(int b=0;b<9;b++){
							boolean b_in_m=false;
							for(int t=0;t<noc;t++) if(b==m[t]) b_in_m=true;
							if(!b_in_m) for(int t=0;t<noc;t++)
								if(s.cell[bi+b/3][bj+b%3].isMark(k[t]))
								{s.cell[bi+b/3][bj+b%3].clearMark(k[t]);unmark=true;}
						}
						if(unmark) return true;
					}
				}
			}
		}
		return false;
	}
	
	//NEED TEST
	static boolean hiddenTripleQuad(int noc,Sudoku s){
		int[] k=new int[4];
		for(k[0]=1;k[0]<=9;k[0]++) for(k[1]=k[0]+1;k[1]<=9;k[1]++) for(k[2]=k[1]+1;k[2]<=9;k[2]++){
			int top; if(noc==3) top=k[2]+1; else top=9;//if Triple then do not loop
			for(k[3]=k[2]+1;k[3]<=top;k[3]++){
				//row
				for(int i=0;i<9;i++){
					int c=0;int[] m=new int[5];
					boolean[] show=new boolean[noc];//all k should show
					for(int j=0;j<9 && c<=noc;j++){
						boolean k_in_mark=false;
						for(int t=0;t<noc;t++)
							if(s.cell[i][j].isMark(k[t])) 
								{k_in_mark=true;show[t]=true;}
						if(k_in_mark) m[c++]=j;
					}
					int kt;for(kt=0;kt<noc;kt++) if(!show[kt]) break;
					if(kt==noc && c==noc){//found
						boolean unmark=false;
						for(int pos=0,n=1;n<=9;n++)//clear //for k[] is in incremental
							if(pos<noc && n==k[pos]){ pos++;continue; }
							else for(int t=0;t<noc;t++)
								if(s.cell[i][m[t]].isMark(n))
								{s.cell[i][m[t]].clearMark(n);unmark=true;}
						if(unmark) return true;
					}
				}
				//column
				for(int j=0;j<9;j++){
					int c=0;int[] m=new int[5];
					boolean[] show=new boolean[noc];//all k should show
					for(int i=0;i<9 && c<=noc;i++){
						boolean k_in_mark=false;
						for(int t=0;t<noc;t++)
							if(s.cell[i][j].isMark(k[t]))
								{k_in_mark=true;show[t]=true;}
						if(k_in_mark) m[c++]=i;
					}
					int kt;for(kt=0;kt<noc;kt++) if(!show[kt]) break;
					if(kt==noc && c==noc){//found
						boolean unmark=false;
						for(int pos=0,n=1;n<=9;n++)//clear //for k[] is in incremental
							if(pos<noc && n==k[pos]){ pos++;continue; }
							else for(int t=0;t<noc;t++)
								if(s.cell[m[t]][j].isMark(n))
								{s.cell[m[t]][j].clearMark(n);unmark=true;}
						if(unmark) return true;
					}
				}
				//block
				for(int bi=0;bi<9;bi+=3) for(int bj=0;bj<9;bj+=3){
					int c=0;int[] m=new int[5];
					boolean[] show=new boolean[noc];//all k should show
					for(int b=0;b<9 && c<=noc;b++){
						boolean k_in_mark=false;
						for(int t=0;t<noc;t++)
							if(s.cell[bi+b/3][bj+b%3].isMark(k[t]))
								{k_in_mark=true;show[t]=true;}
						if(k_in_mark) m[c++]=b;
					}
					int kt;for(kt=0;kt<noc;kt++) if(!show[kt]) break;
					if(kt==noc && c==noc){//found
						boolean unmark=false;
						for(int pos=0,n=1;n<=9;n++)//clear //for k[] is in incremental
							if(pos<noc && n==k[pos]){ pos++;continue; }
							else for(int t=0;t<noc;t++)
								if(s.cell[bi+m[t]/3][bj+m[t]%3].isMark(n))
								{s.cell[bi+m[t]/3][bj+m[t]%3].clearMark(n);unmark=true;}
						if(unmark) return true;
					}
				}
			}
		}
		return false;
	}
	
	static boolean xwing(Sudoku s){
		int c;int[][] m=new int[9][3];
		for(int n=1;n<=9;n++){
			//row
			c=0;
			for(int i=0;i<9;i++){
				int cn=0;
				for(int j=0;j<9 && cn<=2;j++)
					if(s.cell[i][j].isMark(n))
						m[c][cn++]=j;
				if(cn==2) {m[c][2]=i;c++;}
			}
			for(int a=0;a<c;a++) for(int b=a+1;b<c;b++)
				if(m[a][0]==m[b][0] && m[a][1]==m[b][1]){//found
					boolean unmark=false;
					for(int t=0;t<2;t++){
						int j=m[a][t];
						for(int i=0;i<9;i++)if(i!=m[a][2] && i!=m[b][2])
							if(s.cell[i][j].isMark(n))
							{s.cell[i][j].clearMark(n);unmark=true;}
					}
//					if(unmark) {p("n:"+n);p("i:"+m[a][2]);p("j0:"+m[a][0]);p("j1:"+m[a][1]);return true;}
					if(unmark) return true;
				}
			//column
			c=0;
			for(int j=0;j<9;j++){
				int cn=0;
				for(int i=0;i<9 && cn<=2;i++)
					if(s.cell[i][j].isMark(n))
						m[c][cn++]=i;
				if(cn==2) {m[c][2]=j;c++;}
			}
			for(int a=0;a<c;a++) for(int b=a+1;b<c;b++)
				if(m[a][0]==m[b][0] && m[a][1]==m[b][1]){//found
					boolean unmark=false;
					for(int t=0;t<2;t++){
						int i=m[a][t];
						for(int j=0;j<9;j++)if(j!=m[a][2] && j!=m[b][2])
							if(s.cell[i][j].isMark(n))
							{s.cell[i][j].clearMark(n);unmark=true;}
					}
//					if(unmark) {p("n:"+n);p("i:"+m[a][2]);p("j0:"+m[a][0]);p("j1:"+m[a][1]);return true;}
					if(unmark) return true;
				}
		}
		return false;
	}
	
	static boolean samehouse(int i1,int j1,int i2,int j2){
		return i1==i2 || j1==j2 || ( (i1/3*3==i2/3*3)&&(j1/3*3==j2/3*3) );
	}
	static boolean xywing(Sudoku s){
		//found all 2
		int c=0;int[] m=new int[81];int[] mn=new int[81];
		for(int i=0;i<9;i++) for(int j=0;j<9;j++) if(s.cell[i][j].isMarkType()){
			int cn=0;mn[c]=0;
			for(int n=1;n<=9 && cn<=2;n++)
				if(s.cell[i][j].isMark(n)) {cn++;mn[c]|=1<<n;}
			if(cn==2) m[c++]=i*10+j;
		}
		for(int xz=0;xz<c;xz++) for(int xy=0;xy<c;xy++) if(xz!=xy)
			if(mn[xz]!=mn[xy] && (mn[xz]&mn[xy])!=0 && samehouse(m[xz]/10,m[xz]%10,m[xy]/10,m[xy]%10)){
					int x;for(x=1;!((1<<x)==(mn[xz]&mn[xy]));x++);
					int z;for(z=1;!((1<<z)==(mn[xz]^(1<<x)));z++);
					int y;for(y=1;!((1<<y)==(mn[xy]^(1<<x)));y++);
					for(int yz=0;yz<c;yz++) if(yz!=xz && yz!=xy)
						if(mn[yz]==((1<<y)|(1<<z)) && samehouse(m[xy]/10,m[xy]%10,m[yz]/10,m[yz]%10)){
							boolean unmarked=false;
							for(int i=0;i<9;i++) for(int j=0;j<9;j++) if(s.cell[i][j].isMark(z))
								if(i*10+j!=m[xz] && i*10+j!=m[yz])
									if(samehouse(i,j,m[xz]/10,m[xz]%10)) if(samehouse(i,j,m[yz]/10,m[yz]%10)) {
										s.cell[i][j].clearMark(z);unmarked=true;
									}
							if(unmarked) return true;
						}
				}
		return false;
	}
	
	static boolean finned_xwing(Sudoku s){
		int[] m=new int[9] ,c=new int[9];
		for(int n=1;n<=9;n++){
			//row
			for(int i=0;i<9;i++){
				m[i]=0;c[i]=0;
				for(int j=0;j<9;j++) if(s.cell[i][j].isMark(n))
				{ m[i]|=1<<j;c[i]++; }
			}
			for(int i1=0;i1<9;i1++) if(c[i1]==2){
				int j1;for(j1=0;(m[i1]&(1<<j1))==0;j1++);
				int j2;for(j2=j1+1;(m[i1]&(1<<j2))==0;j2++);
				for(int i2=0;i2<9;i2++) if(i2!=i1){
					if((m[i2]&(1<<j1))!=0){
						//check block of i2 j2
						int bi=i2/3*3,bj=j2/3*3;
						boolean ok=true;
						for(int j=0;j<9;j++) if(j!=j1 && !(bj<=j && j<bj+3))
							if(s.cell[i2][j].isMark(n)) ok=false;
						if(ok){
							boolean unmarked=false;
							for(int i=bi;i<bi+3;i++) if(i!=i1 && i!=i2)
								if(s.cell[i][j2].isMark(n))
								{s.cell[i][j2].clearMark(n);unmarked=true;}
							//						System.out.printf("%d %d %d %d\n",i1,i2,j1,j2);
							//						System.out.println("00");
							if(unmarked) return true;
						}
					}
					if((m[i2]&(1<<j2))!=0){
						//check block of i2 j1
						int bi=i2/3*3,bj=j1/3*3;
						boolean ok=true;
						for(int j=0;j<9;j++) if(j!=j2 && !(bj<=j && j<bj+3))
							if(s.cell[i2][j].isMark(n)) ok=false;
						if(!ok) continue;
						boolean unmarked=false;
						for(int i=bi;i<bi+3;i++) if(i!=i1 && i!=i2)
							if(s.cell[i][j1].isMark(n))
							{s.cell[i][j1].clearMark(n);unmarked=true;}
//						System.out.println("01");
						if(unmarked) return true;
					}
				}
			}
			//row
			for(int j=0;j<9;j++){
				m[j]=0;c[j]=0;
				for(int i=0;i<9;i++) if(s.cell[i][j].isMark(n))
				{ m[j]|=1<<i;c[j]++; }
			}
			for(int j1=0;j1<9;j1++) if(c[j1]==2){
				int i1;for(i1=0;(m[j1]&(1<<i1))==0;i1++);
				int i2;for(i2=i1+1;(m[j1]&(1<<i2))==0;i2++);
				for(int j2=0;j2<9;j2++) if(j2!=j1){
					if((m[j2]&(1<<i1))!=0){
						//check block of i2 j2
						int bi=i2/3*3,bj=j2/3*3;
						boolean ok=true;
						for(int i=0;i<9;i++) if(i!=i1 && !(bi<=i && i<bi+3))
							if(s.cell[i][j2].isMark(n)) ok=false;
						if(ok){
							boolean unmarked=false;
							for(int j=bj;j<bj+3;j++) if(j!=j1 && j!=j2)
								if(s.cell[i2][j].isMark(n))
								{s.cell[i2][j].clearMark(n);unmarked=true;}
							//						System.out.println("10");
							if(unmarked) return true;
						}
					}
					if((m[j2]&(1<<i2))!=0){
						//check block of i1 j2
						int bi=i1/3*3,bj=j2/3*3;
						boolean ok=true;
						for(int i=0;i<9;i++) if(i!=i2 && !(bi<=i && i<bi+3))
							if(s.cell[i][j2].isMark(n)) ok=false;
						if(!ok) continue;
						boolean unmarked=false;
						for(int j=bj;j<bj+3;j++) if(j!=j1 && j!=j2)
							if(s.cell[i1][j].isMark(n))
							{s.cell[i1][j].clearMark(n);unmarked=true;}
//						System.out.println("11");
						if(unmarked) return true;
					}
				}
			}
		}
		return false;
	}
	
	static boolean Swordfish_Jellyfish(int N,Sudoku s){
		int[] a=new int[N],ma=new int[N],m=new int[9],pos=new int[9];
		for(int n=1;n<=9;n++){
			//row
			for(int i=0;i<9;i++){
				m[i]=0;// bit of row
				for(int j=0;j<9;j++) if(s.cell[i][j].isMark(n))
					m[i]|=1<<j;
			}
			//init
			for(int i=0;i<N;i++) {a[i]=i;ma[i]=9-N+i;}
			while(true){
				//do
				int mm=0;
				boolean marked=true;
				for(int i=0;i<N;i++)
					if(m[a[i]]==0) {marked=false;break;}
					else mm|=m[a[i]];
				int c2=0;
				for(int j=0;j<9;j++)//count of 1 in mm
					if((mm&(1<<j))!=0) pos[c2++]=j;
				if(marked && c2==N){//found
					boolean unmarked=false;
					for(int i=0;i<9;i++){
						boolean in_a=false;
						for(int k=0;k<N;k++) if(a[k]==i) {in_a=true;break;}
						if(in_a) continue;
						//unmark
						for(int k=0;k<N;k++) if(s.cell[i][pos[k]].isMark(n))
						{s.cell[i][pos[k]].clearMark(n);unmarked=true;}
					}
//					System.out.println(".n:"+n);
					if(unmarked) return true;
				}
				//next
				boolean found=false;
				for(int i=N-1;i>=0;i--)
					if(a[i]<ma[i]){
						a[i]++;
						for(int j=i+1;j<N;j++)
							a[j]=a[j-1]+1;
						found=true;
						break;
					}
				if(!found) break;
			}
			//column
			for(int j=0;j<9;j++){
				m[j]=0;// bit of row
				for(int i=0;i<9;i++) if(s.cell[i][j].isMark(n))
					m[j]|=1<<i;
			}
			//init
			for(int i=0;i<N;i++) {a[i]=i;ma[i]=9-N+i;}
			while(true){
				//do
				int mm=0;
				boolean marked=true;
				for(int j=0;j<N;j++)
					if(m[a[j]]==0) {marked=false;break;}
					else mm|=m[a[j]];
				int c2=0;
				for(int i=0;i<9;i++)//count of 1 in mm
					if((mm&(1<<i))!=0) pos[c2++]=i;
				if(marked && c2==N){//found
					boolean unmarked=false;
					for(int j=0;j<9;j++){
						boolean in_a=false;
						for(int k=0;k<N;k++) if(a[k]==j) {in_a=true;break;}
						if(in_a) continue;
						//unmark
						for(int k=0;k<N;k++) if(s.cell[pos[k]][j].isMark(n))
						{s.cell[pos[k]][j].clearMark(n);unmarked=true;}
					}
//					System.out.println(".n:"+n);
					if(unmarked) return true;
				}
				//next
				boolean found=false;
				for(int i=N-1;i>=0;i--)
					if(a[i]<ma[i]){
						a[i]++;
						for(int j=i+1;j<N;j++)
							a[j]=a[j-1]+1;
						found=true;
					}
				if(!found) break;
			}
		}
		return false;
	}
//##################################################################################################################
	static boolean checkdlx(Sudoku s){
		int[][] m=new int[10][10];
		for(int i=0;i<9;i++) for(int j=0;j<9;j++)
				m[i][j]=(s.cell[i][j].isOnNum())?s.cell[i][j].getNum():0;
		return Dlx.getCount(m)==1;
	}
	
	public static boolean check(Sudoku s){
		int mr,mc;
		for(int i=0;i<9;i++){
			mr=0;mc=0;
			for(int j=0;j<9;j++){
				mr|=1<<s.cell[i][j].getNum();
				mc|=1<<s.cell[j][i].getNum();
			}
			if(mr!=0x3fe || mc!=0x3fe) return false;
		}
		for(int b=0;b<9;b++){
			mc=0;
			int bi=b/3*3,bj=b%3*3;
			for(int i=bi;i<bi+3;i++) for(int j=bj;j<bj+3;j++)
				mc|=1<<s.cell[i][j].getNum();
			if(mc!=0x3fe) return false;
		}
		return true;
	}
	
	static boolean checkfull(Sudoku s){
		boolean full=true;
		for(int i=0;i<9 && full;i++) for(int j=0;j<9 && full;j++)
			if(s.cell[i][j].isOnNum()==false) full=false;
		return full;
	}
	
	public static boolean isLevel(int[][] m,int lev){
		Sudoku su=new Sudoku();
		for(int i=0;i<9;i++) for(int j=0;j<9;j++)
			su.cell[i][j].setNum(m[i][j]);
		
		int[] ans=new int[3]; int i,j,n;
		boolean automarked=false;
		int level=1;
		while(true){
			if(checkfull(su)) break;
			if(automarked && checkMark(su,ans)){
				i=ans[0];j=ans[1];n=ans[2];
				su.cell[i][j].setFixType();su.cell[i][j].setNum(n);
				if(automarked) Automark.automarkSet(su, i, j, n);
				continue;
			}
			if(fullHouse(su,ans)){
				i=ans[0];j=ans[1];n=ans[2];
				su.cell[i][j].setFixType();su.cell[i][j].setNum(n);
				if(automarked) Automark.automarkSet(su, i, j, n);
				continue;
			}
			if(nakedSingle(su,ans)){
				i=ans[0];j=ans[1];n=ans[2];
				su.cell[i][j].setFixType();su.cell[i][j].setNum(n);
				if(automarked) Automark.automarkSet(su, i, j, n);
				continue;
			}
			if(hiddenSingle(su,ans)){
				i=ans[0];j=ans[1];n=ans[2];
				su.cell[i][j].setFixType();su.cell[i][j].setNum(n);
				if(automarked) Automark.automarkSet(su, i, j, n);
				continue;
			}
			if(level<2){
				level=2; if(lev<level) return false;
			}
			if(!automarked) {Automark.automarkAll(su);automarked=true;}
			if(nakedPair(su)){ continue; }
			if(hiddenPair(su)){ continue; }
			if(lockedCandidates(su)){ continue; }
			if(level<3){
				level=3; if(lev<level) return false;
			}
			if(nakedTripleQuad(3,su)){ continue; }
//			if(nakedTripleQuad(4,su)){ continue; }
			if(hiddenTripleQuad(3,su)){ continue; }
//			if(hiddenTripleQuad(4,su)){ continue; }
			if(xwing(su)){ continue; }
			if(level<4){
				level=4; if(lev<level) return false;
			}
		}
		return level==lev;
	}
	
	public static int getLevel(int[][] m){
		Sudoku su=new Sudoku();
		for(int i=0;i<9;i++) for(int j=0;j<9;j++)
			su.cell[i][j].setNum(m[i][j]);
		
		int[] ans=new int[3]; int i,j,n;
		boolean automarked=false;
		int level=1;
		while(true){
			if(checkfull(su)) break;
			if(automarked && checkMark(su,ans)){
				i=ans[0];j=ans[1];n=ans[2];
				su.cell[i][j].setFixType();su.cell[i][j].setNum(n);
				if(automarked) Automark.automarkSet(su, i, j, n);
				continue;
			}
			if(fullHouse(su,ans)){
				i=ans[0];j=ans[1];n=ans[2];
				su.cell[i][j].setFixType();su.cell[i][j].setNum(n);
				if(automarked) Automark.automarkSet(su, i, j, n);
				continue;
			}
			if(nakedSingle(su,ans)){
				i=ans[0];j=ans[1];n=ans[2];
				su.cell[i][j].setFixType();su.cell[i][j].setNum(n);
				if(automarked) Automark.automarkSet(su, i, j, n);
				continue;
			}
			if(hiddenSingle(su,ans)){
				i=ans[0];j=ans[1];n=ans[2];
				su.cell[i][j].setFixType();su.cell[i][j].setNum(n);
				if(automarked) Automark.automarkSet(su, i, j, n);
				continue;
			}
			if(level<2) level=2;
			if(!automarked) {Automark.automarkAll(su);automarked=true;}
			if(nakedPair(su)){ continue; }
			if(hiddenPair(su)){ continue; }
			if(lockedCandidates(su)){ continue; }
			if(level<3) level=3;
			if(nakedTripleQuad(3,su)){ continue; }
//			if(nakedTripleQuad(4,su)){ continue; }
			if(hiddenTripleQuad(3,su)){ continue; }
//			if(hiddenTripleQuad(4,su)){ continue; }
			if(xwing(su)){ continue; }
			if(xywing(su)){ continue; }
			if(finned_xwing(su)){ continue; }
			if(Swordfish_Jellyfish(3,su)){ continue; }
			if(Swordfish_Jellyfish(4,su)){ continue; }
			if(level<4) level=4;
			break;
		}
		return level;
	}
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("C:\\Documents and Settings\\shenchao\\×ÀÃæ\\Levels\\Level 6.txt"));
		int zu=0,solved=0,failed=0,unvaild=0;
		int[] levelcount=new int[5];
		long timesum=0,times=0;
		while(br.ready()){
			Sudoku su=new Sudoku();
			for(int i=0;i<9;i++){
				String s=br.readLine();
				for(int j=0;j<9;j++){
					int num=s.charAt(j)-'0';
					su.cell[i][j].setNum(num);
				}
			}
			//////SKIP////////
			++zu;
//			if(zu<945) continue;
//			else if(zu>945) return;
			if(!checkdlx(su)) {unvaild++;continue;}
//			else System.out.printf("zu:%d\n",zu);
			//////SKIP////////
			int[] ans=new int[3];
			int i,j,n;
			int level=1;
			boolean automarked=false;
			while(true){
				if(checkfull(su)) break;
				if(!checkdlx(su)){
					System.out.println("WARNING!!! zu:"+zu);
					su.p();
					return;
				}
				if(automarked && checkMark(su,ans)){
					i=ans[0];j=ans[1];n=ans[2];
//										System.out.printf("Check mark: %d %d : %d\n",i,j,n);
					su.cell[i][j].setFixType();su.cell[i][j].setNum(n);
					if(automarked) Automark.automarkSet(su, i, j, n);
					continue;
				}
				if(fullHouse(su,ans)){
					i=ans[0];j=ans[1];n=ans[2];
//										System.out.printf("Full house: %d %d : %d\n",i,j,n);
					su.cell[i][j].setFixType();su.cell[i][j].setNum(n);
					if(automarked) Automark.automarkSet(su, i, j, n);
					continue;
				}
				if(nakedSingle(su,ans)){
					i=ans[0];j=ans[1];n=ans[2];
//										System.out.printf("Naked single: %d %d : %d\n",i,j,n);
					su.cell[i][j].setFixType();su.cell[i][j].setNum(n);
					if(automarked) Automark.automarkSet(su, i, j, n);
					continue;
				}
				if(hiddenSingle(su,ans)){
					i=ans[0];j=ans[1];n=ans[2];
//										System.out.printf("hidden single: %d %d : %d\n",i,j,n);
					su.cell[i][j].setFixType();su.cell[i][j].setNum(n);
					if(automarked) Automark.automarkSet(su, i, j, n);
					continue;
				}
				if(level<2) level=2;
				if(!automarked) {Automark.automarkAll(su);automarked=true;}
				if(nakedPair(su)){
					//					System.out.println("Naked Pair");
					continue;
				}
				if(hiddenPair(su)){
//										System.out.println("Hidden Pair");
					continue;
				}
				if(lockedCandidates(su)){
					//					System.out.println("Locked Candidates");
					continue;
				}
				if(level<3) level=3;
				if(nakedTripleQuad(3,su)){//NEED OPTIMIZED
//					System.out.println("Naked Triple");
					continue;
				}
				if(nakedTripleQuad(4,su)){
//					System.out.println("Naked Quad");
					continue;
				}
				if(hiddenTripleQuad(3,su)){
//					System.out.println("Hidden Triple");
					continue;
				}
				if(hiddenTripleQuad(4,su)){
//					System.out.println("Hidden Quad");
					continue;
				}
				if(xwing(su)){
//					System.out.println("XWing");
					continue;
				}
				if(xywing(su)){
//					System.out.println("XyWing");
					continue;
				}
				if(finned_xwing(su)){
//					System.out.println("Finned XWing");
					continue;
				}
//				if(zu==945) su.p();
				long start=System.currentTimeMillis(); 
				if(Swordfish_Jellyfish(3,su)){
					timesum+=System.currentTimeMillis()-start;times++;
					System.out.println("Swordfish");
//					if(zu==945) su.p();
					continue;
				}
				if(Swordfish_Jellyfish(4,su)){
					System.out.println("Jellyfish");
					continue;
				}
				if(level<4) level=4;
				break;
			}
//			long timeused=System.currentTimeMillis()-start; 
//			System.out.println(timeused+"\tms");
//			if(timeused>50) {System.out.println("TOO LONG TIME !!!");return;}
			levelcount[level]++;
			if(check(su)){
				solved++;
				//				System.out.println("------true------");
			}
			else {
				failed++;
//				su.p();
//				System.out.println("Fail on:"+zu);
//				System.out.println("++++++++false++++++++");
//				su.p();
//				return;
			}
		}
		System.out.printf("times:%d time:%d per:%f\n",times,timesum,timesum*1.0/times);
		System.out.printf("Solved/failed/unvaild/all:%d/[%d]/%d/%d\n",solved,failed,unvaild,zu);
		System.out.printf("L1/L2/L3/L4:%d/%d/%d/%d\n",levelcount[1],levelcount[2],levelcount[3],levelcount[4]);
	}
}