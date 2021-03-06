package vn.edu.hust.student.dynamicpool.presentation.gameobject;

import vn.edu.hust.student.dynamicpool.bll.model.Fish;
import vn.edu.hust.student.dynamicpool.bll.model.FishType;
import vn.edu.hust.student.dynamicpool.utils.AppConst;

public class FishUIFactory {
	public static FishUI createFishUI(Fish fish) {
		FishType fishType = fish.getFishType();
		switch (fishType) {
		case FISH1:
			return new FishOneUI(fish);
		case FISH2:
			return new FishTwoUI(fish);
		case FISH3:
			return new FishThreeUI(fish);
		case FISH4:
			return new FishFourUI(fish);
		case FISH5:
			return new FishFiveUI(fish);
		case FISH6:
			return new FishSixUI(fish);
		case FISH7:
			return new FishSevenUI(fish);
		case FISH8:
			return new FishEightUI(fish);
		case FISH9:
			return new FishNineUI(fish);
		case FISH10:
			return new FishTenUI(fish);
		default:
			return new FishOneUI(fish);
		}
	}


	public static int getWith(FishType fishType) {
		switch (fishType) {
		case FISH1:
			return AppConst.FISH_ONE_WIDTH;
		case FISH2:
			return AppConst.FISH_TWO_WIDTH;
		case FISH3:
			return AppConst.FISH_THREE_WIDTH;
		case FISH4:
			return AppConst.FISH_FOUR_WIDTH;
		case FISH5:
			return AppConst.FISH_FIVE_WIDTH;
		case FISH6:
			return AppConst.FISH_SIX_WIDTH;
		case FISH7:
			return AppConst.FISH_SEVEN_WIDTH;
		case FISH8:
			return AppConst.FISH_EIGHT_WIDTH;
		case FISH9:
			return AppConst.FISH_NINE_WIDTH;
		case FISH10:
			return AppConst.FISH_TEN_WIDTH;
		default:
			return 0;
		}
	}
	public static int getHeight(FishType fishType) {
		switch (fishType) {
		case FISH1:
			return AppConst.FISH_ONE_HEIGHT;
		case FISH2:
			return AppConst.FISH_TWO_HEIGHT;
		case FISH3:
			return AppConst.FISH_THREE_HEIGHT;
		case FISH4:
			return AppConst.FISH_FOUR_HEIGHT;
		case FISH5:
			return AppConst.FISH_FIVE_HEIGHT;
		case FISH6:
			return AppConst.FISH_SIX_HEIGHT;
		case FISH7:
			return AppConst.FISH_SEVEN_HEIGHT;
		case FISH8:
			return AppConst.FISH_EIGHT_HEIGHT;
		case FISH9:
			return AppConst.FISH_NINE_HEIGHT;
		case FISH10:
			return AppConst.FISH_TEN_HEIGHT;
		default:
			return 0;
		}
	}
}
