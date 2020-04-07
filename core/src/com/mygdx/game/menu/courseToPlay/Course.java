package com.mygdx.game.menu.courseToPlay;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyActor;
import com.mygdx.game.menu.MenuScreen;
import com.mygdx.physics.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * This class creates a course, according to a course that we made beforehand using course creator
 */

public class Course implements Screen {
    private SpriteBatch batch;
    private Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;
    private TextureAtlas atlas;
    private Skin skin;
    private TextureRegion backgroundTexture;
    private Texture img;
    private TiledMapRenderer tiledMapRenderer;
    private TiledMap tiledMap;
    private InputProcessor processor;
    String fileName;
    Vector2d flag;
    Vector2d start;
    MyActor ball;
    double stepSize;
    Vector2d velocity;
    float xCoord;
    float yCoord;
    String formula = "";
    EulerSolver eulerSolver;
    Game game;
    Texture texture;
    Vector2d holeCoord;
    Vector2d ballCoord;
    double friction_coefficient;
    double maximum_velocity;
    double hole_tolerance;
    double gravitationalAcceleration;
    double mass;
    private BackgroundHeightDifference backgroundMap;
    public static int Height = 640*2;
    public static int Width = 640*8/3;




    /**
     *
     * @param fileName is a name of the file in which we saved how the course should look like
     */
    public Course(String fileName) {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        // Again, style for buttons etc
        atlas = new TextureAtlas("uiskin.atlas");
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        stepSize = 0.01;


        camera = new OrthographicCamera();
        camera.setToOrtho(false, w, h);
        camera.update();
        // The "field" background
        tiledMap = new TmxMapLoader().load("tiles.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        ScreenViewport viewport = new ScreenViewport();
        stage = new Stage(viewport);
        addFormula(fileName);

        backgroundMap = new BackgroundHeightDifference(formula);
        MyActor[][] sprites = backgroundMap.getSpriteMapList();

        for (int i = 0; i< Width; i+=30) {
            for (int j = 0; j < Height; j+=30) {
                sprites[i][j].setPosition(i, j);
                stage.addActor(sprites[i][j]);
            }
        }

        // Adding ball to the stage
        ball = new MyActor(new Texture((Gdx.files.internal("ball.png"))),false );

        // Here we read the text file with all the information about course components, and we add them
        addCourseComponents(fileName);

        /**
         * Here we define what happens when user clicks mouse on the ball
         */
        DragListener listener = new DragListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // Create new euler solver with known physical constants
                eulerSolver = new EulerSolver(stepSize, mass, gravitationalAcceleration, friction_coefficient);
                eulerSolver.set_step_size(stepSize);
                // This should be user input, of the velocity, for now since user can't input anuthing, it stays for debug purposes
                velocity = new Vector2d(300, 15);
                // Method described at the bottom of class
                throwBall();
                return false;
            }

        };
        // }
        ball.addListener(listener);
        final Table mainTable = new Table();
        //Set table to fill stage
        mainTable.setFillParent(true);
        //Set alignment of contents in the table.
        mainTable.top();
        stage.addActor(ball);

