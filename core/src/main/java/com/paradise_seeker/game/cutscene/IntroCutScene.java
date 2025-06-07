package com.paradise_seeker.game.cutscene;

import com.badlogic.gdx.Gdx;
import com.paradise_seeker.game.main.Main;

public class IntroCutScene extends CutScene {
	float timer = 0f;

    public IntroCutScene(Main game) {
        super(game, "cutscene/Chapter 1/1.1.0.png" , "> Press SPACE to skip <", 5f);
    }

    @Override
    protected void onCutsceneEnd() {
        // Sau khi cutscene kết thúc, chuyển sang màn hình chơi game
        if (game.currentGame == null) {
            game.currentGame = new com.paradise_seeker.game.screen.GameScreen(game);
        }
        game.setScreen(game.currentGame);
    }
	@Override
	protected void drawCutsceneContent(float delta) {
		game.font.draw(game.batch, cutsceneText, 9f, 9.5f);
		
		timer += delta;
		if (timer >= cutsceneDuration) {
			onCutsceneEnd();
			return;
		}
	}

	@Override
	protected void handleInput() {
		if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
			onCutsceneEnd();
		}
		
	}
}
