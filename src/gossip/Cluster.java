package gossip;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A Cluster contains multiple processes.
 * @author Katharina Koslowski, s0561010@htw-berlin.de
 */
public class Cluster {
    /**
     * Count of processes in cluster.
     */
    public static final int
            memberCount = 5;
    /**
     * Members of this cluster
     */
    private static ConcurrentMap<Integer, Member>
            members = new ConcurrentHashMap<>();

    /**
     * Add a new process to cluster
     */
    public static void addMember(int processId) {
        members.put(processId, new Member(processId));
    }

    /**
     * Returns the neighbor left of the given one.
     * @param processId process id of given process
     * @return left neighbour
     */
    public static Member getLeftNeighbour(int processId) {
        if (members.isEmpty() || !members.containsKey(processId) || members.size() == 1) return null;
        if (processId-1 < 0) return members.get(members.size()-1);
        return members.get(processId-1);
    }

    /**
     * Returns the neighbor right of the given one.
     * @param processId process id of given process
     * @return right neighbour
     */
    public static Member getRightNeighbour(int processId) {
        if (members.isEmpty() || !members.containsKey(processId) || members.size() == 1) return null;
        if (processId+1 >= members.size()) return members.get(0);
        return members.get(processId+1);
    }

    public static Member getRandomMember(int processId) {
        int randomElementIndex = -1;
        do {
            randomElementIndex
                    = ThreadLocalRandom.current().nextInt(members.size()) % members.size();
        } while (randomElementIndex == processId);
        return members.get(randomElementIndex);
    }

    /**
     * Retrieves member with given process id.
     * @param processId process id
     * @return member
     */
    public static Member getMember(int processId) {
        return members.get(processId);
    }

    /**
     * Starts all member-threads.
     */
    public static void startMembers() {
        for (int i = 0; i < memberCount; i++) {
            Cluster.addMember(i);
        }

        for (ConcurrentMap.Entry<Integer, Member> m : members.entrySet()) {
            m.getValue().start();
        }
    }
}
