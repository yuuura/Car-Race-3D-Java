package YuriReznik.Server.persistancy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import YuriReznik.Message.Person;
import YuriReznik.Server.exceptions.ActionFailedException;

public enum PersonDao {
	INSTANCE;
	private PersonDao() {}

	private static DataBaseAccess dba = DataBaseAccess.INSTANCE;
	public final static String SYSTEM_USERNAME = "SYSTEM";
	public final static String SYSTEM_PASSWORD = "USER";
	
	/**
	 * Retrieves Person object form a database with given name.
	 * @param person
	 * @return Person object with a given name. If found more than one, returns only first
	 */
	public Person getPersonByNameAndPassword(Person person) {
		try {
			PreparedStatement stmt = dba.getPreparedStatement("SELECT * FROM Person WHERE name=? AND password=?");
			stmt.setString(1, person.getName());
			stmt.setString(2, person.getPassword());
			ResultSet rSet = stmt.executeQuery();
			if (rSet.next()) {
				return getPersonFromResultSet(rSet);
			}
		} catch (SQLException e) {
			throw new ActionFailedException(e.getMessage());
		}
		return null;
	}
	
	/**
	 * Retrieves Person object form a database with given ID.
	 * @param personId
	 * @return Person object with a given ID. If found more than one, returns only first
	 */
	public Person getPersonById(long personId) {
		try {
			PreparedStatement stmt = dba.getPreparedStatement("SELECT * FROM Person WHERE id=?");
			stmt.setLong(1, personId);
			ResultSet rSet = stmt.executeQuery();
			if (rSet.next()) {
				return getPersonFromResultSet(rSet);
			}
		} catch (SQLException e) {
			throw new ActionFailedException(e.getMessage());
		}
		return null;
	}

	/**
	 * Updates DB information stored for given person
	 * @param person - person to update
	 * @return <b>true</b> if update was successful
	 */
	public boolean updatePerson(Person person) {
		try {
			PreparedStatement stmt = dba.getPreparedStatement("UPDATE Person SET name=?, password=?, balance=? WHERE id=?");
			stmt.setString(1, person.getName());
			stmt.setString(2, person.getPassword());
			stmt.setDouble(3, person.getBalance());
			stmt.setLong(4, person.getId());
			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			return false;
		}

	}
	
	/**
	 * Saves new Person into DB
	 * @param person
	 */
	public Person addPerson(Person person) {
		try {
			PreparedStatement stmt = dba.getPreparedStatement("INSERT INTO Person(name, password, balance) values (?, ?, ?)");
			stmt.setString(1, person.getName());
			stmt.setString(2, person.getPassword());
			stmt.setDouble(3, person.getBalance());
			stmt.execute();
			return getPersonByNameAndPassword(person);
		} catch (SQLIntegrityConstraintViolationException e) {
			return null;
		}catch (SQLException e) {
			throw new ActionFailedException(e.getMessage());
		} 
	}

	public List<Person> getAllPersons() {
		List<Person> result = new ArrayList<>();
		try {
			ResultSet rSet = dba.executeQuery("SELECT * FROM Person");
			while (rSet.next()) {
				Person person = getPersonFromResultSet(rSet);
				if (person !=  null) {
					result.add(person);
				}
			}
		} catch (SQLException e) {
			throw new ActionFailedException(e.getMessage());
		}
		return result;
	}

	public Person getPersonFromResultSet(ResultSet rSet) throws SQLException {
		Person p = new Person();
		p.setId(rSet.getLong(1));
		p.setName(rSet.getString(2));
		p.setPassword(rSet.getString(3));
		p.setBalance(rSet.getLong(4));
		return p;
	}

	public Person getSystemUser() {
		return getPersonByNameAndPassword(new Person(SYSTEM_USERNAME, SYSTEM_PASSWORD));
	}

	public boolean isSystemUser(Person person) {
		Person systemUser = getSystemUser();
		return systemUser.equals(person);
	}
}
