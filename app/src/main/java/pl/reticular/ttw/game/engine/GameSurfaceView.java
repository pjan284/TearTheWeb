package pl.reticular.ttw.game.engine;

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
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

	private GameEngine gameEngine;

	private GameThread gameThread;

	public GameSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (!isInEditMode()) {

			setFocusable(true);

			SurfaceHolder holder = getHolder();
			holder.addCallback(this);
		}
	}

	public void setGameEngine(GameEngine gameEngine) {
		this.gameEngine = gameEngine;
	}

	@Override
	public boolean onTouchEvent(@NonNull MotionEvent motionEvent) {
		gameEngine.onTouchEvent(motionEvent);
		//return super.onTouchEvent(motionEvent);
		return true;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//Log.i(getClass().getName(), "surfaceCreated");
		setWillNotDraw(false);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		//Log.i(getClass().getName(), "surfaceChanged");
		gameEngine.setSurfaceSize(width, height);
		if (gameThread != null) {
			gameThread.terminate();
		}
		gameThread = new GameThread(getHolder(), gameEngine);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		//Log.i(getClass().getName(), "surfaceDestroyed");
		gameThread.terminate();
		gameThread = null;
	}
}
