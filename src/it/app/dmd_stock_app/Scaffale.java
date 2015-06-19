package it.app.dmd_stock_app;

import java.io.Serializable;

public class Scaffale implements Serializable {

	static final long serialVersionUID = 0;

	private String id_scaffale, descrizione;
	private Etichetta etichetta;

	public Scaffale(String id_scaffale, String descrizione, Etichetta etichetta) {
		this.setIdScaffale(id_scaffale);
		this.setDescrizione(descrizione);
		this.setEtichetta(etichetta);
	}

	public String getIdScaffale() {
		return id_scaffale;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public Etichetta getEtichetta() {
		return etichetta;
	}

	public void setIdScaffale(String id_scaffale) {
		this.id_scaffale = id_scaffale;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public void setEtichetta(Etichetta etichetta) {
		this.etichetta = etichetta;
	}

}
