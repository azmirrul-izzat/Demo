package com.apddemo.core.services.impl;

import com.apddemo.core.services.SearchService;
import com.apddemo.core.utils.NodeUtils;
import com.apddemo.core.utils.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.ArrayList;
import java.util.List;

@Component(immediate = true, service = SearchService.class,
        configurationPid = "com.apddemo.core.daos.impl.SearchServiceImpl")
public class SearchServiceImpl implements SearchService {

	 /**
     * Binary search method that works with generics.<br>
     * NOTE: All arrays must be pre-sorted. Extra private method including 
     * low and high index limits is also required for recursive 
     * implementation.<br>
     * 
     * @param <E> Objects in the array MUST implement Comparable interface.
     * @param array The array to be searched through.
     * @param target The value to search the array for.
     * @return Returns call to binarySearch that includes extra parameters.
     */
	@Override
    public <E extends Comparable<E>> int 
    binarySearch(E[] array, E target) {
        return binarySearch(array, target, 0, array.length - 1);
     }

     /**
      * Recursive private binary search method that works with generics.<br>
      * 
      * @param <E> Objects in the array MUST implement Comparable interface.
      * @param array The array to be searched through.
      * @param target The value to search the array for.
      * @param low The lower index limit of the array for searching through.
      * @param high The upper index limit of the array for searching through.
      * @return Returns the index of the value if found or -1 otherwise.
      */
    @Override
     public <E extends Comparable<E>> int
     binarySearch(E[] array, E target, int low, int high) {
        /**
         * Check if lower and upper array limits are valid.
         * If low is greater than high, return -1.
         */
        if (low > high)
            return -1;

        /**
         * Set int mid to the value of middle index in array.
         */
        int mid = (low + high) / 2;

        /**
         * Set int compareValue to result of array[mid].compareTo(target).
         */
        int compareValue = array[mid].compareTo(target);

        /**
         * RECURSIVE BASE CASE
         * If value at mid in array is equal to target value, return mid.
         * Else, if value at mid is less than target value, return recursive 
         * call with (mid + 1).
         * Else, return recursive call with (mid - 1).
         */
        if (compareValue == 0)
            return mid;
        else if (compareValue > 0)
            return binarySearch(array, target, low, mid - 1);
        else
            return binarySearch(array, target, mid + 1, high);
     }
}
