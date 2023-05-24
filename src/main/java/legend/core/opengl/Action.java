package legend.core.opengl;

public class Action {
  private final Runnable action;
  private int expectedFps;
  private long nextRunTime;

  public Action(final Runnable action, final int expectedFps) {
    this.action = action;
    this.setExpectedFps(expectedFps);
    this.updateTimer();
  }

  public void setExpectedFps(final int expectedFps) {
    this.expectedFps = expectedFps;
  }

  public int getExpectedFps() {
    return this.expectedFps;
  }

  public void tick() {
    if(this.isReady()) {
      this.run();
    }
  }

  public long nanosUntilNextRun() {
    return this.nextRunTime - System.nanoTime();
  }

  public boolean isReady() {
    return this.nanosUntilNextRun() <= 0;
  }

  private void run() {
    this.updateTimer();
    this.action.run();
  }

  private void updateTimer() {
    this.nextRunTime = System.nanoTime() + 1_000_000_000 / this.expectedFps;
  }
}