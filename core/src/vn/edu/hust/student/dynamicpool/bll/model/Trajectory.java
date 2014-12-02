package vn.edu.hust.student.dynamicpool.bll.model;

public abstract class Trajectory {
	private double timeState = 0f;
	private Point location = new Point();

	public Trajectory() {

	}

	public Trajectory(Point localtion) {
		this.setLocation(localtion);
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location.setLocation(location);
	}

	public void setLocation(float x, float y) {
		location.setLocation(x, y);
	}

	public double getTimeState() {
		return timeState;
	}

	public void increaseTimeState(double d) {
		this.timeState += d;
	}

	public void setTimeState(double timeState) {
		this.timeState = timeState;
	}

	public boolean equals(Trajectory t) {
		return this.timeState == t.timeState
				&& this.location.equals(t.location);
	}

	public abstract void changeDirection(EDirection hitTo);

	public abstract ETrajectoryType getTrajectoryType();

	public abstract Point updateLocation(float deltaTime);

	public abstract EDirection getHorizontalDirection();

	public abstract Trajectory clone();
}
