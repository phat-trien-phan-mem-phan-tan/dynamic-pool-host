package vn.edu.hust.student.dynamicpool.presentation.screen;

import vn.edu.hust.student.dynamicpool.bll.model.EDirection;
import vn.edu.hust.student.dynamicpool.presentation.WorldController;
import vn.edu.hust.student.dynamicpool.presentation.gameobject.WidePoolUI;
import vn.edu.hust.student.dynamicpool.utils.AppConst;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

public class GSSettingInputProcessor implements InputProcessor {

	private WorldController worldController;
	private WidePoolUI widePoolUI;

	public GSSettingInputProcessor(WorldController worldController,
			WidePoolUI widePoolUI) {
		this.worldController = worldController;
		this.widePoolUI = widePoolUI;
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
		case Keys.LEFT:
			widePoolUI.setLastDirectionOfMovingActivePool(EDirection.LEFT);
			break;
		case Keys.RIGHT:
			widePoolUI.setLastDirectionOfMovingActivePool(EDirection.RIGHT);
			break;
		case Keys.UP:
			widePoolUI.setLastDirectionOfMovingActivePool(EDirection.TOP);
			break;
		case Keys.DOWN:
			widePoolUI.setLastDirectionOfMovingActivePool(EDirection.BOTTOM);
			break;
		case Keys.ENTER:
			worldController.tryUpdateSettingForClient();
			break;
		case Keys.TAB:

			break;
		case Keys.SPACE:

			break;
		case Keys.ESCAPE:

			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		widePoolUI.setLastDirectionOfMovingActivePool(EDirection.UNKNOWN);
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (isClickOnExitButton(screenX, screenY))
			worldController.exit();
		return false;
	}

	private boolean isClickOnExitButton(int screenX, int screenY) {
		return screenX >= 0 && screenX < 64 && screenY > AppConst.height - 64
				&& screenY <= AppConst.height;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
