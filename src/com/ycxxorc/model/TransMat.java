package com.ycxxorc.model;

import org.opencv.core.Mat;

import android.app.Application;

/**
 * @ClassName:com.ycxxorc.model TransMat
 * @author 
 * @Description: ȫ��ͼ���ࣨ���ڲ�ͬactivity����
 * @date 2014-1-6 ����9:38:25
 */
public class TransMat extends Application {
	
	//ͼ�����
	private Mat mat;
	
	/*************************** getter and setter start ****************************/
	public Mat getMat() {
		return mat;
	}

	public void setMat(Mat mat) {
		this.mat = mat;
	}
}
