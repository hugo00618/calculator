package info.hugoyu.calculator.android.thread;

import android.os.Looper;

public class WbtBooterThread extends Thread {

	private BlurTask bt;

	public WbtBooterThread(BlurTask bt) {
		this.bt = bt;
	}

	@Override
	public void run() {
		while (Looper.getMainLooper().getThread() == Thread.currentThread()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		bt.launch();
	}

}
