package gossip;

import java.time.LocalDateTime;

/**
 * Represents if a process is still active.
 * @author Katharina Koslowski, s0561010@htw-berlin.de
 */
public class HeartBeat {
    /**
     * Current heartbeat.
     */
    private int             heartbeat;
    /**
     * Seconds until heartbeat is considered inactive.
     */
    private final long      failSeconds = 3;
    /**
     * Timestamp of last heartbeat.
     */
    private LocalDateTime   lastHeartBeat;

    /**
     * Initializes heartbeat, saves timestamp of creation.
     */
    public HeartBeat() {
        heartbeat       = 0;
        lastHeartBeat   = LocalDateTime.now();
    }

    /**
     * Heart beats one time, timestamp is saved.
     */
    public void beat() {
        heartbeat++;
        lastHeartBeat = LocalDateTime.now();
    }

    /**
     * Checks if last increase in heartbeat is less than n failSeconds ago.
     * @return true if heartbeat is still active
     */
    public boolean checkHeartBeat() {
        return !LocalDateTime.now().minusSeconds(failSeconds).isAfter(lastHeartBeat);
    }

    public int getHeartbeat() {
        return heartbeat;
    }

    public int compareTo(HeartBeat other) {
        return Integer.compare(heartbeat, other.getHeartbeat());
    }
}
