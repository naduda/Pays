package nik.heatsupply.socket.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Data implements Serializable {
	private static final long serialVersionUID = 1L;
	private Timestamp dt;
	private int idTarif;
	private int idUser;
	private double value1;
	private double value2;
	
	public Data() {
		
	}

	public Timestamp getDt() {
		return dt;
	}

	public void setDt(Timestamp dt) {
		this.dt = dt;
	}

	public int getIdTarif() {
		return idTarif;
	}

	public void setIdTarif(int idTarif) {
		this.idTarif = idTarif;
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public double getValue1() {
		return value1;
	}

	public void setValue1(double value1) {
		this.value1 = value1;
	}

	public double getValue2() {
		return value2;
	}

	public void setValue2(double value2) {
		this.value2 = value2;
	}
}
