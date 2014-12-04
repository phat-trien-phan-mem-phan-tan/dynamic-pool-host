package vn.edu.hust.student.dynamicpool.presentation.gameobject;

import vn.edu.hust.student.dynamicpool.bll.model.Boundary;
import vn.edu.hust.student.dynamicpool.bll.model.Point;
import vn.edu.hust.student.dynamicpool.utils.AppConst;

public class CordinateConvert {

	private Boundary acturalBoundary = new Boundary(new Point(), AppConst.width, AppConst.height);
	private Boundary vituralBoundary = new Boundary(new Point(AppConst.width/4, 0), AppConst.width/2, AppConst.height/2);
	private float scale = 1;

	public void setActuralBoundary(Boundary acturalBoundary) {
		this.acturalBoundary = acturalBoundary;
		updateScale();
	}

	private void updateScale() {
		this.scale = vituralBoundary.getWidth() / acturalBoundary.getWidth();
	}

	public void setVituralBoundary(Boundary vituralBoundary) {
		this.vituralBoundary = vituralBoundary;
		updateScale();
	}

	public Boundary convertBoundary(Boundary boundary) {
		return new Boundary(convertLocation(boundary.getLocation()),
				convertValue(boundary.getWidth()),
				convertValue(boundary.getHeight()));
	}

	public Point convertLocation(Point p) {
		return new Point(convertLocationX(p.getX()), convertLocationY(p.getY()));
	}
	
	public float convertValue(float value) {
		return value * scale;
	}

	public float convertLocationX(float value) {
		return vituralBoundary.getMinX() + convertValue(value) - convertValue(acturalBoundary.getMinX());
	}
	
	public float convertLocationY(float value) {
		return vituralBoundary.getMinY() + convertValue(value) - convertValue(acturalBoundary.getMinY());
	}
}
