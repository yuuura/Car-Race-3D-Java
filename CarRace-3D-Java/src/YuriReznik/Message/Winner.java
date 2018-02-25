package YuriReznik.Message;

import java.io.Serializable;

/**
 * Object that contains winner information
 */
public class Winner implements Serializable {

    private static final long serialVersionUID = -4206307927310759234L;
    private long id;
    private long raceId;
    private long winnerCarId;
    private long distance;

    public Winner() {

    }

    public Winner(long id, long raceId, int winnerCarId) {
        this.id = id;
        this.raceId = raceId;
        this.winnerCarId = winnerCarId;
    }

    public long getId() {
        return id;
    }

    public Winner setId(long id) {
        this.id = id;
        return this;
    }

    public long getRaceId() {
        return raceId;
    }

    public Winner setRaceId(long raceId) {
        this.raceId = raceId;
        return this;
    }

    public long getWinnerCarId() {
        return winnerCarId;
    }

    public Winner setWinnerCarId(long winnerCarId) {
        this.winnerCarId = winnerCarId;
        return this;
    }


    public long getDistance() {
        return distance;
    }

    public Winner setDistance(long distance) {
        this.distance = distance;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Winner winner = (Winner) o;

        if (getId() != winner.getId()) return false;
        if (getRaceId() != winner.getRaceId()) return false;
        return getWinnerCarId() == winner.getWinnerCarId();
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (int) (getRaceId() ^ (getRaceId() >>> 32));
        result = 31 * result + (int) (getWinnerCarId() ^ (getWinnerCarId() >>> 32));
        return result;
    }
}
