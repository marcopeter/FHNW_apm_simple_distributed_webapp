package ch.fhnw.apm.keyvaluestore.web;

import static ch.fhnw.apm.keyvaluestore.web.ClusterStatus.CLUSTER_DEGRADED;
import static ch.fhnw.apm.keyvaluestore.web.ClusterStatus.CLUSTER_FUNCTIONAL;
import static ch.fhnw.apm.keyvaluestore.web.ClusterStatus.CLUSTER_ILLEGAL;
import static com.hazelcast.core.Hazelcast.newHazelcastInstance;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import com.hazelcast.config.Config;
import com.hazelcast.core.Client;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

/**
 * This is a distributed storage for a clustered map. This class
 * requires the hazelcast library.
 */
public class ClusterStorage implements Storage {

    private static HazelcastInstance instance = newHazelcastInstance(new Config());
    private static IMap<Integer, String> storage = instance.getMap("clusterMap");

    @Override
    public boolean store(int id, String value) {
        if (value == null) {
            storage.remove(id);
        } else {
            storage.put(id, value);
        }
        return true;
    }

    @Override
    public String load(int id) {
        return storage.get(id);
    }

    public boolean containsKey(int id) {
        return storage.containsKey(id);
    }

    @Override
    public ClusterStatus getCurrentClusterStatus() {
        // return fully functional if we have more than one cluster node
        if (getMemberList().size() > 1) {
            return CLUSTER_FUNCTIONAL;
        }
        // when only one node is connected return degraded
        if (getMemberList().size() == 1) {
            return CLUSTER_DEGRADED;
        }
        // consider all other states as illegal
        return CLUSTER_ILLEGAL;
    }

    @Override
    public boolean getCurrentNodeStatus(InetAddress ia) {
        // Local node is always considered healthy
        if (ia.isAnyLocalAddress()) {
            return true;
        }
        // Check if given address is in cluster
        for (Client c : instance.getClientService().getConnectedClients()) {
            if (c.getSocketAddress().getAddress().equals(ia)) {
                return true;
            }
        }
        // reject all other addresses
        return false;
    }

    public List<SocketAddress> getMemberList() {
        List<SocketAddress> v = new ArrayList<>();
        for (Client c : instance.getClientService().getConnectedClients()) {
            v.add(c.getSocketAddress());
        }
        return v;
    }
}
