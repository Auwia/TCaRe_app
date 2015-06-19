package it.app.dmd_stock_app;

import java.io.Serializable;

public class Etichetta implements Serializable {

	static final long serialVersionUID = 0;

	private String codice_cliente, descrizione, codice_magazzino,
			codice_ns_articolo, codice_cliente_articolo, unita_misura,
			id_scaffale;

	public Etichetta(String codice_cliente, String descrizione,
			String codice_magazzino, String codice_ns_articolo,
			String codice_cliente_articolo, String unita_misura,
			String id_scaffale) {
		this.setCodiceCliente(codice_cliente);
		this.setDescrizione(descrizione);
		this.setCodiceMagazzino(codice_magazzino);
		this.setCodiceNsArticolo(codice_ns_articolo);
		this.setCodiceClienteArticolo(codice_cliente_articolo);
		this.setUnitaMisura(unita_misura);
		this.setIdScaffale(id_scaffale);
	}

	public String getCodiceCliente() {
		return codice_cliente;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public String getCodiceMagazzino() {
		return codice_magazzino;
	}

	public String getCodiceNsArticolo() {
		return codice_ns_articolo;
	}

	public String getCodiceClienteArticolo() {
		return codice_cliente_articolo;
	}

	public String getUnitaMisura() {
		return unita_misura;
	}

	public String getIdScaffale() {
		return id_scaffale;
	}

	public void setCodiceCliente(String codice_cliente) {
		this.codice_cliente = codice_cliente;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public void setCodiceMagazzino(String codice_magazzino) {
		this.codice_magazzino = codice_magazzino;
	}

	public void setCodiceNsArticolo(String codice_ns_articolo) {
		this.codice_ns_articolo = codice_ns_articolo;
	}

	public void setCodiceClienteArticolo(String codice_cliente_articolo) {
		this.codice_cliente_articolo = codice_cliente_articolo;
	}

	public void setUnitaMisura(String unita_misura) {
		this.unita_misura = unita_misura;
	}

	public void setIdScaffale(String id_scaffale) {
		this.id_scaffale = id_scaffale;
	}

}
