package fr.iambluedev.thelawyer.task;

import java.util.TimerTask;

import fr.iambluedev.thelawyer.App;

public class RefreshTask extends TimerTask {

	@Override
	public void run() {
		if(!App.getInstance().isRefreshing())
			App.getInstance().refresh();
	}

}
