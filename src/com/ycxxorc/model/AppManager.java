package com.ycxxorc.model;

import org.opencv.core.Mat;

import android.app.Application;

/**
 * @ClassName:com.ycxxorc.model TransMat
 * @author 
 * @Description: 全局图像类（用于不同activity处理）
 * @date 2014-1-6 下午9:38:25
 */
public class AppManager extends Application {
	
	//图像矩阵
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
