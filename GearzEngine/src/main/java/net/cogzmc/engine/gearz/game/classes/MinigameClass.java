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

package net.cogzmc.engine.gearz.game.classes;

import com.mongodb.DB;
import com.mongodb.DBObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.cogzmc.engine.activerecord.BasicField;
import net.cogzmc.engine.activerecord.Collection;
import net.cogzmc.engine.activerecord.GModel;
import net.cogzmc.engine.gearz.GearzPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Collection("minigameclasses")
@Data
public final class MinigameClass extends GModel {
    @BasicField private String name;
    @BasicField private String key;
    @BasicField private String minigameKey;
    @BasicField private List<String> description;

    public MinigameClass() {
        super();
    }

    public MinigameClass(DB database) {
        super(database);
    }

    public MinigameClass(DB database, DBObject dBobject) {
        super(database, dBobject);
    }

    public static MinigameClass getObjectFor(GearzPlugin game, GearzClassMeta meta) {
        MinigameClass minigameClass = new MinigameClass();
        minigameClass.setKey(meta.key());
        GModel one = minigameClass.findOne();
        if (one != null && one instanceof MinigameClass) {
            minigameClass = (MinigameClass) one;
        }
        minigameClass.setDescription(Arrays.asList(meta.description()));
        minigameClass.setName(meta.name());
        minigameClass.setMinigameKey(game.getMeta().key());
        return minigameClass;
    }

    public static List<MinigameClass> getClassesFor(GearzPlugin game) {
        MinigameClass minigameClass = new MinigameClass();
        minigameClass.setMinigameKey(game.getMeta().key());
        List<MinigameClass> minigameClasses = new ArrayList<>();
        for (GModel gModel : minigameClass.findMany()) {
            if (gModel instanceof MinigameClass) minigameClasses.add((MinigameClass) gModel);
        }
        return minigameClasses;
    }
}
