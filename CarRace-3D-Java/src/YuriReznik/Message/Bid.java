package YuriReznik.Message;

import java.io.Serializable;

public class Bid implements Serializable {
	
	private static final long serialVersionUID = -4697814117671377764L;
	private long id;
	private long raceId;
	private long personId;
	private long carId;
	private double amount;
	private double amountAfterTax;
	private double profit = 0;
	
	/**
	 * 
	 */
	public Bid() {
		
	}
	
	/**
	 * @param id
	 * @param raceId
	 * @param personId
	 * @param carId
	 * @param amount
	 */
	public Bid(long id, long raceId, long personId, long carId, double amount) {
		this.id = id;
		this.raceId = raceId;
		this.personId = personId;
		this.carId = carId;
		this.amount = amount;
	}
	
	/**
	 * @param raceId
	 * @param personId
	 * @param carId
	 * @param amount
	 */
	public Bid(long raceId, long personId, long carId, double amount) {
		
		this.raceId = raceId;
		this.personId = personId;
		this.carId = carId;
		this.amount = amount;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getRaceId() {
		return raceId;
	}
	public void setRaceId(long raceId) {
		this.raceId = raceId;
	}
	public long getPersonId() {
		return personId;
	}
	public void setPersonId(long personId) {
		this.personId = personId;
	}
	public long getCarId() {
		return carId;
	}
	public void setCarId(long carId) {
		this.carId = carId;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Bid bid = (Bid) o;

		if (getId() != bid.getId()) return false;
		if (getRaceId() != bid.getRaceId()) return false;
		if (getPersonId() != bid.getPersonId()) return false;
		if (getCarId() != bid.getCarId()) return false;
		return Double.compare(bid.getAmount(), getAmount()) == 0;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = (int) (getId() ^ (getId() >>> 32));
		result = 31 * result + (int) (getRaceId() ^ (getRaceId() >>> 32));
		result = 31 * result + (int) (getPersonId() ^ (getPersonId() >>> 32));
		result = 31 * result + (int) (getCarId() ^ (getCarId() >>> 32));
		temp = Double.doubleToLongBits(getAmount());
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	public double getAmountAfterTax() {
		return amountAfterTax;
	}

	public void setAmountAfterTax(double amountAfterTax) {
		this.amountAfterTax = amountAfterTax;
	}

	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Bid{");
		sb.append("id=").append(id);
		sb.append(", raceId=").append(raceId);
		sb.append(", personId=").append(personId);
		sb.append(", carId=").append(carId);
		sb.append(", amount=").append(amount);
		sb.append(", amountAfterTax=").append(amountAfterTax);
		sb.append(", profit=").append(profit);
		sb.append('}');
		return sb.toString();
	}
}
