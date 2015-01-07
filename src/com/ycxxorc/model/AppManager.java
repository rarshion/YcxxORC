package com.ycxxorc.model;

import org.opencv.core.Mat;

import android.app.Application;

/**
 * @ClassName:com.ycxxorc.model TransMat
 * @author 
 * @Description: ȫ��ͼ���ࣨ���ڲ�ͬactivity����
 * @date 2014-1-6 ����9:38:25
 */
public class AppManager extends Application {
	
	//ͼ�����
	private Mat transMat;
	
	//����
	private static AppManager instance;
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}
	
	/*************************** getter and setter start ****************************/
	public Mat getMat() {
		return transMat;
	}

	public void setMat(Mat mat) {
		this.transMat = mat;
	}

	public static AppManager getInstance() {
		return instance;
	}
	
}
