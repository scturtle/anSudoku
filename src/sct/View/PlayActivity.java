package sct.View;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import sct.Lib.Automark;
import sct.Lib.Cell;
import sct.Lib.Sudoku;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

/*
 * the activity to show view to play
 */
public class PlayActivity extends Activity {

	String level; // level of sudoku
	PlayView pv;
	ToggleButton tb;
	TextView tvtime;

	Timer timer=new Timer();
	int time=0;
	//handle the msg to update time
	Handler timerHandler = new Handler(){     
		private String format(int n){if(n<10) return "0"+n; else return ""+n;}
		public void handleMessage(Message msg) {     
			switch (msg.what) {         
			case 29008:
				int h=time/60/60,m=time/60%60,sec=time%60;
				if(h>0)
					tvtime.setText(format(h)+":"+format(m)+":"+format(sec));
				else
					tvtime.setText(format(m)+":"+format(sec));
				++time;
				break;
			}
			super.handleMessage(msg);
		}
	};

	private ProgressDialog progressDialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play);

		//set timer to send msg to update time
		tvtime=(TextView)findViewById(R.id.textViewTime);
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				Message msg=new Message();
				msg.what=29008;
				timerHandler.sendMessage(msg);
				}
		} , 0, 1000);
		// automark button
		tb=(ToggleButton)findViewById(R.id.automarkButton);
		tb.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(tb.isChecked())
					MessageBox("automarkOn","Are you sure to\nclear personal mark?");
				else
					MessageBox("automarkOff","Are you sure?");
			}
		});
		
		//reset button
		((Button)findViewById(R.id.resetButton)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(pv.automark)
					MessageBox("automarkReset","Reset all automark?");
				else
					MessageBox("clear","Clear all mark?");
			}
		});
		
		//hint button
		((Button)findViewById(R.id.hintButton)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				MessageBox("hint","Wanna a hint?");
			}
		});
		
		// back button listener
		((Button)findViewById(R.id.backButton)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		pv=(PlayView)findViewById(R.id.sudokuView1);

		Bundle bundle = getIntent().getExtras();    
		level=bundle.getString("LEVEL");

		if(level.equals("-1")){ // means continue from last game
			//resume sudoku data
			try {
				FileInputStream fi = openFileInput("sudoku.data");
				ObjectInputStream ios = new ObjectInputStream(fi);
				pv.s=(Sudoku)ios.readObject();
				pv.ans=(int[][])ios.readObject();
				pv.automark=((String)ios.readObject()).equals("true");
				time=Integer.parseInt((String)ios.readObject());
				tb.setChecked(pv.automark);
				ios.close();
			} catch (Exception e) { System.out.println(e.toString()); }
		}else{ // new game
			progressDialog = ProgressDialog.show(
					PlayActivity.this, "", "Generating sudoku ......", true, false);
			new Thread() {
				@Override
				public void run() {
					// give the view the level
					pv.generateSudoku(Integer.parseInt(level));
					progressDialog.dismiss();
				}
			}.start();
		}

	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		//save sudoku data
		try {
			FileOutputStream fo = openFileOutput("sudoku.data", 0);
			ObjectOutputStream oos = new ObjectOutputStream(fo);
			for(int i=0;i<9;i++) for(int j=0;j<9;j++)
				pv.s.cell[i][j].setbg(Cell.Bg.white);
			oos.writeObject(pv.s);
			oos.writeObject(pv.ans);
			oos.writeObject(""+pv.automark);
			oos.writeObject(""+time);
			oos.flush();oos.close();
		} catch (Exception e) { System.out.println(e.toString()); }
	}
	
	// ask yes or no before button action 
	public void MessageBox(final String action,String message) {
		AlertDialog.Builder aAlert = new AlertDialog.Builder(this);
		aAlert.setMessage(message);

		// handle all the answers
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				switch(which){
				case DialogInterface.BUTTON_POSITIVE:
					if(action.equals("automarkOn")){
						tb.setChecked(true);
						pv.automark=true;
						Automark.automarkAll(pv.s);
						pv.postInvalidate();
					}else if(action.equals("automarkOff")){
						tb.setChecked(false);
						pv.automark=false;
					}else if(action.equals("automarkReset")){
						Automark.automarkAll(pv.s);
						pv.postInvalidate();
					}else if(action.equals("clear")){
						Automark.clearAllMark(pv.s);
					}else if(action.equals("hint")){
						pv.giveHint();
					}
					 break;
				case DialogInterface.BUTTON_NEGATIVE:
					if(action.equals("automarkOn")){
						tb.setChecked(false);
						pv.automark=false;
					}else if(action.equals("automarkOff"))
						tb.setChecked(true);
					 break;
				};
			}
		};
		aAlert.setPositiveButton("Yes",listener);
		aAlert.setNegativeButton("No",listener);
		aAlert.show();
	}
}