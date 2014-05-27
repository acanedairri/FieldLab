package org.irri.fieldlab.adapter;

import java.util.ArrayList;

import org.irri.fieldlab.activity.R;
import org.irri.fieldlab.model.ImagesItemDetails;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageItemListBaseAdapter extends BaseAdapter {
	private static ArrayList<ImagesItemDetails> itemDetailsrrayList;

	private ArrayList<String> listImages;

	private LayoutInflater l_Inflater;

	public ImageItemListBaseAdapter(Context context, ArrayList<ImagesItemDetails> results, ArrayList<String> imagesList) {
		itemDetailsrrayList = results;
		listImages = imagesList;

		l_Inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return itemDetailsrrayList.size();
	}

	public Object getItem(int position) {
		return itemDetailsrrayList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = l_Inflater.inflate(R.layout.imagedetailview, null);
			holder = new ViewHolder();
			holder.txt_itemName = (TextView) convertView.findViewById(R.id.name);
			holder.txt_itemDescription = (TextView) convertView.findViewById(R.id.itemDescription);
			holder.txt_itemPrice = (TextView) convertView.findViewById(R.id.dateCreated);
			holder.itemImage = (ImageView) convertView.findViewById(R.id.photo);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.txt_itemName.setText(itemDetailsrrayList.get(position).getFileName());
		holder.txt_itemDescription.setText(itemDetailsrrayList.get(position).getImageDescription());
		holder.txt_itemPrice.setText(itemDetailsrrayList.get(position).getDateCreated());
		int imageID = itemDetailsrrayList.get(position).getImageID();

		Bitmap bImg = BitmapFactory.decodeFile(listImages.get(imageID));


		holder.itemImage.setImageBitmap(getResizedBitmap(bImg,100,100));
		//		holder.itemImage.setImageResource(imgid[itemDetailsrrayList.get(position).getImageNumber() - 1]);
		//		imageLoader.DisplayImage("http://192.168.1.28:8082/ANDROID/images/BEVE.jpeg", holder.itemImage);

		return convertView;
	}



	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

		int width = bm.getWidth();

		int height = bm.getHeight();

		float scaleWidth = ((float) newWidth) / width;

		float scaleHeight = ((float) newHeight) / height;

		// create a matrix for the manipulation

		Matrix matrix = new Matrix();

		// resize the bit map

		matrix.postScale(scaleWidth, scaleHeight);

		// recreate the new Bitmap

		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

		return resizedBitmap;

	}


	static class ViewHolder {
		TextView txt_itemName;
		TextView txt_itemDescription;
		TextView txt_itemPrice;
		ImageView itemImage;
	}
}
