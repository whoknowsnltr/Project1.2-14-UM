package com.mygdx.game.menu.courseToPlay;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyActor;
import com.mygdx.game.bot.AStarBot;
import com.mygdx.game.bot.Node;
import com.mygdx.game.gameAdditions.DifferentFriction;
import com.mygdx.game.gameAdditions.Wind;
import com.mygdx.game.menu.MenuScreen;
import com.mygdx.game.obstacles.Obstacles;
import com.mygdx.physics.*;

import java.awt.*;
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
    Vector2d flag;
    Vector2d start;
    final MyActor ball = new MyActor(new Texture((Gdx.files.internal("ball.png"))), false);
    ArrayList<Obstacles> obstacles = new ArrayList<>();
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
    public static int Height = 640 * 2;
    public static int Width = 640 * 8 / 3;
    boolean running;
    double[][] friction = new double[Width][Height];


    /**
     * @param fileName is a name of the file in which we saved how the course should look like
     */
    public Course(String fileName) {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        // Again, style for buttons etc
        atlas = new TextureAtlas("uiskin.atlas");
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        stepSize = 0.1;


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

        for (int i = 0; i < Width; i += 30) {
            for (int j = 0; j < Height; j += 30) {
                sprites[i][j].setPosition(i, j);
                stage.addActor(sprites[i][j]);
            }
        }
        // Here we read the text file with all the information about course components, and we add them
        addCourseComponents(fileName);
        // Adding ball to the stage
        setFriction();
        stage.addActor(ball);



        /**
         * Here we define what happens when user clicks mouse on the ball
         */
        DragListener listener = new DragListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, final int button) {
                // Create new euler solver with known physical constants
                final Slider slider1 = new Slider(0, (int) maximum_velocity, 1, false, skin); //CHANGE THE VELOCITY HERE
                final Slider slider2 = new Slider(0, 360, 1, false, skin);

                final com.badlogic.gdx.scenes.scene2d.ui.Dialog dialog = new Dialog("Directions", skin, "dialog") {
                    public void result(Object obj) {
                    }
                };
                dialog.setHeight(250);
                dialog.setWidth(370);

                Table table = new Table();
                table.setPosition(0, 0);
                table.add(new Label("Velocity (m*10/s) :", skin));
                table.row();
                table.add(slider1);
                table.row();
                table.add(new Label("Direction :", skin));
                table.row();
                table.add(slider2);
                table.row();
                eulerSolver = new EulerSolver(stepSize, mass, gravitationalAcceleration, friction_coefficient);
                eulerSolver.set_step_size(stepSize);
                TextButton button3 = new TextButton("OK", skin);
                button3.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        double speed = slider1.getValue();
                        double direction = (slider2.getValue() * (3.1415 / 180)); // Convert from degrees to radians

                        velocity = new Vector2d((Math.sin(direction) * speed), (Math.cos(direction) * speed));
                        running = true;
                        Vector2d newPosition = new Vector2d(ball.getX(), ball.getY());
                        SequenceAction sequenceAction = new SequenceAction();

                        int i = 0;
                        while (running) {
                            i++;
                            newPosition = throwBall(newPosition);
                            // Move the ball every 20 steps, to prevent game from lagging
                            if (i % 20 == 0) {
                                Action action = Actions.moveTo((float) newPosition.get_x(), (float) newPosition.get_y(), (float) (stepSize));
                                sequenceAction.addAction(action);

                            }
                            if (Math.abs(velocity.get_y()) < 1 && Math.abs(velocity.get_x()) < 1) {
                                Action action = Actions.moveTo((float) newPosition.get_x(), (float) newPosition.get_y(), (float) (stepSize));
                                sequenceAction.addAction(action);
                                running = false;
                            }


                            dialog.hide();
                        }
                        ball.addAction(sequenceAction);

                    }
                });
                TextButton botButton = new TextButton("A* Bot", skin);
                botButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        int partition=30;
                        setTerrain(partition);
                        SequenceAction sequenceAction = new SequenceAction();
                        double distanceBallHole = Math.sqrt((ballCoord.get_x() - holeCoord.get_x()) * (ballCoord.get_x() - holeCoord.get_x()) + ((ballCoord.get_y() - holeCoord.get_y()) * (ballCoord.get_y() - holeCoord.get_y())));
                        Node ballCoords = new Node(new Node(), (int) ball.getX(), (int) ball.getY(), 0, 0);
                        Vector2d newPosition = new Vector2d(ball.getX(), ball.getY());
                        System.out.println( "LOG 1");
                        while (distanceBallHole > hole_tolerance) {
                            // Create new A* algorithm for each shoot to minimize error

                            Node holeCoords = new Node(new Node(), (int) holeCoord.get_x(), (int) holeCoord.get_y(), 0, 0);
                            AStarBot aStarBot = new AStarBot(maximum_velocity, formula, eulerSolver, hole_tolerance, partition, friction, stepSize, mass, gravitationalAcceleration);
                            aStarBot.setNodes(setTerrain(partition));
                            System.out.println( "LOG 2");
                            Vector2d move = aStarBot.appliedBots(ballCoords, holeCoords);
                            System.out.println( "LOG 3");
                            running = true;
                            velocity = new Vector2d(move.get_x(), move.get_y());
                            running = true;
                            // newPosition = new Vector2d(ball.getX(), ball.getY());


                            int i = 0;
                            while (running) {
                                i++;
                                newPosition = throwBall(newPosition);
                                // Move the ball every 20 steps, to prevent game from lagging
                                if (i % 20 == 0) {
                                    Action action = Actions.moveTo((float) newPosition.get_x(), (float) newPosition.get_y(), (float) (stepSize));
                                    sequenceAction.addAction(action);

                                }
                                if (Math.abs(velocity.get_y()) < 1 && Math.abs(velocity.get_x()) < 1) {
                                    Action action = Actions.moveTo((float) newPosition.get_x(), (float) newPosition.get_y(), (float) (stepSize));
                                    sequenceAction.addAction(action);
                                    running = false;
                                }
                            }
                            System.out.println("Ball after shot " + ball.getX() + " " + ball.getY());
                            distanceBallHole = Math.sqrt((ball.getX() - holeCoord.get_x()) * (ball.getX() - holeCoord.get_x()) + ((ball.getY() - holeCoord.get_y()) * (ball.getY() - holeCoord.get_y())));
                            ballCoords = new Node(new Node(), (int) ball.getX(), (int) ball.getY(), 0, 0);
                            dialog.hide();
                        }
                        ball.addAction(sequenceAction);

                    }
                });
                TextButton aBotButton = new TextButton("Human A* Bot", skin);
                aBotButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                      //  System.out.println("L>OG -1");
                        int partition = 100;
                        Node ballCoords = new Node(new Node(), (int) ball.getX(), (int) ball.getY(), 0, 0);
                        Node holeCoords = new Node(new Node(), (int) holeCoord.get_x(), (int) holeCoord.get_y(), 0, 0);
                       // System.out.println("L>OG 0 " + holeCoord.get_x());
                        AStarBot aStarBot = new AStarBot(maximum_velocity, formula, eulerSolver, hole_tolerance, partition, friction, stepSize, mass, gravitationalAcceleration);
                        long startT = System.currentTimeMillis();
                        aStarBot.setNodes(setTerrain(partition));
                      //  System.out.println("L>OG 1");
                        ArrayList<Vector2d> moves = aStarBot.appliedBotsHuman(ballCoords, holeCoords);
                        long endT = System.currentTimeMillis();
                        System.out.print("----------------------Algorithm ran for " + ((endT - startT) / 1000.) + " seconds---------------------- ");
                        double x1 =moves.get(moves.size()-1).get_x();
                        double y1 =moves.get(moves.size()-1).get_y();
                        double ratio = x1/y1;
                        if (ratio<0){
                            ratio=y1/x1;
                        }
                        moves.get(moves.size()-1).setX(x1*ratio*(1+hole_tolerance/200));
                        moves.get(moves.size()-1).setY(y1*ratio*(1+hole_tolerance/200));
                     //   System.out.println("L>OG 2");
                        running = true;
                        SequenceAction sequenceAction = new SequenceAction();
                        Vector2d newPosition = new Vector2d(ball.getX(), ball.getY());
                        for (Vector2d vector2d : moves) {
                            velocity = new Vector2d(vector2d.get_x(), vector2d.get_y());
                            running = true;
                            // newPosition = new Vector2d(ball.getX(), ball.getY());
                            int i = 0;
                            while (running) {
                                i++;
                                newPosition = throwBall(newPosition);
                                // Move the ball every 20 steps, to prevent game from lagging
                                if (i % 20 == 0) {
                                    Action action = Actions.moveTo((float) newPosition.get_x(), (float) newPosition.get_y(), (float) (stepSize));
                                    sequenceAction.addAction(action);
                                }
                                if (Math.abs(velocity.get_y()) < 1 && Math.abs(velocity.get_x()) < 1) {
                                    Action action = Actions.moveTo((float) newPosition.get_x(), (float) newPosition.get_y(), (float) (stepSize));
                                    sequenceAction.addAction(action);
                                    running = false;
                                }
                            }
                            dialog.hide();
                        }
                        ball.addAction(sequenceAction);
                    }
                });
                table.add(button3);
                table.row();
                table.add(botButton);
                table.row();
                table.add(aBotButton);
                //  table.setFillParent(true);

                dialog.add(table);
                dialog.setPosition(ball.getX(), ball.getY());
                stage.addActor(dialog);


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


        // Add some buttons
        TextButton back = new TextButton("Back to Menu", skin);
        back.addListener(new

                                 ClickListener() {
                                     @Override
                                     public void clicked(InputEvent event, float x, float y) {
                                         ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen(game));

                                     }

                                 });
        TextButton moves = new TextButton("Moves from file: ", skin);


        final TextField file = new TextField("", skin);
        moves.addListener(new

                                  ClickListener() {
                                      @Override
                                      public void clicked(InputEvent event, float x, float y) {
                                          ArrayList<Vector2d> arrayList = new ArrayList();
                                          PuttingCourse puttingCourse = new PuttingCourse();
                                          puttingCourse.readMoves(file.getText());
                                          arrayList = puttingCourse.getMove();

                                      }

                                  });
        mainTable.add(back);
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
     *
     * @param name
     */
    public void addCourseComponents(String name) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(name));
            String st = "";
            Stack stack = new Stack();

            while ((st = br.readLine()) != null) {
                String[] splited = st.split("\\s+");
                for (int i = 0; i < splited.length; i++) {
                    if (splited[i].equals("g")) {
                        i += 2;
                        String s = splited[i];
                        gravitationalAcceleration = Double.parseDouble(s.substring(0, s.length() - 1));
                    }
                    if (splited[i].equals("m")) {
                        i += 2;
                        String s = splited[i];
                        mass = Double.parseDouble(s.substring(0, s.length() - 1));
                    }
                    if (splited[i].equals("mu")) {
                        i += 2;
                        String s = splited[i];
                        friction_coefficient = Double.parseDouble(s.substring(0, s.length() - 1));
                    }
                    if (splited[i].equals("vmax")) {
                        i += 2;
                        String s = splited[i];
                        maximum_velocity = Double.parseDouble(s.substring(0, s.length() - 1));
                    }
                    if (splited[i].equals("tol")) {
                        i += 2;
                        String s = splited[i];
                        hole_tolerance = Double.parseDouble(s.substring(0, s.length() - 1));
                    }
                    if (splited[i].equals("start")) {
                        i += 2;
                        String s = splited[i].substring(1, splited[i].length() - 1);
                        i++;
                        String p = splited[i].substring(0, splited[i].length() - 2);
                        start = new Vector2d(Double.parseDouble(s), Double.parseDouble(p));
                        ball.setPosition((float) start.get_x(), (float) start.get_y());
                        ballCoord = new Vector2d((float) start.get_x(), (float) start.get_y());
                        // stage.addActor(ball);
                    }
                    if (splited[i].equals("goal")) {
                        MyActor actor = new MyActor(new Texture((Gdx.files.internal("hole.png"))), false);
                        actor.setName("hole");
                        i += 2;
                        String s = splited[i].substring(1, splited[i].length() - 1);
                        i++;
                        String p = splited[i].substring(0, splited[i].length() - 2);
                        flag = new Vector2d(Double.parseDouble(s), Double.parseDouble(p));
                        actor.setPosition((float) flag.get_x(), (float) flag.get_y());
                        holeCoord = new Vector2d((float) flag.get_x(), (float) flag.get_y());
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
                        obstacles.add(new Obstacles(ball, actor, friction_coefficient));
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
                        obstacles.add(new Obstacles(ball, actor, friction_coefficient));
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
                        obstacles.add(new Obstacles(ball, actor, friction_coefficient));
                        stage.addActor(actor);

                    }

                }

            }
            br.close();
            stage.addActor(ball);
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
                        i += 2;
                        for (int j = i; j < splited.length; j++) {
                            formula += splited[j];
                            formula += " ";
                        }
                        formula = formula.substring(0, formula.length() - 2);
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

    public Vector2d throwBall(Vector2d initialPosition) {
        Wind wind = new Wind(velocity, 0.1);

        double initialPositionX=initialPosition.get_x();
        double initialPositionY=initialPosition.get_y();
        if (initialPosition.get_x()>friction.length){
            initialPositionX = (friction.length-1);
        }
        if (initialPosition.get_y()>friction[0].length){
            initialPositionY = (friction[0].length-1);
        }
        if (initialPosition.get_x()<0){
            initialPositionX = (0);
        }
        if (initialPosition.get_y()<0){
            initialPositionY = (0);
        }
        double frictionValue = friction[(int) initialPositionX][(int) initialPositionY];
        velocity = new Vector2d(wind.applyWindToVelocity().get_x(), wind.applyWindToVelocity().get_y());

       // double frictionValue = friction[(int) initialPosition.get_x()][(int) initialPosition.get_y()];
        // Read the mathematical formula
        FunctionReader reader = new FunctionReader(formula);
        // Get initial position
        // Compute angles for x and y axis
        double angleX = reader.derivativeX(initialPosition);
        double angleY = reader.derivativeY(initialPosition);
        Rectangle ballBounds = new Rectangle((int) initialPosition.get_x(), (int) initialPosition.get_y(), (int) ball.getWidth(), (int) ball.getHeight());
        Vector2d endPosition;
        //      System.out.println("THROW BALL " + ballBounds.getX() + "  " + ballBounds.getY() + " ball " + ball.getX() + " " + ball.getY() );
        for (Obstacles obstacle : obstacles) {
            Vector2d obstacleVector = obstacle.collisionHandler(velocity, ballBounds, new Vector2d(initialPosition.get_x(), initialPosition.get_y()));
            if (obstacleVector.get_x() == 9999) {
            } else if (obstacleVector.get_y() == 9999) {
                // System.out.println(obstacle.toString());
                frictionValue = obstacleVector.get_x();
            } else {
                velocity = new Vector2d(obstacleVector.get_x(), obstacleVector.get_y());
                if (obstacle.getName().equals("pond")) {
                    endPosition = new Vector2d(obstacleVector.get_x(), obstacleVector.get_y());
                    velocity = new Vector2d(0, 0);
                    return endPosition;
                }
            }
        }

        // Compute velocity after a step of time
        Vector2d vector2d = hitWall(velocity, initialPosition);
        EulerSolver eulerSolver1 = new EulerSolver(stepSize, mass, gravitationalAcceleration, frictionValue);
        velocity = eulerSolver1.velocity(vector2d, angleX, angleY);
        // Compute position after a step of time
        endPosition = eulerSolver1.position(initialPosition, velocity);
        return endPosition;
    }

    /**
     * Method that defines what the ball does after collision with a wall
     *
     * @param initialVelocity
     * @return
     */
    public Vector2d hitWall(Vector2d initialVelocity, Vector2d position) {
        Vector2d velocityAfterCollision = new Vector2d(initialVelocity.get_x(), initialVelocity.get_y());
        // Check which wall did the ball hit
        if (position.get_x() <= 0 || position.get_x() >= Width) {
            velocityAfterCollision = new Vector2d((initialVelocity.get_x() * (-1)), initialVelocity.get_y());
        }
        if (position.get_y() <= 0 || position.get_y() >= Height) {
            velocityAfterCollision = new Vector2d(initialVelocity.get_x(), (initialVelocity.get_y() * (-1)));
        }
        //     System.out.println(velocityAfterCollision);
        return velocityAfterCollision;
    }

    public double getFriction_coefficient() {
        return friction_coefficient;
    }

    public void setFriction_coefficient(double friction_coefficient) {
        this.friction_coefficient = friction_coefficient;
    }

    public Vector2d getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2d velocity) {
        this.velocity = velocity;
    }

    /**
     * Method that sets the array that represents terrain, where pond or tree is -1, and 0 elsewhere
     */
    public int[][] setTerrain(double partition) {
        int[][] terrain = new int[(int) (Width/partition)][(int) (Height/partition)];
        for (int i = 0; i < terrain.length; i++) {
            for (int j = 0; j < terrain[0].length; j++) {
                terrain[i][j] = 0;
                for (Obstacles obstacle : obstacles) {
                    if (obstacle.getObstacleBounds().contains(i, j)) {
                        terrain[i][j] = -1;
                        if (i<terrain.length-1)
                            terrain[i+1][j] = -1;
                        if (i>0)
                            terrain[i-1][j] = -1;
                        if (j<terrain[0].length-1)
                            terrain[i][j+1] = -1;
                        if (j>0)
                            terrain[i][j-1] = -1;
                    }
                }
            }
        }
        return terrain;
    }

    /**
     * Method that saves different friction values
     */
    void setFriction(){
        DifferentFriction differentFriction = new DifferentFriction(friction_coefficient);
        differentFriction.setFrictionValues(0.05);
        friction=differentFriction.getFrictionValues();
    }
}
