package pl.reticular.ttw.game.display;

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
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import pl.reticular.ttw.game.Game;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

	private GameThread gameThread;

	public GameSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (!isInEditMode()) {

			setFocusable(true);

			SurfaceHolder holder = getHolder();
			holder.addCallback(this);

			gameThread = new GameThread(holder);
		}
	}


	public GameThread getThread() {
		return gameThread;
	}

	public Game getGame() {
		return gameThread.getGame();
	}

	public void setGame(Game game) {
		gameThread.setGame(game);
	}

	@Override
	public boolean onTouchEvent(@NonNull MotionEvent motionEvent) {
		gameThread.getGame().onTouchEvent(motionEvent);
		//return super.onTouchEvent(motionEvent);
		return true;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i(getClass().getName(), "surfaceChanged");
		gameThread.getGame().setSurfaceSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(getClass().getName(), "surfaceCreated");
		setWillNotDraw(false);
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(getClass().getName(), "surfaceDestroyed");
		gameThread.terminate();
	}
}
