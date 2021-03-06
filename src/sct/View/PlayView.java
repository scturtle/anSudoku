package sct.View;

import java.util.Random;

import sct.Lib.Automark;
import sct.Lib.Cell;
import sct.Lib.LevelGenerator;
import sct.Lib.Solver;
import sct.Lib.Sudoku;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/*
 * the main view to paint sudoku ,pad and handle the onTouch
 */
public class PlayView extends View implements OnTouchListener{
	float xo=15; //xoffset
	float  yo=15; //yoffset
	float  w=450; //width
	Paint paint = new Paint();
	int lightblue=  Color.rgb(164, 209, 255);
	int lightgray=  Color.rgb(200, 200, 200);
	int lightyellow=Color.rgb(255, 255, 128);
	int[][] ans=null;//full sudoku as answer [1~9]!

	float wp=w/9*4;//width of input pad
	int oi,oj,pi,pj;//o:pad for (i,j) p:left and top location of pad
	int shadowNum;// (1~9)
	boolean padOnShow=false,padOnMark=false,shadowOnShow=false,automark=false;
	
	Sudoku s;//num, mark, background color

	public PlayView(Context context)
	{ super(context); init(); }

	public PlayView(Context context,AttributeSet attr) 
	{ super(context,attr); init(); }     

	private void init(){
		setFocusable(true);
		setFocusableInTouchMode(true);
		this.setOnTouchListener(this);
		s=new Sudoku();
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);// useful
	}
	
	/*
	 * function: generate the first sudoku
	 * input: number of holes
	 */
	public void generateSudoku(int level){
		long start=System.currentTimeMillis(); 
		LevelGenerator.getSudokuAtLevel(level);
		System.out.printf("time used: %d ms\n",System.currentTimeMillis()-start);
		int[][] m=LevelGenerator.getSudoku();
		System.out.printf("level: %d \n",Solver.getLevel(m));
		ans=LevelGenerator.getAns();
		
		//m[][] to sudoku.cell[][]
		for(int i=0;i<9;i++) for(int j=0;j<9;j++){
				if(m[i][j]!=0) s.cell[i][j].setFixType();
				s.cell[i][j].setNum(m[i][j]);
			}
	}

	/*
	 * fuction: draw all world, sequence is important
	 */
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);     

		drawSudoku(canvas);
		drawEdge(canvas);
		drawPad(canvas);
	}


	/* function: draw sudoku
	 * input: (Sudoku)
	 */
	void drawSudoku(Canvas canvas){
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++) {
				Cell u = s.cell[i][j];

				// set cell bg color
				if(u.getbg()==Cell.Bg.white)
					paint.setColor(Color.WHITE);
				else if(u.getbg()==Cell.Bg.blue)
					paint.setColor(lightblue);
				else
					paint.setColor(lightgray);

				//rect to show bg color
				canvas.drawRect(w/9*i+xo, w/9*j+yo, w/9*(i+1)+xo, w/9*(j+1)+yo, paint);

				// draw number in cell
				if(u.isMarkType()) {// mark
					paint.setColor(Color.BLACK);
					paint.setTextSize(16);
					float xoo=w/9*i + xo, yoo=w/9*j +yo;
					for(int k=1;k<=9;k++)
						if(u.isMark(k))
							canvas.drawText(""+k,
									4 + (k-1)%3*w/27 + xoo,
									15 + (k-1)/3*w/27 + yoo,
									paint);
				}
				else //fix or guess
				{
					if(u.isFixType())
						paint.setColor(Color.BLACK);
					else
					{
						if(u.getNum()==0) continue;
						if(s.cell[i][j].getNum()==ans[i][j])
							paint.setColor(Color.BLUE);
						else
							paint.setColor(Color.RED);// wrong guess
					}

					paint.setTextSize(32);
					canvas.drawText(""+s.cell[i][j].getNum()
							, 15 + w/9*i + xo,35 + w/9*j +yo, paint);
				}
			}
	}


	/* function: draw the edge of sudoku
	 */
	void drawEdge(Canvas canvas){
		paint.setColor(Color.BLACK);

		//light line
		paint.setStrokeWidth(3);
		for(int i=0;i<=3;i++)
			canvas.drawLine(w/3*i+xo, yo, w/3*i+xo, w+yo, paint);
		for(int i=0;i<=3;i++)
			canvas.drawLine(xo, w/3*i+yo, w+xo, w/3*i+yo, paint);

		//weight line
		paint.setStrokeWidth(1);
		for(int i=0;i<=9;i++)
			canvas.drawLine(w/9*i+xo, yo, w/9*i+xo, w+yo, paint);
		for(int i=0;i<=9;i++)
			canvas.drawLine(xo, w/9*i+yo, w+xo, w/9*i+yo, paint);
	}

	/*
	 * function: draw pad to imput number
	 */
	void drawPad(Canvas canvas){
		float xop=pi*w/9+xo,yop=pj*w/9+yo;//todo

		if(!padOnShow) return;

		//draw yellow bg
		paint.setColor(lightyellow);
		canvas.drawRect(xop, yop, xop+wp, yop+wp+w/9, paint);

		//edge
		paint.setStrokeWidth(3);
		paint.setColor(Color.BLACK);
		for(int i=0;i<=3;i++)//row
			canvas.drawLine(wp/3*i+xop, yop, wp/3*i+xop, wp+w/9+yop, paint);
		for(int i=0;i<=3;i++)//column
			canvas.drawLine(xop, wp/3*i+yop, wp+xop, wp/3*i+yop, paint);
		canvas.drawLine(xop, wp+w/9+yop, wp+xop, wp+w/9+yop, paint);//last column line

		//number
		if(!padOnMark){// pad of guess
			paint.setTextSize(48);
			for(int i=0;i<3;i++) for(int j=0;j<3;j++) {
				int num=i*3+j+1,originNum=s.cell[oi][oj].getNum();
				// show guess num in blue in pad
				if(num==originNum) paint.setColor(Color.BLUE);
				canvas.drawText(""+num, 20+wp/3*j+xop, 50+wp/3*i+yop, paint);
				if(num==originNum) paint.setColor(Color.BLACK);
			}
		}
		else{// pad of mark
			paint.setTextSize(24);
			for(int i=0;i<3;i++) for(int j=0;j<3;j++) {
				int num=i*3+j+1;
				if(s.cell[oi][oj].isMark(num)) paint.setColor(Color.BLUE);
				canvas.drawText(""+num,
						6+wp/3*j+wp/9*j +xop, 19+wp/3*i+wp/9*i +yop,
						paint);
				if(s.cell[oi][oj].isMark(num)) paint.setColor(Color.BLACK);
			}
		}

		// draw mark switch and exit
		paint.setTextSize(16);
		canvas.drawText("mark", 15+xop, 30+wp+yop, paint);
		paint.setTextSize(48);
		canvas.drawText("X", 20+wp/3*2+xop, 40+wp+yop, paint);
	}

	/*
	 * function: get the pad location (pi,pj) for cell (i,j)
	 * intput: i,j
	 */
	void adjustPiPj(int i,int j){
		if(j<=5){
			pi=i/3*3+3; pj=j+1;
			if(pi>5||pi<0||pj>6||pj<0) {
				pi=i+1; pj=j/3*3+3;
			}
			if(pi>5||pi<0||pj>6||pj<0) {
				pi=i/3*3-4; pj=j+1;
			}
			if(pi>5||pi<0||pj>6||pj<0) {
				pi=i-4; pj=j/3*3+3;
			}
		}
		else{
			pi=i/3*3+3; pj=j-5;
			if(pi>5||pi<0||pj>6||pj<0) {
				pi=i+1; pj=j/3*3-5;
			}
			if(pi>5||pi<0||pj>6||pj<0) {
				pi=i/3*3-4; pj=j-5;
			}
			if(pi>5||pi<0||pj>6||pj<0) {
				pi=i-4; pj=j/3*3-5;
			}
		}
	}

	/*
	 * function: if this touch is on pad, 
	 *           before use this, check padOnShow first!
	 */
	boolean touchInPad(int i,int j){
		return (pi<=i && i<pi+4 && pj<=j && j<pj+5);
	}

	/*
	 * function: set the bg of all cell and show the pad (set padOnShow)
	 * 			 set oi,oj(the pad for cell) first!
	 * side effect: if show pad, set shadowOnShow=false
	 * input: showOrNot(boolean)
	 */
	void setShowPad(boolean show){
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++)
				if(show&&(((i/3*3==oi/3*3)&&(j/3*3==oj/3*3))||//same house
						i==oi||j==oj)){
					s.cell[i][j].setbg(Cell.Bg.blue);
				} else
					s.cell[i][j].setbg(Cell.Bg.white);
		s.cell[oi][oj].setbg(Cell.Bg.white);
		padOnShow=show;
		if(show) shadowOnShow=false;
	}

	/*
	 * fuction: show shadow of impossible cell after touch a fix num
	 * input: the i,j of the fix cell
	 */
	void toggleShowShadow(int ti,int tj){
		if(shadowOnShow && shadowNum==s.cell[ti][tj].getNum()){
			for(int i=0;i<9;i++)
				for(int j=0;j<9;j++)
					s.cell[i][j].setbg(Cell.Bg.white);
			shadowOnShow=false;
			return;
		}

		shadowNum=s.cell[ti][tj].getNum();
		shadowOnShow=true;

		//find
		int[] a=new int[81];int top=0;
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++)
				if(s.cell[i][j].isOnNum()){
					if(s.cell[i][j].getNum()==shadowNum)
						a[top++]=i*10+j;
				}

		//set
		for(int k=0;k<top;k++){
			int di=a[k]/10,dj=a[k]%10;
			for(int i=0;i<9;i++)
				for(int j=0;j<9;j++)
					if(s.cell[i][j].isOnNum()|| // on num
					(s.cell[i][j].isMarkType() && !s.cell[i][j].isMark(shadowNum))|| //unmarked this num
					((i/3*3==di/3*3)&&(j/3*3==dj/3*3))|| //same house
					i==di||j==dj) //same row or column
						s.cell[i][j].setbg(Cell.Bg.gray);
		}
		for(int k=0;k<top;k++){
			int di=a[k]/10,dj=a[k]%10;
			s.cell[di][dj].setbg(Cell.Bg.blue);
		}
	}
	/*
	 * function: give a hint
	 */
	public void giveHint(){
		Random r=new Random();
		//check there are place to give hint
		int[] arr=new int[81];
		int top=0;
		for(int i=0;i<9;i++) for(int j=0;j<9;j++)
			if(!s.cell[i][j].isOnNum())
				arr[top++]=i*10+j;
		if(top==0) return;
		
		//clear
		setShowPad(false);
		shadowOnShow=false;
		for(int i=0;i<9;i++) for(int j=0;j<9;j++)
			s.cell[i][j].setbg(Cell.Bg.white);
		
		int luck=r.nextInt(top);
		int li=arr[luck]/10,lj=arr[luck]%10;
//		System.out.printf("%d %d\n",li,lj);
		s.cell[li][lj].setNum(ans[li][lj]);
		if(automark) Automark.automarkSet(s,li,lj,ans[li][lj]);
		s.cell[li][lj].setFixType();
		s.cell[li][lj].setbg(Cell.Bg.blue);
		invalidate();
	}
	/*
	 * function: main touch method, do many things
	 * 
	 */
	public boolean onTouch(View view, MotionEvent event) {
		if(event.getAction()!=MotionEvent.ACTION_DOWN) return false;

		int i=(int)((event.getX()-xo)/(w/9));
		int j=(int)((event.getY()-yo)/(w/9));

		//		System.out.printf("i:%d j:%d\n",i,j);
		if(padOnShow && touchInPad(i,j)){// touch on pad

			int ti=(int) ((event.getX()-(pi*w/9+xo))/(wp/3));
			int tj=(int) ((event.getY()-(pj*w/9+yo))/(wp/3));
			int inputNum=tj*3+ti+1;

			if(inputNum<=9){ // touch guess pad todo:mark
				if(!padOnMark){// mark a guess
					int originNum=s.cell[oi][oj].getNum();
					if(s.cell[oi][oj].isGuessType() && originNum!=0 ){//cancel guess
						s.cell[oi][oj].setNum(0);
						if(automark){
							s.cell[oi][oj].setMarkType();
							Automark.automarkCell(s,oi,oj);
							Automark.automarkClear(s,oi,oj,originNum);
						}
					}
					if(inputNum!=originNum){//really make a guess
						s.cell[oi][oj].setGuessType();
						s.cell[oi][oj].setNum(inputNum);
						if(automark) Automark.automarkSet(s,oi,oj,inputNum);
						setShowPad(false);
					}
				}else{ // make a mark
					s.cell[oi][oj].setMarkType();
					if(s.cell[oi][oj].isMark(inputNum))
						s.cell[oi][oj].clearMark(inputNum);
					else
						s.cell[oi][oj].setMark(inputNum);
				}
			}
			else if(inputNum==10){ // touch mark btn
				padOnMark=!padOnMark;
			}
			else setShowPad(false);
		} else if(0<=i && i<9 && 0<=j && j<9){ // touch on cell
			if(s.cell[i][j].isFixType()){
				setShowPad(false);
				toggleShowShadow(i,j);
			}else{ //guess
				oi=i;oj=j;
				adjustPiPj(i,j);
				setShowPad(true);
			}
		}
		else setShowPad(false);
		invalidate();
		return true;
	}

}