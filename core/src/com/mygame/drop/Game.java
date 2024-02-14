package com.mygame.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class Game extends ApplicationAdapter {
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private long lastDropTime;
	private int score;
	private BitmapFont font;
	private boolean gameOver;
	private static final int MAX_MISSED_DROPS = 5;
	private int dropsMissed;
	private GlyphLayout layout = new GlyphLayout();
	private enum GameState {
		START,
		RUNNING,
		GAME_OVER
	}
	private GameState gameState;
	private float spawnRate = 1.0f;
	private final float spawnRateGrowth = 1.01f;  //Change this to update the spawn rate of the droplets
	private long lastUpdateTime;
	private Rectangle restartButtonBounds;


	@Override
	public void create() {
		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));

		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		rainMusic.setLooping(true);
		rainMusic.setVolume(0.1f);
		rainMusic.play();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		bucket = new Rectangle();
		bucket.x = 800 / 2 - 64 / 2;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;

		raindrops = new Array<>();
		spawnRaindrop();

		score = 0;
		dropsMissed = 0;
		gameOver = false;

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(2);

		gameState = GameState.START;
		lastUpdateTime = TimeUtils.nanoTime();
	}

	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800 - 64);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void render() {
		ScreenUtils.clear(0, 0, 0.2f, 1);
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		switch (gameState) {
			case START:
				handleStartScreen();
				break;
			case RUNNING:
				handleGameLogic();
				break;
			case GAME_OVER:
				showGameOver();
				break;
		}
	}

	private void handleStartScreen() {
		batch.begin();
		layout.setText(font, "Tap anywhere to begin");
		font.draw(batch, "Tap anywhere to begin", (800 - layout.width) / 2, 240);
		batch.end();

		if (Gdx.input.isTouched()) {
			gameState = GameState.RUNNING;
		}
	}

	private void handleGameLogic() {
		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);
		for (Rectangle raindrop : raindrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		layout.setText(font, "Score: " + score);
		font.draw(batch, "Score: " + score, (800 - layout.width) / 2, 475);
		batch.end();

		if (!gameOver) {
			handleInput();
			updateRaindrops();
		}
	}

	private void handleInput() {
		if (Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - 64 / 2;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 300 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 300 * Gdx.graphics.getDeltaTime();

		if (bucket.x < 0) bucket.x = 0;
		if (bucket.x > 800 - 64) bucket.x = 800 - 64;
	}

	private void updateRaindrops() {
		if (TimeUtils.nanoTime() - lastDropTime > 1000000000 / spawnRate) {
			spawnRaindrop();
		}

		Iterator<Rectangle> iter = raindrops.iterator();
		while (iter.hasNext()) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if (raindrop.y + 64 < 0) {
				iter.remove();
				dropsMissed++;
				if (dropsMissed >= MAX_MISSED_DROPS) {
					gameOver = true;
					gameState = GameState.GAME_OVER;
					rainMusic.stop();
				}
			}
			if (raindrop.overlaps(bucket)) {
				dropSound.play();
				score++;
				iter.remove();
			}
		}

		// Update spawn rate every second based on the last update time
		if (TimeUtils.nanoTime() - lastUpdateTime > 1000000000) { // Every second
			spawnRate *= spawnRateGrowth; // Increase spawn rate by the growth factor
			lastUpdateTime = TimeUtils.nanoTime();
		}
	}

	private void showGameOver() {
		batch.begin();
		layout.setText(font, "Game Over! Score: " + score);
		font.draw(batch, "Game Over! Score: " + score, (800 - layout.width) / 2, 300);
		layout.setText(font, "Restart");
		float restartX = (800 - layout.width) / 2;
		float restartY = (250);
		font.draw(batch, "Restart", restartX, restartY);
		restartButtonBounds = new Rectangle(restartX, restartY - layout.height, layout.width, layout.height);
		batch.end();

		if (Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(),Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			if (restartButtonBounds.contains(touchPos.x, touchPos.y)) {
				gameState = GameState.START;
				restartGame();
			}
		}
	}

	private void restartGame() {
		gameOver = false;
		score = 0;
		dropsMissed = 0;
		raindrops.clear();
		spawnRate = 1.0f; // Reset the spawn rate
		lastUpdateTime = TimeUtils.nanoTime(); // Reset the update time
		spawnRaindrop();
		rainMusic.play();
	}

	@Override
	public void dispose() {
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		batch.dispose();
		font.dispose();

	}
}
