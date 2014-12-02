package vn.edu.hust.student.dynamicpool.presentation.screen;

import java.util.List;

import vn.edu.hust.student.dynamicpool.bll.model.Fish;
import vn.edu.hust.student.dynamicpool.bll.model.HostPoolManager;
import vn.edu.hust.student.dynamicpool.presentation.WorldController;
import vn.edu.hust.student.dynamicpool.presentation.WorldRenderer;
import vn.edu.hust.student.dynamicpool.presentation.assets.AssetGameScreen;
import vn.edu.hust.student.dynamicpool.presentation.assets.Assets;
import vn.edu.hust.student.dynamicpool.presentation.gameobject.FishUI;
import vn.edu.hust.student.dynamicpool.presentation.gameobject.FishUICollection;
import vn.edu.hust.student.dynamicpool.presentation.gameobject.WidePoolUI;
import vn.edu.hust.student.dynamicpool.utils.AppConst;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GameScreen implements Screen {

	protected WorldRenderer worldRenderer = null;
	protected WorldController worldController = null;
	protected SpriteBatch batch = null;
	private FishUICollection fishUICollection = null;
	private Texture exitButtonTexture;
	private Texture addFishButtonTexture;
	private Texture selectFishButtonsTexture;
	private Texture selectTrajectoryButtonTexture;
	protected InputProcessor defaultInputProcessor;
	protected InputProcessor selectFishInputProcessor;
	protected InputProcessor selectTrajectoryInputProcessor;
	private BitmapFont defaultFont;
	private WidePoolUI widePoolUI;
	ShapeRenderer shapeRenderer = new ShapeRenderer();
	
	public GameScreen(WorldRenderer worldRenderer,
			WorldController worldController, HostPoolManager hostPoolManager) {
		this.worldRenderer = worldRenderer;
		this.worldController = worldController;
		this.batch = worldRenderer.getBatch();
		this.fishUICollection = worldController.getFishUICollection();
		widePoolUI = new WidePoolUI(hostPoolManager);
	}

	@Override
	public void render(float delta) {
		worldRenderer.beginRender();
		renderFishsUIAndUpdate(delta);
		renderKey();
		renderWidePool();
		renderHubControl();
		worldRenderer.endRender();
	}

	protected void renderFishsUIAndUpdate(float deltaTime) {
		List<Fish> fishs = worldController.getFishs();
		for (Fish fish : fishs) {
			renderAFishUI(fish, deltaTime);
		}
		worldController.updateFishsCordinate(deltaTime);
	}
	
	private void renderKey() {
		String key = worldController.getKey();
		float x = AppConst.width-20-defaultFont.getBounds(key).width;
		float y = 25+defaultFont.getBounds(key).height;
		defaultFont.draw(batch, key, x, y);
	}
	
	private void renderWidePool() {
		shapeRenderer.setProjectionMatrix(worldRenderer.getCamera().combined);
		widePoolUI.draw(this.shapeRenderer);
	}

	private void renderAFishUI(Fish fish, float deltaTime) {
		FishUI fishUI = fishUICollection.getFishUI(fish);
		fishUI.render(batch);
		fishUI.update(deltaTime);
	}

	protected void renderHubControl() {
		batch.draw(exitButtonTexture, 0, 0);
		batch.draw(addFishButtonTexture, 64, 0);
		if (worldController.isShowSelectFishButtons())
			batch.draw(selectFishButtonsTexture, 0, AppConst.height - 100, 480,
					100);
		if (worldController.isShowSelectTrajectoryButtons())
			batch.draw(selectTrajectoryButtonTexture, 0,
					AppConst.height - 100,
					selectTrajectoryButtonTexture.getWidth(),
					selectTrajectoryButtonTexture.getHeight());
	}

	@Override
	public void resize(int width, int height) {
		worldRenderer.resize(width, height);
	}

	@Override
	public void show() {
		AssetGameScreen gameAssets = Assets.instance.gameScreen;
		this.exitButtonTexture = gameAssets.getExitButtonTexture();
		this.addFishButtonTexture = gameAssets.getAddFishButtonTexture();
		this.selectFishButtonsTexture = gameAssets
				.getSelectFishButtonsTexture();
		this.selectTrajectoryButtonTexture = gameAssets.getSelectTrajectoryButtonTexture();
		this.defaultFont = Assets.instance.gameScreen.getDefaultFont();
		createInputprocessors();
		setDefaultInputProcessor();
	}

	protected void createInputprocessors() {
		this.defaultInputProcessor = new GSDefaultInputProcessor(
				worldController);
		this.selectFishInputProcessor = new GSSelectFishInputProcessor(
				worldController);
		this.selectTrajectoryInputProcessor = new GSSelectTrajectoryInputProcessor(
				worldController);
	}

	private void setDefaultInputProcessor() {
		worldController.setGameInputProcessor(defaultInputProcessor);
	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

	public InputProcessor getDefaultInputProcessor() {
		return defaultInputProcessor;
	}

	public InputProcessor getSelectFishInputProcessor() {
		return selectFishInputProcessor;
	}

	public InputProcessor getSelectTrajectoryInputProcessor() {
		return selectTrajectoryInputProcessor;
	}
}
