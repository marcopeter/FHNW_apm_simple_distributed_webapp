package ch.fhnw.apm.keyvaluestore.web;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedHashSet;

// LFU Cache
public class CachedStorage implements  Storage {

    HashMap<Integer, String> vals;//cache K and V
    HashMap<Integer, Integer> counts;//K and counters
    HashMap<Integer, LinkedHashSet<Integer>> lists;//Counter and item list
    int cap;
    int min = -1;
    ClusterStorage clusterStorage;

    public CachedStorage(int cacheSize, ClusterStorage storageToBeCached) {
        this.cap = cacheSize;
        this.clusterStorage = storageToBeCached;

        vals = new HashMap<>();
        counts = new HashMap<>();
        lists = new HashMap<>();
        lists.put(1, new LinkedHashSet<>());
    }

    @Override
    public boolean store(int key, String value) {
        if (cap <= 0)
            return false;
        // If key does exist, we are returning from here
        if (vals.containsKey(key)) {
            vals.put(key, value);
            clusterStorage.store(key, value);
            return true;
        }
        if (vals.size() >= cap) {
            int evit = lists.get(min).iterator().next();
            lists.get(min).remove(evit);
            vals.remove(evit);
            counts.remove(evit);
        }
        // If the key is new, insert the value and current min should be 1 of course
        vals.put(key, value);
        counts.put(key, 1);
        min = 1;
        lists.get(1).add(key);
        clusterStorage.store(key, value);

        return true;
    }

    @Override
    public String load(int key) {
        if (!vals.containsKey(key)) {
            if (clusterStorage.containsKey(key)) {
                String val =  clusterStorage.load(key);
                this.store(key, val);
                return val;
            } else {
                return "";
            }
        } else {
            // Get the count from counts map
            int count = counts.get(key);
            // increase the counter
            counts.put(key, count + 1);
            // remove the element from the counter to linkedhashset
            lists.get(count).remove(key);

            // when current min does not have any data, next one would be the min
            if (count == min && lists.get(count).size() == 0)
                min++;
            if (!lists.containsKey(count + 1))
                lists.put(count + 1, new LinkedHashSet<>());
            lists.get(count + 1).add(key);
            return vals.get(key);
        }
    }

    @Override
    public ClusterStatus getCurrentClusterStatus() {
        return null;
    }

    @Override
    public boolean getCurrentNodeStatus(InetAddress ia) {
        return false;
    }
}
