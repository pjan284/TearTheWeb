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

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;


public class GameThread implements Runnable {

	private static final int maxFPS = 30;

	private final SurfaceHolder surfaceHolder;

	private Thread thread;

	private GameEngine gameEngine;

	private boolean running;

	public GameThread(SurfaceHolder surfaceHolder, GameEngine gameEngine) {
		this.surfaceHolder = surfaceHolder;
		this.gameEngine = gameEngine;

		thread = new Thread(this);
		running = true;
		thread.start();
	}

	public void terminate() {
		if (thread != null) {
			running = false;

			boolean retry = true;
			while (retry) {
				try {
					thread.join();
					retry = false;
				} catch (InterruptedException e) {
				}
			}
			thread = null;
		}
	}

	@Override
	public void run() {
		while (running) {
			long timeStart = System.currentTimeMillis();

			Canvas canvas = surfaceHolder.lockCanvas(null);
			if (canvas != null) {
				try {
					gameEngine.frame(canvas, 1.0f / maxFPS);
				} catch (Exception e) {
					Log.e(getClass().getName(), Log.getStackTraceString(e));
				} finally {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}

			long timeEnd = System.currentTimeMillis();
			long elapsed = timeEnd - timeStart;
			long limit = 1000 / maxFPS;
			try {
				if (elapsed < limit) {
					Thread.sleep(limit - elapsed);
				} else {
					Thread.sleep(10);
				}
			} catch (InterruptedException e) {
			}
		}
	}
}
