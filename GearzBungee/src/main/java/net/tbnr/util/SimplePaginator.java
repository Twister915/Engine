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

package net.tbnr.util;

import net.md_5.bungee.api.CommandSender;

import java.util.List;

/**
 * Created by Jake on 1/27/14.
 *
 * Purpose Of File: Allows for simple pagination,
 * and full customization.
 *
 * Latest Change: Add documentation
 */
public abstract class SimplePaginator<T> {
    int perPage;

    /**
     * Creates an instance of a {@link net.tbnr.util.SimplePaginator}
     *
     * @param perPage number of entries per page
     */
    public SimplePaginator(int perPage) {
        this.perPage = perPage;
    }

    /**
     * Paginates a list of {@link java.lang.Object}
     *
     * @param sender {@link net.md_5.bungee.api.CommandSender} of the command to paginate
     * @param toPaginate the generics {@link java.util.List} to paginate
     * @param page the page that should be sent
     * @param headerOption the option that should be used for the header
     * @param entryOption the option that should be used for the entry
     */
    public void paginate(CommandSender sender, List<? extends T> toPaginate, int page, int headerOption, int entryOption) {
        if (toPaginate.size() == 0) throw new IllegalArgumentException("No results match!");

        int maxPages = toPaginate.size() / this.perPage + 1;
        if (page <= 0 || page > maxPages) throw new IllegalArgumentException("Unknown page selected! " + maxPages + " total pages.");

        sender.sendMessage(formatHeader(headerOption).replace("<page>", page + "").replace("<max>", maxPages + ""));

        for (int i = this.perPage * (page - 1); i < this.perPage * page && i < toPaginate.size(); i++) {
            sender.sendMessage(this.formatEntry(toPaginate.get(i), i, entryOption));
        }
    }

    /**
     * Formats an entry based on the arguments
     * Allows different options to be used when paginating.
     *
     * @param entry the entry to be formatted
     * @param num the entry's number
     * @param optionNum the option number to format it with, see above
     * @return the formatted {@link java.lang.String}
     */
    public abstract String formatEntry(T entry, int num, int optionNum);

    /**
     * Formats the header based on the arguments
     *
     * @param optionNum The option number to use when formatting, see above
     * @return the formatted header
     */
    public abstract String formatHeader(int optionNum);
}
