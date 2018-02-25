package YuriReznik.Server.persistancy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import YuriReznik.Message.Bid;
import YuriReznik.Message.Person;
import YuriReznik.Server.Model;
import YuriReznik.Server.exceptions.ActionFailedException;

public enum BidDao {
    INSTANCE;

    private static DataBaseAccess dba = DataBaseAccess.INSTANCE;
    private PersonDao personDao = PersonDao.INSTANCE;
    private Person systemUser;
    private final static double SYSTEM_FEE_PERC = 0.05;


    private BidDao() {
        if (systemUser == null) {
            systemUser = personDao.getSystemUser();
        }
    }


    /**
     * Creates and executes place bid transaction
     * @param bid - Bid for transaction
     * @return Bid object if transaction succeeded, <b>null</b> otherwise
     */
    public void addBid(Bid bid) {
        // Create and execute bid transaction
        try (Connection connection = dba.createCustomConnection()) {

            double systemFee = bid.getAmount() * SYSTEM_FEE_PERC;
            double bidAfterTax = bid.getAmount() - systemFee;

            // Disable autocommit to enable custom transaction
            connection.setAutoCommit(false);

            // Add User's bid
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO Bid(raceId, personId, carId, bidAmount,afterTax) VALUES (?,?,?,?,?)");
            stmt.setLong(1, bid.getRaceId());
            stmt.setLong(2, bid.getPersonId());
            stmt.setLong(3, bid.getCarId());
            stmt.setDouble(4, bid.getAmount());
            stmt.setDouble(5, bidAfterTax);
            stmt.execute();
            // Add System bid (system revenue)
            stmt = connection.prepareStatement("INSERT INTO Bid(raceId, personId, carId, bidAmount, afterTax) VALUES (?,?,?,?,?)");
            stmt.setLong(1, bid.getRaceId());
            stmt.setLong(2, systemUser.getId());
            stmt.setLong(3, bid.getCarId());
            stmt.setDouble(4, systemFee);
            stmt.setDouble(5, 0);
            stmt.execute();
            // Withdraw funds from Person
            stmt = connection.prepareStatement("UPDATE Person SET balance = balance - ? WHERE id=?");
            stmt.setDouble(1, bid.getAmount());
            stmt.setLong(2, bid.getPersonId());
            stmt.executeUpdate();
            // Add funds to System acount
            stmt = connection.prepareStatement("UPDATE Person SET balance = balance + ? WHERE id=?");
            stmt.setDouble(1, systemFee);
            stmt.setLong(2, systemUser.getId());
            stmt.executeUpdate();
            connection.commit();

            bid.setAmountAfterTax(bidAfterTax);
        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
    }

    /**
     * Update given bid in DB
     * @param bid
     */
    public void updateBid(Bid bid) {
        try {
            PreparedStatement stmt = dba.getPreparedStatement("UPDATE Bid SET raceId=?,personId=?,carId=?,bidAmount=?,afterTax=?,profit=? WHERE id=?");
            stmt.setLong(1, bid.getRaceId());
            stmt.setLong(2, bid.getPersonId());
            stmt.setLong(3, bid.getCarId());
            stmt.setDouble(4, bid.getAmount());
            stmt.setDouble(5, bid.getAmountAfterTax());
            stmt.setDouble(6, bid.getProfit());
            stmt.setLong(7, bid.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
    }

    /**
     * Get given person's bids
     * @param personId
     * @return
     */
    public List<Bid> getBidsByPersonId(long personId) {
        List<Bid> bids = new ArrayList<>();
        try {
            PreparedStatement stmt = dba.getPreparedStatement("SELECT * FROM Bid WHERE personId=?");
            stmt.setLong(1, personId);
            bids.addAll(getBidsByPreparedStatement(stmt));
        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
        return bids;
    }

    /**
     * Get Bids by raceId
     * @param raceId
     * @return
     */
    public List<Bid> getBidsByRaceId(long raceId) {
        List<Bid> bids = new ArrayList<>();
        try {
            PreparedStatement stmt = dba.getPreparedStatement("SELECT * FROM Bid WHERE raceId=? AND personId<>1");
            stmt.setLong(1, raceId);
            bids.addAll(getBidsByPreparedStatement(stmt));
        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
        return bids;
    }

    /**
     * Returns bids by Person Id and raceId
     * @param personId
     * @param raceId
     * @return
     */
    public List<Bid> getBidsByPersonIdAndRaceId(long personId, long raceId) {
        List<Bid> bids = new ArrayList<>();
        try {
            PreparedStatement stmt = dba.getPreparedStatement("SELECT * FROM Bid WHERE personId=? AND raceId=?");
            stmt.setLong(1, personId);
            stmt.setLong(2, raceId);
            bids.addAll(getBidsByPreparedStatement(stmt));
        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
        return bids;
    }

    /**
     * Get Races eligible to start
     * @return a Map with raceId -> totalMoney lookup
     */
    public Map<Long, Long> getRacesEligibleToStart() {
        Map<Long, Long> racesToTotalRaceMoney = new HashMap<>();
        try {
            PreparedStatement stmt = dba.getPreparedStatement("SELECT raceId, totalMoney FROM (\n" +
                    "SELECT Bid.raceId, IFNULL(COUNT(DISTINCT(Bid.carId)), 0) AS carCount, SUM(Bid.bidAmount) AS totalMoney\n" +
                    "\tFROM Bid, Race\n" +
                    "    WHERE Bid.personId <> 1 AND Bid.raceId = Race.id AND ISNULL(Race.raceStartDate)\n" +
                    "    GROUP BY Bid.raceId\n" +
                    ") AS Stats WHERE carCount >= ? ORDER BY totalMoney DESC");
            stmt.setLong(1, Model.MIN_CARS_IN_RACE);
            ResultSet rSet = stmt.executeQuery();
            while(rSet.next()) {
                racesToTotalRaceMoney.put(rSet.getLong(1), rSet.getLong(2));
            }
        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
        return racesToTotalRaceMoney;
    }

    /**
     * Returns bds b carId and raceId
     * @param carId
     * @param raceId
     * @return
     */
    public List<Bid> getBidsByCarIdAndRaceId(long carId, long raceId) {
        List<Bid> bids = new ArrayList<>();
        try {
            PreparedStatement stmt = dba.getPreparedStatement("SELECT * FROM Bid WHERE carId=? AND raceId=? AND personId<>?");
            stmt.setLong(1, carId);
            stmt.setLong(2, raceId);
            stmt.setLong(3, systemUser.getId());
            bids.addAll(getBidsByPreparedStatement(stmt));
        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
        return bids;
    }

    /**
     * Validates Prepared statement and retries list of Bid objects
     * @param stmt
     * @return
     */
    private List<Bid> getBidsByPreparedStatement(PreparedStatement stmt) {
        List<Bid> bids = new ArrayList<>();
        try {
            ResultSet rSet = stmt.executeQuery();
            while (rSet.next()) {
                Bid bid = getBidFromResultSet(rSet);
                if (bid != null) {
                    bids.add(bid);
                }
            }
        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
        return bids;
    }


    /**
     * Calculates total money bets for given race
     * @param raceId
     * @return
     */
    public double getTotalMoneyByRaceId(long raceId) {
        double totalMoney = 0;
        try {
            PreparedStatement stmt = dba.getPreparedStatement("SELECT IFNULL(SUM(afterTax),0) FROM Bid WHERE personId<>1 AND raceId=?");
            stmt.setLong(1, raceId);
            ResultSet rSet = stmt.executeQuery();
            if (rSet.next()) {
                totalMoney = rSet.getLong(1);
            }
        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
        return totalMoney;
    }

    /**
     * Calculates total revenue for given person
     * @param personId personId to check for
     * @return a Map holding raceId -> revenue
     */
    public Map<Long, Double> getTotalMoneyForRaceIdByPerson(long personId) {
        Map<Long, Double> raceToTotalMoney = new HashMap<>();
        try {
            PreparedStatement stmt = dba.getPreparedStatement("SELECT raceId, SUM(profit)-SUM(bidAmount) FROM Bid WHERE personId=? AND personId<>? GROUP BY raceid");
            stmt.setLong(1, personId);
            stmt.setLong(2, systemUser.getId());
            ResultSet rSet = stmt.executeQuery();
            while (rSet.next()) {
                long raceId = rSet.getLong(1);
                double totalMoney = raceToTotalMoney.computeIfAbsent(raceId, k -> 0.0d);
                totalMoney += rSet.getDouble(2);
                raceToTotalMoney.put(raceId, totalMoney);
            }
        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
        return raceToTotalMoney;
    }


    /**
     * Returns persons that place bets
     * @param raceId race to check from
     * @param carId car to check from
     * @return
     */
    public List<Person> getPersonsByRaceIdAndCarId(long raceId, long carId) {
        List<Person> result = new ArrayList<>();
        try {
            PreparedStatement stmt = dba.getPreparedStatement("SELECT Person.id, Person.name, Person.password, Person.balance FROM Bid, Person WHERE Person.id = Bid.personId AND Bid.raceId = ? AND Bid.carId = ?");
            stmt.setLong(1, raceId);
            stmt.setLong(2, carId);
            ResultSet rSet = stmt.executeQuery();
            while(rSet.next()) {
                Person person = personDao.getPersonFromResultSet(rSet);
                if (person != null) {
                    result.add(person);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * returns Bid object from given result set
     * @param rSet
     * @return returns Bid object from given result set
     * @throws SQLException
     */
    public Bid getBidFromResultSet(ResultSet rSet) throws SQLException {
        Bid b = new Bid();
        b.setId(rSet.getLong(1));
        b.setRaceId(rSet.getLong(2));
        b.setPersonId(rSet.getLong(3));
        b.setCarId(rSet.getLong(4));
        b.setAmount(rSet.getDouble(5));
        b.setAmountAfterTax(rSet.getDouble(6));
        b.setProfit(rSet.getDouble(7));
        return b;
    }

}
