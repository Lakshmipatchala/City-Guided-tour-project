package de.hft.city;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class ManualPOIProtoTypeActivity extends ListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// Create an array of Strings, that will be put to our ListActivity
		ArrayAdapter<Model> adapter = new InterActive(this,
				getModel());
		setListAdapter(adapter);
	}

	private List<Model> getModel() {
		List<Model> list = new ArrayList<Model>();
		list.add(get("POI1"));
		list.add(get("POI2"));
		list.add(get("POI3"));
		
		list.get(1).setSelected(true);
		return list;
	}

	private Model get(String s) {
		return new Model(s);
	}

}

