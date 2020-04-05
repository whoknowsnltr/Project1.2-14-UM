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
    Function2d height;
    Vector2d flag;
    Vector2d start;
    private World world;
    MyActor ball;
    //   Game game;
    //   EulerSolver eulerSolver;
    double stepSize;
    Vector2d velocity;
    float xCoord;
    float yCoord;
    String formula = "";
    EulerSolver eulerSolver;
    boolean isBallShot;

    Game game;
    Texture texture;
    Vector2d holeCoord;
    Vector2d ballCoord;
    double friction_coefficient;
    double maximum_velocity;
    double hole_tolerance;
    double gravitationalAcceleration;
    double mass;




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
        ball = new MyActor(new Texture((Gdx.files.internal("ball.png"))),false );


        // Here we read the text file with all the information about course components, and we add them
        addCourseComponents(fileName);

      /*  eulerSolver = new EulerSolver(stepSize, 30, 9.81, 0.31);
        eulerSolver.set_step_size(stepSize);
        velocity = new Vector2d(300, 1); */
        DragListener listener = new DragListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //  eulerSolver = new EulerSolver(stepSize, mass, gravitationalAcceleration, friction_coefficient);
                eulerSolver = new EulerSolver(stepSize, 300, 9.81, 31);
                eulerSolver.set_step_size(stepSize);
                velocity = new Vector2d(30, 15);
                ball.setColor(Color.CHARTREUSE);
                throwBall();

                return false;
            }



            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {

                ShapeRenderer shapeRenderer = new ShapeRenderer();
                shapeRenderer.setProjectionMatrix(camera.combined);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(1, 1, 1, 1);
                shapeRenderer.line(xCoord, yCoord, x, y);
                shapeRenderer.end();

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


        //  if (!eulerSolver.isMoving){


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




        //    ball.moveBy(1f, 0);
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
                    if (splited[i].equals("height")) {
                        i+=2;
                        for (int j = i; j<splited.length; j++){
                            formula+=splited[j];
                            formula+=" ";
                        }
                        formula = formula.substring(0, formula.length()-1);
                    }
                }
            }
            br.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }

     /**
      * This is function to throw a ball, but it does not work - we think it has to do with the eulersolver, but we are as of now not capable of fixing it
      */

    public void throwBall() {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean running = true;
              //  velocity = new Vector2d(3, 3);
                FunctionReader reader = new FunctionReader(formula);
                while (running) {
                        Vector2d initialPosition = new Vector2d(ball.getXcoords(), ball.getYcoords());
                        double angleX = reader.derivativeX(initialPosition);
                        double angleY = reader.derivativeY(initialPosition);
                        velocity = eulerSolver.velocity(velocity, angleX, angleY);
                        Vector2d endPosition = eulerSolver.position(initialPosition, velocity);
                     //  Vector2d endPosition = new Vector2d(initialPosition.get_x()+5, initialPosition.get_y()+5);
                        MoveToAction moveAction = new MoveToAction();
                        moveAction.setPosition((float) endPosition.get_x(), (float) endPosition.get_y());
                        moveAction.setDuration((float) stepSize);
                        ball.addAction(moveAction);
                    /*    if (velocity.get_y() < 10 && velocity.get_x() < 10) {
                            running = false;
                        }

                        if (eulerSolver.isNull) {
                            isBallShot = false;
                            ball.setColor(Color.BLUE);
                            running = false;
                        } */

                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
            }
        }});
        thread.start();

    }


}
