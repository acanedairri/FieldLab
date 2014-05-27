package org.irri.fieldlab.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.irri.fieldlab.adapter.FileArrayAdapter;
import org.irri.fieldlab.model.FileListModel;
import org.irri.fieldlab.utility.FieldLabPath;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class FileChooserActivity extends ListActivity {

	private File currentDir;
	private FileArrayAdapter adapter;
	String folder = null;
	String title="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			folder = extras.getString("folder");
		}
		if(folder.equals("study")){
			//currentDir = (savedInstanceState == null) ? new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/FieldLab/database") : (File) savedInstanceState.getSerializable("CUR_DIR");
			currentDir = (savedInstanceState == null) ? new File(FieldLabPath.STUDY_FOLDER) : (File) savedInstanceState.getSerializable("CUR_DIR");
			title="Study List";
		}else if(folder.equals("workbook")){
			currentDir = (savedInstanceState == null) ? new File(FieldLabPath.WORKBOOK_FOLDER) : (File) savedInstanceState.getSerializable("CUR_DIR");
			title="Select Workbook";
		}else if(folder.equals("maintenance")){
			currentDir = (savedInstanceState == null) ? new File(FieldLabPath.MAINTENANCE_FOLDER) : (File) savedInstanceState.getSerializable("CUR_DIR");
			title="Select File";
		}else if(folder.equals("export")){
			currentDir = (savedInstanceState == null) ? new File(FieldLabPath.EXPORTDATA_FOLDER) : (File) savedInstanceState.getSerializable("CUR_DIR");
			title="Select File";
		}

		fill(currentDir);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("CUR_DIR", currentDir);
	}

	private void fill(File f) {

		File[] dirs = f.listFiles();
		this.setTitle(title);

		List<FileListModel> dir = new ArrayList<FileListModel>();
		List<FileListModel> fls = new ArrayList<FileListModel>();
		try {
			for (File ff : dirs) {
				if (ff.isDirectory())
					dir.add(new FileListModel(ff.getName(), "Folder", ff
							.getAbsolutePath()));
				else {
					if(folder.equals("export")){
						if(!ff.getName().contains("_journal") && ff.getName().contains("csv")){
							fls.add(new FileListModel(ff.getName(), "File Size: "
									+ ff.length(), ff.getAbsolutePath()));
						}
						
					}else{
						if(!ff.getName().contains("-journal")){
							fls.add(new FileListModel(ff.getName(), "File Size: "
									+ ff.length(), ff.getAbsolutePath()));
						}
					}
				}
			}
		}catch (Exception e) {

		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		//		if (!f.getName().equalsIgnoreCase("sdcard"))
		//			dir.add(0, new Option("..", "Parent Directory", f.getParent()));

		adapter = new FileArrayAdapter(FileChooserActivity.this,R.layout.file_view, dir);
		this.setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		FileListModel o = adapter.getItem(position);
		if (o.getData().equalsIgnoreCase("folder")
				|| o.getData().equalsIgnoreCase("parent directory")) {
			currentDir = new File(o.getPath());
			fill(currentDir);
		} else {
			onFileClick(o);
		}
	}

	private void onFileClick(FileListModel o) {
		// Return the file path here!
		//		Toast.makeText(this, "You have chosen: " + o.getName(), Toast.LENGTH_SHORT).show();
		Bundle bundle = new Bundle();
		bundle.putString("FILEPATH", o.getPath());
		bundle.putString("FILENAME", o.getName());

		Intent mIntent = new Intent();
		mIntent.putExtras(bundle);
		setResult(RESULT_OK, mIntent);
		finish();
	}

}
