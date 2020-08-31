package gossip;

/**
 * Contains information of a suspicious node.
 * A suspicious node's heartbeat has not increased in the last [n] seconds
 * and might be inactive.
 * @author Katharina Koslowski, s0561010@htw-berlin.de
 */
public class SuspiciousNodeInfo extends NodeInfo {
    /**
     * Creates a suspicious node for given process id.
     * @param processId process id
     */
    public SuspiciousNodeInfo(int processId) {
        super(processId);
    }

    /**
     * Copies node information.
     * @param nodeInfo node info
     */
    public SuspiciousNodeInfo(NodeInfo nodeInfo) {
        super(nodeInfo);
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(object);
    }

    public String toString() {
        return "Process " + getProcessId() + " is INACTIVE.";
    }
}
