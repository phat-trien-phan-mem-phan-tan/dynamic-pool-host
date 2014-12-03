package vn.edu.hust.student.dynamicpool.dal;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.edu.hust.student.dynamicpool.bll.model.FishPackage;
import vn.edu.hust.student.dynamicpool.bll.model.JSONContentDTO;
import vn.edu.hust.student.dynamicpool.bll.model.Pool;
import vn.edu.hust.student.dynamicpool.dal.client.entity.Client;
import vn.edu.hust.student.dynamicpool.dal.controller.HostMainController;
import vn.edu.hust.student.dynamicpool.dal.statics.Field;
import vn.edu.hust.student.dynamicpool.events.EventDestination;
import vn.edu.hust.student.dynamicpool.events.EventType;
import vn.edu.hust.student.dynamicpool.exeption.DALException;
import vn.edu.hust.student.dynamicpool.utils.AppConst;

public class HostDataAccessLayerImpl {
	private Logger logger = LoggerFactory
			.getLogger(HostDataAccessLayerImpl.class);

	public HostDataAccessLayerImpl() {
		logger.debug("Contruct");
	}

	public String getClientName() {
		return AppConst.DEFAULT_HOST_NAME;
	}

	private Thread createHostThread = new Thread(new Runnable() {

		@Override
		public void run() {
			String key = null;
			try {
				key = HostMainController.getInstance().connectServer();
				boolean isSuccess = HostMainController.getInstance().start();
				if (isSuccess) {
					logger.info("create host success with key: {}", key);
					EventDestination.getInstance()
							.dispatchSuccessEventWithObject(
									EventType.DAL_CREATE_HOST, key);
					return;
				} else {
					logger.info("cannot start server, please close running hosts");
				}
			} catch (DALException e) {
				logger.error("cannot create host {}", e.getMessage());

			} catch (UnknownHostException e) {
				logger.debug("cannot get ip lan {}", e.getMessage());
			}
			EventDestination.getInstance().dispatchFailEvent(
					EventType.DAL_CREATE_HOST);
		}
	});

	public void createHost() {
		logger.debug("create host");
		createHostThread.run();
	}

	public void updateSettingToClient(String clientName, Pool pool) {
		logger.debug("send setting to client {}", clientName);
		if (clientName == AppConst.DEFAULT_HOST_NAME) {
			EventDestination.getInstance().dispatchSuccessEventWithObject(
					EventType.DAL_UPDATE_SETTINGS_RESPONSE, pool);
		} else {
			sendSetingToClient(clientName, pool);
		}
	}

	private void sendSetingToClient(String clientName, Pool pool) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Field.COMMAND, Field.SEND_SETTINGS);
		map.put(Field.POOL, pool);
		Client client = HostMainController.getInstance().getClientManager()
				.getClient(clientName);
		if (client != null) {
			client.send(map);
		} else {
			logger.error("not found for clientName {}", clientName);
		}
	}

	public void sendFishToAClient(FishPackage fishPackage) {
		Client client = HostMainController.getInstance().getClientManager()
				.getClient(fishPackage.getClientName());
		if (client != null) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(Field.COMMAND, Field.SEND_FISH);
			JSONContentDTO jsonContentDTO = JSONContentDTO.fromFish(fishPackage
					.getFish());
			logger.debug(jsonContentDTO.getJsonContent());
			map.put(Field.JSON_CONTENT, jsonContentDTO.toJSONString());
			map.put(Field.CLIENT_NAME, fishPackage.getClientName());
			client.send(map);
		}
	}

	@SuppressWarnings("deprecation")
	public void exit() {
		try {
			this.createHostThread.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
