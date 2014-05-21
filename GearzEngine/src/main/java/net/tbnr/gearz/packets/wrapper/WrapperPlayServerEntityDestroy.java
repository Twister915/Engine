/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.gearz.packets.wrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.primitives.Ints;

import java.util.List;

public class WrapperPlayServerEntityDestroy extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_DESTROY;

    public WrapperPlayServerEntityDestroy() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerEntityDestroy(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve the IDs of the entities that will be destroyed.
     *
     * @return The current entities.
     */
    public List<Integer> getEntities() {
        return Ints.asList(handle.getIntegerArrays().read(0));
    }

    /**
     * Set the entities that will be destroyed.
     *
     * @param entities - new value.
     */
    public void setEntities(int[] entities) {
        handle.getIntegerArrays().write(0, entities);
    }

    /**
     * Set the entities that will be destroyed.
     *
     * @param entities - new value.
     */
    public void setEntities(List<Integer> entities) {
        setEntities(Ints.toArray(entities));
    }
}
