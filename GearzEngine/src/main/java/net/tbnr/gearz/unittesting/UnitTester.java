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
