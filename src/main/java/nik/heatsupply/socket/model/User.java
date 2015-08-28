package nik.heatsupply.socket.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	private int idUser;
	private String userName;
	private String address;
	private int owneraccount1;
	private int owneraccount2;
	private String login;
	private String password;
	private String email;
	private Timestamp lastTime;

	public User() {
		
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getOwneraccount1() {
		return owneraccount1;
	}

	public void setOwneraccount1(int owneraccount1) {
		this.owneraccount1 = owneraccount1;
	}

	public int getOwneraccount2() {
		return owneraccount2;
	}

	public void setOwneraccount2(int owneraccount2) {
		this.owneraccount2 = owneraccount2;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Timestamp getLastTime() {
		return lastTime;
	}

	public void setLastTime(Timestamp lastTime) {
		this.lastTime = lastTime;
	}
}