        // Add some buttons
        TextButton back = new TextButton("Back to Menu", skin);
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen(game));

            }

        });
        TextButton moves = new TextButton("Moves from file: ", skin);


        final TextField file = new TextField("", skin);
        moves.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ArrayList<Vector2d> arrayList = new ArrayList();
                PuttingCourse puttingCourse = new PuttingCourse();
                puttingCourse.readMoves(file.getText());
                arrayList = puttingCourse.getMove();
                //     velocity = arrayList.get(0);
                //    throwBall();

            }

        });
        mainTable.add(back);
        //  mainTable.add(moves);
        mainTable.add(file);
        mainTable.add(moves);
        stage.addActor(mainTable);


        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        stage.act(Gdx.graphics.getDeltaTime());

        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        skin.dispose();
        atlas.dispose();
        batch.dispose();
        img.dispose();
    }

    /**
     * In this method we read which components should be where on the screen and we add them
     * @param name
     */
    public void addCourseComponents(String name) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(name));
            String st = "";


            while ((st = br.readLine()) != null) {
                String[] splited = st.split("\\s+");
                for (int i = 0; i < splited.length; i++) {
                    if (splited[i].equals("g")) {
                        i+=2;
                        String s = splited[i];
                        gravitationalAcceleration = Double.parseDouble(s.substring(0, s.length()-1));
                    }
                    if (splited[i].equals("m")) {
                        i+=2;
                        String s = splited[i];
                        mass = Double.parseDouble(s.substring(0, s.length()-1));
                    }
                    if (splited[i].equals("mu")) {
                        i+=2;
                        String s = splited[i];
                        friction_coefficient = Double.parseDouble(s.substring(0, s.length()-1));
                    }
                    if (splited[i].equals("vmax")) {
                        i+=2;
                        String s = splited[i];
                        maximum_velocity = Double.parseDouble(s.substring(0, s.length()-1));
                    }
                    if (splited[i].equals("tol")) {
                        i+=2;
                        String s = splited[i];
                        hole_tolerance = Double.parseDouble(s.substring(0, s.length()-1));
                    }
                    if (splited[i].equals("start")) {
                  /*      MyActor actor = new MyActor(new Texture((Gdx.files.internal("ball.png"))), false);
                        actor.setName("ball"); */
                        i+=2;
                        String s = splited[i].substring(1, splited[i].length()-1);
                        i++;
                        String p = splited[i].substring(0, splited[i].length()-2);
                        start = new Vector2d(Double.parseDouble(s), Double.parseDouble(p));
                        ball.setPosition((float)start.get_x(), (float)start.get_y());
                        ballCoord = new Vector2d((float)start.get_x(), (float)start.get_y());
                        stage.addActor(ball);
                    }
                    if (splited[i].equals("goal")) {
                        MyActor actor = new MyActor(new Texture((Gdx.files.internal("tree.png"))), false);
                        actor.setName("hole");
                        i+=2;
                        String s = splited[i].substring(1, splited[i].length()-1);
                        i++;
                        String p = splited[i].substring(0, splited[i].length()-2);
                        flag = new Vector2d(Double.parseDouble(s), Double.parseDouble(p));
                        actor.setPosition((float)flag.get_x(), (float)flag.get_y());
                        holeCoord = new Vector2d((float)flag.get_x(), (float)flag.get_y());
                        stage.addActor(actor);
                    }
                    if (splited[i].equals("Pond")) {
                        MyActor actor = new MyActor(new Texture((Gdx.files.internal("pond.png"))), false);
                        actor.setName("pond");
                        i++;
                        float xCoords = Float.parseFloat(splited[i]);
                        i++;
                        float yCoords = Float.parseFloat(splited[i]);
                        actor.setPosition(xCoords, yCoords);
                        stage.addActor(actor);

                    }
                    if (splited[i].equals("Sand")) {
                        MyActor actor = new MyActor(new Texture((Gdx.files.internal("sand.png"))), false);
                        actor.setName("sand");
                        i++;
                        float xCoords = Float.parseFloat(splited[i]);
                        i++;
                        float yCoords = Float.parseFloat(splited[i]);
                        actor.setPosition(xCoords, yCoords);
                        stage.addActor(actor);

                    }
                    if (splited[i].equals("Tree")) {
                        MyActor actor = new MyActor(new Texture((Gdx.files.internal("tree.png"))), false);
                        actor.setName("tree");
                        i++;
                        float xCoords = Float.parseFloat(splited[i]);
                        i++;
                        float yCoords = Float.parseFloat(splited[i]);
                        actor.setPosition(xCoords, yCoords);
                        stage.addActor(actor);

                    }
                }
            }
            br.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }
    public void addFormula(String name) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(name));
            String st = "";


            while ((st = br.readLine()) != null) {
                String[] splited = st.split("\\s+");
                for (int i = 0; i < splited.length; i++) {
                    if (splited[i].equals("height")) {
                        i+=2;
                        for (int j = i; j<splited.length; j++){
                            formula+=splited[j];
                            formula+=" ";
                        }
                        formula = formula.substring(0, formula.length()-2);
                    }
                }
            }
            br.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * This is function to throw a ball, but it does not work - we think it has to do with the eulersolver, but we are as of now not capable of
     * fixing it
     */

    public void throwBall() {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean running = true;
                // Read the mathematical formula
                FunctionReader reader = new FunctionReader(formula);
                while (running) {
                    // Get initial position
                    Vector2d initialPosition = new Vector2d(ball.getXcoords(), ball.getYcoords());
                    // Compute angles for x and y axis
                    double angleX = reader.derivativeX(initialPosition);
                    double angleY = reader.derivativeY(initialPosition);
                    // Compute velocity after a step of time
                    velocity = eulerSolver.velocity(velocity, angleX, angleY);
                    // Compute position after a step of time
                    Vector2d endPosition = eulerSolver.position(initialPosition, velocity);
                    // Create action for the ball
                    MoveToAction moveAction = new MoveToAction();
                    // Define where to move the ball
                    moveAction.setPosition((float) endPosition.get_x(), (float) endPosition.get_y());
                    // Define how long this should take (step size)
                    moveAction.setDuration((float) stepSize);
                    // Add action
                    ball.addAction(moveAction);
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            }});
        thread.start();

    }


}
