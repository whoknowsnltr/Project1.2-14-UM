package com.mygdx.game.menu.courseCreator;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.StringBuilder;
import com.mygdx.physics.Vector2d;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyActor;
import com.mygdx.game.menu.MenuScreen;


import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

public class CourseCreator implements Screen {
    /**
     * This is the screen that allows for creating custom course.
     * It allows to save map, and also input a formula for height
     */

    private Sound sound;
    private Sprite sprite;
    private SpriteBatch batch;
    private Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;
    private TextureAtlas atlas;
    private Skin skin;
    private TextureRegion backgroundTexture;
    private Texture img;
    String formula;
    Game game;
    private TiledMapRenderer tiledMapRenderer;
    private TiledMap tiledMap;
    private boolean toRemove = false; // if it is true, then if we click on lake/sand/tree, it will be removed. if it is false, nothing will happen
    private InputProcessor processor;
    ArrayList<MyActor> ponds = new ArrayList<>();
    ArrayList<MyActor> sands = new ArrayList<>();
    ArrayList<MyActor> trees = new ArrayList<>();
    private Label label;
public int[] holeCoordinates = {-20,-20,-1};
    public int[] ballCoordinates = {-400,-400,-2};
    double friction_coefficient;
    double maximum_velocity;
    double hole_tolerance;
    double gravitationalAcceleration;
    double mass_of_ball;
    MyActor ball = new MyActor(new Texture((Gdx.files.internal("ball.png"))), false);



