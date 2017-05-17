package com.gamater.sdk.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamater.util.ResourceUtil;
import com.tony.viewinterface.BaseOnClickListener;

public class MVLoadingFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				ResourceUtil.getLayoutId("vsgm_tony_fragment_loading"),
				container, false);

		view.setOnClickListener(new BaseOnClickListener() {

			@Override
			public void onBaseClick(View v) {

			}
		});

		return view;
	}

}
