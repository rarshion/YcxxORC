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
	private Mat mat;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	/*************************** getter and setter start ****************************/
	public Mat getMat() {
		return mat;
	}

	public void setMat(Mat mat) {
		this.mat = mat;
	}
}
