package net.cogz.gearz.hub.annotations;

import net.cogz.gearz.hub.GearzHub;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by jake on 2/21/14.
 *
 * Purpose Of File:
 *
 * Latest Change:
 */
public class HubModules {
    List<HubModule> modules;

    public HubModules(String packageName) {
        modules = new ArrayList<>();
        Reflections hubModuleReflections = new Reflections(packageName);

        Set<Class<? extends HubModule>> hubModules = hubModuleReflections.getSubTypesOf(HubModule.class);

        for (Class<? extends HubModule> hubModule : hubModules) {
            HubModuleMeta moduleMeta = hubModule.getAnnotation(HubModuleMeta.class);
            if (moduleMeta == null) continue;
            if (!moduleMeta.enabled()) continue;
            if (GearzHub.getInstance().getConfig().getBoolean("hub-modules." + moduleMeta.key() + ".isEnabled", false)) {
                GearzHub.getInstance().getLogger().info("Enabled module: " + moduleMeta.key());
                try {
                    HubModule module = hubModule.newInstance();
                    modules.add(module);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
