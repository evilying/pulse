package pt.fraunhofer.pulse;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class App extends Activity implements CvCameraViewListener {

    private static final String TAG = "Pulse::App";

    private CameraBridgeViewBase camera;
    private Pulse pulse;
    
    private BaseLoaderCallback loaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {

                case LoaderCallbackInterface.SUCCESS:
                    loaderCallbackSuccess();
                    break;

                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    private void loaderCallbackSuccess() {
        System.loadLibrary("pulse");
        
        pulse = new Pulse();
        
        File dir = getDir("cascade", Context.MODE_PRIVATE);
        File file = createFileFromResource(dir, R.raw.lbpcascade_frontalface, "xml");
        pulse.load(file.getAbsolutePath());
        dir.delete();

        camera.enableView();
    }

    private File createFileFromResource(File dir, int id, String extension) {
        String name = getResources().getResourceEntryName(id) + "." + extension;
        InputStream is = getResources().openRawResource(id);
        File file = new File(dir, name);

        try {
            FileOutputStream os = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
        } catch (IOException ex) {
            Log.e(TAG, "Failed to create file: " + file.getPath(), ex);
        }

        return file;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.app);

        camera = (CameraBridgeViewBase) findViewById(R.id.camera);
        camera.setCvCameraViewListener(this);
        camera.setMaxFrameSize(600, 400);
    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, loaderCallback);
    }

    @Override
    public void onPause() {
        if (camera != null) {
            camera.disableView();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (camera != null) {
            camera.disableView();
        }
        super.onDestroy();
    }

    private int t;
    private Mat out;
    private Size size;

    @Override
    public void onCameraViewStarted(int width, int height) {
        pulse.start(width, height);
        t = 0;
        out = new Mat();
        size = new Size(camera.getWidth(), camera.getHeight());
    }

    @Override
    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(Mat frame) {
        if (t >= 10) {
            pulse.onFrame(frame, out);
            Imgproc.resize(out, out, size);
        } else {
            t++;
            Imgproc.resize(frame, out, size);
        }
        return out;
    }

}
