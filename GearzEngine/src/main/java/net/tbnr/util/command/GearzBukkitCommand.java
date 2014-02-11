package net.tbnr.util.command;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public class GearzBukkitCommand extends BukkitCommand {
    public GearzBukkitCommand(String name, String description, String usageMessage) {
        super(name);
        this.description = description;
        this.usageMessage = usageMessage;
        //this.setPermission();
        //this.setAliases()
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return true;
    }
}
