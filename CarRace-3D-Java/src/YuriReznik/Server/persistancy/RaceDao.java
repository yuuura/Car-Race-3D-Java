package YuriReznik.Server.persistancy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import YuriReznik.Message.Car;
import YuriReznik.Message.Person;
import YuriReznik.Message.Race;
import YuriReznik.Message.Winner;
import YuriReznik.Server.exceptions.ActionFailedException;
import YuriReznik.Server.statistics.datamodel.RaceProperties;

public enum RaceDao {
    INSTANCE;

    private static DataBaseAccess dba = DataBaseAccess.INSTANCE;
    private CarDao carDao = CarDao.INSTANCE;
    private BidDao bidDao = BidDao.INSTANCE;
    private PersonDao personDao = PersonDao.INSTANCE;
    private StatDao statDao = StatDao.INSTANCE;


    /**
     * Create a race in DB
     * @param raceToAdd
     * @return
     */
    public Race createRace(Race raceToAdd) {
        // Create and execute race transaction
        Race race;
        try (Connection connection = dba.createCustomConnection()) {
            long currentMilis = new Date().getTime();

            raceToAdd.setCreateDate(new Timestamp(currentMilis/1000*1000));
            // Prepare bid statement
            PreparedStatement stmt = dba.getPreparedStatement("INSERT INTO Race(raceCreateDate) VALUES (?)");
            stmt.setTimestamp(1, raceToAdd.getCreateDate());
            stmt.execute();
            race = getRaceByCreateTime(raceToAdd.getCreateDate());

            connection.setAutoCommit(false);
            for (Car car : raceToAdd.getCars()) {
                stmt = connection.prepareStatement("INSERT INTO CarRace(carId, raceId) VALUES (?, ?)");
                stmt.setLong(1, car.getId());
                stmt.setLong(2, race.getId());
                stmt.execute();
            }
            connection.commit();
            race = getRaceByCreateTime(raceToAdd.getCreateDate());

        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
        return race;
    }

    /**
     * Create Race object from ResltSet
     * @param rSet
     * @param startIndex
     * @return
     * @throws SQLException
     */
    public Race getRaceFromResultSet(ResultSet rSet, int startIndex) throws SQLException {
        Race race = new Race();
        race.setId(rSet.getLong(startIndex++));
        race.setCreateDate(rSet.getTimestamp(startIndex++));
        race.setStartDate(rSet.getTimestamp(startIndex));
        race.setCars(getCarsByRaceId(race.getId()));
        return race;
    }

    /**
     * GetCars from race
     * @param raceId
     * @return
     */
    public List<Car> getCarsByRaceId(long raceId) {
        List<Car> cars = new ArrayList<>();
        try {
            PreparedStatement stmt = dba.getPreparedStatement("SELECT carId FROM CarRace WHERE raceId=?");
            stmt.setLong(1, raceId);
            ResultSet carRaceRSet = stmt.executeQuery();
            while(carRaceRSet.next()) {
                Car car = carDao.getCarById(carRaceRSet.getLong(1));
                if (car != null) {
                    cars.add(car);
                }
            }
        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
        return cars;
    }

    /**
     * Get race by raceId
     * @param raceId
     * @return
     */
    public Race getRaceById(long raceId) {
        try {
            PreparedStatement stmt = dba.getPreparedStatement("SELECT * FROM Race WHERE id=?");
            stmt.setLong(1, raceId);
            ResultSet rSet = stmt.executeQuery();
            if (rSet.next()) {
                return getRaceFromResultSet(rSet, 1);
            }
        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
        return null;
    }


    /**
     * GetRace by CreateTime
     * @param timestamp
     * @return
     */
    public Race getRaceByCreateTime(Timestamp timestamp) {
        try {
            PreparedStatement stmt = dba.getPreparedStatement("SELECT * FROM Race WHERE raceCreateDate=?");
            stmt.setTimestamp(1, timestamp);
            ResultSet rSet = stmt.executeQuery();
            if (rSet.next()) {
                return getRaceFromResultSet(rSet, 1);
            }
        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
        return null;
    }

    /**
     * update race start time
     * @param race
     */
    public void updateRaceStartDate(Race race) {
        try {
            PreparedStatement stmt = dba.getPreparedStatement("UPDATE Race SET raceStartDate = ? WHERE id = ?");
            stmt.setTimestamp(1, race.getStartDate());
            stmt.setLong(2, race.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
    }

    /**
     * get all races from DB
     * @return
     */
    public List<Race> getAllRaces() {
        List<Race> races = new ArrayList<>();
        try {
            ResultSet rSet = dba.executeQuery("SELECT * FROM Race");
            while (rSet.next()) {
                races.add(getRaceFromResultSet(rSet, 1));
            }
        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
        return races;
    }

    /**
     * Get races and gamblers
     * @param raceId
     * @return
     */
    public List<RaceProperties> getRacesAndGamblers(long raceId) {
        List<RaceProperties> result = new ArrayList<>();

        Winner winner = statDao.getStatByRaceId(raceId);
        List<Car> raceCars = getCarsByRaceId(raceId);

        for (Car car : raceCars) {
            List<Person> persons = bidDao.getPersonsByRaceIdAndCarId(raceId, car.getId());
            String gamblers = persons.stream()
                    .filter(p -> !personDao.isSystemUser(p))
                    .map(Person::getName)
                    .collect(Collectors.joining(", "));
            RaceProperties raceProperties = new RaceProperties(
                    car.toString(),
                    gamblers,
                    winner != null && Objects.equals(winner.getWinnerCarId(), car.getId())
            );
            result.add(raceProperties);
        }

        return result;
    }

}
