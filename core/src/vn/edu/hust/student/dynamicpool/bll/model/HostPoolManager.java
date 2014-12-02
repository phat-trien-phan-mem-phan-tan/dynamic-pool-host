package vn.edu.hust.student.dynamicpool.bll.model;

import java.util.ArrayList;
import java.util.List;

import vn.edu.hust.student.dynamicpool.events.EventDestination;
import vn.edu.hust.student.dynamicpool.events.EventType;

public class HostPoolManager implements PoolManager {
	private List<Pool> pools = new ArrayList<Pool>();
	private List<Fish> fishes = new ArrayList<Fish>();

	public HostPoolManager() {
	}

	public List<Pool> getPools() {
		return pools;
	}

	public void addPool(Pool pool) {
		Point location = getDefaultLocation(pool);
		pool.getBoundary().setLocation(location);
		pools.add(pool);
		calculate();
	}

	private Point getDefaultLocation(Pool poolServer) {
		float x = 0, y = 0;
		for (Pool pool : pools) {
			x = Math.max(pool.getBoundary().getMaxX(), x);
			y = Math.min(pool.getBoundary().getMinY(), y);
		}
		return new Point(x, y);
	}

	public void clear() {
		pools.clear();
	}

	public int size() {
		return this.pools.size();
	}

	public void calculate() {
		FindCommonEdgeFunction.test(pools);
		// FindCommonEdgeFunction.findCommonEdge(this.pools);
	}

	@Override
	public void updateLocationOfFishes(float deltaTime) {
		detectCollision();
		updateLocationOfFishs(deltaTime);
	}

	private void updateLocationOfFishs(float deltaTime) {
		for (Fish fish : fishes) {
			fish.updateLocation(deltaTime);
		}
	}

	private void detectCollision() {
		for (Fish fish : fishes) {
			switch (fish.getFishState()) {
			case INSIDE:
				String neighbour = detectCollisionForInsideFish(fish);
				if (neighbour != null) {
					
				}
				break;
			case OUTSIDE:
			case PASSING:
			case RETURN:
			default:
				if (fish.isInside()) {
					fish.setFishState(FishState.INSIDE);
				}
				break;
			}
		}
	}

	private String detectCollisionForInsideFish(Fish fish) {
		Boundary fishBoundary = fish.getBoundary();
		Pool pool = fish.getPool();
		if (pool == null) return null;
		Boundary poolBoundary = pool.getBoundary();
		if (fishBoundary.getMinX() <= poolBoundary.getMinX()) {
			return hitLeft(pool, fish);
		} else if (fishBoundary.getMaxX() >= poolBoundary.getMaxX()) {
			return hitRight(pool, fish);
		} else if (fishBoundary.getMinY() <= poolBoundary.getMinY()) {
			return hitBottom(pool, fish);
		} else if (fishBoundary.getMaxY() >= poolBoundary.getMaxY()) {
			return hitTop(pool, fish);
		}
		return null;
	}

	private String hitLeft(Pool pool, Fish fish) {
		Segment segment = pool.detectCollisionLeftSegments(fish.getBoundary());
		fish.setFishState(FishState.RETURN);
		if (segment == null) {
			fish.getTrajectory().changeDirection(EDirection.LEFT);
			return null;
		} else {
			movingOverNeighbourPool(fish, segment);
			return segment.getNeighbourClientName();
		}
	}

	private String hitRight(Pool pool, Fish fish) {
		Segment segment = pool.detectCollisionRightSegments(fish.getBoundary());
		fish.setFishState(FishState.RETURN);
		if (segment == null) {
			fish.getTrajectory().changeDirection(EDirection.RIGHT);
			return null;
		} else {
			movingOverNeighbourPool(fish, segment);
			return segment.getNeighbourClientName();
		}
	}

	private String hitBottom(Pool pool, Fish fish) {
		Segment segment = pool
				.detectCollisionBottomSegments(fish.getBoundary());
		fish.setFishState(FishState.RETURN);
		if (segment == null) {
			fish.getTrajectory().changeDirection(EDirection.BOTTOM);
			return null;
		} else {
			movingOverNeighbourPool(fish, segment);
			return segment.getNeighbourClientName();
		}
	}

	private String hitTop(Pool pool, Fish fish) {
		Segment segment = pool.detectCollisionTopSegments(fish.getBoundary());
		fish.setFishState(FishState.RETURN);
		if (segment == null) {
			fish.getTrajectory().changeDirection(EDirection.TOP);
			return null;
		} else {
			movingOverNeighbourPool(fish, segment);
			return segment.getNeighbourClientName();
		}
	}

	private void movingOverNeighbourPool(Fish fish, Segment segment) {
		FishPackage fishPackage = new FishPackage(
				segment.getNeighbourClientName(), fish);
		EventDestination.getInstance().dispatchSuccessEventWithObject(
				EventType.BLL_SEND_FISH, fishPackage);
	}

	public void addFish(String clientName, Fish fish) {
		Pool pool = getPool(clientName);
		if (pool != null) {
			fish.increaseLocation(pool.getBoundary().getMinX(), pool
					.getBoundary().getMinY());
			fishes.add(fish);
		}
	}

	private Pool getPool(String clientName) {
		for (Pool pool : pools) {
			String poolClientName = pool.getDeviceInfo().getClientName();
			if (poolClientName.equals(clientName))
				return pool;
		}
		return null;
	}

	public Pool getPoolForClient(String clientName) {
		Pool pool = getPool(clientName);
		if (pool == null) {
			return null;
		}
		Pool clientPool = new Pool(pool.getDeviceInfo());
		clientPool.setScale(pool.getScale());
		for (Segment segment : pool.getSegments()) {
			Segment clientSegment = segment.clone();
			clientPool.getSegments().add(clientSegment);
		}
		return clientPool;
	}

	public Fish getFishForClient(Fish fish, String clientName) {
		if (fish == null)
			return null;
		Pool pool = getPool(clientName);
		if (pool == null)
			return null;
		Fish newFish = fish.clone();
		Boundary poolBoundary = pool.getBoundary();
		fish.increaseLocation(-poolBoundary.getMinX(), -poolBoundary.getMinY());
		return newFish;
	}

	public List<Fish> getFishes() {
		return fishes;
	}
}