package gossip;

/**
 * Contains information about a healthy node.
 * Healthy nodes are active processes and have an increasing heartbeat.
 * @author Katharina Koslowski, s0561010@htw-berlin.de
 */
public class HealthyNodeInfo extends NodeInfo {
    /**
     * Creates healthy node info for given process id.
     * @param processId process id
     */
    public HealthyNodeInfo(int processId) {
        super(processId);
    }

    /**
     * Copies node information.
     * @param nodeInfo node info
     */
    public HealthyNodeInfo(NodeInfo nodeInfo) {
        super(nodeInfo);
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(object);
    }

    public String toString() {
        return "Process " + getProcessId() + " is ACTIVE.";
    }
}
