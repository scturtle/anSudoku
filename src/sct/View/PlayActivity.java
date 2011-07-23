package sct.View;

import sct.Lib.Sudoku;
import sct.Lib.Unit;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PlayActivity extends Activity {

	String noh; // number of holes from FrontActivity
	PlayView pv;

	private ProgressDialog progressDialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play);

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
			oos.flush();oos.close();
		} catch (Exception e) { System.out.println(e.toString()); }
	}
}