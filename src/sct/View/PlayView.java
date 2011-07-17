package sct.View;

import java.lang.reflect.Array;

import sct.Lib.DigHoles;
import sct.Lib.Sudoku;
import sct.Lib.Unit;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class PlayView extends View implements OnTouchListener {

	float xo=15; //xoffset
	float  yo=10; //yoffset
	float  w=450; //width
	Paint paint = new Paint();
	int lightblue=  Color.rgb(164, 209, 255);
	int lightgray=  Color.rgb(200, 200, 200);
	int lightyellow=Color.rgb(255, 255, 128);
	int[][] ans=null;//full sudoku as answer [1~9]!

	float wp=w/9*4;//width of input pad
	int oi,oj,pi,pj;//o:pad for (i,j) p:left and top location of pad
	int shadowNum;
	boolean padOnShow=false,padOnMark=false,shadowOnShow=false;

	private Sudoku sudoku;//num, mark, background color

	public PlayView(Context context) { super(context); init(); }

	public PlayView(Context context,AttributeSet attr) 
	{ super(context,attr); init(); }     

	private void init(){
		setFocusable(true);
		setFocusableInTouchMode(true);
		this.setOnTouchListener(this);
		sudoku=new Sudoku();
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);
	}

	/*
	 * function: generate the first sudoku
	 * input: number of holes
	 */
	public void generateSudoku(int holes){
//		long start=System.currentTimeMillis(); 
		int [][]m = null;
		Object arr = DigHoles.tryDigLoop(holes);
		ans=(int[][])Array.get(arr, 0);//answer
		m=(int[][])Array.get(arr, 1);// sudoku with holes
//		int times=(Integer) Array.get(arr, 2);
//		long end=System.currentTimeMillis(); 
//		System.out.printf("Try:%dtimes using:%dms\n",times,end-start);

		//m[][] to sudoku.unit[][]
		for(int i=1;i<=9;i++)
			for(int j=1;j<=9;j++)
			{
				if(m[i][j]!=0)
					sudoku.unit[i-1][j-1].settype(Unit.Type.fix);
				sudoku.unit[i-1][j-1].setNum(m[i][j]);
			}
	}

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
				Unit u = sudoku.unit[i][j];

				// set unit bg color
				if(u.getbg()==Unit.Bg.white)
					paint.setColor(Color.WHITE);
				else if(u.getbg()==Unit.Bg.blue)
					paint.setColor(lightblue);
				else
					paint.setColor(lightgray);
				
				//rect to show bg color
				canvas.drawRect(w/9*i+xo, w/9*j+yo, w/9*(i+1)+xo, w/9*(j+1)+yo, paint);

				// draw number in unit
				if(u.gettype()==Unit.Type.mark) {// mark
					paint.setColor(Color.BLACK);
					paint.setTextSize(10);
					float xoo=w/9*i + xo, yoo=w/9*j +yo;
					for(int k=1;k<=9;k++)
						if(u.isMark(k))
							canvas.drawText(""+k,
									6 + (k-1)%3*w/27 + xoo,
									12 + (k-1)/3*w/27 + yoo,
									paint);
				}
				else //fix or guess
				{
					if(u.gettype()==Unit.Type.fix)
						paint.setColor(Color.BLACK);
					else
					{
						if(u.getNum()==0) continue;
						if(sudoku.unit[i][j].getNum()==ans[i+1][j+1])
							paint.setColor(Color.BLUE);
						else
							paint.setColor(Color.RED);// wrong guess
					}

					paint.setTextSize(32);
					canvas.drawText(""+sudoku.unit[i][j].getNum()
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
		if(!padOnMark){// pad of mark //todo
			paint.setTextSize(48);
			for(int i=0;i<3;i++) for(int j=0;j<3;j++)
				{
					int num=i*3+j+1,originNum=sudoku.unit[oi][oj].getNum();
					// show guess num in blue in pad
					if(num==originNum) paint.setColor(Color.BLUE);
					canvas.drawText(""+num, 20+wp/3*j+xop, 50+wp/3*i+yop, paint);
					if(num==originNum) paint.setColor(Color.BLACK);
				}
		}
		else{// pad of guess
			paint.setTextSize(16);
			for(int i=0;i<3;i++) for(int j=0;j<3;j++)
					if(true)//todo
						canvas.drawText(""+(i*3+j+1),
								10+wp/3*j+wp/9*j +xop, 18+wp/3*i+wp/9*i +yop,
								paint);
		}

		// draw mark switch and exit
		paint.setTextSize(16);
		canvas.drawText("mark", 15+xop, 30+wp+yop, paint);
		paint.setTextSize(48);
		canvas.drawText("X", 20+wp/3*2+xop, 40+wp+yop, paint);
	}

	/*
	 * function: get the pad location (pi,pj) for unit (i,j)
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
		return (pi<=i && i<=pi+4 && pj<=j && j<=pj+5);
	}
	
	/*
	 * function: set the bg of all unit and show the pad (set padOnShow)
	 * 			 set oi,oj(the pad for unit) first!
	 * side effect: if show pad, set shadowOnShow=false
	 * input: showOrNot(boolean)
	 */
	void setShowPad(boolean show){
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++)
				if(show&&(((i/3*3==oi/3*3)&&(j/3*3==oj/3*3))||//same house
						i==oi||j==oj)){
					sudoku.unit[i][j].setbg(Unit.Bg.blue);
				} else
					sudoku.unit[i][j].setbg(Unit.Bg.white);
		padOnShow=show;
		if(show) shadowOnShow=false;
	}
	
	/*
	 * fuction: show shadow of impossible unit after touch a fix num
	 * input: the i,j of the fix unit
	 */
	void toggleShowShadow(int ti,int tj){
		if(shadowOnShow && shadowNum==sudoku.unit[ti][tj].getNum()){
			for(int i=0;i<9;i++)
				for(int j=0;j<9;j++)
					sudoku.unit[i][j].setbg(Unit.Bg.white);
			shadowOnShow=false;
			return;
		}
		
		shadowNum=sudoku.unit[ti][tj].getNum();
		shadowOnShow=true;
		
		//find
		int[] a=new int[81];int top=0;
		int num=sudoku.unit[ti][tj].getNum();
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++)
				if(sudoku.unit[i][j].getNum()==num)
					a[top++]=i*10+j;
		
		//set
		for(int k=0;k<top;k++){
			int di=a[k]/10,dj=a[k]%10;
			for(int i=0;i<9;i++)
				for(int j=0;j<9;j++)
						if(sudoku.unit[i][j].getNum()!=0||
						((i/3*3==di/3*3)&&(j/3*3==dj/3*3))||//same house
								i==di||j==dj)
							sudoku.unit[i][j].setbg(Unit.Bg.gray);
		}
		for(int k=0;k<top;k++){
			int di=a[k]/10,dj=a[k]%10;
			sudoku.unit[di][dj].setbg(Unit.Bg.blue);
		}
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
			if(!padOnMark){ //pad on guess
				int ti=(int) ((event.getX()-(pi*w/9+xo))/(wp/3));
				int tj=(int) ((event.getY()-(pj*w/9+yo))/(wp/3));
				int inputNum=tj*3+ti+1;
				if(inputNum<=9){ // touch guess pad todo:mark
					int originNum=sudoku.unit[oi][oj].getNum();
					if(originNum==inputNum)
						sudoku.unit[oi][oj].setNum(0);
					else
						sudoku.unit[oi][oj].setNum(inputNum);
					setShowPad(false);
				}
				else if(inputNum==10){ // touch mark btn
				}
				else setShowPad(false);
			}
		}else if(0<=i && i<9 && 0<=j && j<9){ // touch on unit
			if(sudoku.unit[i][j].gettype()==Unit.Type.fix){
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