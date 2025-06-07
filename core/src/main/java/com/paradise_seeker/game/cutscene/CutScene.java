package com.paradise_seeker.game.cutscene;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.paradise_seeker.game.main.Main;

public abstract class CutScene implements Screen {
    protected final Main game;
    protected Texture background;
    protected float cutsceneDuration; // Thời gian của cutscene, có thể được sử dụng trong lớp con
    protected String cutsceneText; // Văn bản của cutscene, có thể được sử dụng trong lớp con

    public CutScene(Main game, String backgroundPath, String cutsceneText, float cutsceneDuration) {
        this.game = game;
        this.background = new Texture(backgroundPath);
        this.cutsceneText = cutsceneText;
        this.cutsceneDuration = cutsceneDuration;
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);

        game.batch.begin();
        game.batch.draw(background, 0, 0, game.viewport.getWorldWidth(), game.viewport.getWorldHeight());
        drawCutsceneContent(delta);
        game.batch.end();

        handleInput();
    }

    // Các phương thức abstract để lớp con định nghĩa nội dung
    protected abstract void drawCutsceneContent(float delta);
    protected abstract void handleInput();

    @Override public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        background.dispose();
    }

	protected void onCutsceneEnd() {
		// TODO Auto-generated method stub
		
	}
}
