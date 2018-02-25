package YuriReznik.Message;

import java.io.Serializable;
import java.util.List;

/**
 * YuriReznik.Message transferred between server and client
 */
public class Envelope implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MessageType messageType;
	private Answer answer = Answer.YES;
	private Bid bid;
	private List<Bid> personBids;
	private List<Race> races;
	private List<Car> cars;
	private Person person;
	private Winner winner;

	public Envelope(){
	}

	public Answer getAnswer() {
		return answer;
	}

	public void setAnswer(Answer answer) {
		this.answer = answer;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public Envelope setMessageType(MessageType messageType) {
		this.messageType = messageType;
		return this;
	}

	public Bid getBid() {
		return bid;
	}

	public Envelope setBid(Bid bid) {
		this.bid = bid;
		return this;
	}

	public List<Bid> getPersonBids() {
		return personBids;
	}

	public Envelope setPersonBids(List<Bid> personBids) {
		this.personBids = personBids;
		return this;
	}

	public List<Race> getRaces() {
		return races;
	}

	public Envelope setRaces(List<Race> races) {
		this.races = races;
		return this;
	}

	public Person getPerson() {
		return person;
	}

	public Envelope setPerson(Person person) {
		this.person = person;
		return this;
	}

	public Winner getWinner() {
		return winner;
	}

	public Envelope setWinner(Winner winner) {
		this.winner = winner;
		return this;
	}

	public List<Car> getCars() {
		return cars;
	}

	public Envelope setCars(List<Car> cars) {
		this.cars = cars;
		return this;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Envelope{");
		sb.append("messageType=").append(messageType);
		sb.append(", answer=").append(answer);
		if (bid != null)
			sb.append(", bid=").append(bid);
		if (personBids != null)
			sb.append(", personBids=").append(personBids);
		if (races != null)
			sb.append(", races=").append(races);
		if (person != null)
			sb.append(", person=").append(person);
		if (winner != null)
			sb.append(", winner=").append(winner);
		sb.append('}');
		return sb.toString();
	}

}
