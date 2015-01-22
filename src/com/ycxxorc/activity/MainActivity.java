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
 * @Description: 图像采集与显示
 * @date 2014-1-6 下午9:38:25
 */
public class MainActivity extends Activity implements CvCameraViewListener2 {
	
	private CameraBridgeViewBase mOpenCvCameraView;	//图像显示界面
	private SeekBar setZoomSeekBar;
	
	
	//菜单选项
	private static int state = 0x00;//图像预处理选择
	static final int CANNY = 0x01;//二值化
	static final int THRESHOLD = 0x02;//阈值
	static final int GRAY = 0x03;//灰度化
	static final int PLAIN = 0x04;
	static final int READ = 0x05;//读取
	static final int SAVE = 0x06;//存储
	static final int OPENLIGHT =  0x07;//开启闪光灯
	static final int CLOSELIGHT = 0x08;//关闭闪光灯
	
	private Camera camera;
	private static double thresh = 128;//阈值大小
	private Mat  mIntermediateMat;//本activity中的mat对象
	private Mat  transportMat;//需要在不同activity中传输的mat对象
	private boolean isOpenCameraLight = false;//是否需要打开摄像头闪光灯
	private boolean isOpenNextActivity = false;//是否开始下一个activity
	
	private static final String  TAG = "ORCDemo::Activity";
	
	//安全地访问摄像头
	private static Camera getCameraInstance(){
		Camera  c = null;
		try{
			c = Camera.open();
		}catch(Exception e){
			Log.d(TAG, "Erro is" + e.toString());
		}
		return c;
	}
	
	//检查设备是否支持摄像头 
    private boolean CheckCameraHardware(Context mContext)  
    {  
        if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))  
        {   
            // 摄像头存在   
            return true;   
        } else {   
            // 摄像头不存在   
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
        //一直保持在屏幕上
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main_activity);
        //获取图像显示界面与设置界面监听
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.image_manipulations_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        //焦距调节
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
    
   
    //图像采集回调函数
    @Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
    	
		 	Mat rgba = inputFrame.rgba();//获取摄像头采集的图像帧
	        Size sizeRgba = rgba.size();//获取图像帧大小
	        Mat rgbaInnerWindow;//在边框内的rgba图像
	        int rows = (int) sizeRgba.height;
	        int cols = (int) sizeRgba.width;
	        int left = cols / 8;
	        int top = rows * 3 / 8;
	        int width = cols * 3 / 4;
	        int height = rows * 1 / 8;
	        //截取中间边框内的图像
	        rgbaInnerWindow = rgba.submat(top, top + height, left, left + width);
	        Imgproc.Canny(rgbaInnerWindow, mIntermediateMat, 80, 90);
	        Mat t = new Mat();
	    	rgbaInnerWindow.copyTo(t);
	    	transportMat = t;
	        rgbaInnerWindow.release();//释放	      
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
		//通过Intent切换activity
		Intent intent = new Intent(MainActivity.this, ModifyActivity.class);
		//获取在application中保存的图像帧
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
		mIntermediateMat = new Mat();//开始显示摄像头预览时新建mat对象
	}

	@Override
	public void onCameraViewStopped() {
		if(mIntermediateMat != null)
			mIntermediateMat.release();//停止显示摄像头预览时销毁mat对象
		mIntermediateMat = null;
	}
	
	//打开/关闭 闪光灯
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
	
	//焦距调节
	private void setZoom(int value){
		if(camera != null){
			Parameters params=camera.getParameters();  
	        params.setZoom(value);  
	        camera.setParameters(params);
		}
	}
	

	
}
