package com.codepath.frimfram.gridimagesearch.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.codepath.frimfram.gridimagesearch.R;
import com.codepath.frimfram.gridimagesearch.models.SearchFilter;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link FilterDialog.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link FilterDialog#newInstance} factory method to create an instance of this
 * fragment.
 * 
 */
public class FilterDialog extends DialogFragment implements OnClickListener {
	private static final String ARG_PARAM1 = "filter";

	private SearchFilter mFilter;
	
	private Spinner spImageSize;
	private Spinner spColor;
	private Spinner spImageType;
	private EditText etSite;
	private Button saveButton;
	private Button cancelButton;

	private OnFragmentInteractionListener mListener;

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * @return A new instance of fragment FilterDialog.
	 */
	public static FilterDialog newInstance(SearchFilter param1) {
		FilterDialog fragment = new FilterDialog();
		Bundle args = new Bundle();
		args.putParcelable(ARG_PARAM1, param1);
		fragment.setArguments(args);
		return fragment;
	}

	public FilterDialog() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mFilter = getArguments().getParcelable(ARG_PARAM1);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle(R.string.advanced_filters);
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_filter_dialog, container,
				false);
		spImageSize = (Spinner)view.findViewById(R.id.spImageSize);
		spColor = (Spinner)view.findViewById(R.id.spColorFilter);
		spImageType = (Spinner)view.findViewById(R.id.spImageType);
		etSite = (EditText)view.findViewById(R.id.etSiteFilterUri);	
		saveButton = (Button)view.findViewById(R.id.btnSave);
		cancelButton = (Button)view.findViewById(R.id.btnCancel);
		saveButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		if(mFilter != null) {
			spImageSize.setSelection(mFilter.getImageSize());
			spColor.setSelection(mFilter.getImageColorIndex());
			spImageType.setSelection(mFilter.getImageType());
			etSite.setText(mFilter.getSite());
		}		
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		
		public void onFragmentInteraction(SearchFilter filter);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btnSave:
			if(validate()) {
				SearchFilter filter = new SearchFilter();
				filter.setImageSize(spImageSize.getSelectedItemPosition());
				filter.setImageType(spImageType.getSelectedItemPosition());
				filter.setImageColor((String)spColor.getSelectedItem());
				filter.setImageColorIndex(spColor.getSelectedItemPosition());
				if(etSite.getText() != null) {
					filter.setSite(etSite.getText().toString());
				}
				if (mListener != null) {
					mListener.onFragmentInteraction(filter);
				}				
				dismiss();
			}else{
				etSite.setError(getResources().getString(R.string.invalid_site_url));
			}
			
			break;
		case R.id.btnCancel:
			dismiss();
			break;
		}
		
	}
	
	private boolean validate() {
		if(etSite.getText() != null && (etSite.getText().length() > 0)) {
			String siteUrl = etSite.getText().toString();
			if(Patterns.WEB_URL.matcher(siteUrl).matches()) {
				//return NetworkHelper.isOnline(siteUrl);
				return true;
			}else{
				return false;
			}
		}
		return true;
	}

}
