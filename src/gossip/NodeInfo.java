package gossip;

/**
 * Contains information about a node (process).
 * @author Katharina Koslowski, s0561010@htw-berlin.de
 */
public abstract class NodeInfo {
    /**
     * Process Id
     */
    private int         processId;
    /**
     * Heartbeat of this process
     */
    private HeartBeat   heartbeat;
    /**
     * Name of this process
     */
    private String      name;

    /**
     * Creates new node info for process id.
     * @param processId process id
     */
    public NodeInfo(int processId) {
        heartbeat       = new HeartBeat();
        this.processId  = processId;
        this.name       = "Process " + processId;
    }

    /**
     * Copies node info.
     * @param nodeInfo info to be copied
     */
    public NodeInfo(NodeInfo nodeInfo) {
        this.heartbeat  = nodeInfo.getHeartbeat();
        this.processId  = nodeInfo.getProcessId();
        this.name       = nodeInfo.getName();
    }

    /**
     * Triggers one heartbeat.
     */
    public void heartbeat(){
        heartbeat.beat();
    }

    public String getName() {
        return name;
    }

    public int getProcessId() {
        return processId;
    }

    public HeartBeat getHeartbeat() {
        return heartbeat;
    }

    /**
     * Checks if heartbeat has increased recently.
     * @return true, if heartbeat has increased recently.
     */
    public boolean isHeartBeatActive() {
        return heartbeat.checkHeartBeat();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof NodeInfo)) return false;
        NodeInfo nodeInfo = (NodeInfo)object;
        return this.processId == nodeInfo.getProcessId();
    }
}
