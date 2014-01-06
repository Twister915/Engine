package net.tbnr.gearz;

import net.craftminecraft.bungee.bungeeyaml.supereasyconfig.Config;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;

/**
 * Created by jake on 1/5/14.
 */
public class GearzConfig extends Config {
    public GearzConfig(Plugin plugin) {
        super();
        CONFIG_FILE = new File("plugins" + File.separator + plugin.getDescription().getName(), "config.yml");
        CONFIG_HEADER = "GearzBungee Config File";
    }

    public String database = "gearz_test";
    public String host = "one.tbnr.pw";
    public int port = 27017;
}
