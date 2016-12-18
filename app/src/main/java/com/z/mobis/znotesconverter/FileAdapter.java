package com.z.mobis.znotesconverter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

class FileAdapter extends BaseAdapter {
	private final static String LOG_TAG = "FileAdapter.";
	private final Activity mContext;
	private final int mState;
	private boolean convertAnable;
	
	private ArrayList<MyFile> mFiles;
	
	public FileAdapter(Activity context, int mState, ArrayList<MyFile> mFiles) {
	    this.mContext = context;
	    this.mState = mState;
	    this.mFiles = mFiles;
	}

	static class MyFile {
		String fileFullPath; // Список файлов и папок в браузере с полными
								// путями
		String fileName; // Список имен файлов и папок в браузере
		String fileDesc; // Список описаний файлов в браузере
		Integer fileTxtColor; // Список цветов файлов в браузере
		Integer fileBkgColor; // Список цветов файлов в браузере
		Long fileEditTime; // Время редактирования
		Boolean fileSync; // Синхронизация
		Long fileSyncTime; // Время синхронизации
		Boolean isFolder; // Список папок браузере
		int icoResId;
		boolean convertable;

		MyFile(String fileFullPath, String fileName, String fileDesc,
			   Integer fileTxtColor, Integer fileBkgColor, Long fileEditTime,
			   Boolean fileSync, Long fileSyncTime, Boolean isFolder, int icoResId, boolean convertable) {
			this.fileFullPath = fileFullPath;
			this.fileName = fileName;
			this.fileDesc = fileDesc;
			this.fileTxtColor = fileTxtColor;
			this.fileBkgColor = fileBkgColor;
			this.fileEditTime = fileEditTime;
			this.fileSync = fileSync;
			this.fileSyncTime = fileSyncTime;
			this.isFolder = isFolder;
			this.icoResId = icoResId;
			this.convertable = convertable;
		}
	}
	
    // Класс для сохранения во внешний класс и для ограничения доступа
    // из потомков класса
    static class ViewHolder {
    	public LinearLayout file_row;
        public ImageView logo;
        public TextView file_name;
		Button convertBtn;
    }
    
	public void myClickHandler(View v) {
		 Log.v(LOG_TAG, "GGGGGG");
	    } 
	 
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		//return this.files.size();
		return this.mFiles.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		//return this.files.get(arg0);
		return this.mFiles.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	
	private class Button_Clicker implements Button.OnClickListener
    {
		@Override
        public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Hello!! button Clicked", Toast.LENGTH_SHORT).show();
    }}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//Log.i(LOG_TAG + "getView position:", Integer.toString(position));
		//books.refr++;
		
		final ViewHolder holder;
		//View itemView = convertView;
		MyFile mFile = mFiles.get(position);
		final Boolean row_isFolder = mFile.isFolder;
		
		String row = mFile.fileName;
		
		//Log.v("getView", "1.3");
	    if (convertView == null) {
	    	//Log.v("itemView", "null");
	    	LayoutInflater inflater = mContext.getLayoutInflater();
	    	convertView = inflater.inflate(R.layout.files_row, parent, false);
	    	holder = new ViewHolder();
	    	holder.file_row = (LinearLayout) convertView.findViewById(R.id.file_row);
		    holder.logo = (ImageView) convertView.findViewById(R.id.logo);
		    holder.file_name = (TextView) convertView.findViewById(R.id.file_name);
		    holder.convertBtn = (Button) convertView.findViewById(R.id.convert_btn);

			Log.v(LOG_TAG + "getView", "holder.file_name = null: " + Boolean.toString(holder.file_name == null));
			Log.v(LOG_TAG + "getView", "MainProperties.FontSizeName: " + Integer.toString(MainProperties.FontSizeName));
		    holder.file_name.setTextSize(MainProperties.FontSizeName);
		    
		    convertView.setTag(holder);
	    } else{
	    	//Log.v("itemView", "<>null");
	    	holder = (ViewHolder) convertView.getTag();
		}

    	holder.file_name.setText(row);
		
	    if (mFile.icoResId != 0) {
			holder.logo.setVisibility(View.VISIBLE);
			holder.logo.setImageResource(mFile.icoResId);

			setConvertBtn(holder, (convertAnable && mFile.convertable), position);
	    }else{
	    	holder.logo.setVisibility(View.GONE);
			holder.convertBtn.setVisibility(View.GONE);
	    }
	    //Log.v(LOG_TAG + "getView", "fin");
	    return convertView;
	}

	private void setConvertBtn(ViewHolder holder, boolean convertable, final int position) {
		if (convertable) {
			holder.convertBtn.setVisibility(View.VISIBLE);
			holder.convertBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					convertListener.onConvert(position);
				}
			});
		}else{
			holder.convertBtn.setVisibility(View.GONE);
		}
	}

	public interface OnConvertListener{
		void onConvert(int position);
	}
	OnConvertListener convertListener;

	public void setConvertAnable(boolean convertAnable, OnConvertListener listener){
		this.convertAnable = convertAnable;
		this.convertListener = listener;
		notifyDataSetChanged();
	}
}