    public CourseCreator() {

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        // Again, style for buttons etc
        atlas = new TextureAtlas("uiskin.atlas");
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, w, h);
        camera.update();
        // The "field" background
        tiledMap = new TmxMapLoader().load("tiles.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);


        ScreenViewport viewport = new ScreenViewport();
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);



    }


    @Override
    public void show() {
        // We override some methods in InputProcessor -> if a pond/lake/tree is clicked, and button "remove" was clicked before, remove it

        processor = new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            /**
             * This is the remove method to remove pond/sand/tree after button "remove" was pressed
             * @param screenX
             * @param screenY
             * @param pointer
             * @param button
             * @return
             */
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector2 coord = stage.screenToStageCoordinates(new Vector2((float) screenX, (float) screenY));
                Actor hitActor = stage.hit(coord.x, coord.y, false);

                if (toRemove) {
                    if (hitActor == null || hitActor.getName() == null) {
                        toRemove = false;
                    } else if (hitActor.getName().equals("pond")) {
                        ponds.remove(hitActor);
                        hitActor.remove();
                    } else if (hitActor.getName().equals("sand")) {
                        sands.remove(hitActor);
                        hitActor.remove();
                    } else if (hitActor.getName().equals("tree")) {
                        trees.remove(hitActor);
                        hitActor.remove();
                    }
                }

                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(int amount) {
                return false;
            }
        };
        // InputMultiplexer allows for multiple input processors. 1 is the stage ( so that we can click and interact with buttons from this class)
        // 2 are Actors (pond, tree, sand), so that we can click on them and remove them
        InputMultiplexer multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);
        multiplexer.addProcessor(processor);
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);

        //Create Table
        final Table mainTable = new Table();
        //Set table to fill stage
        mainTable.setFillParent(true);
        //Set alignment of contents in the table.
        mainTable.top();


        //Create buttons
        TextButton addPond = new TextButton("Add pond", skin);
        TextButton addSand = new TextButton("Add sand", skin);
        TextButton addTree = new TextButton("Add tree", skin);
        TextButton setHole = new TextButton("Set hole", skin);
        TextButton setBall = new TextButton("Set ball", skin);
        TextButton remove = new TextButton("Remove", skin);
        TextButton save = new TextButton("Save", skin);
        TextButton back = new TextButton("Back to menu", skin);
        final Dialog dialog2 = new Dialog("Physical constants", skin);
        dialog2.setBounds(stage.getWidth()/2, stage.getHeight()/2, 300, 300);

        setHole.addListener(new ClickListener() {
                                @Override
                                public void clicked(InputEvent event, float x, float y) {
                                    final Dialog dialog1 = new Dialog("Set hole", skin);

                                    dialog1.setBounds(stage.getWidth()/2, stage.getHeight()/2, 300, 300);
                                    stage.addActor(dialog1);
                                    Label labelX = new Label("X coordinates:", skin);
                                    final TextField coordsX = new TextField("0", skin);
                                    Label labelY = new Label("Y coordinates:", skin);
                                    final TextField coordsY = new TextField("0", skin);
                                    Label labelZ = new Label("Set hole tolerance:", skin);
                                    final TextField tolerance = new TextField("0", skin);
                                    TextButton ok = new TextButton("OK", skin);
                                    holeCoordinates = new int[3];
                                    ok.addListener(new ClickListener() {
                                        @Override
                                        public void clicked(InputEvent event, float x, float y) {
                                            holeCoordinates[0] = Integer.parseInt(coordsX.getText());
                                            holeCoordinates[1] = Integer.parseInt(coordsY.getText());
                                            holeCoordinates[2] = Integer.parseInt(tolerance.getText());
                                            MyActor hole = new MyActor(new Texture((Gdx.files.internal("hole1.png"))), false);
                                            hole.setName("hole");
                                            hole.setPosition(holeCoordinates[0], holeCoordinates[1]);
                                            stage.addActor(hole);
                                            dialog1.hide();

                                        }
                                    });
                                    Table table1 = new Table();
                                    table1.add(labelX);
                                    table1.add(coordsX);
                                    table1.row();
                                    table1.add(labelY);
                                    table1.add(coordsY);
                                    table1.row();
                                    table1.add(labelZ);
                                    table1.add(tolerance);
                                    table1.row();
                                    table1.add(ok);
                                    table1.setFillParent(true);
                                    dialog1.addActor(table1);
                                };
                            });
        setBall.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final Dialog dialog1 = new Dialog("Set ball", skin);

                dialog1.setBounds(stage.getWidth()/2, stage.getHeight()/2, 300, 300);
                stage.addActor(dialog1);
                Label labelX = new Label("X coordinates:", skin);
                final TextField coordsX = new TextField("0", skin);
                Label labelY = new Label("Y coordinates:", skin);
                final TextField coordsY = new TextField("0", skin);
                TextButton ok = new TextButton("OK", skin);
               ballCoordinates = new int[3];

                ok.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        ballCoordinates[0] = Integer.parseInt(coordsX.getText());
                        ballCoordinates[1] = Integer.parseInt(coordsY.getText());
                        ballCoordinates[2]=holeCoordinates[2];
                        ball.setName("hole");
                        ball.setPosition(ballCoordinates[0], ballCoordinates[1]);
                        stage.addActor(ball);
                        dialog1.hide();

                    }
                });
                Table table1 = new Table();
                table1.add(labelX);
                table1.add(coordsX);
                table1.row();
                table1.add(labelY);
                table1.add(coordsY);
                table1.row();
                table1.add(ok);
                table1.setFillParent(true);
                dialog1.addActor(table1);
            };
        });
        //Add listeners to buttons
        /**
         * Here what we will do is for each button Add x, where x is pond, sand or tree, if we click that button, another pond, sand or tree appears on the screen
         */
        addPond.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MyActor actor = new MyActor(new Texture((Gdx.files.internal("pond.png"))), true);
                actor.setName("pond");
                stage.addActor(actor);
                ponds.add(actor);

            }

        });
        addSand.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MyActor actor = new MyActor(new Texture((Gdx.files.internal("sand.png"))), true);
                actor.setName("sand");
                stage.addActor(actor);
                sands.add(actor);
            }
        });
        addTree.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MyActor actor = new MyActor(new Texture((Gdx.files.internal("tree.png"))), true);
                actor.setName("tree");
                stage.addActor(actor);
                trees.add(actor);
            }
        });
        /**
         * This method is called if the "remove" button is clicked. Then, if we click on an actor, it removes it
         */
        remove.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toRemove = true;

            }

        });
        /**
         * This method returns to the main menu screen
         */
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen(game));

            }

        });
        save.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final TextField textField = new TextField("", skin);
                textField.setPosition(24, 73);
                textField.setSize(88, 14);
                mainTable.add(textField);
                final TextButton ok = new TextButton("OK", skin);
                mainTable.add(ok);
                ok.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        String name = textField.getText();
                        SaveCourse course = new SaveCourse(friction_coefficient, maximum_velocity,
                                hole_tolerance,
                                gravitationalAcceleration,
                                mass_of_ball, ponds, sands, trees, holeCoordinates, ballCoordinates, formula
                        );
                        course.saveToFile(name);
                        writeFile(name);
                        mainTable.removeActor(textField);
                        mainTable.removeActor(ok);
                    }
                });
            }

        });


        //Add buttons to table
        mainTable.padTop(30);
        mainTable.add(addPond);
        mainTable.add(addSand);
        mainTable.add(addTree);
        mainTable.add(remove);
        mainTable.add(setHole);
        mainTable.add(setBall);
        mainTable.add(back);
        mainTable.add(save);


        // Adding the equation inputter
        final Dialog dialog = new Dialog("Terrain equation", skin) {
            @Override
            public float getPrefWidth() {
                return 1000;
            }

            @Override
            public float getPrefHeight() {
                return 500;
            }

            {
                text("Height = ");

            }

            @Override
            protected void result(final Object object) {
                text(object.toString());
            }

        };
        label = new Label("Height =", skin);
        label.setName("Height = ");
        final Table dialogTable = new Table();
        final int random = (int)Math.random()*10;
        final TextField xField = new TextField(String.valueOf(random), skin);

        TextButton OK = new TextButton("OK", skin);
        OK.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
             /*   FunctionHandler functionHandler = new FunctionHandler();
                String equation = label.getName().substring(9, label.getName().length());
                functionHandler.setFormula(equation);
                label.setText(functionHandler.evaluateFunction(equation, 1, 2)); */
             formula = xField.getText();
                dialog.hide();
                dialog2.show(stage);

            }

        });
        dialogTable.setFillParent(true);
        dialogTable.add(label);
        dialogTable.row();
        dialogTable.add(xField);
        dialogTable.add(OK);


     dialog.addActor(dialogTable);

        dialog.show(stage);
        dialog.setBounds(500, 500, dialog.getPrefWidth(), dialog.getPrefHeight());


        //Add table to stage

        Table table = new Table();
        table.setFillParent(true);
        final TextField textField = new TextField(String.valueOf(random), skin);
        TextButton gravity = new TextButton("gravity constant", skin);
        gravity.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gravitationalAcceleration = Double.parseDouble(textField.getText());
            }
        });
        final TextField textField1 = new TextField(String.valueOf(random), skin);
        TextButton friction = new TextButton("friction coefficient", skin);
        friction.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                friction_coefficient = Double.parseDouble(textField1.getText());
            }
        });
        final TextField textField2 = new TextField(String.valueOf(random), skin);
        final TextButton mass = new TextButton("mass of ball", skin);
        mass.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
               mass_of_ball = Double.parseDouble(textField2.getText());
            }
        });
        final TextField textField3 = new TextField(String.valueOf(random), skin);
        TextButton velMax = new TextButton("maximal velocity", skin);
        velMax.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                maximum_velocity = Double.parseDouble(textField3.getText());
            }
        });
        TextButton OK1 = new TextButton("OK", skin);
        OK1.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                dialog2.hide();
                            }
                        });
        table.padTop(10);
        table.add(textField);
        table.add(gravity);
        table.row();
        table.add(textField1);
        table.add(friction);
        table.row();
        table.add(textField2);
        table.add(mass);
        table.row();
        table.add(textField3);
        table.add(velMax);
        table.row();
        table.add(OK1);
        dialog2.add(table);
        dialog2.setBounds(300, 300, 500, 500);



        stage.addActor(mainTable);

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
         if ((ballCoordinates[0]>holeCoordinates[0]-5&&ballCoordinates[0]<holeCoordinates[0]+45)&&
                (ballCoordinates[1]>holeCoordinates[1]-5&&ballCoordinates[1]<holeCoordinates[1]+45)){
            ballCoordinates[0]=holeCoordinates[0]+10;
            ballCoordinates[1]=holeCoordinates[1]+10;
            ball.setPosition(holeCoordinates[0]+20,holeCoordinates[1]+20);

        }
        if ((ballCoordinates[0]>holeCoordinates[0]+5&&ballCoordinates[0]<holeCoordinates[0]+35)&&
                (ballCoordinates[1]>holeCoordinates[1]+5&&ballCoordinates[1]<holeCoordinates[1]+35)) {
            //sound.play();
            //sound = Gdx.audio.newSound(Gdx.files.internal("BallDropGame.wav"));
            batch = new SpriteBatch();
            img = new Texture("YouWonMate.png");
            sprite = new Sprite(img);
            sprite.setPosition(
                    Gdx.graphics.getWidth() / 2 - sprite.getWidth() / 2,
                    Gdx.graphics.getHeight() / 2 - sprite.getHeight() / 2);
            batch.begin();
            batch.draw(sprite, sprite.getX(), sprite.getY());
            batch.end();

        }
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

    private void writeFile(String name) {
        try {
            FileWriter fw = new FileWriter("CourseNames.txt", true);
            fw.write(name + ".txt ");
            fw.close();

        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }
}
