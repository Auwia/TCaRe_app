package it.app.dmd_stock_app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MainActivity extends Activity {

	private Button acquisisci;
	private EditText quantita;
	private String contents, format;
	private LetturaListViewAdapter adapter;
	private Lettura lettura;
	private Etichetta etichetta;

	public void cancella_record(View v) {
		Lettura itemToRemove = (Lettura) v.getTag();
		adapter.remove(itemToRemove);
	}

	public void modifica_record(View v) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				MainActivity.this);

		alertDialog.setTitle("Acquisisci QR Code");
		alertDialog.setMessage("Inserire la quantità prelevata");

		quantita = new EditText(MainActivity.this);
		quantita.setInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL
				| InputType.TYPE_NUMBER_FLAG_SIGNED);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		quantita.setLayoutParams(lp);

		alertDialog.setView(quantita);
		alertDialog.setPositiveButton("CONFERMA",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						String tot = quantita.getText().toString();
						if (tot.contains(",")) {

							tot = tot.replace(",", ".");
						}

						lettura.setQuantitaPrelevata(Double.valueOf(tot));

						adapter.clear();
						adapter.add(lettura);

					}
				});

		alertDialog.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		adapter = new LetturaListViewAdapter(MainActivity.this,
				R.layout.list_view_custom, new ArrayList<Lettura>());
		ListView letturaListView = (ListView) findViewById(R.id.griglia);
		letturaListView.setAdapter(adapter);

		acquisisci = (Button) this.findViewById(R.id.acquisisci);
		acquisisci.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						MainActivity.this);

				alertDialog.setTitle("Acquisisci QR Code");
				alertDialog.setMessage("Inserire la quantità prelevata");

				Intent intent = new Intent(
						"com.google.zxing.client.android.SCAN");
				intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
				startActivityForResult(intent, 0);

				quantita = new EditText(MainActivity.this);
				quantita.setInputType(InputType.TYPE_CLASS_NUMBER
						| InputType.TYPE_NUMBER_FLAG_DECIMAL
						| InputType.TYPE_NUMBER_FLAG_SIGNED);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
				quantita.setLayoutParams(lp);

				alertDialog.setView(quantita);
				alertDialog.setPositiveButton("CONFERMA",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								String tot = quantita.getText().toString();
								if (tot.contains(",")) {

									tot = tot.replace(",", ".");
								}

								// adapter.add(new Lettura(contents,
								// format,Double.valueOf(tot)));

								Date date = new Date();
								SimpleDateFormat sdf = new SimpleDateFormat(
										"MM/dd/yyyy h:mm:ss a");
								String formattedDate = sdf.format(date);

								Query query = new Query(getApplicationContext());
								String[] result = query.select("MAGAZZINIERE",
										new String[] { "ID_MAGAZZINIERE",
												"NOME", "COGNOME" });

								Magazziniere magazziniere;

								if (result != null) {
									magazziniere = new Magazziniere(Integer
											.parseInt(result[0]), result[1],
											result[2]);
								} else {
									magazziniere = new Magazziniere(2,
											"NOME FAKE", "COGNOME FAKE");
								}

								Movimenti movimenti = new Movimenti(
										formattedDate, 1, magazziniere
												.getIdMagazziniere());

								lettura = new Lettura(1, etichetta,
										formattedDate, Double.valueOf(tot),
										movimenti.getIdMovimento());
								adapter.add(lettura);

							}
						});

				alertDialog.show();

			}
		});

	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {

				contents = intent.getStringExtra("SCAN_RESULT");
				format = intent.getStringExtra("SCAN_RESULT_FORMAT");

				String[] value_split = contents.split("\\|");

				etichetta = new Etichetta(value_split[0], value_split[1],
						value_split[2], value_split[3], value_split[4],
						value_split[5], value_split[6]);

			}
		}
	}
}
