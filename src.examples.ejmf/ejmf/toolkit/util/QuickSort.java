package ejmf.toolkit.util;

//
// QuickSort - a class to sort arrays of objects (quickly even)
//

public class QuickSort
{
    // least to greatest sorting member functions
    public static void sort(Sortable[] a)
    {
        sort(a, 0, a.length - 1);
    }

    public static void sort(Sortable[] a, int lo0, int hi0)
    {
		int lo = lo0;
		int hi = hi0;
		Sortable mid;

		if (hi0 > lo0) {

		/* Arbitrarily establishing partition element as the midpoint of
		 * the array.
		 */
		mid = a[(lo0 + hi0) / 2];

		// loop through the array until indices cross
		while (lo <= hi) {
		/* find the first element that is greater than or equal to 
		  * the partition element starting from the left Index.
		*/
		    while ((lo < hi0) && (a[lo].lessThan(mid))) {
				++lo;
		    }

		   /* find an element that is smaller than or equal to 
		    * the partition element starting from the right Index.
		    */
		    while(( hi > lo0) && (mid.lessThan(a[hi]))) {
				--hi;
		    }

		    // if the indexes have not crossed, swap
		    if (lo <= hi) {
			swap(a, lo, hi);
			++lo;
			--hi;
		    }
		}

		/* If the right index has not reached the left side of array
		 * must now sort the left partition.
		 */
		if (lo0 < hi) {
		    sort(a, lo0, hi);
		}

		/* If the left index has not reached the right side of array
		 * must now sort the right partition.
		 */
		if (lo < hi0) {
		    sort(a, lo, hi0);
		}
	}
    }

    private static void swap(Sortable[] a, int i, int j) {
	Sortable T;
	T = a[i]; 
	a[i] = a[j];
	a[j] = T;
    }
}
