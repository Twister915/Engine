package net.tbnr.gearz.effects.entityblocks;

import net.tbnr.util.EntityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.reflections.Reflections;

import java.util.UUID;

/**
 * Created by George on 04/02/14.
 * <p/>
 * Purpose Of File:
 * <p/>
 * Latest Change:
 */
public class EntityBlock extends GearzBlock {

	private final UUID UUID;

	EntityBlock(Location location) {
		super(location);
		this.UUID = spawnMinecart();
		giveNBTData();
	}

	public UUID spawnMinecart() {
		return block.getWorld().spawnEntity(block.getLocation(), EntityType.MINECART).getUniqueId();
	}

	public void giveNBTData() {

		final String NMS_PATH =
			"net.minecraft.server."+
			(Bukkit.getServer() != null ?
					Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] :
					"UNKNOWN"
			);

		Minecart m = (Minecart) EntityUtil.UUID2Entity(UUID);


		EntityInsentient nmsEntity = (EntityInsentient) ((CraftLivingEntity) entity).getHandle();
		AttributeInstance attributes = nmsEntity.a(GenericAttributes.d);
	}

}
