package sct.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

/*
 * a homemade dialog to let player to select the level of sudoku
 */
public class LevelDialog extends Dialog {  
	public int holes;
	public boolean ok;
	int level;
	RadioButton l1bt,l2bt,l3bt,l4bt;

	public LevelDialog(Context context) {
		super(context);  
	}  

	protected void onCreate(Bundle savedInstanceState){  
		super.onCreate(savedInstanceState);  

		setContentView(R.layout.leveldialog);  
		setTitle("Level:");  

		l1bt=((RadioButton)findViewById(R.id.easyButton));
		l2bt=((RadioButton)findViewById(R.id.moderateButton));
		l3bt=((RadioButton)findViewById(R.id.challengingButton));
		l4bt=((RadioButton)findViewById(R.id.difficultButton));
		
		((Button)findViewById(R.id.OkButton)).setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				ok=true;
				if(l1bt.isChecked()) level=1;
				else if(l2bt.isChecked()) level=2;
				else if(l3bt.isChecked()) level=3;
				else if(l4bt.isChecked()) level=4;
				dismiss();
			}
		});
		((Button)findViewById(R.id.CancelButton)).setOnClickListener(new Button.OnClickListener(){  
			public void onClick(View v) {
				ok=false;
				dismiss();  
			}  
		});  
	}  
}  