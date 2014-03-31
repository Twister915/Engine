package net.tbnr.gearz.arena;

import net.tbnr.gearz.GearzException;

import java.util.List;

public final class RegionIterator extends ArenaIterator<Region> {
    public RegionIterator(List<Region> stuff) throws GearzException {
        super(stuff);
    }

    public RegionIterator() throws GearzException {
    }
}
