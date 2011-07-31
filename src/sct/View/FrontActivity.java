package sct.View;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/*
 * the first activity
 */
public class FrontActivity extends Activity {

	HolesDialog hd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		((Button)findViewById(R.id.newButton)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				hd.show();
			}
		});

		((Button)findViewById(R.id.ContinueButton)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// if firstrun then not continue
			    SharedPreferences prefs = getSharedPreferences("anSudoku", MODE_PRIVATE); 
			    String firstrun = prefs.getString("firstRun","yes");
			    if(firstrun.equals("yes")) return;
			    
				Intent intent = new Intent(FrontActivity.this, PlayActivity.class);
				Bundle holesBundle = new Bundle();
				holesBundle.putString("NOH","-1"); // means continue
				intent.putExtras(holesBundle);
				startActivityForResult(intent, 0);
			}
		});
		
		hd=new HolesDialog(this);
		hd.setOnDismissListener(new DialogInterface.OnDismissListener(){

			public void onDismiss(DialogInterface arg0) {
				if(hd.ok) {
					SharedPreferences prefs = getSharedPreferences("anSudoku", MODE_PRIVATE); 
				    Editor editor = prefs.edit(); 
				    editor.putString("firstRun","no"); 
				    editor.commit(); 
					
					Intent intent = new Intent(FrontActivity.this, PlayActivity.class);
					Bundle holesBundle = new Bundle();
					holesBundle.putString("NOH",""+hd.holes);
					intent.putExtras(holesBundle);
					startActivityForResult(intent, 0);
				}
			} 
		});
	}
}