package pl.reticular.ttw.game.model.meta;

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

public class MetaDataHelper {

	public interface MetaDataObserver {
		void onMetaDataChanged(MetaDataMsg.Reason reason, MetaData metaData);
	}

	private MetaData metaData;

	private MetaDataObserver observer;

	public MetaDataHelper() {
		this.metaData = new MetaData();
	}

	public MetaDataHelper(MetaData metaData) {
		this.metaData = metaData;
	}

	public void setObserver(MetaDataObserver observer) {
		this.observer = observer;

		observer.onMetaDataChanged(MetaDataMsg.Reason.Init, metaData);
	}

	public void levelUp() {
		metaData = metaData.levelUp();

		observer.onMetaDataChanged(MetaDataMsg.Reason.LevelUp, metaData);
	}

	public void addScore(int add) {
		metaData = metaData.addScore(add);

		observer.onMetaDataChanged(MetaDataMsg.Reason.ScoreChanged, metaData);
	}

	public void die() {
		metaData = metaData.die();

		if (metaData.isFinished()) {
			observer.onMetaDataChanged(MetaDataMsg.Reason.GameOver, metaData);
		} else {
			observer.onMetaDataChanged(MetaDataMsg.Reason.LivesDecreased, metaData);
		}
	}

	public MetaData getMetaData() {
		return metaData;
	}
}
