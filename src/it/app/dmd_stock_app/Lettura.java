package it.app.dmd_stock_app;

import java.io.Serializable;

public class Lettura implements Serializable {

	static final long serialVersionUID = 0;

	private String data;
	private Etichetta etichetta;
	private double quantita_prelevata;
	private int id_lettura, id_movimento;

	public Lettura(int id_lettura, Etichetta etichetta, String data,
			double quantita_prelevata, int id_movimento) {
		this.setIdLettura(id_lettura);
		this.setEtichetta(etichetta);
		this.setData(data);
		this.setQuantitaPrelevata(quantita_prelevata);
		this.setIdMovimento(id_movimento);
	}

	public int getIdLettura() {
		return id_lettura;
	}

	public Etichetta getEtichetta() {
		return etichetta;
	}

	public String getData() {
		return data;
	}

	public double getQuantitaPrelevata() {
		return quantita_prelevata;
	}

	public int getIdMovimento() {
		return id_movimento;
	}

	public void setIdLettura(int id_lettura) {
		this.id_lettura = id_lettura;
	}

	public void setEtichetta(Etichetta etichetta) {
		this.etichetta = etichetta;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setQuantitaPrelevata(double quantita_prelevata) {
		this.quantita_prelevata = quantita_prelevata;
	}

	public void setIdMovimento(int id_movimento) {
		this.id_movimento = id_movimento;
	}

}
