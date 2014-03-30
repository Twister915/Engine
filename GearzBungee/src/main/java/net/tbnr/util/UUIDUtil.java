package net.tbnr.util;

import com.google.common.collect.Lists;
import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.Profile;
import com.mojang.api.profiles.ProfileCriteria;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by jake on 3/29/2014.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class UUIDUtil implements Runnable {
    private static String AGENT = "minecraft";
    private final HttpProfileRepository repository = new HttpProfileRepository();
    private final List<UUIDRunner> uuidRunnables = new ArrayList<>();
    private List<String> usernames = new ArrayList<>();
    private UUIDCallback callback;

    public UUIDUtil(List<String> usernames, UUIDCallback callback) {
        this.usernames = usernames;
        this.callback = callback;
    }

    public UUIDUtil(String username, UUIDCallback callback) {
        this.usernames = Lists.newArrayList(username);
        this.callback = callback;
    }

    @Override
    public void run() {
        for (String username : this.usernames) {
            UUIDRunner uuidRunner = new UUIDRunner(repository, username);
            this.uuidRunnables.add(uuidRunner);
            new Thread(uuidRunner).start();
        }

        Iterator<UUIDRunner> i = uuidRunnables.iterator();
        while (i.hasNext()) {
            UUIDRunner job = i.next();
            if (job.getComplete().get()) {
                this.callback.complete(job.getUsername(), job.getUuid());
                i.remove();
            }
        }
    }

    public static class UUIDException extends Exception {
        public UUIDException(String message) {
            super(message);
        }
    }

    public static class UUIDRunner implements Runnable {
        private static final String AGENT = "minecraft";
        private final HttpProfileRepository httpProfileRepository;
        @Getter private final String username;
        @Getter private String uuid;
        @Getter private final AtomicBoolean complete = new AtomicBoolean(false);

        public UUIDRunner(HttpProfileRepository httpProfileRepository, String username) {
            this.httpProfileRepository = httpProfileRepository;
            this.username = username;
        }

        @Override
        public void run() {
            Profile[] profiles = httpProfileRepository.findProfilesByCriteria(new ProfileCriteria(username, AGENT));
            if (profiles[0] == null) {
                this.uuid = null;
            } else {
                uuid = profiles[0].getId();
            }
            complete.set(true);
        }
    }

    public static interface UUIDCallback {
        public void complete(String username, String uuid);
    }
}
