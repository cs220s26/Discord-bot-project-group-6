package typingracebot.model;

import java.util.HashSet;
import java.util.Set;

public class Race {
    private final String raceId;
    private long hostId;
    private int currentRound;
    private int totalRounds;
    private final Set<Long> players = new HashSet<>();

    public Race(String raceId) {
        this.raceId = raceId;
    }

    public String getRaceId() {
        return raceId;
    }

    public long getHostId() {
        return hostId;
    }

    public void setHostId(long hostId) {
        this.hostId = hostId;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(int totalRounds) {
        this.totalRounds = totalRounds;
    }

    public Set<Long> getPlayers() {
        return players;
    }

    public void addPlayer(long userId) {
        players.add(userId);
    }
}
