package com.ycxxorc.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import com.example.test.R;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.ycxxorc.model.TransMat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * @ClassName:com.ycxxorc.model TransMat
 * @author 
 * @Description: 图像预处理与字符识别
 * @date 2014-1-6 下午9:38:25
 */
public class ModifyActivity extends Activity {

	private final String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	private TransMat transMat;
	private ImageView imgView;
	private SeekBar seekBar;
	private Mat mat;
	private Mat firstMat;
	private Bitmap currentBitmap;//当前处理的bitmap格式图像帧
	private ArrayList<MatOfPoint> list;	
	private ArrayList<MatOfPoint> templateList;
	private static double thresh = 0;//全局图像阈值
	
	final int FIND_CONTOUR = 0x11b;
	final int TEST = 0x11c;
	final int DILATE = 0x11d;
	final int ERODE = 0x120;
	final int GRAY = 0x121;
	final int SHARPEN = 0x122;
	final int THRESHOLD = 0x11f;
	final int BLUR = 0x123;
	final int SAVE = 0x11e;
	
	//调用识别引擎API
	private TessBaseAPI baseApi = new TessBaseAPI();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ModifyActivity.this.setResult(0);
		setContentView(R.layout.modify_activity);
		//获取控件
		imgView = (ImageView)findViewById(R.id.modifyImageView);
		seekBar = (SeekBar)findViewById(R.id.threadHoldSeekBar);
		//添加滑动条响应事件
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				thresh = 255.0 * arg1 / 100;
				threshold();
				Log.e("see", thresh+"");
			}
		});
		
		//获取采集到的图像帧
		transMat = (TransMat)getApplication();
		mat = transMat.getMat();
		firstMat = mat;
		Log.e("see", mat.size().width+" "+mat.size().height );
		changeImage();
		//加载字符字典
		baseApi.init("mnt/sdcard/sdcard2", "newl");
	}
	
	//显示图像处理操作菜单
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, BLUR, 0, "blur");
		menu.add(0, SHARPEN, 0, "sharpen");
		menu.add(0, FIND_CONTOUR, 0, "find contour");
		menu.add(0, TEST, 0, "test");
		menu.add(0, DILATE, 0, "dilate");
		menu.add(0, ERODE, 0, "erode");
		menu.add(0, GRAY, 0, "gray");
		menu.add(0, THRESHOLD, 0, "threshold");
		menu.add(0, SAVE, 0, "save");
		return super.onCreateOptionsMenu(menu);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String s = "";
		switch(item.getItemId()) {
		case BLUR:
			blur();
			break;
		case FIND_CONTOUR:
			drawContours();
			break;
		case SHARPEN:
			sharpen();
			break;
		case TEST:
//			startActivity(new Intent(ModifyActivity.this, TestActivity.class));
			test();
			break;
		case DILATE:
			dilate();
			break;
		case ERODE:
			erode();
			break;
		case GRAY:
			gray();
			break;     
		case THRESHOLD:
			threshold();
			break;
		case SAVE:
			try {
				save();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void drawContours() {
		Mat t = new Mat();
		Mat temp = new Mat(mat.size(), CvType.CV_8UC1);
		if(mat.type()==CvType.CV_8UC1 || mat.type()==CvType.CV_32SC1) {
			temp = mat;
		}else {
			Imgproc.cvtColor(mat, temp, Imgproc.COLOR_BGR2GRAY);
		}
		list = new ArrayList<MatOfPoint>();
		Imgproc.findContours(temp, list, t, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
		Log.e("see", "listSize=" + list.size());
		for(int i=0; i<list.size(); i++) {
			if(list.get(i).size().area() < 32) {
				list.remove(i);
			}
			
			Imgproc.boundingRect(list.get(i));
		}
		Mat result = new Mat(mat.size(), CvType.CV_8UC1, new Scalar(0));
		Imgproc.drawContours(result, list, -1, new Scalar(255), 1);
		mat = result;
		changeImage();
	}
	
	private void dilate() {
		Mat result = new Mat(mat.size(), CvType.CV_8U, new Scalar(255));
		Imgproc.dilate(mat, result, new Mat(), new Point(1,1), 1);
		mat = result;
		changeImage();
	}
	
	private void erode() {
		Mat result = new Mat(mat.size(), CvType.CV_8U, new Scalar(255));
		Imgproc.erode(mat, result, new Mat(), new Point(1,1), 1);
		mat = result;
		changeImage();
	}
	
	//灰度化
	private void gray() {
		Mat result = new Mat(mat.size(), CvType.CV_8U, new Scalar(255));
		Imgproc.cvtColor(mat, result, Imgproc.COLOR_BGR2GRAY);
		mat = result;
		changeImage();
	}
	
	private void test() {
		String send = "";		
		baseApi.setImage(currentBitmap);
		send = baseApi.getUTF8Text();
		baseApi.clear();
		Toast.makeText(this, send, Toast.LENGTH_LONG).show();
	}
	
	//保存图像
	private void save() throws IOException {
		File f = new File("mnt/sdcard/sdcard2/just4test.png");
		if(f.exists()) f.delete();
		FileOutputStream fos = new FileOutputStream(f);
		currentBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
		fos.flush();
		fos.close();
	}
	
	
	private void initTemplate() {
		Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.test);
		Mat t = new Mat();
		Utils.bitmapToMat(b, t);
		Mat mediaMat = new Mat();
		Imgproc.cvtColor(t, mediaMat, Imgproc.COLOR_BGRA2GRAY);
		
		Mat tt = new Mat();
		templateList = new ArrayList<MatOfPoint>();
		Imgproc.findContours(mediaMat, templateList, tt, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
		Log.e("see", "listSize=" + templateList.size());
	}
	
	//转化图像帧格式
	private void changeImage() {
		int h = (int)mat.size().height;
		int w = (int)mat.size().width;
		currentBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(mat, currentBitmap);
		imgView.setImageBitmap(currentBitmap);
	}
	
	private void threshold() {
		Mat t = new Mat();
		Imgproc.threshold(firstMat, t, thresh, 255, Imgproc.THRESH_BINARY);
		mat = t;
		changeImage();
	}
	
	//锐化
	private void sharpen() {
		Mat result = new Mat(mat.size(), CvType.CV_8U, new Scalar(255));
		Mat kernel = new Mat(3, 3, CvType.CV_32F, new Scalar(0));
		kernel.put(1, 1, 5.0);
		kernel.put(0, 1, -1.0);
		kernel.put(1, 0, -1.0);
		kernel.put(1, 2, -1.0);
		kernel.put(2, 1, -1.0);
		Imgproc.filter2D(mat, result, mat.depth(), kernel);
		mat = result;
		changeImage();
	}
	
	//
	private void blur() {
		Mat result = new Mat(mat.size(), CvType.CV_8U, new Scalar(255));
		Imgproc.medianBlur(mat, result, 5);
		mat = result;
		changeImage();
	}
	
	
}
