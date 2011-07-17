package sct.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class HolesDialog extends Dialog {  
	TextView text;
	public int holes=15+38;
	public boolean ok;

	public HolesDialog(Context context) {
		super(context);  
	}  

	protected void onCreate(Bundle savedInstanceState){  
		super.onCreate(savedInstanceState);  

		setContentView(R.layout.holesdialog);  
		setTitle("Number of holes:");  

		text = (TextView)findViewById(R.id.textView1);  

		((Button)findViewById(R.id.OkButton)).
			setOnClickListener(new Button.OnClickListener(){
				public void onClick(View v) {
					ok=true;
					dismiss();
				}
			});
		((Button)findViewById(R.id.CancelButton)).
			setOnClickListener(new Button.OnClickListener(){  
				public void onClick(View v) {  
					ok=false;
					dismiss();  
				}  
			});  
		
		((SeekBar)findViewById(R.id.seekBar1)).setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
				holes=progress+38;
				text.setText(""+holes);
			}

			public void onStartTrackingTouch(SeekBar arg0) {
			}

			public void onStopTrackingTouch(SeekBar arg0) {
			}
		});
	}  
}  