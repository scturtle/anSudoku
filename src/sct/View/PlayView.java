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

	float xo=10; //xoffset
	float  yo=60; //yoffset
	float  w=460; //width
    Paint paint = new Paint();
    int lightblue= Color.rgb(164, 209, 255);
    int lightgray= Color.rgb(200, 200, 200);
    int lightyello=Color.rgb(255, 255, 128);
    int[][] ans=null;
    
    float wp=w/9*4,hp=wp/3*4; 
    int pi=0,pj=0;
    boolean padOnShow=false,padOnMark=false;
    
    private Sudoku sudoku;

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
     * output: none
     */
    public void generateSudoku(int holes){
		long start=System.currentTimeMillis(); 
		int [][]m = null;
		Object arr = DigHoles.tryDigLoop(holes);
		ans=(int[][])Array.get(arr, 0);
		m=(int[][])Array.get(arr, 1);
		int times=(Integer) Array.get(arr, 2);
		long end=System.currentTimeMillis(); 
		System.out.printf("Try:%dtimes using:%dms\n",times,end-start);
		
    	for(int i=1;i<=9;i++)
    		for(int j=1;j<=9;j++)
    		{
    			if(m[i][j]!=0)
    				sudoku.unit[i-1][j-1].settype(Unit.Type.fix);
    				sudoku.unit[i-1][j-1].setNum(m[i][j]);
    		}
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
        
        drawSudoku(canvas);
        drawEdge(canvas);
        padOnShow=true;
        drawPad(canvas);
    }
    
    
    /* function: draw sudoku
     * input: Canvas, (Sudoku)
     * output: none
     */
    void drawSudoku(Canvas canvas){
    	for(int i=0;i<9;i++)
    		for(int j=0;j<9;j++) {
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
    
    /*
     * function: draw pad to imput number
     */
    void drawPad(Canvas canvas){
    	int xop=10,yop=60;//todo
    	
    	if(!padOnShow) return;
    	
    	//draw fill
    	paint.setColor(lightyello);
    	canvas.drawRect(xop, yop, xop+wp, yop+hp, paint);
    	
    	//edge
        paint.setStrokeWidth(3);
    	paint.setColor(Color.BLACK);
        for(int i=0;i<=3;i++)
        	canvas.drawLine(wp/3*i+xop, yop, wp/3*i+xop, hp+yop, paint);
        for(int i=0;i<=4;i++)
        	canvas.drawLine(xop, hp/4*i+yop, wp+xop, hp/4*i+yop, paint);
        
        //number
        if(!padOnMark){
        	paint.setTextSize(48);
        	for(int i=0;i<3;i++)
        		for(int j=0;j<3;j++)
        			canvas.drawText(""+(i*3+j+1), 20+wp/3*j+xop, 50+hp/4*i+yop, paint);
        }
        else{
        	paint.setTextSize(16);
        	for(int i=0;i<3;i++)
        		for(int j=0;j<3;j++)
        			if(true)//todo
        				canvas.drawText(""+(i*3+j+1),
        						10+wp/3*j+wp/9*j +xop,
        						18+hp/4*i+hp/12*i+yop,
        						paint);
        	
        }
        
        // mark switch and exit
    	paint.setTextSize(16);
		canvas.drawText("mark", 15+xop, 40+hp/4*3+yop, paint);
    	paint.setTextSize(48);
		canvas.drawText("X", 20+wp/3*2+xop, 50+hp/4*3+yop, paint);
    }
    
    public boolean onTouch(View view, MotionEvent event) {
//    	int i=(int)((event.getX()-xo)/9);
//    	int j=(int)((event.getY()-yo)/9);
//        System.out.printf("i:%d j:%d\n",i,j);
//        dosomething
//    	sudoku.unit[0][8].setNum(sudoku.unit[0][8].getNum()+1);
//    	invalidate();
//        return true;
    	return true;
    }

//    public void MessageBox(String message){
//    		Toast.makeText(this.getContext(),message,Toast.LENGTH_SHORT).show();
//    }
}