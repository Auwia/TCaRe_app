package it.app.tcare;

import android.app.Activity;
import android.widget.Button;
import android.widget.ImageButton;

public class Utility {

	private Activity activity;
	private ImageButton play, stop, pause;

	public Utility(Activity activity) {
		this.activity = activity;
		play = (ImageButton) activity.findViewById(R.id.button_play);
		stop = (ImageButton) activity.findViewById(R.id.button_stop);
		pause = (ImageButton) activity.findViewById(R.id.button_pause);

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
			play.setPressed(true);
			stop.setPressed(false);
			pause.setPressed(false);
		}
	}

	public void setC_STOP(String returnCode) {

		if (returnCode == "00") {
			stop.setPressed(true);
			play.setPressed(false);
			pause.setPressed(false);
		}
	}

	public void setC_PAUSE(String returnCode) {

		if (returnCode == "02") {
			pause.setPressed(true);
			play.setPressed(false);
			stop.setPressed(false);
		}
	}
}
