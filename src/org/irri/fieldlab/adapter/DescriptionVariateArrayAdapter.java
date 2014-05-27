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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

public class DescriptionVariateArrayAdapter extends ArrayAdapter<DescriptionModel> {

	private List<DescriptionModel> list;
	private Activity context;
	private DescriptionManager descriptionManager;

	public DescriptionVariateArrayAdapter(Activity context, List<DescriptionModel> list,DescriptionManager descriptionManager) {
		super(context,R.layout.traitvariaterow, list);
		this.context = context;
		this.list = list;
		this.descriptionManager=descriptionManager;
	}

	static class ViewHolder {
		protected TextView traitcode;
		protected CheckBox checkbox;
		protected EditText minimumvalue;
		protected EditText maximumvalue;
		protected Spinner spinnerDatatype;
		protected TextView property;
		protected TextView scale;
		protected TextView method;
		protected TextView scoring;
		protected TableLayout tblVariate;

		protected Object tbl;
	}

	public View getView(final int position, final View convertView, ViewGroup parent) {
		View view = null;
		final int pos = position;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.traitvariaterow, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.traitcode = (TextView) view.findViewById(R.id.txtVariateCode);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
			viewHolder.minimumvalue=(EditText) view.findViewById(R.id.txtmin);
			viewHolder.maximumvalue=(EditText) view.findViewById(R.id.txtmax);
			viewHolder.spinnerDatatype = (Spinner)view.findViewById(R.id.spinnerDataType);
			// inside frame
			viewHolder.property = (TextView) view.findViewById(R.id.txtproperty);
			viewHolder.scale = (TextView) view.findViewById(R.id.txtscale);
			viewHolder.method = (TextView) view.findViewById(R.id.txtMethod);
			viewHolder.scoring = (TextView) view.findViewById(R.id.txtScoring);
			viewHolder.tblVariate = (TableLayout) view.findViewById(R.id.tableVariateInfo);
//			viewHolder.imgInfo= (ImageView) view.findViewById(R.id.imgInfo);
//			viewHolder.btnHide= (ImageView) view.findViewById(R.id.btnHide);

			ArrayAdapter<CharSequence> adapterDatatype = ArrayAdapter.createFromResource(
					DescriptionVariateArrayAdapter.this.context, R.array.datatype_choices,
					android.R.layout.simple_spinner_item);
			adapterDatatype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			viewHolder.spinnerDatatype.setAdapter(adapterDatatype);

			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(list.get(position));

		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
		}


		final ViewHolder holder = (ViewHolder) view.getTag();
		holder.traitcode.setText(list.get(position).getTraitcode());
		holder.checkbox.setChecked(list.get(position).isSelected());

		if(list.get(position).getInputtype().equals("label") || list.get(position).getDatatype().equals("C") ){

			holder.minimumvalue.setVisibility(View.INVISIBLE);
			holder.maximumvalue.setVisibility(View.INVISIBLE);
			
			if(list.get(position).getInputtype().equals("label")){
				holder.spinnerDatatype.setVisibility(View.INVISIBLE);
			}

		}else{
			holder.minimumvalue.setVisibility(View.VISIBLE);
			holder.maximumvalue.setVisibility(View.VISIBLE);
			holder.spinnerDatatype.setVisibility(View.VISIBLE);
			holder.minimumvalue.setText(String.valueOf(list.get(position).getMinimumvalue()));
			holder.maximumvalue.setText(String.valueOf(list.get(position).getMaximumvalue()));
		}

		holder.property.setText(String.valueOf(list.get(position).getProperty()));
		holder.scale.setText(String.valueOf(list.get(position).getScale()));
		holder.method.setText(String.valueOf(list.get(position).getMethod()));
		holder.scoring.setText(String.valueOf(list.get(position).getScoring()));


		//we need to update adapter once we finish with editing
		holder.minimumvalue.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus){
					//final int pos=v.getId();
					final long id = list.get(position).getId();
					final EditText minimumvalue = (EditText) v;
					descriptionManager.updateVariateMinimumValue(id, Double.valueOf(minimumvalue.getText().toString()));
					list.get(position).setMinimumvalue(Double.valueOf(minimumvalue.getText().toString()));
				}
			}
		});

		holder.maximumvalue.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus){
					final long id = list.get(position).getId();
					final EditText maximumvalue = (EditText) v;
					descriptionManager.updateVariateMaximumValue(id, Double.valueOf(maximumvalue.getText().toString()));
					list.get(position).setMaximumvalue(Double.valueOf(maximumvalue.getText().toString()));
				}
			}
		});

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
					descriptionManager.updateDescriptionInVisible(element.getId());
				}
			}
		});

		holder.spinnerDatatype.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				list.get(pos).setDatatype(holder.spinnerDatatype.getSelectedItem().toString());
				descriptionManager.updateVariateDatatype(list.get(pos).getId(), holder.spinnerDatatype.getSelectedItem().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

//		holder.imgInfo.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				holder.tblVariate.setVisibility(View.VISIBLE);
//			}
//		});

//		holder.btnHide.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				holder.tblVariate.setVisibility(View.GONE);
//			}
//		});


		String traitDatatype = list.get(position).getDatatype();
		int idx=0;
		if(traitDatatype.equals("N"))
			idx=0;
		else if(traitDatatype.equals("C")){
			idx=1;
		}else if(traitDatatype.equals("D")){
			idx=2;
		}else if(traitDatatype.equals("CD")){
			idx=3;
		}
		holder.spinnerDatatype.setSelection(idx);

		view.refreshDrawableState();
		return view;
	}

}
