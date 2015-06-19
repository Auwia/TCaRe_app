package it.app.dmd_stock_app;

import java.io.Serializable;

public class Magazziniere implements Serializable {

	static final long serialVersionUID = 0;

	private String nome, cognome;
	private int id_magazziniere;

	public Magazziniere(int id_magazziniere, String nome, String cognome) {
		this.setNome(nome);
		this.setCognome(cognome);
		this.setIdMagazziniere(id_magazziniere);
	}

	public String getNome() {
		return nome;
	}

	public String getCognome() {
		return cognome;
	}

	public int getIdMagazziniere() {
		return id_magazziniere;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public void setIdMagazziniere(int id_magazziniere) {
		this.id_magazziniere = id_magazziniere;
	}

}
