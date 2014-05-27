package org.irri.fieldlab.adapter;

import java.util.List;

import org.irri.fieldlab.activity.R;
import org.irri.fieldlab.database.manager.DescriptionManager;
import org.irri.fieldlab.model.DescriptionModel;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

public class DescriptionConstantArrayAdapter extends ArrayAdapter<DescriptionModel> {

	private List<DescriptionModel> list;
	private Activity context;
	private DescriptionManager descriptionManager;

	public DescriptionConstantArrayAdapter(Activity context, List<DescriptionModel> list,DescriptionManager descriptionManager) {
		super(context,R.layout.traitconditionrow, list);
		this.context = context;
		this.list = list;
		this.descriptionManager=descriptionManager;
	}

	static class ViewHolder {
		protected TextView traitcode;
		protected TextView txtDataType;
		protected TextView txtValueCondition;
		protected TextView property;
		protected TextView scale;
		protected TextView method;
		protected TableLayout tblVariate;
//		protected ImageView imgInfo;
//		protected ImageView btnHide;
		protected Object tbl;
	}

	public View getView(final int position, final View convertView, ViewGroup parent) {
		View view = null;
		final int pos = position;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.traitconditionrow, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.traitcode = (TextView) view.findViewById(R.id.txtVariateCode);
			viewHolder.txtValueCondition=(EditText) view.findViewById(R.id.txtValueCondition);
			// inside frame
			viewHolder.txtDataType = (TextView) view.findViewById(R.id.txtDataType);
			viewHolder.property = (TextView) view.findViewById(R.id.txtproperty);
			viewHolder.scale = (TextView) view.findViewById(R.id.txtscale);
			viewHolder.method = (TextView) view.findViewById(R.id.txtMethod);
			viewHolder.tblVariate = (TableLayout) view.findViewById(R.id.tableVariateInfo);
//			viewHolder.imgInfo= (ImageView) view.findViewById(R.id.imgInfo);
//			viewHolder.btnHide= (ImageView) view.findViewById(R.id.btnHide);

			view.setTag(viewHolder);
			viewHolder.txtValueCondition.setTag(list.get(position));

		} else {
			view = convertView;
			((ViewHolder) view.getTag()).txtValueCondition.setTag(list.get(position));
		}


		final ViewHolder holder = (ViewHolder) view.getTag();
		holder.traitcode.setText(list.get(position).getTraitcode());
		holder.txtDataType.setVisibility(View.VISIBLE);
		holder.txtValueCondition.setText(String.valueOf(list.get(position).getValue()));
		holder.property.setText(String.valueOf(list.get(position).getProperty()));
		holder.scale.setText(String.valueOf(list.get(position).getScale()));
		holder.method.setText(String.valueOf(list.get(position).getMethod()));


		//we need to update adapter once we finish with editing
		holder.txtValueCondition.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus){
					//final int pos=v.getId();
//					final long id = list.get(position).getId();
//					final EditText minimumvalue = (EditText) v;
					//					traitManager.updateTraitMinimumValue(id, Double.valueOf(minimumvalue.getText().toString()));
					//					list.get(position).setMinimumvalue(Double.valueOf(minimumvalue.getText().toString()));
				}
			}
		});

//		holder.imgInfo.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				holder.tblVariate.setVisibility(View.VISIBLE);
//			}
//		});
//
//		holder.btnHide.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				holder.tblVariate.setVisibility(View.GONE);
//			}
//		});


		String traitDatatype = list.get(position).getDatatype();
		holder.txtDataType.setText(traitDatatype);

		view.refreshDrawableState();
		return view;
	}

}
