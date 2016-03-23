package pl.reticular.ttw.game.control;

/*
 * Copyright (C) 2016 Piotr Jankowski
 *
 * This file is part of Tear The Web.
 *
 * Tear The Web is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Tear The Web is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Tear The Web. If not, see <http://www.gnu.org/licenses/>.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;

import org.json.JSONException;

import pl.reticular.ttw.game.display.GameDisplay;
import pl.reticular.ttw.game.model.Game;
import pl.reticular.ttw.game.model.Spider;
import pl.reticular.ttw.game.model.SpiderSet;
import pl.reticular.ttw.game.model.meta.MetaData;
import pl.reticular.ttw.game.model.meta.MetaDataHelper;
import pl.reticular.ttw.game.model.meta.MetaDataMsg;
import pl.reticular.ttw.game.model.web.Spring;
import pl.reticular.ttw.game.model.web.Web;
import pl.reticular.ttw.game.model.web.WebType;
import pl.reticular.ttw.utils.Vector2;

public class GameControl implements Web.WebObserver, SpiderSet.SpiderObserver,
		MetaDataHelper.MetaDataObserver {
	private Context context;

	private Handler messageHandler;

	private Game game;

	private GameDisplay gameDisplay;

	private Vector2 gravity;

	public GameControl(Context context, Handler messageHandler, Game game) {
		this.context = context;
		this.messageHandler = messageHandler;
		this.game = game;

		gameDisplay = new GameDisplay(context);

		game.getWeb().setObserver(this);
		game.getMetaDataHelper().setObserver(this);
		game.getSpiderSet().setObserver(this);

		gravity = new Vector2(0.0f, 1.0f);
	}

	public synchronized void setSurfaceSize(int width, int height) {
		gameDisplay.getCanvasHelper().setSize(width, height);

		WebType webType = game.getWebType();
		gameDisplay.setupBackground(webType);
	}

	public synchronized void frame(Canvas canvas, float dt) {
		game.update(dt, gravity, gameDisplay.getCanvasHelper().getArea());

		gameDisplay.draw(game, canvas, gameDisplay.getCanvasHelper().getScale());
	}

	public synchronized void onTouchEvent(MotionEvent motionEvent) {
		Vector2 touch;
		switch (motionEvent.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touch = gameDisplay.getCanvasHelper().transform(motionEvent.getX(), motionEvent.getY());
				game.getFinger().startTracking(touch, game.getWeb(), game.getSpiderSet());
				break;
			case MotionEvent.ACTION_MOVE:
				touch = gameDisplay.getCanvasHelper().transform(motionEvent.getX(), motionEvent.getY());
				game.getFinger().continueTracking(touch);
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				game.getFinger().cancelTracking();
				break;
		}
	}

	public synchronized void setGravity(float x, float y, float z) {
		gravity.set(-x, y + Math.abs(z)); // y is backwards, so here is positive
		gravity.scale(0.2f);
	}

	public synchronized boolean isFinished() {
		return game.getMetaDataHelper().getMetaData().isFinished();
	}

	public synchronized Game getGame() {
		return game;
	}

	@Override
	public void onSpringBroken(Spring spring) {
		if (!isFinished()) {
			game.getFinger().cancelTracking();
			game.getMetaDataHelper().addScore(1);
		}

		game.getSpiderSet().onSpringUnAvailable(spring);
	}

	@Override
	public void onSpringOut(Spring spring) {
		game.getSpiderSet().onSpringUnAvailable(spring);
	}

	@Override
	public void onSpiderOut(Spider spider) {
		if (!isFinished()) {
			game.getMetaDataHelper().addScore(10);
		}
	}

	private void onLevelUp() {
		game.prepareLevel();

		game.getWeb().setObserver(this);

		gameDisplay.setupBackground(game.getWebType());
	}

	@Override
	public void onMetaDataChanged(MetaDataMsg.Reason reason, MetaData metaData) {

		switch (reason) {
			case LevelUp:
				onLevelUp();
				break;
			default:
				break;
		}

		//forward to activity
		try {
			Message msg = messageHandler.obtainMessage();
			Bundle b = new Bundle();
			b.putString(MetaDataMsg.Fields.Reason.toString(), reason.toString());
			b.putString(MetaDataMsg.Fields.Data.toString(), metaData.toJSON().toString());
			msg.setData(b);
			messageHandler.sendMessage(msg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
