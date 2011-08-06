/*
 * Copyright 2011 Mark Hahnenberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.naqtscoresheet;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
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
