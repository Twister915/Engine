package net.tbnr.gearz.effects.entityblocks;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.tbnr.gearz.packets.wrapper.WrapperPlayServerEntityMetadata;
import net.tbnr.gearz.packets.wrapper.WrapperPlayServerSpawnEntity;
import net.tbnr.gearz.packets.wrapper.WrapperPlayServerSpawnEntity.ObjectTypes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by George on 04/02/14.
 * <p/>
 * Purpose Of File:
 * <p/>
 * Latest Change:
 */
public class EntityBlock {

	Location location;
	Material type;
	byte data;
	static int entityIDLevel = 1000;

	private EntityBlock(Location location, Material material, byte data) {
		this.location = location;
		this.type = material;
		this.data = data;
	}

	public int showBlock(Player player) {
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();

		// Use a counter to get new entity IDs
		int newEntityID = entityIDLevel;
		if(newEntityID >= 5000) newEntityID = 1000;

		// Give the illusion of containing a portal block
		WrapperPlayServerSpawnEntity spawnVehicle = new WrapperPlayServerSpawnEntity();
		WrapperPlayServerEntityMetadata entityMeta = new WrapperPlayServerEntityMetadata();
		WrappedDataWatcher watcher = new WrappedDataWatcher();

		spawnVehicle.setEntityID(newEntityID);
		spawnVehicle.setType(ObjectTypes.MINECART);
		spawnVehicle.setX(location.getX());
		spawnVehicle.setY(location.getY());
		spawnVehicle.setZ(location.getZ());

		watcher.setObject(20, type.getId() | (data << 16));
		watcher.setObject(21, 160);
		watcher.setObject(22, (byte)1);

		// Initialize packet
		entityMeta.setEntityMetadata(watcher.getWatchableObjects());
		entityMeta.setEntityId(newEntityID);

		try {
			manager.sendServerPacket(player, spawnVehicle.getHandle());
			manager.sendServerPacket(player, entityMeta.getHandle());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		entityIDLevel++;
		return newEntityID;
	}

	public static EntityBlock newBlock(Location location, Material material, byte data) {
		return new EntityBlock(location, material, data).register();
	}

	private EntityBlock register() {
		return EntityBlockManager.registerBlock(this);
	}
}
