package oldcask.android.Kannada4Android;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

public class CameraActivity extends Activity implements SurfaceHolder.Callback {

	private static final String LOG_TAG = "Kannada4AndroidCamera";
	private Camera camera;
	private static final String PIC_DATA = "PIC_DATA";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);
		setUpSurface();

		Button takePicButton = (Button) findViewById(R.id.TakePicture);
		takePicButton.setOnClickListener(new CameraClickListener());
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
		camera.startPreview();
		camera.autoFocus(new Camera.AutoFocusCallback() {
			public void onAutoFocus(boolean success, Camera camera) {
				//do nothing
			}
		});
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open();
		Parameters cameraParameters = camera.getParameters();
		cameraParameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
		cameraParameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
		cameraParameters.setPictureSize(320, 240);
		camera.setParameters(cameraParameters);
		try {
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			Log.e(LOG_TAG, "Somethings gone a bit wrong..." + e);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.release();
	}

	private final class CameraClickListener implements View.OnClickListener {
		private ShutterCallback shutterCallback = new ShutterCallbackListener();
		private PictureCallback rawCallback = new RawImageCallbackListener();
		private PictureCallback jpegCallback = new JPEGImageCallbackListener();

		public void onClick(View v) {
			camera.takePicture(shutterCallback, rawCallback, jpegCallback);
		}
	}

	private final class JPEGImageCallbackListener implements PictureCallback {

		public void onPictureTaken(byte[] data, Camera camera) {
			Bundle bundle = new Bundle();
			bundle.putByteArray(PIC_DATA, data);
			
			Intent resultIntent = new Intent(getBaseContext(), ProcessingActivity.class);
			resultIntent.putExtras(bundle);
			
			startActivity(resultIntent);
		}
	}

	// do nothing
	private final class RawImageCallbackListener implements PictureCallback {
		public void onPictureTaken(byte[] data, Camera camera) {
		}
	}

	private final class ShutterCallbackListener implements ShutterCallback {
		public void onShutter() {
		}
	}
}
