package com.ycxxorc.utils;

import java.io.File;
import java.io.FileFilter;


/**
 * @ClassName:com.ycxxorc.utils ImageFileFilter
 * @author 
 * @Description: 图像文件浏览过滤器
 * @date 2014-1-6 下午9:38:25
 */
public class ImageFileFilter implements FileFilter{

	@Override
	public boolean accept(File arg0) {
		String s = arg0.getName();
		if(arg0.isDirectory() || 
				s.endsWith(".png") ||
				s.endsWith(".jpg")||
				s.endsWith(".gif") || 
				s.endsWith("bmp")) {
			return true;
		}else {
			return false;
		}
	}

}
