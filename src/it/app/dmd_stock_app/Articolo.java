package it.app.dmd_stock_app;

import java.io.Serializable;

public class Articolo implements Serializable {

	static final long serialVersionUID = 0;

	private String codice, descrizione;
	private boolean confezionata;

	public Articolo(String codice, String descrizione, Boolean confezionata) {
		this.setCodice(codice);
		this.setDescrizione(descrizione);
		this.setConfezionata(confezionata);
	}

	public String getCodice() {
		return codice;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public boolean getConfezionata() {
		return confezionata;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public void setConfezionata(boolean confezionata) {
		this.confezionata = confezionata;
	}

}
