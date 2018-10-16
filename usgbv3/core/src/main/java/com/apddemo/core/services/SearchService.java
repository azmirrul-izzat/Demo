package com.apddemo.core.services;


import java.util.List;

public interface SearchService {

	
	public <E extends Comparable<E>> int binarySearch(E[] array, E target);
	public <E extends Comparable<E>> int binarySearch(E[] array, E target, int low, int high);

}
