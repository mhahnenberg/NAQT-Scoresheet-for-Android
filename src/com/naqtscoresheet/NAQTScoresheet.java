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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.naqtscoresheet.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NAQTScoresheet extends Activity {
	private static final int MAX_TOSSUPS = 24;
	private static final int NEW_GAME_DIALOG = 1;
	private static final int CHOOSE_TEAM_ADD_DIALOG = 2;
	private static final int CHOOSE_TEAM_A_PLAYER_NAME_DIALOG = 6;
	private static final int CHOOSE_TEAM_B_PLAYER_NAME_DIALOG = 7;
	private static final int CHOOSE_TEAM_REMOVE_DIALOG = 3;
	private static final int CHOOSE_TEAM_A_PLAYERS_DIALOG = 4;
	private static final int CHOOSE_TEAM_B_PLAYERS_DIALOG = 5;
	private static final int TEAM_A_NAME_DIALOG = 8;
	private static final int TEAM_B_NAME_DIALOG = 9;
	private static final int LOAD_GAME_DIALOG = 10;
	public static final int LOAD_GAME_RESULT = 0;
	private Game game;
	public static String teamAName = "Team A";
	public static String teamBName = "Team B";
	private boolean modifyingRadioButtons = false;
	private boolean modifyingCheckBoxes = false;
	private boolean modifyingSpinner = false;
	
	private void enableBonusBoxes() {
		modifyingCheckBoxes = true;
		CheckBox bbox1 = (CheckBox)findViewById(R.id.bonuscheck1);
		CheckBox bbox2 = (CheckBox)findViewById(R.id.bonuscheck2);
		CheckBox bbox3 = (CheckBox)findViewById(R.id.bonuscheck3);
		bbox1.setEnabled(true);
		bbox2.setEnabled(true);
		bbox3.setEnabled(true);
		modifyingCheckBoxes = false;
	}
	
	private void disableBonusBoxes() {
		modifyingCheckBoxes = true;
		CheckBox bbox1 = (CheckBox)findViewById(R.id.bonuscheck1);
		CheckBox bbox2 = (CheckBox)findViewById(R.id.bonuscheck2);
		CheckBox bbox3 = (CheckBox)findViewById(R.id.bonuscheck3);
		bbox1.setEnabled(false);
		bbox2.setEnabled(false);
		bbox3.setEnabled(false);
		modifyingCheckBoxes = false;
	}
	
	private void clearBonusBoxes() {
		// intentionally not blocking the score from updating with modifyingCheckBoxes flag
		CheckBox bbox1 = (CheckBox)findViewById(R.id.bonuscheck1);
		CheckBox bbox2 = (CheckBox)findViewById(R.id.bonuscheck2);
		CheckBox bbox3 = (CheckBox)findViewById(R.id.bonuscheck3);
		bbox1.setChecked(false);
		bbox2.setChecked(false);
		bbox3.setChecked(false);
	}
	
	private void updateBonusBoxes(Tossup t) {
		Bonus b = t.getBonus();
		CheckBox bbox1 = (CheckBox)findViewById(R.id.bonuscheck1);
		CheckBox bbox2 = (CheckBox)findViewById(R.id.bonuscheck2);
		CheckBox bbox3 = (CheckBox)findViewById(R.id.bonuscheck3);
		if (t.getWinnerPoints() > 0 && this.game.getCurrTossupNum() <= MAX_TOSSUPS) {
			modifyingCheckBoxes = true;
			bbox1.setChecked(b.isPartCorrect(1));
			bbox2.setChecked(b.isPartCorrect(2));
			bbox3.setChecked(b.isPartCorrect(3));
			modifyingCheckBoxes = false;
		}
		else {
			modifyingCheckBoxes = true;
			clearBonusBoxes();
			modifyingCheckBoxes = false;
			disableBonusBoxes();
		}
	}
	
	private void updatePointsSelector(Tossup t) {
		Team winner = t.getWinnerTeam();
		RadioGroup winnerGroup;
		RadioGroup loserGroup;
		
		if (winner.equals(game.getTeamA())) {
			winnerGroup = (RadioGroup)findViewById(R.id.teamaradiogroup);
			loserGroup = (RadioGroup)findViewById(R.id.teambradiogroup);
		}
		else {
			winnerGroup = (RadioGroup)findViewById(R.id.teambradiogroup);
			loserGroup = (RadioGroup)findViewById(R.id.teamaradiogroup);
		}
		this.modifyingRadioButtons = true;
		switch(t.getWinnerPoints()) {
		case 15:
			((RadioButton)winnerGroup.getChildAt(0)).setChecked(true);
			enableBonusBoxes();
			break;
		case 10:
			((RadioButton)winnerGroup.getChildAt(1)).setChecked(true);
			enableBonusBoxes();
			break;
		case 0:
			((RadioButton)winnerGroup.getChildAt(2)).setChecked(true);
			disableBonusBoxes();
			break;
		case -5:
			throw new RuntimeException("Winner can't have -5");
		default:
			throw new RuntimeException("Invalid point value for winner");
		}
		
		switch(t.getLoserPoints()) {
		case 0:
			((RadioButton)loserGroup.getChildAt(2)).setChecked(true);
			break;
		case -5:
			((RadioButton)loserGroup.getChildAt(3)).setChecked(true);
			break;
		default:
			// this should never happen
			((RadioButton)loserGroup.getChildAt(2)).setChecked(true);
			break;
		}
		this.modifyingRadioButtons = false;
	}
	
	private void updateGlobalScore() {
		TextView score = (TextView)findViewById(R.id.score);
		score.setText(this.game.getTeamA().getScore() + " - " + this.game.getTeamB().getScore());
	}
	
	private void updateTossupNum() {
		TextView tv = (TextView)findViewById(R.id.tossupnum);
		if (this.game.getCurrTossupNum() <= MAX_TOSSUPS) {
			tv.setText("Tossup " + game.getCurrTossupNum());
		}
		else {
			tv.setText("Tiebreaker " + (game.getCurrTossupNum() - MAX_TOSSUPS));
		}
	}
	
	private OnClickListener prevButtonClicked = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Tossup t = game.prevTossup();
			if (t != null) {
				updatePointsSelector(t);
				updateBonusBoxes(t);
				updateTossupNum();
				updatePlayerSpinner();
			}
		}
	};
	
	private OnClickListener nextButtonClicked = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Tossup t = game.nextTossup();
			if (t != null) {
				updatePointsSelector(t);
				updateBonusBoxes(t);
				updateTossupNum();
				updatePlayerSpinner();
			}
		}
	};
	
	private CompoundButton.OnCheckedChangeListener bonusBoxChecked = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (modifyingCheckBoxes) {
				return;
			}
			CheckBox bbox1 = (CheckBox)findViewById(R.id.bonuscheck1);
			CheckBox bbox2 = (CheckBox)findViewById(R.id.bonuscheck2);
			CheckBox bbox3 = (CheckBox)findViewById(R.id.bonuscheck3);
			List<Boolean> bboxes = Arrays.asList(bbox1.isChecked(), bbox2.isChecked(), bbox3.isChecked());
			int score = 0;
			for (Boolean b : bboxes) {
				if (b) {
					score += 10;
				}
			}
			game.updateCurrBonus(new Bonus(score, bboxes));
			updateGlobalScore();
		}
	};
	
	private OnCheckedChangeListener teamPointsSelected = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup rg, int checkedId) {
			if (modifyingRadioButtons) {
				return;
			}
			modifyingRadioButtons = true;
			RadioGroup teamARG = (RadioGroup)findViewById(R.id.teamaradiogroup);
			RadioGroup teamBRG = (RadioGroup)findViewById(R.id.teambradiogroup);
			RadioButton teamARB = (RadioButton)findViewById(teamARG.getCheckedRadioButtonId());
			RadioButton teamBRB = (RadioButton)findViewById(teamBRG.getCheckedRadioButtonId());
			int aPoints = Integer.parseInt(teamARB.getText().toString());
			int bPoints = Integer.parseInt(teamBRB.getText().toString());
			
			// somebody needs to be reset to 0
			if ((aPoints > 0 && bPoints > 0) || (aPoints < 0 && bPoints < 0)) {
				// team A was clicked, so team B should reset
				if (rg.equals(teamARG)) {
					bPoints = 0;
					((RadioButton)findViewById(R.id.teambradio0)).setChecked(true);
				}
				// team B was clicked, so team A should reset
				else {
					aPoints = 0;
					((RadioButton)findViewById(R.id.teamaradio0)).setChecked(true);
				}
			}
			
			if (aPoints > 0 || bPoints > 0) {
				enableBonusBoxes();
			}
			else {
				clearBonusBoxes();
				disableBonusBoxes();
			}
			
			Tossup oldTossup = game.currTossup();
			Tossup t;
			if (aPoints >= bPoints) {
				t = new Tossup(game.getCurrTossupNum(), game.getTeamA(), aPoints, game.getTeamB(), bPoints, oldTossup.isTiebreaker());
			}
			else {
				t = new Tossup(game.getCurrTossupNum(), game.getTeamB(), bPoints, game.getTeamA(), aPoints, oldTossup.isTiebreaker());
			}
			game.updateCurrTossup(t);
			updateGlobalScore();
			updatePlayerSpinner();
			updateBonusBoxes(t);
			modifyingRadioButtons = false;
		}
	};
	
	private OnClickListener teamANameClicked = new OnClickListener() {
		@Override
		public void onClick(View v) {
			NAQTScoresheet.this.showDialog(TEAM_A_NAME_DIALOG);
		}
	};
	
	private OnClickListener teamBNameClicked = new OnClickListener() {
		@Override
		public void onClick(View v) {
			NAQTScoresheet.this.showDialog(TEAM_B_NAME_DIALOG);
		}
	};
	
	private OnItemSelectedListener playerSpinnerSelected = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> adapter, View view, int pos, long id) {
			if (modifyingSpinner) {
				return;
			}
			Tossup t = game.currTossup();
			String playerName = (String)adapter.getSelectedItem();
			
			// team a was selected
			if (adapter.getId() == R.id.teamaplayerspinner) {
				// team a won
				if (t.getWinnerTeam().equals(game.getTeamA())) {
					Player p = t.getWinnerTeam().getPlayer(playerName);
					t.setWinnerPlayer(p);
				}
				// team a lost
				else {
					Player p = t.getLoserTeam().getPlayer(playerName);
					t.setLoserPlayer(p);
				}
			}
			// team b was selected
			else {
				// team b won
				if (t.getWinnerTeam().equals(game.getTeamB())) {
					Player p = t.getWinnerTeam().getPlayer(playerName);
					t.setWinnerPlayer(p);
				}
				// team b lost
				else {
					Player p = t.getLoserTeam().getPlayer(playerName);
					t.setLoserPlayer(p);
				}
			}
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			if (modifyingSpinner) {
				return;
			}
			game.currTossup().setWinnerPlayer(null);
		}
	};
	
	private void updatePlayerSpinner() {
		modifyingSpinner = true;
		Tossup t = game.currTossup();
		Spinner winnerSpinner, loserSpinner;
		if (t.getWinnerTeam().getName().equals(teamAName)) {
			winnerSpinner = (Spinner)findViewById(R.id.teamaplayerspinner);
			loserSpinner = (Spinner)findViewById(R.id.teambplayerspinner);
		}
		else {
			winnerSpinner = (Spinner)findViewById(R.id.teambplayerspinner);
			loserSpinner = (Spinner)findViewById(R.id.teamaplayerspinner);
		}
		
		ArrayAdapter<String> winnerAdapter, loserAdapter;
		
		if (t.getWinnerPoints() > 0) {
			List<Player> players = t.getWinnerPlayers();
			String[] playerNames = new String[players.size()];
			for (int i = 0; i < players.size(); i++) {
				playerNames[i] = players.get(i).getName();
			}
			winnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, playerNames);
			winnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			int pos = players.indexOf(t.getWinnerPlayer());
			winnerSpinner.setAdapter(winnerAdapter);
			winnerSpinner.setSelection(pos);
			if (playerNames.length > 0) {
				winnerSpinner.setEnabled(true);
			}
			else {
				winnerSpinner.setEnabled(false);
			}
		}
		else {
			winnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[0]);
			winnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			winnerSpinner.setAdapter(winnerAdapter);
			winnerSpinner.setEnabled(false);
		}
		
		if (t.getLoserPoints() < 0) {
			List<Player> players = t.getLoserPlayers();
			String[] playerNames = new String[players.size()];
			for (int i = 0; i < players.size(); i++) {
				playerNames[i] = players.get(i).getName();
			}
			loserAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, playerNames);
			loserAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			int pos = players.indexOf(t.getLoserPlayer());
			loserSpinner.setAdapter(loserAdapter);
			loserSpinner.setSelection(pos);
			if (playerNames.length > 0) { 
				loserSpinner.setEnabled(true);
			}
			else {
				loserSpinner.setEnabled(false);
			}
		}
		else {
			loserAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[0]);
			loserAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			loserSpinner.setAdapter(loserAdapter);
			loserSpinner.setEnabled(false);
		}
		modifyingSpinner = false;
	}
	
	private void startNewGame() {
		this.game = new Game(new Team(teamAName), new Team(teamBName), MAX_TOSSUPS);
		Tossup firstTossup = this.game.currTossup();
		this.updateBonusBoxes(firstTossup);
		this.updatePointsSelector(firstTossup);
		this.updateGlobalScore();
		this.updateTossupNum();
		this.updatePlayerSpinner();
	}
	
	private void removeFromRemainingTossups(Team team, String playerName) {
		Tossup currTossup = this.game.currTossup();
		int i = this.game.getCurrTossupNum();
		while (currTossup != null) {
			currTossup.removePlayer(team, playerName);
			i += 1;
			currTossup = this.game.getNthTossup(i);
		}
	}
	
	private void addToRemainingTossups(Team team, Player p) {
		Tossup currTossup = this.game.currTossup();
		int i = this.game.getCurrTossupNum();
		while (currTossup != null) {
			currTossup.addPlayer(team, p);
			i += 1;
			currTossup = this.game.getNthTossup(i);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
    	Dialog dialog;
    	AlertDialog.Builder alertBuilder;
		final CharSequence[] items = {teamAName, teamBName};

		switch(id) {
		case NEW_GAME_DIALOG:
			alertBuilder = new AlertDialog.Builder(this);
			alertBuilder.setMessage("Are you sure you want to start a new game?")
    		       .setCancelable(false)
    		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		                NAQTScoresheet.this.startNewGame();
    		           }
    		       })
    		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		                dialog.cancel();
    		           }
    		       });
    		dialog = alertBuilder.create();
    		break;
		case LOAD_GAME_DIALOG:
			alertBuilder = new AlertDialog.Builder(this);
			alertBuilder.setMessage("Are you sure you want to leave this game?")
    		       .setCancelable(false)
    		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		        	   Intent intent = new Intent(NAQTScoresheet.this, LoadGameScreen.class);
    		        	   startActivityForResult(intent, LOAD_GAME_RESULT);
    		           }
    		       })
    		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		                dialog.cancel();
    		           }
    		       });
    		dialog = alertBuilder.create();
    		break;
		case CHOOSE_TEAM_ADD_DIALOG:
			alertBuilder = new AlertDialog.Builder(this);
			alertBuilder.setTitle("Pick a team");
			alertBuilder.setItems(items, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
		        	dialog.dismiss();
		        	NAQTScoresheet.this.removeDialog(CHOOSE_TEAM_ADD_DIALOG);
			        if (items[item].equals(game.getTeamA().getName())) {
			        	NAQTScoresheet.this.showDialog(CHOOSE_TEAM_A_PLAYER_NAME_DIALOG);
			        }
			        else {
			        	NAQTScoresheet.this.showDialog(CHOOSE_TEAM_B_PLAYER_NAME_DIALOG);
			        }
			    }
			});
			alertBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {			
				@Override
				public void onCancel(DialogInterface dialog) {
					NAQTScoresheet.this.removeDialog(CHOOSE_TEAM_ADD_DIALOG);
				}
			});
			dialog = alertBuilder.create();
			break;
		case CHOOSE_TEAM_A_PLAYER_NAME_DIALOG:
			final Dialog finalDialog = new Dialog(NAQTScoresheet.this);
			dialog = finalDialog;
			dialog.setContentView(R.layout.add_player_dialog);
			dialog.setTitle("Enter Player Name");
			dialog.setCancelable(true);
			Button b = (Button)dialog.findViewById(R.id.addplayerbutton);
			b.setOnClickListener(new OnClickListener() {
				private Dialog parentDialog = finalDialog;
				@Override
				public void onClick(View view) {
					EditText textbox = (EditText)parentDialog.findViewById(R.id.newplayername);
					String playerName = textbox.getText().toString();
					Player newPlayer = new Player(playerName);
					NAQTScoresheet.this.game.getTeamA().addPlayer(newPlayer);
					NAQTScoresheet.this.addToRemainingTossups(game.getTeamA(), newPlayer);
					parentDialog.dismiss();
					NAQTScoresheet.this.removeDialog(CHOOSE_TEAM_A_PLAYER_NAME_DIALOG);
					NAQTScoresheet.this.updatePlayerSpinner();
				}
			});
			break;
		case CHOOSE_TEAM_B_PLAYER_NAME_DIALOG:
			final Dialog bFinalDialog = new Dialog(NAQTScoresheet.this);
			dialog = bFinalDialog;
			dialog.setContentView(R.layout.add_player_dialog);
			dialog.setTitle("Enter Player Name");
			dialog.setCancelable(true);
			Button bButton = (Button)dialog.findViewById(R.id.addplayerbutton);
			bButton.setOnClickListener(new OnClickListener() {
				private Dialog parentDialog = bFinalDialog;
				@Override
				public void onClick(View view) {
					EditText textbox = (EditText)parentDialog.findViewById(R.id.newplayername);
					String playerName = textbox.getText().toString();
					Player newPlayer = new Player(playerName);
					NAQTScoresheet.this.game.getTeamB().addPlayer(newPlayer);
					NAQTScoresheet.this.addToRemainingTossups(game.getTeamB(), newPlayer);
					parentDialog.dismiss();
					NAQTScoresheet.this.removeDialog(CHOOSE_TEAM_B_PLAYER_NAME_DIALOG);
					NAQTScoresheet.this.updatePlayerSpinner();
				}
			});
			break;
		case TEAM_A_NAME_DIALOG:
			final Dialog teamANameDialog = new Dialog(NAQTScoresheet.this);
			dialog = teamANameDialog;
			dialog.setContentView(R.layout.team_name_dialog);
			dialog.setTitle("Enter Team Name");
			dialog.setCancelable(true);
			Button teamANameButton = (Button)dialog.findViewById(R.id.teamnamebutton);
			teamANameButton.setOnClickListener(new OnClickListener() {
				private Dialog parentDialog = teamANameDialog;
				@Override
				public void onClick(View view) {
					EditText textbox = (EditText)parentDialog.findViewById(R.id.teamnameeditext);
					String teamName = textbox.getText().toString();
					TextView tv = (TextView)NAQTScoresheet.this.findViewById(R.id.teama);
					tv.setText(teamName);
					teamAName = teamName;
					parentDialog.dismiss();
					NAQTScoresheet.this.removeDialog(TEAM_A_NAME_DIALOG);
					game.getTeamA().setName(teamAName);
				}
			});
			break;
		case TEAM_B_NAME_DIALOG:
			final Dialog teamBNameDialog = new Dialog(NAQTScoresheet.this);
			dialog = teamBNameDialog;
			dialog.setContentView(R.layout.team_name_dialog);
			dialog.setTitle("Enter Team Name");
			dialog.setCancelable(true);
			Button teamBNameButton = (Button)dialog.findViewById(R.id.teamnamebutton);
			teamBNameButton.setOnClickListener(new OnClickListener() {
				private Dialog parentDialog = teamBNameDialog;
				@Override
				public void onClick(View view) {
					EditText textbox = (EditText)parentDialog.findViewById(R.id.teamnameeditext);
					String teamName = textbox.getText().toString();
					TextView tv = (TextView)NAQTScoresheet.this.findViewById(R.id.teamb);
					tv.setText(teamName);
					teamBName = teamName;
					game.getTeamB().setName(teamBName);
					parentDialog.dismiss();
					NAQTScoresheet.this.removeDialog(TEAM_B_NAME_DIALOG);
				}
			});
			break;
		case CHOOSE_TEAM_REMOVE_DIALOG:
			alertBuilder = new AlertDialog.Builder(this);
			alertBuilder.setTitle("Pick a team");
			alertBuilder.setItems(items, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			    	dialog.dismiss();
			        NAQTScoresheet.this.removeDialog(CHOOSE_TEAM_REMOVE_DIALOG);
			        if (items[item].equals(game.getTeamA().getName())) {
			        	NAQTScoresheet.this.showDialog(CHOOSE_TEAM_A_PLAYERS_DIALOG);
			        }
			        else {
			        	NAQTScoresheet.this.showDialog(CHOOSE_TEAM_B_PLAYERS_DIALOG);
			        }
			    }
			});
			dialog = alertBuilder.create();
			break;
		case CHOOSE_TEAM_A_PLAYERS_DIALOG:
			final Player[] players = game.getTeamA().getPlayers().toArray(new Player[0]);
			final CharSequence[] playerNames = new CharSequence[players.length];
			for (int i = 0; i < playerNames.length; i++) {
				playerNames[i] = players[i].getName();
			}
			alertBuilder = new AlertDialog.Builder(this);
			alertBuilder.setTitle("Pick a player");
			alertBuilder.setItems(playerNames, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int idx) {
			    	NAQTScoresheet.this.removeFromRemainingTossups(game.getTeamA(), playerNames[idx].toString());
			        game.getTeamA().removePlayer(playerNames[idx].toString());
			    	dialog.dismiss();
			    	NAQTScoresheet.this.removeDialog(CHOOSE_TEAM_A_PLAYERS_DIALOG);
			    	NAQTScoresheet.this.updatePlayerSpinner();
			    }
			});
			dialog = alertBuilder.create();
			break;
		case CHOOSE_TEAM_B_PLAYERS_DIALOG:
			final Player[] bPlayers = game.getTeamB().getPlayers().toArray(new Player[0]);
			final CharSequence[] bPlayerNames = new CharSequence[bPlayers.length];
			for (int i = 0; i < bPlayerNames.length; i++) {
				bPlayerNames[i] = bPlayers[i].getName();
			}
			alertBuilder = new AlertDialog.Builder(this);
			alertBuilder.setTitle("Pick a player");
			alertBuilder.setItems(bPlayerNames, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int idx) {
			    	NAQTScoresheet.this.removeFromRemainingTossups(game.getTeamB(), bPlayerNames[idx].toString());
			        game.getTeamB().removePlayer(bPlayerNames[idx].toString());
			    	dialog.dismiss();
			    	NAQTScoresheet.this.removeDialog(CHOOSE_TEAM_B_PLAYERS_DIALOG);
			    	NAQTScoresheet.this.updatePlayerSpinner();
			    }
			});
			dialog = alertBuilder.create();
			break;
		default:
			dialog = null;
			break;
		}
		return dialog;
	}

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	this.game = new Game(new Team(teamAName), new Team(teamBName), MAX_TOSSUPS);
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button back = (Button)findViewById(R.id.backbutton);
        back.setOnClickListener(prevButtonClicked);
        Button forward = (Button)findViewById(R.id.forwardbutton);
        forward.setOnClickListener(nextButtonClicked);
        
        RadioGroup lhs = (RadioGroup)findViewById(R.id.teamaradiogroup);
        lhs.setOnCheckedChangeListener(teamPointsSelected);
        RadioGroup rhs = (RadioGroup)findViewById(R.id.teambradiogroup);
        rhs.setOnCheckedChangeListener(teamPointsSelected);
        
        CheckBox bbox1 = (CheckBox)findViewById(R.id.bonuscheck1);
		CheckBox bbox2 = (CheckBox)findViewById(R.id.bonuscheck2);
		CheckBox bbox3 = (CheckBox)findViewById(R.id.bonuscheck3);
		bbox1.setOnCheckedChangeListener(bonusBoxChecked);
		bbox2.setOnCheckedChangeListener(bonusBoxChecked);
		bbox3.setOnCheckedChangeListener(bonusBoxChecked);
		
		Spinner spinnerA = (Spinner)findViewById(R.id.teamaplayerspinner);
		spinnerA.setOnItemSelectedListener(playerSpinnerSelected);
		Spinner spinnerB = (Spinner)findViewById(R.id.teambplayerspinner);
		spinnerB.setOnItemSelectedListener(playerSpinnerSelected);
		this.updatePlayerSpinner();
		
		TextView teamAName = (TextView)findViewById(R.id.teama);
		TextView teamBName = (TextView)findViewById(R.id.teamb);
		teamAName.setOnClickListener(teamANameClicked);
		teamBName.setOnClickListener(teamBNameClicked);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.addplayer:
    		showDialog(CHOOSE_TEAM_ADD_DIALOG);
    		return true;
    	case R.id.removeplayer:
    		showDialog(CHOOSE_TEAM_REMOVE_DIALOG);
    		return true;
    	case R.id.newgame:
    		showDialog(NEW_GAME_DIALOG);
    		return true;
    	case R.id.stats:
    		Intent intent = new Intent(NAQTScoresheet.this, StatsScreen.class);
    		Bundle bundle = new Bundle();
    		bundle.putSerializable("game", this.game);
    		intent.putExtras(bundle);
    		startActivity(intent);
    		return true;
    	case R.id.savegame:
    		this.saveGame();
    		return true;
    	case R.id.loadgame:
    		showDialog(LOAD_GAME_DIALOG);
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }

	private void saveGame() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd - HH:mm");
		String filename = sdf.format(now) +  " - " + 
			this.game.getTeamA().getName() + " vs " + this.game.getTeamB().getName();
		Toast t;
		
		try {
			FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this.game);
			t = Toast.makeText(this, "Game saved!", Toast.LENGTH_SHORT);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			t = Toast.makeText(this, "Error saving game!", Toast.LENGTH_SHORT);
		} catch (IOException e) {
			e.printStackTrace();
			t = Toast.makeText(this, "Error saving game!", Toast.LENGTH_SHORT);
		}
		
		t.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// user cancelled out of list
		if (data == null) {
			return;
		}
		switch(requestCode) {
		case LOAD_GAME_RESULT:
			String filename = data.getStringExtra("filename");
			try {
				ObjectInputStream ois = new ObjectInputStream(openFileInput(filename));
				Game newGame = (Game)ois.readObject();
				ois.close();
				if (newGame == null) {
					break;
				}
				this.game = newGame;
				Tossup currTossup = this.game.currTossup();
				this.updateBonusBoxes(currTossup);
				this.updatePointsSelector(currTossup);
				this.updateGlobalScore();
				this.updateTossupNum();
				this.updatePlayerSpinner();
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
				throw new RuntimeException();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}
}