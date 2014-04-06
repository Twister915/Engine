/*
 * Copyright (c) 2014.
 * Cogz Development LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.util;

import java.util.Collection;
import java.util.LinkedList;

/**
 * <p/>
 * Latest Change:
 * <p/>
 *
 * @author George
 * @since 06/04/14
 */
public class NoDuplicatesList<E> extends LinkedList<E> {
	@Override
	public boolean add(E e) {
		return !this.contains(e) && super.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		Collection<E> copy = new LinkedList<E>(collection);
		copy.removeAll(this);
		return super.addAll(copy);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> collection) {
		Collection<E> copy = new LinkedList<E>(collection);
		copy.removeAll(this);
		return super.addAll(index, copy);
	}

	@Override
	public void add(int index, E element) {
		if (!this.contains(element)) super.add(index, element);
	}
}