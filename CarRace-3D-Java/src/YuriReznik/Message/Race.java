package YuriReznik.Message;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class Race implements Serializable {

	private static final long serialVersionUID = -1029520880801912520L;
	private long id;
	private Timestamp createDate;
	private Timestamp startDate;
	private List<Car> cars;
	
	/**
	 * 
	 */
	public Race() {
		
	}
	/**
	 * @param id
	 * @param createDate
	 * @param cars
	 */
	public Race(long id, Timestamp createDate, List<Car> cars) {
		this.id = id;
		this.createDate = createDate;
		this.cars = cars;
	}
	public List<Car> getCars() {
		return cars;
	}
	public Race setCars(List<Car> cars) {
		this.cars = cars;
		return this;
	}
	public long getId() {
		return id;
	}
	public Race setId(long id) {
		this.id = id;
		return this;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public Race setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
		return this;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public Race setStartDate(Timestamp startDate) {
		this.startDate = startDate;
		return this;
	}

	public boolean isStarted() {
		return getStartDate() != null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Race race = (Race) o;

		if (id != race.id) return false;
		if (createDate != null ? !createDate.equals(race.createDate) : race.createDate != null) return false;
		if (startDate != null ? !startDate.equals(race.startDate) : race.startDate != null) return false;
		return cars != null ? cars.equals(race.cars) : race.cars == null;
	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
		result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
		result = 31 * result + (cars != null ? cars.hashCode() : 0);
		return result;
	}
}
