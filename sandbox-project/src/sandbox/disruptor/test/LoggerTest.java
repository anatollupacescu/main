package sandbox.disruptor.test;

import sandbox.disruptor.BackgroundLogger;

public class LoggerTest {

	public static void main(String[] args) {
		
		final BackgroundLogger bl1 = new BackgroundLogger();
		
		for(int i = 0; i < 5; i++) {
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					while(true){
						try {
							bl1.log(Thread.currentThread().getName());
						} catch (Exception e) {
							bl1.stop();
							return;
						}
					}
				}
			});
			t.start();
		}
	}
}
