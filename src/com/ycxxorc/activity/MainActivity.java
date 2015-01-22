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
import com.ycxxorc.model.TransMat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * @ClassName:com.ycxxorc.model TransMat
 * @author 
 * @Description: ͼ��ɼ�����ʾ
 * @date 2014-1-6 ����9:38:25
 */
public class MainActivity extends Activity implements CvCameraViewListener2 {
	
	private CameraBridgeViewBase mOpenCvCameraView;	//ͼ����ʾ����
	private SeekBar setZoomSeekBar;
	
	
	//�˵�ѡ��
	private static int state = 0x00;//ͼ��Ԥ����ѡ��
	static final int CANNY = 0x01;//��ֵ��
	static final int THRESHOLD = 0x02;//��ֵ
	static final int GRAY = 0x03;//�ҶȻ�
	static final int PLAIN = 0x04;
	static final int READ = 0x05;//��ȡ
	static final int SAVE = 0x06;//�洢
	static final int OPENLIGHT =  0x07;//���������
	static final int CLOSELIGHT = 0x08;//�ر������
	
	private Camera camera;
	private static double thresh = 128;//��ֵ��С
	private Mat  mIntermediateMat;//��activity�е�mat����
	private Mat  transportMat;//��Ҫ�ڲ�ͬactivity�д����mat����
	private boolean isOpenCameraLight = false;//�Ƿ���Ҫ������ͷ�����
	private boolean isOpenNextActivity = false;//�Ƿ�ʼ��һ��activity
	
	private static final String  TAG = "ORCDemo::Activity";
	
	//��ȫ�ط�������ͷ
	private static Camera getCameraInstance(){
		Camera  c = null;
		try{
			c = Camera.open();
		}catch(Exception e){
			Log.d(TAG, "Erro is" + e.toString());
		}
		return c;
	}
	
	//����豸�Ƿ�֧������ͷ 
    private boolean CheckCameraHardware(Context mContext)  
    {  
        if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))  
        {   
            // ����ͷ����   
            return true;   
        } else {   
            // ����ͷ������   
            return false;   
        }   
    }  
	
	
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
        //�������
        setZoomSeekBar = (SeekBar)findViewById(R.id.FocalLengthSeekBar);
        setZoomSeekBar.setMax(camera.getParameters().getMaxZoom());
        setZoomSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				Log.i(TAG, "zoom change to" + arg1);
				setZoom(setZoomSeekBar.getProgress());
			}
		});
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
	        Mat t = new Mat();
	    	rgbaInnerWindow.copyTo(t);
	    	transportMat = t;
	        rgbaInnerWindow.release();//�ͷ�	      
	        return rgba;
	}
    
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(isOpenNextActivity) 
		{return super.onTouchEvent(event);}
		Log.e(TAG, "capture image");
		if(transportMat == null) {
			Log.e("see", "transportMat is null");
			return super.onTouchEvent(event);
		}
		//ͨ��Intent�л�activity
		Intent intent = new Intent(MainActivity.this, ModifyActivity.class);
		//��ȡ��application�б����ͼ��֡
		TransMat myApp = ((TransMat)getApplication());
		Mat t = new Mat();
		transportMat.copyTo(t);
		myApp.setMat(t);
		Log.e(TAG, t.size().width+" "+t.size().height);
		isOpenNextActivity = true;
		startActivityForResult(intent, 0);
		return super.onTouchEvent(event);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==0 && resultCode==0) {
			isOpenNextActivity = false;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
    
    @Override
	public void onResume()
	{
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
	}

    
    public void Pause()
    {
    	super.onPause();
    	if(mOpenCvCameraView != null)
    		mOpenCvCameraView.disableView();
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0,OPENLIGHT,0,"OpenLigth");
    	menu.add(0,CLOSELIGHT,0,"CloseLigth");
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
        if(id == OPENLIGHT || id == CLOSELIGHT ){
        	openLight(id);
        }
        return super.onOptionsItemSelected(item);
    }

    
	@Override
	public void onCameraViewStarted(int arg0, int arg1) {
		camera = getCameraInstance();
		mIntermediateMat = new Mat();//��ʼ��ʾ����ͷԤ��ʱ�½�mat����
	}

	@Override
	public void onCameraViewStopped() {
		if(mIntermediateMat != null)
			mIntermediateMat.release();//ֹͣ��ʾ����ͷԤ��ʱ����mat����
		mIntermediateMat = null;
	}
	
	//��/�ر� �����
	private void openLight(int mode){
		Log.i(TAG,"Camera Light change");
		if(camera != null){
			Parameters parameter = camera.getParameters();
			if(mode == OPENLIGHT)
				parameter.setFlashMode(Parameters.FLASH_MODE_TORCH);
			else if(mode == CLOSELIGHT)
				parameter.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(parameter);
		}
	
	}
	
	//�������
	private void setZoom(int value){
		if(camera != null){
			Parameters params=camera.getParameters();  
	        params.setZoom(value);  
	        camera.setParameters(params);
		}
	}
	

	
}
