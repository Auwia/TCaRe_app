package it.app.dmd_stock_app;

import java.io.Serializable;

public class Movimenti implements Serializable {

	static final long serialVersionUID = 0;

	private String data;
	private int id_movimento, id_magazziniere;

	public Movimenti(String data, int id_movimento, int id_magazziniere) {
		this.setData(data);
		this.setIdMovimento(id_movimento);
		this.setIdMagazziniere(id_magazziniere);
	}

	public String getData() {
		return data;
	}

	public int getIdMovimento() {
		return id_movimento;
	}

	public int getIdMagazziniere() {
		return id_magazziniere;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setIdMovimento(int id_movimento) {
		this.id_movimento = id_movimento;
	}

	public void setIdMagazziniere(int id_magazziniere) {
		this.id_magazziniere = id_magazziniere;
	}

}
