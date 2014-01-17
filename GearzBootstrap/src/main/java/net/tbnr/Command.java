package net.tbnr;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by rigor789 on 2014.01.17..
 */
public class Command {
    @Getter private String name;
    @Setter private CommandExecutor executor;

    public Command(String name){
        this.name = name;
    }

    public boolean execute(String[] args){
        return executor.onCommand(this, args);
    }
}
