package com.naqtscoresheet;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class StatsScreen extends TabActivity {
	private Game game;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.stats);

	    this.game = (Game)getIntent().getExtras().getSerializable("game");
	    
	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, TeamStatsScreen.class);
	    Bundle bundle = new Bundle();
		bundle.putSerializable("game", this.game);
		intent.putExtras(bundle);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("teamstats").setIndicator("Team",
	                      res.getDrawable(R.drawable.ic_menu_allfriends))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tab
	    intent = new Intent().setClass(this, PlayerStatsScreen.class);
		intent.putExtras(bundle);
	    spec = tabHost.newTabSpec("playerstats").setIndicator("Individual",
	                      res.getDrawable(R.drawable.ic_menu_friendslist))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    tabHost.setCurrentTab(0);
	}
}
