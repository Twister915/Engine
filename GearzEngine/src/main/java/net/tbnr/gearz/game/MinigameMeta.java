/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.gearz.game;

import com.mongodb.DB;
import com.mongodb.DBObject;
import lombok.Getter;
import net.tbnr.gearz.activerecord.BasicField;
import net.tbnr.gearz.activerecord.GModel;
import org.bukkit.ChatColor;

public final class MinigameMeta extends GModel {
	@Getter @BasicField private String longName;
	@Getter @BasicField private String shortName;
	@Getter @BasicField private String version;
	@Getter @BasicField private String author;
	@Getter @BasicField private String description;
	@Getter @BasicField private ChatColor mainColor;
	@Getter @BasicField private ChatColor secondaryColor;
	@Getter @BasicField private String key;
	@Getter @BasicField private int minPlayers;
	@Getter @BasicField private int maxPlayers;
	@Getter @BasicField private String pluginClass;
	@Getter @BasicField private String gameClass;


	@SuppressWarnings("unused")
	public MinigameMeta() {
		super();
	}

	public MinigameMeta(DB database) {
		super(database);
	}

	@SuppressWarnings("unused")
	public MinigameMeta(DB database, DBObject dBobject) {
		super(database, dBobject);
	}

    public MinigameMeta(DB db, GameMeta meta) {
        super(db);
        this.key = meta.key();
    }

	public MinigameMeta(DB database, GameMeta meta, String pluginClass, String gameClass) {
        this(database);
		this.longName = meta.longName();
		this.shortName = meta.shortName();
		this.version = meta.version();
		this.author = meta.author();
		this.description = meta.description();
		this.mainColor = meta.mainColor();
		this.secondaryColor = meta.secondaryColor();
		this.key = meta.key();
		this.minPlayers = meta.minPlayers();
		this.maxPlayers = meta.maxPlayers();
		this.pluginClass = pluginClass;
		this.gameClass = gameClass;
	}
}
