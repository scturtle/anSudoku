package sct.View;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

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
        
        hd=new HolesDialog(this);
        hd.setOnDismissListener(new DialogInterface.OnDismissListener(){

			public void onDismiss(DialogInterface arg0) {
				if(hd.ok) {
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