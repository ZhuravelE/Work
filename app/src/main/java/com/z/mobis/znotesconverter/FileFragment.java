package com.z.mobis.znotesconverter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FileFragment extends Fragment {
	
	public static FileFragment newInstance(int mState) {
		FileFragment f = new FileFragment();
        Bundle args = new Bundle();
        args.putInt(ext_mState, mState);
        f.setArguments(args);

        return f;
    }

	// static notes notes;
	private Activity mActivity;
	private Context context;

	public static final String ext_mState = "mState";

	private int mState;

	private static String APP_FOLDER = "/zNotes/";
	public static String dbPath = Environment.getExternalStorageDirectory() + APP_FOLDER;
	public static String DB_TYPE = ".dbz";

	public static final int PICK_DB_FOR_OPEN = 31;
	// static final int PICK_DB_FOR_IMPORT = 32;
	public static final int PICK_DIRECTORY = 33;
	public static final int PICK_FILE_FOR_ADD_ATTACH = 34;
	public static final int PICK_FILE_FOR_CHANGE_ITEM_ATTACH = 35;
	public static final int PICK_FILE_FOR_CHANGE_PARENT_ATTACH = 36;
	// static final int PICK_PICTURE_FOR_ATTACH = 35;
	public static final int PICK_DB_FOR_RESTORE = 37;
	static final int PICK_DB_FOR_SYNC = 38;

	private final static String LOG_TAG = "FM.";

	private TextView textInfo;
	private TextView titleManager;
	private ListView lv;

	private File currentDirectory = new File("/"); // Текущая директория

	public static ArrayList<FileAdapter.MyFile> mFiles = new ArrayList<>();

	private List<String> fileType = new ArrayList<>();
	private int folderLevel = 0; // Уровень вложения папки для восстановления. При 2-е
							// восстановить вложения
							private int clrBackground;

	private static Locale locale;

	// when application started
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.v(LOG_TAG + "onCreateView", "1");
		View v = inflater.inflate(R.layout.file_manager, container, false);

		locale = getResources().getConfiguration().locale;

		lv = (ListView) v.findViewById(R.id.list);
		textInfo = (TextView) v.findViewById(R.id.textInfo);
		titleManager = (TextView) v.findViewById(R.id.titleManager);

		mState = getArguments().getInt("mState");

		textInfo.setVisibility(View.GONE);
		titleManager.setVisibility(View.GONE);

		lv.setOnItemClickListener((parent, view, position, id) -> {
			lv.setItemChecked(position, true);
			onListItemClick(position);
		});
		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActivity = getActivity();
		context = mActivity;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.v(LOG_TAG + "onActCr-d()", "1");

	}

	@Override
	public void onStart() {
		super.onStart();

		titleManager.setVisibility(View.GONE);
		fileType.add(DB_TYPE);
		fileType.add(".txt");
		fileType.add(".htm");
		fileType.add(".dbz-journal");

		browseTo(getExistParent(new File(dbPath)));
	}

	private File getExistParent(File dir){
		while (!dir.exists()) {
			dir = dir.getParentFile();
		}
		return dir;
	}

	private void convertBook(int position, int format) {
		/*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String dbName = prefs.getString("LibraryName", OpenDbHelper.DEFAULT_DB_NAME);*/

		String selectedFileString = mFiles.get(position).fileFullPath;

		if (!selectedFileString.equals("..")) {
			// browse to clicked file or directory using browseTo()
			File clickedFile = new File(selectedFileString);

			int fileNameLen = clickedFile.getName().lastIndexOf(".");
			final String fileName;
			String extension = "";
			if (fileNameLen > 0) {
				fileName = clickedFile.getName().substring(0, fileNameLen);
			} else {
				fileName = clickedFile.getName();
			}

			switch (format) {
				case 0:	//"txt"
					new AsyncConverter().execute(FileConverter.getConverterToTxt(getContext(), dbPath, fileName));
					break;
				case 1:	//"htm"
					new AsyncConverter().execute(FileConverter.getConverterToHtml(getContext(), dbPath, fileName));
					break;
			}

		}
	}

	class AsyncConverter extends AsyncTask<FileConverter, Integer, Void>{
		ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.please_wait), true, true);
		}

		@Override
		protected Void doInBackground(FileConverter... params) {
			params[0].convert();
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			progressDialog.cancel();
			Toast.makeText(context, R.string.conversion_completed, Toast.LENGTH_SHORT).show();
			files(currentDirectory.listFiles());
		}
	}

	// browse to parent directory
	private void upOneLevel() {
		Log.v("FileManager upOneLevel", " ");
		if (currentDirectory.getParent() != null) {
			// Log.v("FileManager", "currentDirectory");
			if (mState == PICK_DB_FOR_RESTORE) {
				folderLevel--;
			}
			browseTo(currentDirectory.getParentFile());
		}
	}

	// browse to file or directory
	private void browseTo(final File aDirectory) {
		Log.v("FileManager browseTo", aDirectory.toString());
		// if we want to browse directory
		if (aDirectory.isDirectory()) {
			Log.v("FileManager browseTo", "isDirectory");
			// fill list with files from this directory
			currentDirectory = aDirectory;
			files(aDirectory.listFiles());
			// titleManager.setText(aDirectory.getAbsolutePath());
			titleManager.setText(aDirectory.getPath());
		} else {
			Log.v("FileManager browseTo", "isFile");
			pickFileOrDirectory(aDirectory);
		}
	}

	// fill list
	private void files(File[] files) {
		//Log.v("FileManager.files", "1");
		mFiles.clear();
		Log.i("FileManager.files", "currentDirectory: " + currentDirectory.toString());

		if ((currentDirectory.getParent() != null)) {
			if (!currentDirectory.toString().equals(Environment
					.getExternalStorageDirectory().getPath())) {
				if (!(mState == PICK_DB_FOR_RESTORE && folderLevel == 0)) {
					mFiles.add(new FileAdapter.MyFile("..", "..", "", -1, 0,
							null, false, null, true, R.drawable.bt_left, false));
				}
			}
		}

		// add every file into list
		for (File file : files) {
			// this.directoryEntries.add(file.getAbsolutePath());
			if (file.canWrite() && !file.isHidden()) {
				if (file.isFile()) {
					int endName = file.getName().lastIndexOf(".");
					// Log.i("file", file.getName());
					// Log.v("endNameIndex", Integer.toString(endName));
					if (endName > 0) {
						// Log.v("endName", file.getName().substring(endName));
						//fileName = fileName.substring(0, endName);
						switch (mState) {
						case PICK_DB_FOR_OPEN:
						case PICK_DB_FOR_SYNC:
							// Log.v("file", "PICK_DB");
							String fileName = file.getName();
							switch (fileType.indexOf(fileName
									.substring(endName).toLowerCase(locale))) {
								case 0:	//dbz
									mFiles.add(new FileAdapter.MyFile(file.getAbsolutePath(),
											fileName, fileName,
											-1, 0, file.lastModified(), false, 0L, false, R.drawable.ic_znote, true));
									break;
								case 1:	//txt
									mFiles.add(new FileAdapter.MyFile(file.getAbsolutePath(),
											fileName, fileName,
											-1, 0, file.lastModified(), false, 0L, false, R.drawable.ic_file_txt, false));
									break;
								case 2:	//htm
									mFiles.add(new FileAdapter.MyFile(file.getAbsolutePath(),
											fileName, fileName,
											-1, 0, file.lastModified(), false, 0L, false, R.drawable.ic_file_htm, false));
									break;
								case 3:	//".dbz-journal"
									break;
								default:
									mFiles.add(new FileAdapter.MyFile(file.getAbsolutePath(),
											fileName, fileName,
											-1, 0, file.lastModified(), false, 0L, false, R.drawable.ic_file, false));
									break;
							}
							break;
						}
					}
				} else {
					String fileName = file.getName();
					if (fileName.equals("zNotes")) {
						mFiles.add(new FileAdapter.MyFile(file.getAbsolutePath(), fileName,
								fileName, -1, 0, file.lastModified(), false, null, false, R.drawable.ic_znote, false));
					}else{
						mFiles.add(new FileAdapter.MyFile(file.getAbsolutePath(), fileName,
								fileName, -1, 0, file.lastModified(), false, null, false, R.drawable.ic_folder, false));
					}
				}
			}
		}
		// create array adapter to show everything
		Log.i("FileManager.files", "directories qty: " + Integer.toString(mFiles.size()));

		updateAdapter();
	}

	private void updateAdapter() {
		FileAdapter adapter = new FileAdapter(getActivity(), mState, mFiles);
		lv.setAdapter(adapter);
		adapter.setConvertAnable(true, (position) ->
				showConvertDialog(position));
	}

	private void showConvertDialog(final int position) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
		dialog.setTitle(R.string.peak_format);
		ArrayList<String> formats = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.convert_format)));

		String fName = mFiles.get(position).fileName;
		int extPos = fName.lastIndexOf(".");

		if (extPos > -1) {
			String extension = fName.substring(extPos + 1);
			if (formats.get(0).contains(extension)){
				formats.remove(0);
			}else {
				formats.remove(extension);
			}
			dialog.setSingleChoiceItems(formats.toArray(new String[]{}), 1, null);
			dialog.setPositiveButton("OK", (d, which) -> {
				ListView lv = ((AlertDialog) d).getListView();
				convertBook(position, lv.getCheckedItemPosition());
			});
			dialog.setNegativeButton("Cancel", (DialogInterface d, int which) ->
					d.dismiss());
			dialog.show();
		}
	}

	// when you clicked onto item
	// @Override
	private void onListItemClick(int position) {
		// Log.v("FM onListItemClick", "position - " +
		// Integer.toString(position));
		Log.v(LOG_TAG + "onListItemClick",
				"directoryEntries - " + Integer.toString(mFiles.size()) + " - "
						+ mFiles.get(position).fileFullPath);
		// get selected file name
		String selectedFileString = mFiles.get(position).fileFullPath;
		// Log.v("FM selectedFileString", selectedFileString);
		// if we select ".." then go upper

		if (selectedFileString.equals("..")) {
			upOneLevel();
		} else {
			// browse to clicked file or directory using browseTo()
			File clickedFile = new File(selectedFileString);
			folderLevel++;
			browseTo(clickedFile);
		}
	}

	private void pickFileOrDirectory(File aDirectory) {
		Log.v(LOG_TAG + "pickFileOrDirectory", "aDirectory: " + aDirectory.getName());
		
		int nameLen = aDirectory.getName().lastIndexOf(DB_TYPE);
		String fileName;
		if (nameLen > 0) {
			fileName = aDirectory.getName().substring(0, nameLen);
			launchZNotes(fileName);
		} else {
			launchFile(aDirectory);
		}
	}

	private void launchZNotes(String DB_NAME){
		Intent launch = getActivity().getPackageManager().getLaunchIntentForPackage("com.z.MobiS.notes");
		if (launch != null){
			launch.setAction("com.z.MobiS.notes.action.widgetStart");
			launch.putExtra("ParentDB", DB_NAME);
			startActivity(launch);
		}
	}

	private void launchFile(File file){
		int nameLen = file.getName().lastIndexOf(".");
		String extension = file.getName().substring(nameLen + 1);
		String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		Intent launch = new Intent(Intent.ACTION_VIEW);
		launch.setDataAndType(Uri.fromFile(file), mime);
		startActivity(launch);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return (super.onOptionsItemSelected(item));
	}
}
