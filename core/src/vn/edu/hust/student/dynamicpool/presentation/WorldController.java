package vn.edu.hust.student.dynamicpool.presentation;

import java.util.List;

import vn.edu.hust.student.dynamicpool.GameCenter;
import vn.edu.hust.student.dynamicpool.bll.HostBusinessLogicLayerImpl;
import vn.edu.hust.student.dynamicpool.bll.model.ETrajectoryType;
import vn.edu.hust.student.dynamicpool.bll.model.Fish;
import vn.edu.hust.student.dynamicpool.bll.model.FishType;
import vn.edu.hust.student.dynamicpool.bll.model.host.HostPoolManager;
import vn.edu.hust.student.dynamicpool.events.EventDestination;
import vn.edu.hust.student.dynamicpool.events.EventType;
import vn.edu.hust.student.dynamicpool.presentation.assets.Assets;
import vn.edu.hust.student.dynamicpool.presentation.gameobject.FishUICollection;
import vn.edu.hust.student.dynamicpool.presentation.gameobject.FishUIFactory;
import vn.edu.hust.student.dynamicpool.presentation.screen.DeviceInfoScreen;
import vn.edu.hust.student.dynamicpool.presentation.screen.ErrorInputProcessor;
import vn.edu.hust.student.dynamicpool.presentation.screen.ErrorScreen;
import vn.edu.hust.student.dynamicpool.presentation.screen.GameScreen;
import vn.edu.hust.student.dynamicpool.presentation.screen.LoadingScreen;
import vn.edu.hust.student.dynamicpool.presentation.screen.MainMenuScreen;
import vn.edu.hust.student.dynamicpool.presentation.screen.SplashScreen;
import vn.edu.hust.student.dynamicpool.utils.AppConst;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.eposi.eventdriven.Event;
import com.eposi.eventdriven.implementors.BaseEventListener;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

public class WorldController {
	private Timer timer = new Timer();
	private GameCenter game = null;
	private HostBusinessLogicLayerImpl hostBusinessLogicLayer = null;
	private SplashScreen splashScreen = null;
	private MainMenuScreen mainMenuScreen = null;
	private LoadingScreen loadingScreen = null;
	private GameScreen gameScreen = null;
	private DeviceInfoScreen deviceInfoScreen;
	private ErrorScreen errorScreen = null;
	private FishUICollection fishUICollection = new FishUICollection();
	private int addingFishStep = 0;
	private FishType selectedFishType = FishType.FISH1;
	private float size;
	private boolean isShowingSetting = false;

	public WorldController(GameCenter game) {
		this.game = game;
		this.registerEvents();
	}

	private void registerEvents() {
		EventDestination.getInstance().addEventListener(EventType.APP_ERROR,
				new BaseEventListener(this, "onErrorCallbackHander"));
		EventDestination.getInstance().addEventListener(
				EventType.BLL_BEGIN_SETTING,
				new BaseEventListener(this, "onBeginSettingCallbackHander"));
	}

	public void init() {
		showSplashScreen();
	}

	public void showSplashScreen() {
		WorldRenderer worldRenderer = game.getWorldRenderer();
		splashScreen = new SplashScreen(worldRenderer);
		game.setScreen(splashScreen);
		waitFewSecondsAndShowMenus();
	}

	private void waitFewSecondsAndShowMenus() {
		timer.scheduleTask(new Task() {
			@Override
			public void run() {
				showMainMenuScreen();
			}
		}, AppConst.DELAY_TIME);
		loadMainMenuScreenResources();
		loadLoadingResources();
	}

	public void showMainMenuScreen() {
		game.setScreen(mainMenuScreen);
	}

	private void loadMainMenuScreenResources() {
		Assets.instance.initMainMenuAssets();
		WorldRenderer worldRenderer = game.getWorldRenderer();
		mainMenuScreen = new MainMenuScreen(worldRenderer, this);
	}

	private void loadLoadingResources() {
		Assets.instance.initLoadingAssets();
		WorldRenderer worldRenderer = game.getWorldRenderer();
		loadingScreen = new LoadingScreen(worldRenderer);
	}

	private void showGameScreen() {
		game.setScreen(gameScreen);
	}

	private void showLoadingScreen() {
		game.setScreen(loadingScreen);
	}

	private void showFullScreen() {
		// DisplayMode desktopDisplayMode =
		// Gdx.graphics.getDesktopDisplayMode();
		// Gdx.graphics.setDisplayMode(desktopDisplayMode.width,
		// desktopDisplayMode.height, true);
	}

	public void createHost() {
		createHostBusinessLogicLayer();
		EventDestination.getInstance().addEventListener(
				EventType.BLL_CREATE_HOST,
				new BaseEventListener(this, "onCreateHostCallbackHander"));
		showLoadingScreen();
		this.hostBusinessLogicLayer.createHost();
	}

	private void createHostBusinessLogicLayer() {
		this.hostBusinessLogicLayer = new HostBusinessLogicLayerImpl();
	}

	@Deprecated
	public void onCreateHostCallbackHander(Event event) {
		if (EventDestination.parseEventToBoolean(event)) {
			loadDeviceInfoScreenResource();
			showDeviceInforScreen();
			loadHostGameResources();
		}
	}

