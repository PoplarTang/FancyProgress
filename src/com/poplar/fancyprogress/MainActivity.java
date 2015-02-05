package com.poplar.fancyprogress;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	private FancyProgress4 view;
	private FancyProgress2 view2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		view = (FancyProgress4) findViewById(R.id.fp4);
		view.show();
		
		view2 = (FancyProgress2) findViewById(R.id.fp2);
		view2.show();
	}

	@Override
	protected void onDestroy() {
		if(view != null){
			view.dismiss();
		}
		if(view2 != null){
			view2.dismiss();
		}
		super.onDestroy();
	}
	
}
