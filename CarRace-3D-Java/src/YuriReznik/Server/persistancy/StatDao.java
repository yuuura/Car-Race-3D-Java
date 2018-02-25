package YuriReznik.Server.persistancy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import YuriReznik.Message.Bid;
import YuriReznik.Message.Car;
import YuriReznik.Message.Person;
import YuriReznik.Message.Race;
import YuriReznik.Message.Winner;
import YuriReznik.Server.exceptions.ActionFailedException;

public enum StatDao {
    INSTANCE;
    private StatDao() {}

    private static DataBaseAccess dba = DataBaseAccess.INSTANCE;
    private PersonDao personDao = PersonDao.INSTANCE;
    private BidDao bidDao = BidDao.INSTANCE;
    private CarDao carDao = CarDao.INSTANCE;
    private RaceDao raceDao = RaceDao.INSTANCE;

    /**
     * Add statistical entry as well ass profit field on bet
     * @param stat
     */
    public void addStat(Winner stat) {
        try {
            PreparedStatement stmt = dba.getPreparedStatement("INSERT INTO Stats(raceId, winnerCarId, distance) values (?, ?, ?);");
            stmt.setLong(1, stat.getRaceId());
            stmt.setLong(2, stat.getWinnerCarId());
            stmt.setLong(3, stat.getDistance());
            stmt.execute();

            List<Bid> bids = bidDao.getBidsByCarIdAndRaceId(stat.getWinnerCarId(), stat.getRaceId());
            if (bids != null && !bids.isEmpty()) {
                double totalMoneyOnRace = bidDao.getTotalMoneyByRaceId(stat.getRaceId());
                // Update revenue on all person bids
                for (Bid bid : bids) {
                    double percentFromTotal = bid.getAmountAfterTax() / totalMoneyOnRace;
                    bid.setProfit(totalMoneyOnRace * percentFromTotal);
                    bidDao.updateBid(bid);
                    Person person = personDao.getPersonById(bid.getPersonId());
                    personDao.updatePerson(person.setBalance(person.getBalance() + bid.getProfit()));
                }
            }
        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
    }


    public Winner getStatByRaceId(long raceId) {
        try {
            PreparedStatement stmt = dba.getPreparedStatement("SELECT * FROM Stats WHERE raceId=?");
            stmt.setLong(1, raceId);
            ResultSet rSet = stmt.executeQuery();
            if (rSet.next()) {
                return getStatFromResultSet(rSet);
            }
        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
        return null;
    }

    public List<Person> getWinnerPersonsByRaceId(long raceId) {
        List<Person> persons = new ArrayList<>();
        Winner winnerByRaceId = getStatByRaceId(raceId);
        List<Bid> bidsByCarIdAndRaceId = bidDao.getBidsByCarIdAndRaceId(winnerByRaceId.getWinnerCarId(), winnerByRaceId.getRaceId());
        for (Bid bid : bidsByCarIdAndRaceId) {
            Person person = personDao.getPersonById(bid.getPersonId());
            if(person != null) {
                persons.add(person);
            }
        }
        return persons;
    }

    public Car getWinnerCarByRaceId(long raceId) {
        Winner winnerByRaceId = getStatByRaceId(raceId);
        return carDao.getCarById(winnerByRaceId.getWinnerCarId());
    }

    public List<Race> getWonRacesByCarId(int carId) {
        List<Race> races = new ArrayList<>();
        try {
            PreparedStatement stmt = dba.getPreparedStatement("SELECT * FROM Winner WHERE winnerCarId=?");
            stmt.setInt(1, carId);
            ResultSet rSet = stmt.executeQuery();
            while(rSet.next()) {
                Winner carWinner = getStatFromResultSet(rSet);
                Race race = raceDao.getRaceById(carWinner.getRaceId());
                if (race != null) {
                    races.add(race);
                }
            }
            return races;
        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
    }

    public static Winner getStatFromResultSet(ResultSet rSet) throws SQLException {
        Winner s = new Winner();
        s.setId(rSet.getLong(1));
        s.setRaceId(rSet.getLong(2));
        s.setWinnerCarId(rSet.getLong(3));
        s.setDistance(rSet.getLong(4));
        return s;
    }
}