	private void loadHostGameResources() {
		Assets.instance.initGameAssets();
		WorldRenderer worldRenderer = game.getWorldRenderer();
		HostPoolManager hostPoolManager = hostBusinessLogicLayer
				.getHostPoolManager();
		gameScreen = new GameScreen(worldRenderer, this, hostPoolManager);
	}

	private void loadDeviceInfoScreenResource() {
		WorldRenderer worldRenderer = game.getWorldRenderer();
		deviceInfoScreen = new DeviceInfoScreen(worldRenderer, this);
	}

	private void showDeviceInforScreen() {
		game.setScreen(deviceInfoScreen);
	}

	public void enterScreenSizeDone() {
		showFullScreen();
		hostBusinessLogicLayer.updateDeviceInfo(AppConst.width,
				AppConst.height, size);
		showGameScreen();
	}

	public void onAddDeviceCallbackHander(Event event) {
		showGameScreen();
	}

	public boolean isValidScreenSize(String screenSize) {
		try {
			size = Float.parseFloat(screenSize);
			if (size <= 0 || size > 30)
				return false;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public FishUICollection getFishUICollection() {
		return fishUICollection;
	}

	public List<Fish> getFishs() {
		List<Fish> fishs = hostBusinessLogicLayer.getFishes();
		return fishs;
	}

	public void updateFishsCordinate(float deltaTime) {
		hostBusinessLogicLayer.update(deltaTime + 0.05f);
	}

	public void exit() {
		hostBusinessLogicLayer.exit();
		Gdx.app.exit();
	}

	public void addFishButtonClick() {
		if (isShowingSetting)
			return;
		if (this.addingFishStep == 0) {
			this.addingFishStep = 1;
		} else {
			this.addingFishStep = 0;
		}
		InputProcessor selectFishInputProcessor = gameScreen
				.getSelectFishInputProcessor();
		this.setGameInputProcessor(selectFishInputProcessor);
	}

	public boolean isShowSelectFishButtons() {
		return addingFishStep == 1;
	}

	public void setGameInputProcessor(InputProcessor inputProcessor) {
		Gdx.input.setInputProcessor(inputProcessor);
	}

	public void cancelAddFish() {
		this.addingFishStep = 0;
		this.selectedFishType = FishType.FISH1;
	}

	public void selectFish(FishType fishType) {
		this.selectedFishType = fishType;
		this.addingFishStep = 2;
		InputProcessor selectTrajectoryInputProcessor = gameScreen
				.getSelectTrajectoryInputProcessor();
		this.setGameInputProcessor(selectTrajectoryInputProcessor);
	}

	public boolean isShowSelectTrajectoryButtons() {
		return addingFishStep == 2;
	}

	public void selectTrajectory(ETrajectoryType trajectoryType) {
		createFish(selectedFishType, trajectoryType);
		cancelAddFish();
	}

	private void createFish(FishType fishType, ETrajectoryType trajectoryType) {
		hostBusinessLogicLayer.createFish(fishType, trajectoryType,
				FishUIFactory.getWith(fishType),
				FishUIFactory.getHeight(fishType));
	}

	public String getKey() {
		return hostBusinessLogicLayer.getKeyOfHost();
	}

	@Deprecated
	public void onErrorCallbackHander(Event event) {
		if (EventDestination.parseEventToBoolean(event)) {
			Object object = EventDestination.parseEventToTargetObject(event);
			String errorMessage = object == null ? "App error" : object
					.toString();
			showErrorScreen(errorMessage);
		}
	}

	private void showErrorScreen(String errorMessage) {
		if (this.errorScreen == null) {
			loadErrorScreen();
		}
		errorScreen.showError(errorMessage);
		game.setScreen(errorScreen);
		this.setGameInputProcessor(new ErrorInputProcessor(this));
	}

	private void loadErrorScreen() {
		errorScreen = new ErrorScreen(game.getWorldRenderer());
	}

	@Deprecated
	public void onBeginSettingCallbackHander(Event event) {
		this.isShowingSetting = true;
		cancelAddFish();
		setGameInputProcessor(gameScreen.getSettingInputProcessor());
		showSettingGuideMessage(AppConst.SETTING_GUID_TEXT);
	}

	public boolean isShowingSetting() {
		return isShowingSetting;
	}

	public void tryUpdateSettingForClient() {
		HostPoolManager hostPoolManager = hostBusinessLogicLayer
				.getHostPoolManager();
		if (hostPoolManager.isValidSetting()) {
			showSettingGuideMessage(AppConst.SETTING_UPDATING_MESSAGE);
			hostBusinessLogicLayer.saveUpdateSettingForAllClient();
			timer.scheduleTask(new Task() {
				@Override
				public void run() {
					showSettingGuideMessage(null);
					hiddenSetting();
				}
			}, 1);
			timer.start();
		} else {
			showSettingGuideMessage(AppConst.SETTING_INVALID_TEXT);
			timer.scheduleTask(new Task() {
				@Override
				public void run() {
					showSettingGuideMessage(AppConst.SETTING_GUID_TEXT);
				}
			}, 1);
			timer.start();
		}
	}

	protected void hiddenSetting() {
		isShowingSetting = false;
		setGameInputProcessor(gameScreen.getDefaultInputProcessor());
	}

	private void showSettingGuideMessage(String message) {
		if (gameScreen != null) {
			if (message == null) {
				gameScreen.hideMessage();
			} else {
				gameScreen.showMessage(message);
			}
		}
	}
}