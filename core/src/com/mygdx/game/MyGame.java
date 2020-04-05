package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.menu.MenuScreen;
import com.mygdx.game.menu.courseCreator.CourseCreator;

/**
 * this class is the basic for the game, we need to use it for now, for now it is enough that it is almost empty, maybe it will be more useful next time
 */

public class MyGame extends Game {

    @Override
    public void create() {
        this.setScreen(new MenuScreen(this));

    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

}