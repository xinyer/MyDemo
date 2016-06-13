package com.wangxin.takephoto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

/**
 * take photo utils
 * Created by wangxin on 16/6/12.
 */
public class TakePhoto4Roomis10 {

    private static final String TAG = TakePhoto4Roomis10.class.getSimpleName();

    private SurfaceHolder mPreviewHolder = null;
    private Camera mCamera = null;
    private boolean mIsInPreview = false;
    private boolean mIsCameraConfigured = false;

    public void init(SurfaceView preview) {
        mPreviewHolder = preview.getHolder();
        mPreviewHolder.addCallback(mSurfaceCallback);
        mPreviewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void takePicture() {
        if (mIsInPreview) {
            mCamera.takePicture(null, null, mPictureCallback);
            mIsInPreview = false;
        }
    }

    public void onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            Camera.CameraInfo info = new Camera.CameraInfo();

            for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                Camera.getCameraInfo(i, info);

                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    mCamera = Camera.open(i);
                }
            }
        }

        if (mCamera == null) {
            mCamera = Camera.open();
        }

        startPreview();
    }

    private void startPreview() {
        if (mIsCameraConfigured && mCamera != null) {
            mCamera.startPreview();
            mIsInPreview = true;
//            int maxFaces = mCamera.getParameters().getMaxNumDetectedFaces();
//            Log.d(TAG, "maxFaces=" + maxFaces);
            mCamera.setFaceDetectionListener(faceDetectionListener);
//            mCamera.startFaceDetection();
        }
    }

    private Camera.FaceDetectionListener faceDetectionListener = new Camera.FaceDetectionListener() {
        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
            Log.d(TAG, "faceDetectionListener| faces.lenght=" + faces.length);
        }
    };

    public void onPause() {
        if (mIsInPreview) {
            mCamera.stopPreview();
        }

        mCamera.release();
        mCamera = null;
        mIsInPreview = false;
    }

    private void initPreview(int width, int height) {
        if (mCamera != null && mPreviewHolder.getSurface() != null) {
            try {
                mCamera.setPreviewDisplay(mPreviewHolder);
            } catch (Throwable t) {
				Log.e(TAG, "Exception in setPreviewDisplay()", t);
            }

            if (!mIsCameraConfigured) {
                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size size = getBestPreviewSize(width, height, parameters);
                Camera.Size pictureSize = getSmallestPictureSize(parameters);

                if (size != null && pictureSize != null) {
                    parameters.setPreviewSize(size.width, size.height);
                    parameters.setPictureSize(pictureSize.width, pictureSize.height);
                    parameters.setPictureFormat(ImageFormat.JPEG);
                    mCamera.setParameters(parameters);
                    mIsCameraConfigured = true;
                }
            }
        }
    }



    private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            Log.d(TAG, "surface created.");
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d(TAG, "surface changed.");
            initPreview(width, height);
            startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG, "surface destroyed.");
        }
    };

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();
            BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, BitmapFactoryOptionsbfo);
            if (findFace(bitmap)) {
                new SavePhotoTask().execute(data);
            } else {

            }
            camera.startPreview();
            mIsInPreview = true;
        }
    };

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }

        return result;
    }

    private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPictureSizes()) {
            if (result == null) {
                result = size;
            } else {
                int resultArea = result.width * result.height;
                int newArea = size.width * size.height;

                if (newArea < resultArea) {
                    result = size;
                }
            }
        }

        return result;
    }

    class SavePhotoTask extends AsyncTask<byte[], String, String> {
        @Override
        protected String doInBackground(byte[]... jpg) {
            File photo = new File(Environment.getExternalStorageDirectory(), "photo.jpg");

            if (photo.exists()) {
                photo.delete();
            }

            try {
                FileOutputStream fos = new FileOutputStream(photo.getPath());

                fos.write(jpg[0]);
                fos.close();
            } catch (java.io.IOException e) {
                Log.e("PictureDemo", "Exception in photoCallback", e);
            }
            return null;
        }
    }

    public boolean findFace(Bitmap bmp) {
        // Ask for 1 face
        FaceDetector.Face faces[] = new FaceDetector.Face[1];
        FaceDetector detector = new FaceDetector(bmp.getWidth(), bmp.getHeight(), 1);
        int count = detector.findFaces( bmp, faces);
        Log.d(TAG, "face count->" + count);
        if (count>0) return true;

//        Face face = null;

//        if( count > 0 ) {
//            face = faces[0];
//
//            PointF midEyes = new PointF();
//            face.getMidPoint( midEyes );
//            Log.i( TAG,
//                    "Found face. Confidence: " + face.confidence() + ". Eye Distance: " + face.eyesDistance() + " Pose: ("
//                            + face.pose( FaceDetector.Face.EULER_X ) + "," + face.pose( FaceDetector.Face.EULER_Y ) + ","
//                            + face.pose( FaceDetector.Face.EULER_Z ) + "). Eye Midpoint: (" + midEyes.x + "," + midEyes.y + ")" );
//
//            float eyedist = face.eyesDistance();
//            PointF lt = new PointF( midEyes.x - eyedist * 2.0f, midEyes.y - eyedist * 2.5f );
//            // Create rectangle around face.  Create a box based on the eyes and add some padding.
//            // The ratio of head height to width is generally 9/5 but that makes the rect a bit to tall.
//            return new Rect(
//                    Math.max( (int) ( lt.x ), 0 ),
//                    Math.max( (int) ( lt.y ), 0 ),
//                    Math.min( (int) ( lt.x + eyedist * 4.0f ), bmp.getWidth() ),
//                    Math.min( (int) ( lt.y + eyedist * 5.5f ), bmp.getHeight() )
//            );
//        }

        return false;
    }
}
