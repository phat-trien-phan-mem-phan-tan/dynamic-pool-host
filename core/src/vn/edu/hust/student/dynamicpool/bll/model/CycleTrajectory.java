package vn.edu.hust.student.dynamicpool.bll.model;

import java.util.Random;

import vn.edu.hust.student.dynamicpool.utils.AppConst;
import flexjson.JSON;

public class CycleTrajectory extends Trajectory {

	private float centerX;
	private float centerY;

	private float a = 80;
	private float d = 0.2f;
	
	public CycleTrajectory() {
		super();
	}

	public CycleTrajectory(Point location) {
		super(location);
		this.centerX = location.getX();
		int randomInt = Math.abs(new Random().nextInt() % AppConst.height);
		this.centerY = location.getY() + randomInt;
		increaseTimeState(-Math.PI / 2);
		a = Math.abs(this.centerY - location.getY());
	}
	
	@flexjson.JSON(include=false)
	public void init(float centerX, float centerY, float a, float d) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.a = a;
		this.d = d;
	}

	public float getCenterX() {
		return centerX;
	}

	public void setCenterX(float centerX) {
		this.centerX = centerX;
	}

	public float getCenterY() {
		return centerY;
	}

	public void setCenterY(float centerY) {
		this.centerY = centerY;
	}

	public float getA() {
		return a;
	}

	public void setA(float a) {
		this.a = a;
	}

	public float getD() {
		return d;
	}

	public void setD(float d) {
		this.d = d;
	}

	@JSON(include=false)
	public ETrajectoryType getTrajectoryType() {
		return ETrajectoryType.CYCLE;
	}

	public Point updateLocation(float deltaTime) {
		increaseTimeState(deltaTime * d);
		float x = (float) (centerX + a * Math.cos(getTimeState()));
		float y = (float) (centerY + a * Math.sin(getTimeState()));
		setLocation(x, y);
		return getLocation();
	}

	public void changeDirection(EDirection direction) {
		switch (direction) {
		case LEFT:
		case RIGHT:
			centerY = 2 * getLocation().getY() - centerY;
			setTimeState(-getTimeState());
			break;
		case TOP:
		case BOTTOM:
			centerX = 2 * getLocation().getX() - centerX;
			setTimeState(Math.PI - getTimeState());
			break;
		default:
			break;
		}
	}

	@JSON(include=false)
	public EDirection getHorizontalDirection() {
		return Math.sin(getTimeState()) > 0 ? EDirection.LEFT
				: EDirection.RIGHT;
	}

	public Trajectory clone() {
		CycleTrajectory t = new CycleTrajectory(getLocation().clone());
		t.init(centerX, centerY, a, d);
		t.setTimeState(this.getTimeState());
		return t;
	}
	
	public boolean equals(CycleTrajectory t) {
		return this.centerX == t.centerX && this.centerY == t.centerY && this.a == t.a && this.d == d && super.equals(t);
	}
}
