package ch.fhnw.apm.keyvaluestore.web;

import java.net.InetAddress;

public interface Storage {

    /***
     * Stores the value in the Cluster.
     *
     * @param key The key slot in which the value is stored
     * @param value The value to store. Use <code>null</code> to remove a
     * value from the store.
     * @return <code>true</code> if the value has been successfully stored
     */
    public boolean store(int key, String value);

    /***
     * Loads the value from the cluster.
     *
     * @param key The key to be retrieved
     * @return The value requested or <code>null</code> if it does not exist
     */
    public String load(int key);

    /***
     * Retrieves the current status of the storage cluster.
     *
     * The status is refreshed at least every 5s.
     *
     * @return The Cluster status
     */
    public ClusterStatus getCurrentClusterStatus();

    /***
     * The status of a specific cluster node.
     *
     * @param ia The address of a cluster node to be checked
     * @return <code>true</code> if the storage cluster node is running
     */
    public boolean getCurrentNodeStatus(InetAddress ia);

}
