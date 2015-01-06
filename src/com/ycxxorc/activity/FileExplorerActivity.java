package com.ycxxorc.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import com.example.test.R;
import com.ycxxorc.model.TransMat;
import com.ycxxorc.utils.ImageFileFilter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


/**
 * @ClassName:com.ycxxorc.activity FileExplorerActivity
 * @author 
 * @Description: 图像文件浏览与选择
 * justFortest
 * @date 2014-1-6 下午9:38:25
 */
public class FileExplorerActivity extends Activity {
	
	ListView listView;
	TextView textView;
	File currentParentFile;
	File[] currentFiles;
	EditText editText;
	static File root;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//获取控件
		setContentView(R.layout.file_explorer);
		listView = (ListView) findViewById(R.id.listView1);
		textView = (TextView) findViewById(R.id.path);
		
		//获取外部存储器路径
		root = new Environment().getExternalStorageDirectory();
		if(root.exists()) {
			currentParentFile = root;
			currentFiles = root.listFiles(new ImageFileFilter());
			inflateListView(currentFiles);
		}
	
		//添加文件选择事件监听器
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				//如果为图像文件，则进入图像处理
				if(currentFiles[position].isFile()) {
					String s = currentFiles[position].getAbsolutePath();
					if(s.endsWith(".png") || s.endsWith(".jpg") || s.endsWith("gif") || s.endsWith("bmp")) {
						Mat mat = Highgui.imread(s);
						TransMat transMat = (TransMat)getApplication();
						transMat.setMat(mat);						
						startActivity(new Intent(FileExplorerActivity.this, ModifyActivity.class));
						FileExplorerActivity.this.finish();
					}
					return;
				}
				//如果为路径，则显示文件
				File[] temp = currentFiles[position].listFiles(new ImageFileFilter());
				if(temp == null || temp.length == 0) {
					Toast.makeText(FileExplorerActivity.this,
							"",
							Toast.LENGTH_SHORT).show();
				}
				else {
					currentParentFile = currentFiles[position];
					currentFiles = temp;
					inflateListView(currentFiles);
				}
			}
		});
	}
	
	//显示文件
	private void inflateListView(File[] fileList) {
		
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for(int i=0; i<fileList.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			if(fileList[i].isDirectory()) {
				listItem.put("icon", R.drawable.folder);
			}
			else {
				listItem.put("icon", R.drawable.file);
			}
			listItem.put("fileName", fileList[i].getName());
			listItems.add(listItem);
		}
		
		//显示文件数组适配器
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, 
				listItems, R.layout.file_line,
				new String[]{"icon", "fileName"},
				new int[] {R.id.icon, R.id.fileName});
		listView.setAdapter(simpleAdapter);
		
		try{
			textView.setText("当前路径为："+
			currentParentFile.getCanonicalPath());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			Log.e("onkeypress", keyCode+"");
			try{
				if(!currentParentFile.getCanonicalPath().equals(root.getCanonicalPath()))
				{
//					System.out.println("cParentFile.path="+currentParentFile.getCanonicalPath().toString());
					currentParentFile = currentParentFile.getParentFile();
					currentFiles = currentParentFile.listFiles();
					inflateListView(currentFiles);
				}
				else {
					return super.onKeyDown(keyCode, event);
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	
	
	
	
	
	
}
