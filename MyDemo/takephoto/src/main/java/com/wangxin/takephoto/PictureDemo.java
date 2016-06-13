package com.wangxin.takephoto;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;

public class PictureDemo extends Activity {

	private TakePhoto4Roomis10 takePhoto;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture);

		takePhoto = new TakePhoto4Roomis10();
		takePhoto.init((SurfaceView) findViewById(R.id.preview));


		findViewById(R.id.btn_take).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				takePhoto.takePicture();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		takePhoto.onResume();
	}

	@Override
	public void onPause() {
		takePhoto.onPause();
		super.onPause();
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		new MenuInflater(this).inflate(R.menu.option, menu);
//
//		return (super.onCreateOptionsMenu(menu));
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		if (item.getItemId() == R.id.camera) {
//			if (inPreview) {
//				camera.takePicture(null, null, photoCallback);
//				inPreview = false;
//			}
//		}
//
//		return (super.onOptionsItemSelected(item));
//	}

}
