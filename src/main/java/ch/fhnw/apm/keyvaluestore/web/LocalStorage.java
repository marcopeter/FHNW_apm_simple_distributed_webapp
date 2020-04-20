package ch.fhnw.apm.keyvaluestore.web;

import static ch.fhnw.apm.keyvaluestore.web.ClusterStatus.CLUSTER_FUNCTIONAL;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalStorage implements Storage {

    private static Map<Integer, String> storage = new ConcurrentHashMap<>();

    @Override
    public boolean store(int key, String value) {
        if (value == null) {
            storage.remove(key);
        } else {
            storage.put(key, value);
        }
        return true;
    }

    @Override
    public String load(int key) {
        return storage.get(key);
    }

    @Override
    public ClusterStatus getCurrentClusterStatus() {
        return CLUSTER_FUNCTIONAL;
    }

    @Override
    public boolean getCurrentNodeStatus(InetAddress ia) {
        if (ia.isAnyLocalAddress()) {
            return true;
        } else {
            return false;
        }
    }
}
