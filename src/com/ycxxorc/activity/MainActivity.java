package com.ycxxorc.activity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.example.test.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

/**
 * @ClassName:com.ycxxorc.model TransMat
 * @author 
 * @Description: ͼ��ɼ�����ʾ
 * @date 2014-1-6 ����9:38:25
 */
public class MainActivity extends Activity implements CvCameraViewListener2 {
	
	private CameraBridgeViewBase mOpenCvCameraView;	//ͼ����ʾ����
	
	//�˵�ѡ��
	private static int state = 0x00;//ͼ��Ԥ����ѡ��
	static final int CANNY = 0x01;//��ֵ��
	static final int THRESHOLD = 0x02;//��ֵ
	static final int GRAY = 0x03;//�ҶȻ�
	static final int PLAIN = 0x04;
	static final int READ = 0x05;//��ȡ
	static final int SAVE = 0x06;//�洢
	
	
	private static double thresh = 128;//��ֵ��С
	private Mat  mIntermediateMat;//��activity�е�mat����
	private Mat  transportMat;//��Ҫ�ڲ�ͬactivity�д����mat����
	private boolean isOpenCameraLight = false;//�Ƿ���Ҫ������ͷ�����
	private boolean isOpenNextActivity = false;//�Ƿ�ʼ��һ��activity
	
	private static final String  TAG = "ORCDemo::Activity";
	
	private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } 
                break;
                default:
                {
                    super.onManagerConnected(status);
                } 
                break;
            }
        }
    };
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //һֱ��������Ļ��
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main_activity);
        //��ȡͼ����ʾ���������ý������
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.image_manipulations_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    
    //ͼ��ɼ��ص�����
    @Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
    	
		 	Mat rgba = inputFrame.rgba();//��ȡ����ͷ�ɼ���ͼ��֡
	        Size sizeRgba = rgba.size();//��ȡͼ��֡��С
	        Mat rgbaInnerWindow;//�ڱ߿��ڵ�rgbaͼ��
	        
	        int rows = (int) sizeRgba.height;
	        int cols = (int) sizeRgba.width;
	        int left = cols / 8;
	        int top = rows * 3 / 8;
	        int width = cols * 3 / 4;
	        int height = rows * 1 / 8;
	        
	        //��ȡ�м�߿��ڵ�ͼ��
	        rgbaInnerWindow = rgba.submat(top, top + height, left, left + width);
	        Imgproc.Canny(rgbaInnerWindow, mIntermediateMat, 80, 90);
	      
//	      if(state==CANNY) { 
//	    	  Imgproc.Canny(rgbaInnerWindow, mIntermediateMat, thresh, 200);
////	    	  Imgproc.Canny(rgbaInnerWindow, mIntermediateMat, 80, 90);
//	    	  Imgproc.cvtColor(mIntermediateMat, rgbaInnerWindow, Imgproc.COLOR_GRAY2BGRA, 4);
//	    	  transportMat = mIntermediateMat;
//	      }
//	      if(state==THRESHOLD) {
//	    	  Imgproc.cvtColor(rgbaInnerWindow, mIntermediateMat, Imgproc.COLOR_BGR2GRAY);
//	    	  Mat t = new Mat();
////	    	  Imgproc.threshold(mIntermediateMat, t, 128, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C);
//	    	  Imgproc.threshold(mIntermediateMat, t, thresh, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C);
//	    	  Imgproc.cvtColor(t, rgbaInnerWindow, Imgproc.COLOR_GRAY2BGRA);
//	    	  transportMat = t;
//	      }
//	      if(state==GRAY) {
//	    	  Imgproc.cvtColor(rgbaInnerWindow, mIntermediateMat, Imgproc.COLOR_BGR2GRAY);
//	    	  transportMat = mIntermediateMat;
//	    	  Imgproc.cvtColor(mIntermediateMat, rgbaInnerWindow, Imgproc.COLOR_GRAY2BGR);
//	      }
//	      if(state==PLAIN) {
//	    	  Mat t = new Mat();
//	    	  rgbaInnerWindow.copyTo(t);
//	    	  transportMat = t;
//	      }
	      rgbaInnerWindow.release();//�ͷ�	      
	      return rgba;
	}
    
//    @Override
//    public void Pause()
//    {
//    	super.onPause();
//    	if(mOpenCvCameraView != null)
//    		mOpenCvCameraView.disableView();
//    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, CANNY, 0, "Canny");
		menu.add(0, THRESHOLD, 0, "Threshold");
		menu.add(0, READ, 0, "Read");
		menu.add(0, GRAY, 0, "Gray");
		menu.add(0, PLAIN, 0, "Plain");
		return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
        int id = item.getItemId();
        
        //if(id ==  )
        return super.onOptionsItemSelected(item);
    }

    
	@Override
	public void onCameraViewStarted(int arg0, int arg1) {
		mIntermediateMat = new Mat();//��ʼ��ʾ����ͷԤ��ʱ�½�mat����
	}

	@Override
	public void onCameraViewStopped() {
		if(mIntermediateMat != null)
			mIntermediateMat.release();//ֹͣ��ʾ����ͷԤ��ʱ����mat����
		mIntermediateMat = null;
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
	}

	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}
}
