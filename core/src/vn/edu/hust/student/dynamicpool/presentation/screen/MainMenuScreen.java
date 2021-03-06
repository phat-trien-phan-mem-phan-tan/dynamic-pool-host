package vn.edu.hust.student.dynamicpool.presentation.screen;

import vn.edu.hust.student.dynamicpool.presentation.WorldController;
import vn.edu.hust.student.dynamicpool.presentation.WorldRenderer;
import vn.edu.hust.student.dynamicpool.presentation.assets.AssetMainMenuScreen;
import vn.edu.hust.student.dynamicpool.presentation.assets.Assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MainMenuScreen implements Screen {
	private WorldRenderer worldRenderer = null;
	private AssetMainMenuScreen mainMenuAssets = null;
	private Stage stage = new Stage();
	private Table table = new Table();
	private ImageButton createHostButton = null;
	private WorldController worldController;

	public MainMenuScreen(WorldRenderer worldRenderer,
			WorldController worldController) {
		this.worldRenderer = worldRenderer;
		this.worldController = worldController;
	}

	@Override
	public void render(float delta) {
		worldRenderer.beginRender();
		stage.act();
		stage.draw();
		worldRenderer.endRender();
	}

	@Override
	public void resize(int width, int height) {
		worldRenderer.resize(width, height);
	}

	@Override
	public void show() {
		mainMenuAssets = Assets.instance.mainMenuScreen;
		initCreateButton();
		initBackground();
		configureTableAndStage();
	}

	private void initCreateButton() {
		TextureRegionDrawable creatHostImageUp = mainMenuAssets
				.getCreateHostDrawable();
		createHostButton = new ImageButton(creatHostImageUp);
		addCreateClickListener();
		table.add(createHostButton).row();
	}

	private void addCreateClickListener() {
		createHostButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				createHostClickHander();
			}
		});
	}

	protected void createHostClickHander() {
		worldController.createHost();
	}

	private void initBackground() {
		TextureRegionDrawable background = mainMenuAssets
				.getMainMenuBackgroundDrawable();
		table.setBackground(background);
	}

	private void configureTableAndStage() {
		table.setFillParent(true);
		stage.addActor(table);
		Gdx.input.setInputProcessor(stage);
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
}