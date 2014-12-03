package vn.edu.hust.student.dynamicpool.presentation.gameobject;

import java.util.List;

import vn.edu.hust.student.dynamicpool.bll.model.Boundary;
import vn.edu.hust.student.dynamicpool.bll.model.EDirection;
import vn.edu.hust.student.dynamicpool.bll.model.Fish;
import vn.edu.hust.student.dynamicpool.bll.model.Point;
import vn.edu.hust.student.dynamicpool.bll.model.Pool;
import vn.edu.hust.student.dynamicpool.bll.model.Segment;
import vn.edu.hust.student.dynamicpool.bll.model.host.HostPoolManager;
import vn.edu.hust.student.dynamicpool.utils.AppConst;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class WidePoolUI {
	private static final Color POOL_COLOR = Color.WHITE;
	private static final Color FISH_COLOR = Color.ORANGE;
	private static final Color SEGMENT_COLOR = Color.GREEN;
	private static final Color ACTIVE_POOL_COLOR = Color.CYAN;
	private static final int dx = 1;
	private static final int dy = 1;
	private static final Color ERROR_POOL_COLOR = Color.RED;
	CordinateConvert convert = new CordinateConvert();
	private HostPoolManager hostPoolManager;
	private ShapeRenderer shapeRenderer;
	private EDirection movingActivePoolDirection = EDirection.UNKNOWN;
	private String message = null;

	public WidePoolUI(HostPoolManager poolManager) {
		this.hostPoolManager = poolManager;
	}

	public void draw(ShapeRenderer shapeRenderer) {
		this.shapeRenderer = shapeRenderer;
		updateChange(hostPoolManager.getPools());
		drawPoolsAndSegments(shapeRenderer, hostPoolManager.getPools());
		drawFishes(shapeRenderer);
	}
	
	public void updateChange(List<Pool> pools) {
		float minX = 0, minY = 0, maxX = 0, maxY = 0;
		for (Pool pool : pools) {
			minX = Math.min(minX, pool.getBoundary().getMinX());
			minY = Math.min(minY, pool.getBoundary().getMinY());
			maxX = Math.max(maxX, pool.getBoundary().getMaxX());
			maxY = Math.max(maxY, pool.getBoundary().getMaxY());
		}
		Boundary acturalBoundary = new Boundary(new Point(minX, maxY), maxX
				- minX, maxY - minY);
		convert.setActuralBoundary(acturalBoundary);
		Boundary vituralBoundary = new Boundary(
				new Point(AppConst.width / 4, 0), AppConst.width / 2,
				AppConst.height / 2);
		convert.setVituralBoundary(vituralBoundary);
	}

	private void drawPoolsAndSegments(ShapeRenderer shapeRenderer, List<Pool> pools) {
		shapeRenderer.begin(ShapeType.Line);
		for (Pool pool : pools) {
			Boundary convertedBoundary = this.drawPool(pool);
			shapeRenderer.setColor(SEGMENT_COLOR);
			for (Segment segment : pool.getSegments()) {
				this.drawSegment(pool, segment, convertedBoundary);
			}
		}
		shapeRenderer.end();
	}
	
	private Boundary drawPool(Pool pool) {
		shapeRenderer.setColor(POOL_COLOR);
		Boundary poolBoundary = convert.convertBoundary(pool.getBoundary());
		shapeRenderer.rect(poolBoundary.getMinX(), poolBoundary.getMinY(),
				poolBoundary.getWidth(), poolBoundary.getHeight());
		return poolBoundary;
	}
	
	private void drawSegment(Pool pool, Segment segment,
			Boundary convertedBoundary) {
		float x1 = 0, y1 = 0, x2 = 0, y2 = 0;
		switch (segment.getSegmentDirection()) {
		case LEFT:
			x1 = x2 = convertedBoundary.getMinX() + 1;
			y1 = convert.convertLocationY(segment.getBeginPoint());
			y2 = convert.convertLocationY(segment.getEndPoint());
			break;
		case RIGHT:
			x1 = x2 = convertedBoundary.getMaxX() - 1;
			y1 = convert.convertLocationY(segment.getBeginPoint());
			y2 = convert.convertLocationY(segment.getEndPoint());
			break;
		case TOP:
			y1 = y2 = convertedBoundary.getMaxY() - 1;
			x1 = convert.convertLocationX(segment.getBeginPoint());
			x2 = convert.convertLocationX(segment.getEndPoint());
			break;
		case BOTTOM:
			y1 = y2 = convertedBoundary.getMinY() + 1;
			x1 = convert.convertLocationX(segment.getBeginPoint());
			x2 = convert.convertLocationX(segment.getEndPoint());
			break;
		default:
			return;
		}
		shapeRenderer.line(x1, y1, x2, y2);
	}
	
	private void drawFishes(ShapeRenderer shapeRenderer) {
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(FISH_COLOR);
		for (Fish fish : hostPoolManager.getFishes()) {
			this.drawFish(fish);
		}
		shapeRenderer.end();
	}
	
	private void drawFish(Fish fish) {
		Boundary boundary = convert.convertBoundary(fish.getBoundary());
		shapeRenderer.rect(boundary.getMinX(), boundary.getMinY(),
				boundary.getWidth(), boundary.getHeight());
	}
	
	public void drawSetting(ShapeRenderer shapeRenderer) {
		this.shapeRenderer = shapeRenderer;
		updateChange(hostPoolManager.getTempPools());
		drawActivePool(this.hostPoolManager.getActivedPool());
		drawPoolsAndSegments(shapeRenderer, hostPoolManager.getTempPools());
	}

	private void drawActivePool(Pool activedPool) {
		if (activedPool == null) return;
		updateActivePoolPosition();
		Boundary boundary = convert.convertBoundary(activedPool.getBoundary());
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(hostPoolManager.isValidSetting() ? ACTIVE_POOL_COLOR : ERROR_POOL_COLOR);
		shapeRenderer.rect(boundary.getMinX(), boundary.getMinY(),
				boundary.getWidth(), boundary.getHeight());
		shapeRenderer.end();
	}
	
	

	public void setLastDirectionOfMovingActivePool(EDirection movingDirection) {
		this.movingActivePoolDirection = movingDirection;
	}
	
	public void updateActivePoolPosition() {
		switch (movingActivePoolDirection) {
		case LEFT:
			hostPoolManager.moveActivePool(-dx, 0);
			break;
		case RIGHT:
			hostPoolManager.moveActivePool(dx, 0);
			break;
		case BOTTOM:
			hostPoolManager.moveActivePool(0, -dy);
			break;
		case TOP:
			hostPoolManager.moveActivePool(0, dy);
			break;
		default:
			break;
		}
	}
}