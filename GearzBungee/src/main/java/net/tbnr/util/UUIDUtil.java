/*
 * Copyright (c) 2014.
 * Cogz Development LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.util;

import com.google.common.collect.Lists;
import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.Profile;
import com.mojang.api.profiles.ProfileCriteria;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.tbnr.gearz.GearzBungee;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Util to allow for username to UUID conversion
 * Uses a callback for async call.
 *
 * <p>
 * Latest Change: Created util
 * <p>
 *
 * @author Jake
 * @since 3/29/2014
 */
public class UUIDUtil {
    private final HttpProfileRepository repository = new HttpProfileRepository();
    private final List<UUIDRunner> uuidRunnables = new ArrayList<>();
    private List<String> usernames = new ArrayList<>();
    private UUIDCallback callback;

    public UUIDUtil(List<String> usernames, UUIDCallback callback) {
        this.usernames = usernames;
        this.callback = callback;
    }

    public UUIDUtil(String user, UUIDCallback callback) {
        this.usernames = Lists.newArrayList(user);
        this.callback = callback;

        for (String username : this.usernames) {
            UUIDRunner uuidRunner = new UUIDRunner(repository, username, this.callback);
            this.uuidRunnables.add(uuidRunner);
            new Thread(uuidRunner).start();
        }
    }

    public static class UUIDRunner implements Runnable {
        private static final String AGENT = "minecraft";
        private final HttpProfileRepository httpProfileRepository;
        @Getter private final String username;
        @Getter private String uuid;
        @Getter private final AtomicBoolean complete = new AtomicBoolean(false);
        @Getter private UUIDCallback callback;

        public UUIDRunner(HttpProfileRepository httpProfileRepository, String username, UUIDCallback callback) {
            this.httpProfileRepository = httpProfileRepository;
            this.username = username;
            this.callback = callback;
        }

        @Override
        public void run() {
            Profile[] profiles = httpProfileRepository.findProfilesByCriteria(new ProfileCriteria(username, AGENT));
            if (profiles[0] == null) {
                this.uuid = null;
            } else {
                uuid = profiles[0].getId();
            }
            callback.complete(username, uuid);
        }
    }

    public static interface UUIDCallback {
        public void complete(String username, String uuid);
    }
}
