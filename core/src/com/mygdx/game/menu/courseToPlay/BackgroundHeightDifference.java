package com.mygdx.game.menu.courseToPlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygdx.game.MyActor;
import com.mygdx.physics.FunctionReader;
import com.mygdx.physics.Vector2d;

/**
 * This class paints a tinted overlay on the background that shows the height difference of the terrain
 */

public class BackgroundHeightDifference {

    // Dimentions of the background
    public static int Height = 640 * 2;
    public static int Width = 640 * 8 / 3;
    // Minimum and maximum values of the function for the background terrain
    private double minimum = 0;
    private double maximum = 0;
    private MyActor[][] spriteMapList = new MyActor[Width][Height]; // This array contains sprites that are tinted for an appropriate shade depending on the height difference
    private double[][] heightValues = new double[Width][Height]; // This array contains the height values

    /**
     * Constructor, it immediately calls its two methods, therefore they don't need to be called again when we want to use them
     * @param formula
     */
    public BackgroundHeightDifference(String formula) {
        setBackgroundArray(formula);
        setColour();
    }

    /**
     * A method that computes the height values for given places on the field and finds maximum and minimum value
     * @param formula
     */
    public void setBackgroundArray(String formula) {
        FunctionReader reader = new FunctionReader(formula);
        for (int i = 0; i < Width; i += 30) {
            for (int j = 0; j < Height; j += 30) {
                Vector2d vector2d = new Vector2d(i, j);
                double height = reader.evaluate(vector2d);
                heightValues[i][j] = height;
                if (height == Math.min(height, minimum)) {
                    minimum = height;
                }
                if (height == Math.max(height, maximum)) {
                    maximum = height;
                }
            }
        }
    }

    /**
     * A method that sets the appropriate shade for the height
     */
    public void setColour() {
        TiledMap map = new TiledMap();
        TiledMapTileLayer mapLayer = new TiledMapTileLayer(Width, Height, 30, 30);
        map.getLayers().add(mapLayer);
        for (int row = 0; row < Width; row += 30) {
            for (int col = 0; col < Height; col += 30) {
                MyActor sprite = new MyActor(new Texture(Gdx.files.internal("pixel.png")), false);
                float colourOverlay = (float) ((heightValues[row][col] - minimum) / (maximum - minimum));
                sprite.setColor(colourOverlay, colourOverlay, colourOverlay, 0.5f);
                spriteMapList[row][col] = sprite;
            }
        }
    }

    /**
     * A getter method for spriteMapList
     * @return
     */
    public MyActor[][] getSpriteMapList() {
        return spriteMapList;
    }
}

