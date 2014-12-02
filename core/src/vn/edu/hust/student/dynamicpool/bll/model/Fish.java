package vn.edu.hust.student.dynamicpool.bll.model;

import java.util.UUID;

import com.badlogic.gdx.graphics.g2d.freetype.FreeType.Face;

import flexjson.JSON;

public class Fish {
	private String fishId = UUID.randomUUID().toString();
	private FishType fishType = FishType.FISH1;
	private Trajectory trajectory = new NoneTrajectory();
	private Boundary boundary = new Boundary();
	private FishState fishState = FishState.INSIDE;
	private Pool pool;

	public Fish() {

	}

	public Fish(FishType fishType, Trajectory fishTrajectory,
			Boundary fishBoundary) {
		this.fishType = fishType;
		this.trajectory = fishTrajectory;
		this.boundary = fishBoundary;
	}

	public String getFishId() {
		return fishId;
	}

	public void setFishId(String fishId) {
		this.fishId = fishId;
	}

	public Boundary getBoundary() {
		return boundary;
	}

	public void setBoundary(Boundary boundary) {
		this.boundary = boundary;
	}

	public Trajectory getTrajectory() {
		if (trajectory == null)
			return new NoneTrajectory();
		return trajectory;
	}

	public void setTrajectory(Trajectory trajectory) {
		this.trajectory = trajectory;
	}

	public FishType getFishType() {
		return fishType;
	}

	public void setFishType(FishType fishType) {
		this.fishType = fishType;
	}

	public FishState getFishState() {
		return fishState;
	}

	public void setFishState(FishState fishState) {
		this.fishState = fishState;
	}

	public Pool getPool() {
		return pool;
	}

	public void setPool(Pool pool) {
		this.pool = pool;
	}

	public void updateLocation(float deltaTime) {
		Point updateLocation = getTrajectory().updateLocation(deltaTime);
		boundary.getLocation().setLocation(updateLocation.getX(),
				updateLocation.getY());
	}

	public Fish clone() {
		FishType fishType = this.fishType;
		Trajectory trajectory = this.getTrajectory().clone();
		Boundary boundary = this.getBoundary().clone();
		Fish fish = new Fish(fishType, trajectory, boundary);
		fish.setFishId(fishId);
		fish.setFishState(fishState);
		return fish;
	}

	public boolean equals(Fish f) {
		return this.fishId.equals(f.getFishId())
				&& this.fishType == f.getFishType()
				&& this.fishState == f.getFishState()
				&& this.trajectory.equals(f.getTrajectory())
				&& this.boundary.equals(f.getBoundary());
	}

	@JSON(include=false)
	public void increaseLocation(float dx, float dy) {
		this.boundary.setLocation(boundary.getMinX()+dx, boundary.getMinY()+dy);
		this.trajectory.setLocation(this.boundary.getLocation());
	}

	@JSON(include=false)
	public boolean isInside() {
		if (pool == null) return false;
		return getBoundary().isInside(pool.getBoundary());
	}
}
