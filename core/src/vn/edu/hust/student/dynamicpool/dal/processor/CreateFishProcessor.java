package vn.edu.hust.student.dynamicpool.dal.processor;

import java.util.Map;

import vn.edu.hust.student.dynamicpool.bll.model.FishPackage;
import vn.edu.hust.student.dynamicpool.bll.model.Fish;
import vn.edu.hust.student.dynamicpool.dal.dto.JSONContentDTO;
import vn.edu.hust.student.dynamicpool.dal.statics.Field;
import vn.edu.hust.student.dynamicpool.events.EventDestination;
import vn.edu.hust.student.dynamicpool.events.EventType;

public class CreateFishProcessor extends Processor {

	@Override
	public ProcessorExecutionResponse execute(ProcessorExecutionRequest request) {
		Map<String, Object> map = request.getParameters();
		if (map.containsKey(Field.JSON_CONTENT)
				&& map.containsKey(Field.CLIENT_NAME)) {
			Object jsonContentObject = map.get(Field.JSON_CONTENT);
			System.err.println(jsonContentObject.toString());
			if (jsonContentObject instanceof String) {
				try {
					JSONContentDTO jsonContentDTO = JSONContentDTO.createDTOfromJSONString((String)jsonContentObject);
					Fish fish = JSONContentDTO.toFish(jsonContentDTO);
					String clientName = (String) map.get(Field.CLIENT_NAME);
					FishPackage fishPackage = new FishPackage(clientName, fish);
					EventDestination.getInstance().dispatchSuccessEventWithObject(
							EventType.DAL_CREATE_FISH_REQUEST, fishPackage);
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
		}
		return null;
	}
}
