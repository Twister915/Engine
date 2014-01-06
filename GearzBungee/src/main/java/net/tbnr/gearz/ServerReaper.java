package net.tbnr.gearz;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.tbnr.gearz.server.Server;
import net.tbnr.gearz.server.ServerManager;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Joey on 12/20/13.
 */
public class ServerReaper implements Runnable {
    private HashMap<Server, ReapableServer> reapables;
    private boolean scheduled = false;

    public ServerReaper() {
        this.reapables = new HashMap<>();
    }

    @Data
    @RequiredArgsConstructor
    private static class ReapableServer {
        @NonNull
        private Server server;
        private Integer timesFailed = 0;
        private static final Integer failureThreshold = 2;

        private boolean shouldReap() {
            return this.timesFailed > ReapableServer.failureThreshold;
        }

        public void check() {
            ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server.getBungee_name());
            if (serverInfo == null) {
                this.timesFailed++;
            } else {
                this.timesFailed = 0;
            }
        }

        public boolean reap() {
            if (this.shouldReap()) {
                this.server.remove();
                return true;
            }
            return false;
        }
    }

    @Override
    public void run() {
        populateReaps();
        for (ReapableServer reapableServer : reapables.values()) {
            reapableServer.check();
            reapableServer.reap();
        }
    }

    private void populateReaps() {
        List<Server> allServers = ServerManager.getAllServers();
        for (Server s : allServers) {
            if (this.reapables.containsKey(s)) continue;
            this.reapables.put(s, new ReapableServer(s));
        }
        this.scheduled = false;
        schedule();
    }

    public void schedule() {
        if (this.scheduled) return;
        this.scheduled = true;
        ProxyServer.getInstance().getScheduler().schedule(GearzBungee.getInstance(), this, 5, TimeUnit.SECONDS);
    }
}
