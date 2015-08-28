package nik.heatsupply.socket.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Tarif implements Serializable {
	private static final long serialVersionUID = 1L;
	private Timestamp dt;
	private int idtarif;
	private double tarif1;
	private double tarif2;

	public Tarif() {
		
	}

	public Timestamp getDt() {
		return dt;
	}

	public void setDt(Timestamp dt) {
		this.dt = dt;
	}

	public int getIdtarif() {
		return idtarif;
	}

	public void setIdtarif(int idtarif) {
		this.idtarif = idtarif;
	}

	public double getTarif1() {
		return tarif1;
	}

	public void setTarif1(double tarif1) {
		this.tarif1 = tarif1;
	}

	public double getTarif2() {
		return tarif2;
	}

	public void setTarif2(double tarif2) {
		this.tarif2 = tarif2;
	}
}