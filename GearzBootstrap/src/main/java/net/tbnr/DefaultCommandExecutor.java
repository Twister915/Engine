package net.tbnr;

/**
 * Created by rigor789 on 2014.01.17..
 */
public class DefaultCommandExecutor extends CommandExecutor {
    @Override
    public boolean onCommand(Command cmd, String[] args) {
        if(cmd.getName().equalsIgnoreCase("about")){
            System.out.println("GearzBootstrap v0.1 by Rigi LOL :D");
        } else if (cmd.getName().equalsIgnoreCase("exit")){
            System.out.println("Good Bye!");
            GearzBootstrap.getInstance().setExit(true);
        }
        return false;
    }
}
