package sct.View;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import sct.Lib.Sudoku;
import sct.Lib.Unit;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

public class PlayActivity extends Activity {

	String noh; // number of holes from FrontActivity
	PlayView pv;
	ToggleButton tb;

	private ProgressDialog progressDialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play);

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
		noh=bundle.getString("NOH");

		if(noh.equals("-1")){ // means continue
			//resume sudoku data
			try {
				FileInputStream fi = openFileInput("sudoku.data");
				ObjectInputStream ios = new ObjectInputStream(fi);
				pv.sudoku=(Sudoku)ios.readObject();
				pv.ans=(int[][])ios.readObject();
				pv.automark=((String)ios.readObject()).equals("y");
				tb.setChecked(pv.automark);
//				System.out.println((String)(ios.readObject()));
				ios.close();
			} catch (Exception e) { System.out.println(e.toString()); }
		}else{ // new game
			progressDialog = ProgressDialog.show(
					PlayActivity.this, "", "Generating sudoku ......", true, false);
			new Thread() {
				@Override
				public void run() {
					pv.generateSudoku(Integer.parseInt(noh));
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
				pv.sudoku.unit[i][j].setbg(Unit.Bg.white);
			oos.writeObject(pv.sudoku);
			oos.writeObject(pv.ans);
			oos.writeObject(pv.automark?"y":"n");
			oos.flush();oos.close();
		} catch (Exception e) { System.out.println(e.toString()); }
	}
	
	// ask yes or no before button action 
	public void MessageBox(final String action,String message) {
		AlertDialog.Builder aAlert = new AlertDialog.Builder(this);
		aAlert.setMessage(message);

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				switch(which){
				case DialogInterface.BUTTON_POSITIVE:
					if(action.equals("automarkOn")){
						tb.setChecked(true);
						pv.automark=true;
						pv.automarkAll();
						pv.postInvalidate();
					}else if(action.equals("automarkOff")){
						tb.setChecked(false);
					}else if(action.equals("automarkReset")){
						pv.automarkAll();
						pv.postInvalidate();
					}else if(action.equals("clear")){
						pv.clearAllMark();
					}else if(action.equals("hint")){
						pv.giveHint();
					}
					 break;
				case DialogInterface.BUTTON_NEGATIVE:
					if(action.equals("automarkOn"))
						tb.setChecked(false);
					else if(action.equals("automarkOff"))
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