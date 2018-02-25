package YuriReznik.Message;

import java.io.Serializable;

public class Person implements Serializable {

	private static final long serialVersionUID = -2516627869511414480L;
	private long id;
	private String name;
	private String password;
	private double balance;
	
	
	
	/**
	 * Default constructor of person object
	 */
	public Person() {
		super();
	}

	/**
	 * Person constructor.
	 * @param id
	 * @param name
	 * @param balance
	 */
	public Person(long id, String name, String password, double balance) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.balance = balance;
	}

	/**
	 * Person constructor
	 * @param name
	 * @param password
	 * @param money
	 */
	public Person(String name, String password, double money) {
		this(0L, name, password, money);
	}


	/**
	 * Person constructor
	 * @param name
	 * @param password
	 */
	public Person(String name, String password) {
		this(0L, name, password, 0.0d);
	}
	
	public long getId() {
		return id;
	}
	public Person setId(long id) {
		this.id = id;
		return this;
	}
	public String getName() {
		return name;
	}
	public Person setName(String name) {
		this.name = name;
		return this;
	}
	public double getBalance() {
		return balance;
	}
	public Person setBalance(double balance) {
		this.balance = balance;
		return this;
	}
	public String getPassword() {
		return password;
	}
	public Person setPassword(String password) {
		this.password = password;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Person person = (Person) o;

		if (getName() != null ? !getName().equals(person.getName()) : person.getName() != null) return false;
		return getPassword() != null ? getPassword().equals(person.getPassword()) : person.getPassword() == null;
	}

	@Override
	public int hashCode() {
		int result = getName() != null ? getName().hashCode() : 0;
		result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return String.valueOf(name);
	}
}
