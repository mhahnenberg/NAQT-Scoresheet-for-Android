package com.naqtscoresheet;

import java.util.Arrays;
import java.util.Comparator;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LoadGameScreen extends ListActivity {	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.refreshList();
		registerForContextMenu(getListView());
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent data = new Intent();
		data.putExtra("filename", ((TextView)v).getText());
		this.setResult(NAQTScoresheet.LOAD_GAME_RESULT, data);
		finish();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.loadgame_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.deletegame:
			deleteFile(((TextView)info.targetView).getText().toString());
			refreshList();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void refreshList() {
		String[] gameFiles = fileList();
		Arrays.sort(gameFiles, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return -s1.compareTo(s2);
			}
		});
		setListAdapter(new ArrayAdapter<String>(this, R.layout.loadgame_list_item, gameFiles));
	}
}
