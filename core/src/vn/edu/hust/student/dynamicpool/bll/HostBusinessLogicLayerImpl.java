package vn.edu.hust.student.dynamicpool.bll;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.edu.hust.student.dynamicpool.bll.model.DeviceInfo;
import vn.edu.hust.student.dynamicpool.bll.model.ETrajectoryType;
import vn.edu.hust.student.dynamicpool.bll.model.Fish;
import vn.edu.hust.student.dynamicpool.bll.model.FishFactory;
import vn.edu.hust.student.dynamicpool.bll.model.FishPackage;
import vn.edu.hust.student.dynamicpool.bll.model.FishType;
import vn.edu.hust.student.dynamicpool.bll.model.Pool;
import vn.edu.hust.student.dynamicpool.bll.model.host.HostPoolManager;
import vn.edu.hust.student.dynamicpool.dal.HostDataAccessLayerImpl;
import vn.edu.hust.student.dynamicpool.events.EventDestination;
import vn.edu.hust.student.dynamicpool.events.EventType;

import com.eposi.eventdriven.Event;
import com.eposi.eventdriven.implementors.BaseEventListener;

public class HostBusinessLogicLayerImpl {
	private HostPoolManager hostPoolManager = new HostPoolManager();
	private Logger logger = LoggerFactory
			.getLogger(HostBusinessLogicLayerImpl.class);
	private HostDataAccessLayerImpl dataAccessLayer = new HostDataAccessLayerImpl();
	private String key = "";

	public HostBusinessLogicLayerImpl() {
		registerEvents();
	}

	public void update(float deltaTime) {
		hostPoolManager.updateLocationOfFishes(deltaTime);
	}

	protected void registerEvents() {
		EventDestination.getInstance().addEventListener(
				EventType.DAL_CREATE_HOST,
				new BaseEventListener(this, "onCreateHostCallbackHander"));
		EventDestination.getInstance()
				.addEventListener(
						EventType.DAL_ADD_DEVICE_REQUEST,
						new BaseEventListener(this,
								"onAddDiviceRequestCallbackHander"));
		EventDestination.getInstance()
				.addEventListener(
						EventType.DAL_CREATE_FISH_REQUEST,
						new BaseEventListener(this,
								"onCreateFishRequestCallbackHander"));
		EventDestination.getInstance().addEventListener(
				EventType.BLL_SEND_FISH,
				new BaseEventListener(this, "onSendFishCallbackHander"));
	}

	public void createHost() {
		dataAccessLayer.createHost();
	}

	@Deprecated
	public void onCreateHostCallbackHander(Event event) {
		logger.debug("on create host callback hander");
		if (EventDestination.parseEventToBoolean(event)) {
			Object keyObject = EventDestination.parseEventToTargetObject(event);
			saveKey(keyObject.toString());
			logger.info("create host success");
			EventDestination.getInstance().dispatchSuccessEvent(
					EventType.BLL_CREATE_HOST);
		} else {
			logger.error("cannot create host");
			EventDestination.getInstance().dispatchSuccessEventWithObject(
					EventType.APP_ERROR, "cannot start host, please close running hosts");
		}
	}

	private void saveKey(String keyOfHost) {
		this.key = keyOfHost;
	}

	@Deprecated
	public void onAddDiviceRequestCallbackHander(Event event) {
		logger.debug("on add device request callback hander");
		if (EventDestination.parseEventToBoolean(event)) {
			Object deviceObject = EventDestination
					.parseEventToTargetObject(event);
			if (DeviceInfo.class.isInstance(deviceObject)) {
				logger.info("add device success");
				DeviceInfo deviceInfo = (DeviceInfo) deviceObject;
				logger.info("add device request: client name {}",
						deviceInfo.getClientName());
				Pool pool = new Pool(deviceInfo);
				hostPoolManager.addPool(pool);
				logger.debug("begin setting");
				EventDestination.getInstance().dispatchSuccessEventWithObject(
						EventType.BLL_BEGIN_SETTING, pool);
				return;
			}
			logger.error("cannot add device: target object is not an instance of DiviceInfo");
		} else {
			logger.error("Add Device Callback Hander: event false");
		}
	}

	public void saveUpdateSettingForAllClient() {
		for (Pool pool : hostPoolManager.getPools()) {
			String clientName = pool.getDeviceInfo().getClientName();
			Pool poolForClient = hostPoolManager.getPoolForClient(clientName);
			if (poolForClient == null) {
				logger.error("cannot get pool {}", clientName);
				return;
			}
			logger.debug("send update setting for client {}", clientName);
			dataAccessLayer.updateSettingToClient(clientName, poolForClient);
		}
	}

	@Deprecated
	public void onCreateFishRequestCallbackHander(Event event) {
		if (EventDestination.parseEventToBoolean(event)) {
			Object fishPackageObject = EventDestination
					.parseEventToTargetObject(event);
			if (fishPackageObject instanceof FishPackage) {
				FishPackage fishPackage = (FishPackage) fishPackageObject;
				hostPoolManager.addFish(fishPackage.getClientName(),
						fishPackage.getFish());
			}
		}
	}

	@Deprecated
	public void onSendFishCallbackHander(Event event) {
		if (EventDestination.parseEventToBoolean(event)) {
			Object fishPackageObject = EventDestination
					.parseEventToTargetObject(event);
			if (fishPackageObject instanceof FishPackage) {
				FishPackage fishPackage = (FishPackage) fishPackageObject;
				dataAccessLayer.sendFishToAClient(fishPackage);
			}
		}
	}

	public HostPoolManager getHostPoolManager() {
		return this.hostPoolManager;
	}

	public List<Fish> getFishes() {
		return hostPoolManager.getFishes();
	}

	public void exit() {
		dataAccessLayer.exit();
	}

	public void createFish(FishType fishType, ETrajectoryType trajectoryType,
			int with, int height) {
		Fish fish = FishFactory.createFishWithTrajectoryType(fishType,
				trajectoryType, with, height);
		hostPoolManager.addFish(fish);
	}

	public String getKeyOfHost() {
		return this.key;
	}

	public void updateDeviceInfo(int width, int height, float screenSize) {
		hostPoolManager.updateWidthAndHeight(width, height, screenSize);
	}

}