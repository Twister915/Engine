package net.tbnr.gearz;

import net.tbnr.gearz.server.ServerManager;
import net.tbnr.util.TPlugin;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 10/11/13
 * Time: 12:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class GearzSetup extends TPlugin {
    private static GearzSetup instance;

    public static GearzSetup getInstance() {
        return GearzSetup.instance;
    }

    @Override
    public void enable() {
        GearzSetup.instance = this;
        GameSetupFactory setupFactory = new GameSetupFactory();
        registerEvents(setupFactory);
        registerCommands(setupFactory);
        ServerManager.setOpenForJoining(true);
    }

    @Override
    public void disable() {
    }

    @Override
    public String getStorablePrefix() {
        return "setup";  //To change body of implemented methods use File | Settings | File Templates.
    }
}
