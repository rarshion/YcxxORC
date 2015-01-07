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
	private Mat transMat;
	
	//单例
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
