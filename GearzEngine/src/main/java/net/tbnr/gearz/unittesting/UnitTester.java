package net.tbnr.gearz.unittesting;

import lombok.Getter;

/**
 * Created by George on 20/01/14.
 * <p/>
 * Purpose Of File:
 * <p/>
 * Latest Change:
 */
public class UnitTester {

    @Getter
    public final UnitTesterConfig config = new UnitTesterConfig();

    public UnitTester(String title) {
        config.setTitle(title);
    }


    public boolean test(Object expected, Object got) {
        return expected.equals(got);
    }

}
