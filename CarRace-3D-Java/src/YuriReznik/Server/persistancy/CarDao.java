package YuriReznik.Server.persistancy;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import YuriReznik.Message.Car;
import YuriReznik.Message.CarManufacture;
import YuriReznik.Message.CarShape;
import YuriReznik.Message.CarSize;
import YuriReznik.Server.exceptions.ActionFailedException;

public enum CarDao {
    INSTANCE;
    private CarDao() {}

    private static DataBaseAccess dba = DataBaseAccess.INSTANCE;

    public void addCar(Car car) {
        try {
            PreparedStatement stmt = dba.getPreparedStatement("INSERT INTO Car(carManufacture, color, carSize, carShape) values (?, ?, ?, ?);");
            stmt.setString(1, car.getCarManufacture().getManufactureName());
            stmt.setString(2, car.getColor().toString());
            stmt.setString(3, car.getCarSize().getSizeName());
            stmt.setString(4, car.getCarShape().getShapeName());
            stmt.execute();
        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
    }

    public List<Car> getCars() {
        List<Car> cars = new ArrayList<>();
        try {
            ResultSet rSet  = dba.executeQuery("SELECT * FROM Car");
            while(rSet.next()) {
                cars.add(getCarFromResultSet(rSet, 1));
            }
        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
        return cars;
    }

    public Car getCarById(long carId) {
        try {
            PreparedStatement stmt = dba.getPreparedStatement("SELECT * FROM Car WHERE id=?");
            stmt.setLong(1, carId);
            ResultSet rSet = stmt.executeQuery();
            if (rSet.next()) {
                return getCarFromResultSet(rSet, 1);
            }
        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
        return null;
    }

    public List<Car> getAllCars() {
        List<Car> result = new ArrayList<>();
        try {
            ResultSet rSet = dba.executeQuery("SELECT * FROM Car");
            while (rSet.next()) {
                Car car = getCarFromResultSet(rSet, 1);
                if (car != null) {
                    result.add(car);
                }
            }
        } catch (SQLException e) {
            throw new ActionFailedException(e.getMessage());
        }
        return result;
    }

    public Car getCarFromResultSet(ResultSet rSet, int startIndex) throws SQLException {
        Car car = new Car();
        car.setId(rSet.getLong(startIndex++));
        car.setCarManufacture(CarManufacture.fromString(rSet.getString(startIndex++)));
        car.setColor(new Color(Integer.parseInt(rSet.getString(startIndex++),16)));
        car.setCarSize(CarSize.fromString(rSet.getString(startIndex++)));
        car.setCarShape(CarShape.fromString(rSet.getString(startIndex)));
        return car;
    }

}

