package com.mygdx.game.menu;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.menu.courseCreator.CourseCreator;
import com.mygdx.game.menu.courseToPlay.Course;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Class that displays the Menu screen (options to play, create new field, and exit)
 */
public class MenuScreen implements Screen {
    private SpriteBatch batch;
    private Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;
    private TextureAtlas atlas;
    private Skin skin;
    private Texture img;
    private Game game;


    public MenuScreen(Game game) {
        this.game = game;
        // Atlas and skin are files that specify how do the font and buttons look like, we should change them after because I used some online ones
        atlas = new TextureAtlas("uiskin.atlas");
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        batch = new SpriteBatch();
        // Background
        img = new Texture("menuBack.jpg");


        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 480, camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        stage = new Stage(viewport, batch);
    }


    @Override
    public void show() {
        //Stage should control input:
        Gdx.input.setInputProcessor(stage);

        //Create Table with buttons (Like grid layout in Java Swing)
        final Table mainTable = new Table();
        //Set table to fill stage
        mainTable.setFillParent(true);
        //Set alignment of contents in the table.
        mainTable.top();


        //Create buttons
        TextButton playButton = new TextButton("Play", skin);
        TextButton createCourseButton = new TextButton("Create new course", skin);
        final Label label = new Label("Enter course from file", skin);
        final TextField file = new TextField("", skin);
        final TextButton ok = new TextButton("OK", skin);
        ok.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new Course(file.getText()));
            }
        });
        TextButton exitButton = new TextButton("Exit", skin);

        //Add listeners to buttons
        playButton.addListener(new ClickListener() {
            String fileName = "";

            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Creating a Select box with all avaiable course names
                final SelectBox<String> dropdown = new SelectBox<String>(skin);
                dropdown.setSize(100, 100 / 10);
                dropdown.setPosition(0, dropdown.getHeight());
                dropdown.setItems(courseNames());
                dropdown.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        fileName = (String) dropdown.getSelected();
                    }
                });
                mainTable.add(dropdown);
                // Creating an OK button, to start a game
                TextButton ok = new TextButton("OK", skin);
                mainTable.add(ok);
                ok.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new Course(fileName));
                    }
                });


            }
        });
        createCourseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Here we open a course creator
                ((Game) Gdx.app.getApplicationListener()).setScreen(new CourseCreator());
            }
        });
        // Here we exit the game
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        //Add buttons to table
        mainTable.padTop(30);
        mainTable.add(playButton);
        mainTable.row().padTop(15);
        mainTable.add(createCourseButton);
        mainTable.row().padTop(15);
        mainTable.add(label);
        mainTable.row().padTop(15);
        mainTable.add(file);
        mainTable.add(ok);
        mainTable.row().padTop(15);
        mainTable.add(exitButton);

        //Add table to stage
        stage.addActor(mainTable);
    }

    /**
     * This method does some render-y thingies, needs to be here but we shouldn't change too much here
     *
     * @param delta
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(img, 0, 0);
        batch.end();
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
     * This method reads all avaiable course names
     * @return
     */
    public Array<String> courseNames() {
        Array<String> courseNames = new Array<>();
        File file = new File("CourseNames.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st = "";

            while ((st = br.readLine()) != null) {
                String[] splited = st.split("\\s+");
                for (String s : splited) {
                    courseNames.add(s);
                }
            }
            br.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return courseNames;
    }
}