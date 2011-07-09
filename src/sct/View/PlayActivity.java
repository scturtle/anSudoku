package sct.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PlayActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play);
        
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