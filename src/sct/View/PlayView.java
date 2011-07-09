package sct.View;

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

	float xo=10; //xoffset
	float  yo=60; //yoffset
	float  w=460; //width
    Paint paint = new Paint();
    int lightblue= Color.rgb(164, 209, 255);
    int lightgray= Color.rgb(200, 200, 200);
    
    private Sudoku sudoku;

    private void init(){
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        sudoku=new Sudoku();
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);
    }
    
    public PlayView(Context context) {
        super(context);
        init();
    }
    
    public PlayView(Context context,AttributeSet attr)     
    {
        super(context,attr);
        init();
    }     
    
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);     
        
        sudoku.unit[0][0].settype(Unit.Type.fix);
        sudoku.unit[0][0].setNum(1);
        sudoku.unit[0][0].setbg(Unit.Bg.gray);
        sudoku.unit[0][1].setNum(1);
        sudoku.unit[0][1].setbg(Unit.Bg.blue);
        sudoku.unit[1][0].settype(Unit.Type.mark);
        for(int i=1;i<=9;i++)
        	sudoku.unit[1][0].setMark(i);
        drawSudoku(canvas);
        drawEdge(canvas);
    }
    
    
    /* function: draw sudoku
     * input: Canvas, (Sudoku)
     * output: none
     */
    void drawSudoku(Canvas canvas){
    	for(int i=0;i<9;i++)
    		for(int j=0;j<9;j++)
    		{
    			Unit u = sudoku.unit[i][j];
    			
    			// draw unit background
    			if(u.getbg()==Unit.Bg.white)
    				paint.setColor(Color.WHITE);
    			else if(u.getbg()==Unit.Bg.blue)
    				paint.setColor(lightblue);
    			else
    				paint.setColor(lightgray);
    			
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
    					paint.setColor(Color.BLUE);
    				}

    				paint.setTextSize(32);
    				canvas.drawText(""+sudoku.unit[i][j].getNum()
    						, 15 + w/9*i + xo,35 + w/9*j +yo, paint);
    			}
    		}
    }
    

    /* function: draw the edge of sudoku
     * input: Canvas
     * output: none
     */
    void drawEdge(Canvas canvas){
        paint.setColor(Color.BLACK);
        
        paint.setStrokeWidth(3);
        for(int i=0;i<=3;i++)
        	canvas.drawLine(w/3*i+xo, yo, w/3*i+xo, w+yo, paint);
        for(int i=0;i<=3;i++)
        	canvas.drawLine(xo, w/3*i+yo, w+xo, w/3*i+yo, paint);
        
        paint.setStrokeWidth(1);
        for(int i=0;i<=9;i++)
        	canvas.drawLine(w/9*i+xo, yo, w/9*i+xo, w+yo, paint);
        for(int i=0;i<=9;i++)
        	canvas.drawLine(xo, w/9*i+yo, w+xo, w/9*i+yo, paint);
    }
    
    public boolean onTouch(View view, MotionEvent event) {
    	int i=(int)((event.getX()-xo)/9);
    	int j=(int)((event.getY()-yo)/9);
        System.out.printf("i:%d j:%d\n",i,j);
//        dosomething
//        invalidate();
        return true;
    }
}