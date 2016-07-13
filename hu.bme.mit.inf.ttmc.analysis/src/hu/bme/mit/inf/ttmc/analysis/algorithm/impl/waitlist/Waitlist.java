package hu.bme.mit.inf.ttmc.analysis.algorithm.impl.waitlist;

import java.util.Collection;

public interface Waitlist<T> {
	void add(T item);

	void addAll(Collection<? extends T> items);

	boolean isEmpty();

	T remove();

	void clear();
}
