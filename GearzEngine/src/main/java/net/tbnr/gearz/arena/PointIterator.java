package net.tbnr.gearz.arena;

import net.tbnr.gearz.GearzException;

import java.util.List;

public class PointIterator extends ArenaIterator<Point> {
    public PointIterator(List<Point> stuff) throws GearzException {
        super(stuff);
    }

    public PointIterator() throws GearzException {
    }
}
