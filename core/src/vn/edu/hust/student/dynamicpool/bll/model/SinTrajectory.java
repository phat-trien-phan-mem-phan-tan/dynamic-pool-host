package vn.edu.hust.student.dynamicpool.bll.model;

import flexjson.JSON;


/**
 * x = x+dx
 * y = y0 + a * sin(t)
 * @author Quang Tien
 *
 */
public class SinTrajectory extends Trajectory {
	

	private float a = 100;
	private float dx = 15;
	private float y0 = 0;
	
	public SinTrajectory() {
		super();
	}

	public SinTrajectory(Point location) {
		super(location);
		y0 = location.getY();
	}
	
	@flexjson.JSON(include=false)
	public void init(float a, float dx, float y0) {
		this.a = a;
		this.dx = dx;
		this.y0 = y0;
	}

	public float getA() {
		return a;
	}

	public void setA(float a) {
		this.a = a;
	}

	public float getDx() {
		return dx;
	}

	public void setDx(float dx) {
		this.dx = dx;
	}

	public float getY0() {
		return y0;
	}

	public void setY0(float y0) {
		this.y0 = y0;
	}

	@JSON(include=false)
	public ETrajectoryType getTrajectoryType() {
		return ETrajectoryType.SIN;
	}

	public Point updateLocation(float deltaTime) {
		increaseTimeState(deltaTime);
		float x = getLocation().getX() + dx * deltaTime;
		float y = (float) (y0 + a * Math.sin(getTimeState()));
		setLocation(x, y);
		return getLocation();
	}

	public void changeDirection(EDirection direction) {
		switch (direction) {
		case LEFT:
		case RIGHT:
			dx = -dx;
			y0 = 2 * getLocation().getY() - y0;
			setTimeState(-getTimeState());
			break;
		case TOP:
		case BOTTOM:
		default:
			increaseTimeState(Math.PI);
			break;
		}
	}

	@JSON(include=false)
	public EDirection getHorizontalDirection() {
		return dx < 0 ? EDirection.LEFT : EDirection.RIGHT;
	}

	public Trajectory clone() {
		SinTrajectory t = new SinTrajectory(this.getLocation());
		t.init(a, dx, y0);
		t.setTimeState(getTimeState());
		return t;
	}
	
	public boolean equals(SinTrajectory t) {
		return this.a == t.a && this.dx == t.dx && this.y0 == y0 && super.equals(t);
	}
}
