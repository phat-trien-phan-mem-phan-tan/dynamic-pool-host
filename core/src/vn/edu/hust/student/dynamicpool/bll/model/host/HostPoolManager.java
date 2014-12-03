package vn.edu.hust.student.dynamicpool.bll.model.host;

import java.util.ArrayList;
import java.util.List;

import vn.edu.hust.student.dynamicpool.bll.model.Boundary;
import vn.edu.hust.student.dynamicpool.bll.model.DeviceInfo;
import vn.edu.hust.student.dynamicpool.bll.model.EDirection;
import vn.edu.hust.student.dynamicpool.bll.model.Fish;
import vn.edu.hust.student.dynamicpool.bll.model.FishPackage;
import vn.edu.hust.student.dynamicpool.bll.model.FishState;
import vn.edu.hust.student.dynamicpool.bll.model.Point;
import vn.edu.hust.student.dynamicpool.bll.model.Pool;
import vn.edu.hust.student.dynamicpool.bll.model.PoolManager;
import vn.edu.hust.student.dynamicpool.bll.model.Segment;
import vn.edu.hust.student.dynamicpool.events.EventDestination;
import vn.edu.hust.student.dynamicpool.events.EventType;
import vn.edu.hust.student.dynamicpool.utils.AppConst;

public class HostPoolManager implements PoolManager {
	private List<Pool> pools = new ArrayList<Pool>();
	private List<Fish> fishes = new ArrayList<Fish>();
	private Pool mainPool = new Pool();
	private Pool activedPool = null;
	private List<Pool> tempPools = new ArrayList<Pool>();
	private double DPI = 0f;

	public HostPoolManager() {
		this.pools.add(mainPool);
	}

	public List<Pool> getPools() {
		return pools;
	}

	public void addPool(Pool pool) {
		pool = fixWidthAndHeightByDPI(pool);
		Point location = getDefaultLocation(pool);
		pool.getBoundary().setLocation(location);
		pools.add(pool);
		initTempPools(pool);
	}

	private Pool fixWidthAndHeightByDPI(Pool pool) {
		double poolDPI = getDPI(pool);
		if (DPI == 0)
			DPI = poolDPI;
		int width = (int) (mainPool.getBoundary().getWidth() * DPI / poolDPI);
		int height = (int) (mainPool.getBoundary().getHeight() * DPI / poolDPI);
		pool.getBoundary().setWidth(width);
		pool.getBoundary().setHeight(height);
		return pool;
	}

	private Point getDefaultLocation(Pool poolServer) {
		float x = 0, y = 0;
		for (Pool pool : pools) {
			x = Math.max(pool.getBoundary().getMaxX(), x);
			y = Math.min(pool.getBoundary().getMinY(), y);
		}
		return new Point(x, y);
	}

	private void initTempPools(Pool activePool) {
		tempPools.clear();
		for (Pool pool : pools) {
			Pool clone = pool.clone();
			tempPools.add(clone);
			if (pool.equals(activePool))
				this.activedPool = clone;
		}
	}

	public int fishCount() {
		return this.fishes.size();
	}

