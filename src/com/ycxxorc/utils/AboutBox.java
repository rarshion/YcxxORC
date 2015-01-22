package com.ycxxorc.utils;

import com.example.test.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @ClassName:com.ycxxorc.activity FileExplorerActivity
 * @author 
 * @Description: 关于窗口
 * @date 2014-1-8 下午10:40:25
 */
public class AboutBox {

	static String versionName(Context context){
		try{
			return context.getPackageManager().getPackageInfo(context.getPackageName(),
					0).versionName;
		}catch(NameNotFoundException ex){
			return "UnKown";
		}
	}
	
	public static void Show(Activity callingActivity){
		
		SpannableString aboutText = new SpannableString("Version" +
				versionName(callingActivity));//+ "\n\n" + 
				//callingActivity.getString(R.id.about);
		View about;
		TextView tvAbout;
		try{
			LayoutInflater inflater = callingActivity.getLayoutInflater();
			about = inflater.inflate(R.layout.about_box, 
					(ViewGroup)callingActivity.findViewById(R.id.aboutLayout));
			tvAbout = (TextView)about.findViewById(R.id.aboutText);
		}catch(InflateException ex){
			about = tvAbout = new TextView(callingActivity);
		}
		
		tvAbout.setText(aboutText);
		Linkify.addLinks(tvAbout, Linkify.ALL);
		new AlertDialog.Builder(callingActivity)
			.setTitle("关于 " + callingActivity.getString(R.string.app_name))
			.setCancelable(true)
			.setIcon(R.drawable.ic_launcher)
			.setPositiveButton("OK", null)
			.setView(about);
	}
	
	
}
