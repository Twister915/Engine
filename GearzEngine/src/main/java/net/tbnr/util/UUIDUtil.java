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

package net.tbnr.util;

import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.Profile;
import com.mojang.api.profiles.ProfileCriteria;
import lombok.Getter;

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

    public UUIDUtil(String user, UUIDCallback callback) {
        HttpProfileRepository repository = new HttpProfileRepository();
        UUIDRunner uuidRunner = new UUIDRunner(repository, user, callback);
        new Thread(uuidRunner).start();
    }

    public static class UUIDRunner implements Runnable {
        private static final String AGENT = "minecraft";
        private final HttpProfileRepository httpProfileRepository;
        @Getter private final String username;
        @Getter private String uuid;
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
                uuid = String.format("%s-%s-%s-%s-%s", uuid.substring(0, 8),
                        uuid.substring(8, 12), uuid.substring(12, 16),
                        uuid.substring(16, 20), uuid.substring(20, 32));
            }
            callback.complete(username, uuid);
        }
    }

    public static interface UUIDCallback {
        public void complete(String username, String uuid);
    }
}
