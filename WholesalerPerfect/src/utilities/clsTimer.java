package utilities;

import javax.swing.JLabel;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class clsTimer
{
	public clsTimer(JLabel sLabel, int sSeconds)
	{
		timer = new Timer();
		timer.scheduleAtFixedRate(new clsTime(sLabel), 0, sSeconds * 1000);
	}
	
	public class clsTime extends TimerTask 
	{
		JLabel sLabel    = new JLabel();
		boolean visible  = false;

		clsTime(JLabel sLabel){this.sLabel = sLabel; }
		
		public void run() 
		{
			cal  = Calendar.getInstance();
			date = cal.getTime();
			dateFormatter = DateFormat.getTimeInstance();
			sLabel.setText("Today is: " + DateFormat.getDateInstance().format(date) + " [ " + dateFormatter.format(date) + " ] ");
		}
	}

	//<!-- DECLARE_VARIABLES
	Date date;
	Timer timer;
	Calendar cal;
	DateFormat dateFormatter;
	//-->
}