	public void calculate() {
		// FindCommonEdgeFunction.test(pools);
		// FindCommonEdgeFunction.findCommonEdge(this.pools);
		FindCommonEdgeFunction.calucalteCommonEdge(tempPools);
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
				detectCollisionForInsideFish(fish);
				break;
			case OUTSIDE:
			case PASSING:
			case RETURN:
			default:
				if (fish.isInside()) {
					fish.setFishState(FishState.INSIDE);
					fish.setPassingDirection(EDirection.UNKNOWN);
				}
				break;
			}
		}
	}

	private void detectCollisionForInsideFish(Fish fish) {
		Boundary fishBoundary = fish.getBoundary();
		Pool pool = fish.getPool();
		if (pool == null)
			return;
		Boundary poolBoundary = pool.getBoundary();
		if (fishBoundary.getMinX() <= poolBoundary.getMinX()) {
			hitLeft(pool, fish);
		} else if (fishBoundary.getMaxX() >= poolBoundary.getMaxX()) {
			hitRight(pool, fish);
		} else if (fishBoundary.getMinY() <= poolBoundary.getMinY()) {
			hitBottom(pool, fish);
		} else if (fishBoundary.getMaxY() >= poolBoundary.getMaxY()) {
			hitTop(pool, fish);
		}
	}

	private void hitLeft(Pool pool, Fish fish) {
		Segment segment = pool.detectCollisionLeftSegments(fish.getBoundary());
		fish.setFishState(FishState.RETURN);
		if (segment == null) {
			fish.changeDirection(EDirection.LEFT);
		} else {
			fish.setPassingDirection(EDirection.LEFT);
			movingOverNeighbourPool(fish, segment);
		}
	}

	private void hitRight(Pool pool, Fish fish) {
		Segment segment = pool.detectCollisionRightSegments(fish.getBoundary());
		fish.setFishState(FishState.RETURN);
		if (segment == null) {
			fish.changeDirection(EDirection.RIGHT);
		} else {
			fish.setPassingDirection(EDirection.RIGHT);
			movingOverNeighbourPool(fish, segment);
		}
	}

	private void hitBottom(Pool pool, Fish fish) {
		Segment segment = pool
				.detectCollisionBottomSegments(fish.getBoundary());
		fish.setFishState(FishState.RETURN);
		if (segment == null) {
			fish.changeDirection(EDirection.BOTTOM);
		} else {
			fish.setPassingDirection(EDirection.BOTTOM);
			movingOverNeighbourPool(fish, segment);
		}
	}

	private void hitTop(Pool pool, Fish fish) {
		Segment segment = pool.detectCollisionTopSegments(fish.getBoundary());
		fish.setFishState(FishState.RETURN);
		if (segment == null) {
			fish.changeDirection(EDirection.TOP);
		} else {
			fish.setPassingDirection(EDirection.TOP);
			movingOverNeighbourPool(fish, segment);
		}
	}

	private void movingOverNeighbourPool(Fish fish, Segment segment) {
		Pool pool = getPool(segment.getNeighbourClientName());
		if (pool == null) {
			this.fishes.remove(fish);
			return;
		}
		fish.setPool(pool);
		Fish cloneFish = fish.clone();
		cloneFish.increaseLocation(-pool.getBoundary().getMinX(), -pool
				.getBoundary().getMinY());
		FishPackage fishPackage = new FishPackage(
				segment.getNeighbourClientName(), cloneFish);
		EventDestination.getInstance().dispatchSuccessEventWithObject(
				EventType.BLL_SEND_FISH, fishPackage);
	}

	public void addFish(String clientName, Fish fish) {
		Pool pool = getPool(clientName);
		if (pool != null) {
			fish.increaseLocation(pool.getBoundary().getMinX(), pool
					.getBoundary().getMinY());
			fish.setPool(pool);
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

	public List<Fish> getFishes() {
		return fishes;
	}

	public void addFish(Fish fish) {
		fish.setPool(mainPool);
		fishes.add(fish);
	}

	public void updateWidthAndHeight(int width, int height, float screenSize) {
		this.mainPool.getBoundary().setWidth(width);
		this.mainPool.getBoundary().setHeight(height);
		DeviceInfo deviceInfo = new DeviceInfo(width, height, screenSize);
		deviceInfo.setClientName(AppConst.DEFAULT_HOST_NAME);
		this.mainPool.setDeviceInfo(deviceInfo);
		this.DPI = getDPI(mainPool);
	}

	private double getDPI(Pool pool) {
		float width = pool.getBoundary().getWidth();
		float screenSize = pool.getDeviceInfo().getScreenSize();
		float height = pool.getBoundary().getHeight();
		return Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)) / screenSize;
	}

	public Pool getActivedPool() {
		return activedPool;
	}

	public void moveActivePool(int dx, int dy) {
		if (activedPool == null)
			return;
		this.activedPool.getBoundary().increaseLocation(dx, dy);
		this.tempValid = 0;
	}

	private int tempValid = 0;

	public boolean isValidSetting() {
		if (tempValid == 1) {
			return true;
		} else if (tempValid == 2) {
			return false;
		}
		boolean valid = FindCommonEdgeFunction.isValid(tempPools);
		tempValid = valid ? 1 : 2;
		if (valid)
			calculate();
		return valid;
	}

	public List<Pool> getTempPools() {
		return tempPools;
	}

	public void saveSetting() {
		for (Pool pool : pools) {
			List<Segment> segments = pool.getSegments();
			Pool updatePool = getUpdateSegment(pool);
			segments.clear();
			segments.addAll(updatePool.getSegments());
			pool.getBoundary().setLocation(updatePool.getBoundary().getMinX(),
					updatePool.getBoundary().getMinY());
		}
	}

	private Pool getUpdateSegment(Pool pool) {
		String clientName = pool.getDeviceInfo().getClientName();
		for (Pool temp : tempPools) {
			if (clientName.equals(temp.getDeviceInfo().getClientName())) {
				return temp;
			}
		}
		return null;
	}
}