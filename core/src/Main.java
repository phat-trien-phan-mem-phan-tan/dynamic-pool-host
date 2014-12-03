import java.util.ArrayList;
import java.util.List;

import vn.edu.hust.student.dynamicpool.bll.model.DeviceInfo;
import vn.edu.hust.student.dynamicpool.bll.model.Pool;
import vn.edu.hust.student.dynamicpool.bll.model.Segment;
import vn.edu.hust.student.dynamicpool.bll.model.host.FindCommonEdgeFunction;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<Pool> list = new ArrayList<Pool>();
		Pool pool1 = new Pool(new DeviceInfo(1366, 768, 14));
		list.add(pool1);
		Pool pool2 = new Pool(new DeviceInfo(1366, 768, 14));
		pool2.getBoundary().setLocation(1366, 0);
		list.add(pool2);
		Pool pool3 = new Pool(new DeviceInfo(1366, 768, 14));
		pool3.getBoundary().setLocation(0, 768);
		list.add(pool3);
		List<Pool> pools = FindCommonEdgeFunction.calucalteCommonEdge(list);
		for (Pool pool : pools) {
			System.out.println("--------------------");
			for (Segment seg : pool.getSegments()) {
				System.out.println(seg);
			}
		}
	}

}
