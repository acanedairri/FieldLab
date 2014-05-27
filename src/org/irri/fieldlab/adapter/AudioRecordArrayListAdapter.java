package org.irri.fieldlab.adapter;



import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.irri.fieldlab.activity.R;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class AudioRecordArrayListAdapter extends ArrayAdapter<File> {
	private final Activity context;
	private final ArrayList<File> values;
	private MediaPlayer mp = new MediaPlayer();
	public AudioRecordArrayListAdapter(Activity context, ArrayList<File>values) {
		super(context,R.layout.audiorecordingrow, values);
		this.context = context;
		this.values = values;
	}

	
	
	  static class ViewHolder {
		    public TextView text;
		    public ImageButton delete;
		    public ImageButton play;
		  }
	
	
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    View rowView = convertView;
	    if (rowView == null) {
	      LayoutInflater inflater =  context.getLayoutInflater();
	      rowView = inflater.inflate(R.layout.audiorecordingrow, null);
	      ViewHolder viewHolder = new ViewHolder();
	      viewHolder.text = (TextView) rowView.findViewById(R.id.title);
	      viewHolder.delete = (ImageButton) rowView.findViewById(R.id.btnDelete);
	      viewHolder.play = (ImageButton) rowView.findViewById(R.id.btnPlay);
	      rowView.setTag(viewHolder);
	    }

	    ViewHolder holder = (ViewHolder) rowView.getTag();
	    String s = values.get(position).getName();
	    holder.text.setText(s);

	    holder.delete.setTag(position);

		holder.play.setTag(new buttonObject(holder.play,holder.delete,values.get(position)));
		holder.delete.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick( View v) {
			
				final int pos = (Integer) v.getTag();
				Log.d("DEBUG","CLICKEDDDDDDDDDDDDDD: " + pos);
				final File selectedFile = values.get( (Integer) v.getTag());
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        case DialogInterface.BUTTON_POSITIVE:
				           if(mp.isPlaying()) mp.stop();
				        	selectedFile.delete();
				            
				            values.remove(pos);
				            AudioRecordArrayListAdapter.this.notifyDataSetChanged();
				            
				            
				            break;

				        case DialogInterface.BUTTON_NEGATIVE:
				            //No button clicked
				            break;
				        }
				    }
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage("Are you sure you want to delete?").setPositiveButton("Yes", dialogClickListener)
				    .setNegativeButton("No", dialogClickListener).show();
				
			}
			
		});
		holder.play.setOnClickListener(new OnClickListener(){

		

			@Override
			public void onClick(View v) {
				
				final buttonObject rowObj = (buttonObject) v.getTag();
				try {
					if(mp.isPlaying()) {
						mp.stop();
						rowObj.play.setImageResource(R.drawable.media_playback_start);
						return;
					}
					 mp = new MediaPlayer();
					mp.setDataSource(rowObj.audioPath.getAbsolutePath());
					mp.prepare();
					mp.start();
					
					
					rowObj.play.setImageResource(R.drawable.media_playback_stop);
					
					mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					    public void onCompletion(MediaPlayer mp) {
					    	rowObj.play.setImageResource(R.drawable.media_playback_start);
					    }
					});
					
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
	    return rowView;
	  }

	
	public class buttonObject {
		public ImageButton play;
		public ImageButton delete;
		public File audioPath;
		
		public buttonObject (ImageButton p, ImageButton d, File file){
			play = p;
			delete = d;
			audioPath = file;
		}
	}
	
	
}
