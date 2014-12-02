package vn.edu.hust.student.dynamicpool.dal;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.edu.hust.student.dynamicpool.bll.model.Fish;
import vn.edu.hust.student.dynamicpool.bll.model.Pool;
import vn.edu.hust.student.dynamicpool.dal.client.entity.Client;
import vn.edu.hust.student.dynamicpool.dal.controller.HostMainController;
import vn.edu.hust.student.dynamicpool.dal.dto.JSONContentDTO;
import vn.edu.hust.student.dynamicpool.dal.statics.Field;
import vn.edu.hust.student.dynamicpool.events.EventDestination;
import vn.edu.hust.student.dynamicpool.events.EventType;
import vn.edu.hust.student.dynamicpool.exception.DALException;
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

	public void createHost() {
		logger.debug("create host");
		String key = null;
		try {
			key = HostMainController.getInstance().connectServer();
			HostMainController.getInstance().start();
			logger.info("create host success with key: {}", key);
			EventDestination.getInstance().dispatchSuccessEventWithObject(
					EventType.DAL_CREATE_HOST, key);
		} catch (DALException e) {
			logger.error("cannot create host {}", e.getMessage());

		} catch (UnknownHostException e) {
			logger.debug("cannot get ip lan {}", e.getMessage());
		}
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
		Map<String, Object> map = new HashMap<>();
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

	public void respondCreateFishRequest(String clientName, boolean isSuccess,
			Fish fish) {
		if (clientName.equals(getClientName())) {
			if (isSuccess) {
				EventDestination.getInstance().dispatchSuccessEventWithObject(
						EventType.DAL_CREATE_FISH_RESPONSE, fish);
			} else {
				EventDestination.getInstance().dispatchFailEvent(
						EventType.DAL_CREATE_FISH_RESPONSE);
			}
		} else {
			sendFishToClientViaSocket(clientName, isSuccess, fish);
		}
	}

	private void sendFishToClientViaSocket(String clientName,
			boolean isSuccess, Fish fish) {
		Client client = HostMainController.getInstance().getClientManager()
				.getClient(clientName);
		if (client != null) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(Field.COMMAND, Field.SEND_FISH);
			JSONContentDTO jsonContentDTO = JSONContentDTO.fromFish(fish);
			map.put(Field.JSON_CONTENT, jsonContentDTO.toString());
			map.put(Field.SUCCESSFUL, isSuccess);
			map.put(Field.CLIENT_NAME, clientName);
			client.send(map);
		} else {
			logger.error("Not found for clientName {}", clientName);
		}
	}
}
