package system;

import java.util.Calendar;

import utils.FileLoader;

public class UpdateService extends Thread {

	private boolean	start;
	private Runnable runnable;
	private IUpdateServiceDelegate delegate;

	public UpdateService(Runnable runnable, IUpdateServiceDelegate delegate) {
		this.delegate = delegate;
		setRunnable(runnable);
		getRunnable().run();//run the parsing action
		setStart(true);
		start();
	}

	public void run() {
		while (isStart()) {
			checkTime();
			try {
				long timeToSleep = 3600000; //1 hour
				timeToSleep -= Calendar.getInstance().get(Calendar.MINUTE) * 60;
				Thread.sleep(timeToSleep); // sleep 1 hour - count of current minute
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void checkTime() {
		int hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if (hours >= 0 && hours < 6) {
			FileLoader.startLoading(delegate.getResources());
		}
	}

	public boolean isStart() {
		return start;
	}

	public void setStart(boolean start) {
		this.start = start;
	}

	public Runnable getRunnable() {
		return runnable;
	}

	public void setRunnable(Runnable runnable) {
		this.runnable = runnable;
	}

}
