package it.app.tcare;

import android.app.Activity;
import android.widget.Button;

public class Utility {

	private Activity activity;

	public Utility(Activity activity) {
		this.activity = activity;

	}

	public void setC_FREQ440(String returnCode) {
		Button frequency = (Button) activity.findViewById(R.id.frequency);
		if (returnCode == "00")
			frequency.setText("440 KHz");
		else
			frequency.setText("ERROR");
	}

	public void setC_FREQ500(String returnCode) {
		Button frequency = (Button) activity.findViewById(R.id.frequency);
		if (returnCode == "01")
			frequency.setText("500 KHz");
		else
			frequency.setText("ERROR");
	}

	public void setC_FREQ720(String returnCode) {
		Button frequency = (Button) activity.findViewById(R.id.frequency);
		if (returnCode == "02")
			frequency.setText("720 KHz");
		else
			frequency.setText("ERROR");
	}

	public void setC_FREQ1000(String returnCode) {
		Button frequency = (Button) activity.findViewById(R.id.frequency);
		if (returnCode == "03")
			frequency.setText("1000 KHz");
		else
			frequency.setText("ERROR");
	}

	public void setC_START(String returnCode) {

		if (returnCode == "01") {

		}
	}

	public void setC_STOP(String returnCode) {

		if (returnCode == "00") {

		}
	}

	public void setC_PAUSE(String returnCode) {

		if (returnCode == "02") {

		}
	}
}
