package pl.reticular.ttw.game.meta;

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

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.json.JSONException;

public class MetaDataHelper {

	private MetaData metaData;
	private Handler messageHandler;

	public MetaDataHelper(Handler messageHandler) {
		this.metaData = new MetaData();
		this.messageHandler = messageHandler;

		sendMessage(MetaDataMsg.Reason.Init);
	}

	public MetaDataHelper(MetaData metaData, Handler messageHandler) {
		this.metaData = metaData;
		this.messageHandler = messageHandler;

		sendMessage(MetaDataMsg.Reason.Init);
	}

	public void levelUp() {
		metaData = metaData.levelUp();

		sendMessage(MetaDataMsg.Reason.LevelUp);
	}

	public void addScore(int add) {
		metaData = metaData.addScore(add);

		sendMessage(MetaDataMsg.Reason.ScoreChanged);
	}

	public void die() {
		metaData = metaData.die();

		if (metaData.isFinished()) {
			sendMessage(MetaDataMsg.Reason.GameOver);
		} else {
			sendMessage(MetaDataMsg.Reason.LivesDecreased);
		}
	}

	public MetaData getMetaData() {
		return metaData;
	}

	private void sendMessage(MetaDataMsg.Reason reason) {
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
