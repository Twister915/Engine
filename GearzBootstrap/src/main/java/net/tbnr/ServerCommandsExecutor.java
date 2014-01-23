package net.tbnr;

/**
 * Created by rigor789 on 2014.01.18..
 */
public class ServerCommandsExecutor extends CommandExecutor {
    @Override
    public boolean onCommand(Command cmd, String[] args) {
        if(cmd.getName().equalsIgnoreCase("deploy")){
            ServerManager.getInstance().deployServer(ServerManager.Minigame.SURVIVALGAMES);
        } else if(cmd.getName().equalsIgnoreCase("attach")){
            ServerManager.getInstance().attach();
        }
        return false;
    }
}
