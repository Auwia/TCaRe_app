package it.app.dmd_stock_app;

import java.util.List;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class LetturaListViewAdapter extends ArrayAdapter<Lettura> {

	private int resLayout;
	private Context context;
	private List<Lettura> items;

	public View row;

	public LetturaListViewAdapter(Context context, int layoutResourceId,
			List<Lettura> items) {
		super(context, layoutResourceId, items);

		resLayout = R.layout.list_view_custom;
		this.context = context;
		this.items = items;
	}

	private void setCodiceTextChangeListener(final LetturaHolder holder) {
		holder.codice.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				holder.lettura.getEtichetta().setCodiceNsArticolo(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void setDescrizioneTextChangeListener(final LetturaHolder holder) {
		holder.descrizione.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				holder.lettura.getEtichetta().setDescrizione(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void setQuantitaTextChangeListener(final LetturaHolder holder) {
		holder.quantita.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				String tot = s.toString();
				if (tot.contains(",")) {
					tot = tot.replace(",", ".");
				}
				holder.lettura.setQuantitaPrelevata(Double.parseDouble(tot));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		LetturaHolder holder = null;

		if (row == null) {
			LayoutInflater ll = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = ll.inflate(resLayout, parent, false);
		}

		holder = new LetturaHolder();
		holder.lettura = items.get(position);

		holder.elimina = (Button) row.findViewById(R.id.elimina);
		holder.elimina.setTag(holder.lettura);

		holder.modifica = (Button) row.findViewById(R.id.modifica);
		holder.modifica.setTag(holder.lettura);

		holder.codice = (TextView) (row != null ? row.findViewById(R.id.codice)
				: null);
		holder.descrizione = (TextView) (row != null ? row
				.findViewById(R.id.descrizione) : null);
		holder.quantita = (TextView) row.findViewById(R.id.quantita);

		setCodiceTextChangeListener(holder);
		setDescrizioneTextChangeListener(holder);
		setQuantitaTextChangeListener(holder);

		row.setTag(holder);

		holder.codice.setText(holder.lettura.getEtichetta()
				.getCodiceNsArticolo());
		holder.descrizione.setText(String.valueOf(holder.lettura.getEtichetta()
				.getDescrizione()));
		holder.quantita.setText(String.valueOf(holder.lettura
				.getQuantitaPrelevata()));

		return row;
	}

	public static class LetturaHolder {
		Lettura lettura;
		TextView codice;
		TextView descrizione;
		TextView quantita;

		Button elimina, modifica;
	}
}
