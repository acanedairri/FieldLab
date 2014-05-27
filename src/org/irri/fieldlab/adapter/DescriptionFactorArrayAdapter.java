package org.irri.fieldlab.adapter;

import java.util.List;

import org.irri.fieldlab.activity.R;
import org.irri.fieldlab.database.manager.DescriptionManager;
import org.irri.fieldlab.model.DescriptionModel;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TextView;

public class DescriptionFactorArrayAdapter extends ArrayAdapter<DescriptionModel> {

	private List<DescriptionModel> list;
	private Activity context;
	private DescriptionManager descriptionManager;

	public DescriptionFactorArrayAdapter(Activity context, List<DescriptionModel> list,DescriptionManager descriptionManager) {
		super(context,R.layout.traitfactorrow, list);
		this.context = context;
		this.list = list;
		this.descriptionManager=descriptionManager;
	}

	static class ViewHolder {
		protected TextView traitcode;
		protected CheckBox checkbox;
		protected TextView txtDataType;
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
			view = inflator.inflate(R.layout.traitfactorrow, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.traitcode = (TextView) view.findViewById(R.id.txtVariateCode);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
			viewHolder.txtDataType = (TextView)view.findViewById(R.id.txtDataType);
			// inside frame
			viewHolder.property = (TextView) view.findViewById(R.id.txtproperty);
			viewHolder.scale = (TextView) view.findViewById(R.id.txtscale);
			viewHolder.method = (TextView) view.findViewById(R.id.txtMethod);
			viewHolder.tblVariate = (TableLayout) view.findViewById(R.id.tableVariateInfo);
//			viewHolder.imgInfo= (ImageView) view.findViewById(R.id.imgInfo);
//			viewHolder.btnHide= (ImageView) view.findViewById(R.id.btnHide);
			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(list.get(position));

		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
		}


		final ViewHolder holder = (ViewHolder) view.getTag();
		holder.traitcode.setText(list.get(position).getTraitcode());
		holder.checkbox.setChecked(list.get(position).isSelected());
		holder.property.setText(String.valueOf(list.get(position).getProperty()));
		holder.scale.setText(String.valueOf(list.get(position).getScale()));
		holder.method.setText(String.valueOf(list.get(position).getMethod()));

		holder.checkbox
		.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				DescriptionModel element = (DescriptionModel) holder.checkbox.getTag();
				element.setSelected(buttonView.isChecked());
				if(element.isSelected()){
					descriptionManager.updateDescriptionVisible(element.getId());
				}else{
					descriptionManager.updateDescriptionVisible(element.getId());
				}
			}
		});

//
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
