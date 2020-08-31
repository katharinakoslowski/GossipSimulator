package gossip;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A process member of a cluster.
 * @author Katharina Koslowski, s0561010@htw-berlin.de
 */
public class Member extends Thread {
    /**
     * Process Id.
     */
    private int     processId;
    /**
     * True, if process is currently active
     */
    private boolean isActive;
    /**
     * List of active processes this member knows of.
     */
    private ConcurrentMap<Integer, HealthyNodeInfo>
                    healthyNodes;
    /**
     * List of inactive processes this member knows of.
     */
    private ConcurrentMap<Integer, SuspiciousNodeInfo>
                    suspiciousNodes;
    /**
     * For property change events.
     */
    private PropertyChangeSupport
                    changes = new PropertyChangeSupport( this );

    /**
     * Initializes a member with given process Id.
     * @param processId process id
     */
    public Member(int processId) {
        isActive        = true;
        this.processId  = processId;
        healthyNodes    = new ConcurrentHashMap<>();
        suspiciousNodes = new ConcurrentHashMap<>();
    }

    /**
     * (De-)Activates the member (switches its state).
     */
    public void activate() {
        isActive = !isActive;
        changes.firePropertyChange ( "label", "", toString());
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                if (!this.isActive) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            sendGossip();
            sendInfo();
            checkList();
        }
    }

    /**
     * Sends gossip (information about itself being alive) to its neighbours.
     */
    private void sendGossip() {
        try {
            Cluster.getLeftNeighbour(processId).receiveGossip(processId);
        } catch (NullPointerException e) {}
        try {
            Cluster.getRightNeighbour(processId).receiveGossip(processId);
        } catch (NullPointerException e) {}

    }

    /**
     * Receives gossip from other member and updates its node lists.
     * @param processId
     */
    public synchronized void receiveGossip(int processId) {
        if (!isActive) return;
        if (this.processId == processId) return;

        if (suspiciousNodes.containsKey(processId)) {
            // remove from sus
            NodeInfo nodeInfo = suspiciousNodes.get(processId);
            suspiciousNodes.remove(processId);
            nodeInfo.heartbeat();
            healthyNodes.put(processId, new HealthyNodeInfo(nodeInfo));
            changes.firePropertyChange( "label", "", toString());
            return;
        }

        if (healthyNodes.containsKey(processId)) {
            // update healthy
            healthyNodes.get(processId).heartbeat();
            return;
        }
        if (!healthyNodes.containsKey(processId)) {
            // add new
            healthyNodes.put(processId, new HealthyNodeInfo(processId));
        }
    }

    /**
     * Sends all information about known nodes and its own to a random member in cluster.
     */
    private void sendInfo() {
        Member random = Cluster.getRandomMember(processId);
        random.receiveInfo(healthyNodes, suspiciousNodes, processId);
    }

    /**
     * Receives all information about known nodes from a random member and updates lists.
     * @param healthy healthy nodes list
     * @param suspicious suspicious nodes list
     * @param processId id of random member
     */
    public void receiveInfo(Map<Integer, HealthyNodeInfo> healthy, Map<Integer, SuspiciousNodeInfo> suspicious, int processId) {
        if (!isActive) return;
        for (Map.Entry<Integer, HealthyNodeInfo> h : healthy.entrySet()) {
            updateHealthyNode(h.getValue());
        }
        receiveGossip(processId);
    }

    /**
     * Updates a known based on given healthy node information.
     * @param healthyNodeInfo given information
     */
    private void updateHealthyNode(HealthyNodeInfo healthyNodeInfo) {
        // is it my info? return
        if (processId == healthyNodeInfo.getProcessId()) return;

        // does it exist in list? update, return
        if (healthyNodes.containsKey(healthyNodeInfo.getProcessId())) {
            HealthyNodeInfo h = healthyNodes.get(healthyNodeInfo.getProcessId());
            if (h.getHeartbeat().compareTo(healthyNodeInfo.getHeartbeat()) < 0) {
                healthyNodes.put(healthyNodeInfo.getProcessId(), healthyNodeInfo);
                return;
            }
        }

        // does is exist in sus? swap, return
        if (suspiciousNodes.containsKey(healthyNodeInfo.getProcessId())) {
            suspiciousNodes.remove(healthyNodeInfo.getProcessId());
            healthyNodes.put(healthyNodeInfo.getProcessId(), healthyNodeInfo);
            return;
        }

        // does it not exist? add new
        healthyNodes.put(healthyNodeInfo.getProcessId(), healthyNodeInfo);
    }

    /**
     * Checks if a node in healthy list has been inactive and updates accordingly.
     */
    private void checkList() {
        for (Map.Entry<Integer, HealthyNodeInfo> h : healthyNodes.entrySet()) {
            if (!h.getValue().isHeartBeatActive()) {
                NodeInfo nodeInfo = h.getValue();
                suspiciousNodes.put(nodeInfo.getProcessId(), new SuspiciousNodeInfo(nodeInfo));
                healthyNodes.remove(nodeInfo.getProcessId());

                changes.firePropertyChange( "label", "", toString());
            }
        }
    }

    public String toString() {
        if (!isActive) {
            return "I am INACTIVE\n";
        }
        String label = "I am ACTIVE\n";
        for (Map.Entry<Integer, HealthyNodeInfo> h : healthyNodes.entrySet()) {
            label += h.getValue().toString() + "\n";
        }
        for (Map.Entry<Integer, SuspiciousNodeInfo> s : suspiciousNodes.entrySet()) {
            label += s.getValue().toString() + "\n";
        }
        return label;
    }
    public void addPropertyChangeListener( PropertyChangeListener l )
    {
        changes.addPropertyChangeListener( l );
    }

    public void removePropertyChangeListener( PropertyChangeListener l )
    {
        changes.removePropertyChangeListener( l );
    }
}
