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

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import pl.reticular.ttw.game.Game;


public class GameThread implements Runnable {

	private static final int maxFPS = 30;

	private SurfaceHolder surfaceHolder;

	private Thread thread;

	private Game game;

	private boolean paused;

	public GameThread(SurfaceHolder surfaceHolder) {
		this.surfaceHolder = surfaceHolder;

		thread = new Thread(this);
		paused = false;
	}

	public synchronized void pause() {
		paused = true;
	}

	public synchronized void resume() {
		paused = false;

		if (thread.getState() == Thread.State.TERMINATED) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public synchronized void terminate() {
		thread.interrupt();

		boolean retry = true;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public void run() {
		boolean running = true;

		while (running) {
			long timeStart = System.currentTimeMillis();
			if (!paused && surfaceHolder.getSurface().isValid()) {
				Canvas c = surfaceHolder.lockCanvas(null);

				try {
					frame(c);
				} catch (Exception e) {
					Log.e(getClass().getName(), Log.getStackTraceString(e));
				} finally {
					surfaceHolder.unlockCanvasAndPost(c);
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
				running = false;
			}
		}
	}

	private synchronized void frame(Canvas canvas) {
		game.frame(canvas, 1.0f / maxFPS);
	}

	/**
	 * Must be called once
	 */
	public synchronized void setGame(Game g) {
		game = g;
		thread.start();
	}

	public synchronized Game getGame() {
		return game;
	}
}
