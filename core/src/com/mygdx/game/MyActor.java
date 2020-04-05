package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

/**
 * This class creates an object of Actor (pond, lake or tree) with a property that when it is clicked on, it moves alongside mouse
 */

public class MyActor extends Image {
    public MyActor(final Texture texture, final boolean setMovable) {
        super(texture);
        setBounds(getX(), getY(), getWidth(), getHeight());
        addListener(new DragListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            /**
             * This method provides that Actor moves alongside the mouse
             * @param event
             * @param x
             * @param y
             * @param pointer
             */
            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (setMovable) {
                    moveBy(x - getWidth() / 2, y - getHeight() / 2);
                }
            }
        });

    }

    public float getXcoords(){
        return getX();
    }
    public float getYcoords(){
        return getY();
    }

}