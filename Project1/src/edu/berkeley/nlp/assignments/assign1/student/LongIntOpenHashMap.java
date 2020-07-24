package edu.berkeley.nlp.assignments.assign1.student;

import edu.berkeley.nlp.util.CollectionUtils;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Open address hash map with linear probing. Maps Strings to long's. Note that
 * long's are assumed to be non-negative, and -1 is returned when a key is not
 * present.
 *
 * @author adampauls
 *
 */
public class LongIntOpenHashMap {

    private boolean extra;

    private long[] keys;

    private int[] values;

    private int[] denom = null;

    private int[] alpha = null;

    private int size = 0;

    private final long EMPTY_KEY = -1;

    private final double MAX_LOAD_FACTOR;

    public boolean put(long k, int v) {
        if (size / (double) keys.length > MAX_LOAD_FACTOR) {
            rehash();
        }

        return putHelp(k, v, keys, values);

    }

    public LongIntOpenHashMap(boolean extra) {
        this(100000, extra);
    }

    public LongIntOpenHashMap(int initialCapacity_, boolean extra) {
        this(initialCapacity_, 0.8, extra);
    }

    public LongIntOpenHashMap(int initialCapacity_, double loadFactor, boolean extra) {

        int cap = Math.max(5, (int) (initialCapacity_ / loadFactor));
        this.extra = extra;
        MAX_LOAD_FACTOR = loadFactor;
        values = new int[cap];
        keys = new long[cap];
        Arrays.fill(keys, -1);
        if (this.extra) {
            denom = new int[cap];
            alpha = new int[cap];
        }
//        System.out.println(keys.length);
    }

    /**
     *
     */
    private void rehash() {
//        System.out.println("rehash");
        long[] newKeys = new long[(int) (keys.length * 1.5)];
        int[] newValues = new int[(int) (values.length * 1.5)];
        Arrays.fill(newKeys, -1);
        int[] newDenom = null;
        int[] newAlpha = null;
        if (extra) {
            newDenom = new int[(int) (values.length * 1.5)];
            newAlpha = new int[(int) (values.length * 1.5)];
        }

        size = 0;
        for (int i = 0; i < keys.length; ++i) {
            long curr = keys[i];
            if (curr != -1) {
                int val = values[i];
                putHelp(curr, val, newKeys, newValues);
            }
        }
        keys = newKeys;
        values = newValues;
        denom = newDenom;
        alpha = newAlpha;

    }

    /**
     * @param k
     * @param v
     */
    private boolean putHelp(long k, int v, long[] keyArray, int[] valueArray) {
        int pos = getInitialPos(k, keyArray);
        long curr = keyArray[pos];
        while (curr != -1 && curr != k) {
            pos++;
            if (pos == keyArray.length) pos = 0;
            curr = keyArray[pos];
        }

        valueArray[pos] = v;
        if (curr == -1) {
//            System.out.println(size);
            size++;
            keyArray[pos] = k;
            return true;
        }
        return false;
    }

    /**
     * @param k
     * @param keyArray
     * @return
     */
    private int getInitialPos(long k, long[] keyArray) {
        long pos =  (k ^ (k >>> 32)) * 3875239;
//        int pos = hash % keyArray.length;
        if (pos < 0) pos = pos % keyArray.length + keyArray.length;
        if (pos >= keyArray.length) pos =  pos % keyArray.length;
        // N.B. Doing it this old way causes Integer.MIN_VALUE to be
        // handled incorrect since -Integer.MIN_VALUE is still
        // Integer.MIN_VALUE
//		if (hash < 0) hash = -hash;
//		int pos = hash % keyArray.length;
        return (int) pos;
    }

    public int get(long k) {
        int pos = find(k);

        return values[pos];
    }

    public void setDenom(long k, int v) {
        int pos = find(k);
        denom[pos] = v;
    }

    public void setAlpha(long k, int v) {
        int pos = find(k);
        alpha[pos] = v;
    }

    public int getDenom(long k){
        int pos = find(k);
        return denom[pos];
    }

    public int getAlpha(long k){
        int pos = find(k);
        return alpha[pos];
    }

    /**
     * @param k
     * @return
     */
    private int find(long k) {
        int pos = getInitialPos(k, keys);
        long curr = keys[pos];
        while ( curr != -1 && curr != k ) {
            pos++;
            if (pos == keys.length) pos = 0;
            curr = keys[pos];
        }
        return pos;
    }

    public void increment(long k, int c) {
        int pos = find(k);
        long currKey = keys[pos];
        if (currKey == -1) {
            put(k, c);
        } else
            values[pos]++;
    }

    public static class Entry
    {
        /**
         * @param key
         * @param value
         */
        public Entry(long key, int value) {
            super();
            this.key = key;
            this.value = value;
        }

        public long key;

        public int value;

        public long getKey() {
            return key;
        }

        public int getValue() {
            return value;
        }

    }

    private class EntryIterator extends MapIterator implements Iterator {
        public Entry next() {
            final int nextIndex = nextIndex();

            return new Entry(keys[nextIndex], values[nextIndex]);
        }
    }

    private abstract class MapIterator implements Iterator
    {
        public MapIterator() {
            end = keys.length;
            next = -1;
            nextIndex();
        }

        public boolean hasNext() {
            return next < end;
        }

        int nextIndex() {
            int curr = next;
            do {
                next++;
            } while (next < end && keys[next] == EMPTY_KEY);
            return curr;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        private int next, end;
    }

    public Iterable<Entry> entrySet() {
        return CollectionUtils.iterable(new EntryIterator());
    }

    public int size() {
        return size;
    }

}
