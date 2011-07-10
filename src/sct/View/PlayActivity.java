package sct.View;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PlayActivity extends Activity {
    /** Called when the activity is first created. */
	private ProgressDialog progressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play);
        
    	///////////////////
    	progressDialog = ProgressDialog.show(
    			PlayActivity.this, "", "Generating sudoku ......", true, false);
    	new Thread() {
    		@Override
    		public void run() {
    			((PlayView)findViewById(R.id.sudokuView1)).generateSudoku(50);
    			progressDialog.dismiss();
    		}
    	}.start();
    	
        ((Button)findViewById(R.id.backButton)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
			}
        });
    }
}