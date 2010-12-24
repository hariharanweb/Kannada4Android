package oldcask.android.Kannada4Android;

import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

public class CameraActivity extends Activity implements SurfaceHolder.Callback{

	private static final String LOG_TAG = "Kannada4AndroidCamera";
	private Camera camera;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);
		setUpSurface();
		Button takePicButton = (Button) findViewById(R.id.TakePicture);
		takePicButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
			}
		});
		
	}

	private void setUpSurface() {
		SurfaceView cameraSurface = (SurfaceView) findViewById(R.id.cameraSurface);
		SurfaceHolder surfaceHolder = cameraSurface.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.i(LOG_TAG, "surface changed");
		camera.startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(LOG_TAG, "surface created");
		
		camera = Camera.open();
		Parameters cameraParameters = camera.getParameters();
		cameraParameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
		
		try {
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			Log.e(LOG_TAG, "Somethings gone a bit wrong..."+e);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(LOG_TAG, "surface destroyed");
		camera.release();
	}
}
