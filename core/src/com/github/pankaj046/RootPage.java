package com.github.pankaj046;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class RootPage extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Texture redBoxTexture, blueBoxTexture, yellowBoxTexture, greenBoxTexture, greyBoxTexture;
    private Rectangle[][] boxes;
    private boolean[][] isGrey;
    private BitmapFont font;
    private int score;
    private float timer;
    private boolean gameOver;
    int mScreenWidth = 0;
    float gameOverTimer = 0;
    boolean isGreyVisible = false;

    @Override
    public void create() {
        int screenWidth = Gdx.graphics.getWidth();
        mScreenWidth = screenWidth;
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        redBoxTexture = new Texture("red.png");
        blueBoxTexture = new Texture("blue.png");
        yellowBoxTexture = new Texture("yellow.png");
        greenBoxTexture = new Texture("green.png");
        greyBoxTexture = new Texture("grey.png");

        boxes = new Rectangle[2][2];
        isGrey = new boolean[2][2];
        int width = screenWidth/2;
        int height = screenWidth/2;

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                boxes[row][col] = new Rectangle(col * width, Gdx.graphics.getHeight() - 100 - (row + 1) * height, width, height);
                isGrey[row][col] = false;
            }
        }
        font = new BitmapFont();
        score = 0;
        timer = 0;
        gameOverTimer = 0;
        gameOver = false;
    }

    @Override
    public void render() {
        if (!gameOver) {
            update();
            draw();
        } else {
            handleGameOver();
        }
    }

    private void update() {
        timer += Gdx.graphics.getDeltaTime();
        gameOverTimer += Gdx.graphics.getDeltaTime();
        if (timer > 1) {
            int randomRow = MathUtils.random(0, 1);
            int randomCol = MathUtils.random(0, 1);
            for (int row = 0; row < 2; row++) {
                for (int col = 0; col < 2; col++) {
                    isGrey[row][col] = (row == randomRow && col == randomCol);
                }
            }
            timer = 0;
        }

        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            for (int row = 0; row < 2; row++) {
                for (int col = 0; col < 2; col++) {
                    if (boxes[row][col].contains(touchPos.x, touchPos.y)) {
                        if (isGrey[row][col]) {
                            score++;
                            gameOverTimer = 0;
                        } else {
                            gameOver = true;
                        }
                        break;
                    }
                }
            }
        }else {
            if (isGreyVisible && gameOverTimer>1){
                gameOver = true;
            }
        }
    }


    private void draw() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        font.getData().setScale(2f);

        batch.setColor(Color.WHITE);

        batch.draw(new TextureRegion(new Texture(Gdx.files.internal("white.png"))),
                10, Gdx.graphics.getHeight() - 60, Math.min(50, Gdx.graphics.getWidth() - 20), 50);
        font.setColor(Color.BLACK);
        font.draw(batch, "Score: " + score, (float) (mScreenWidth - 100) /2 , Gdx.graphics.getHeight() - 20);

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                if (isGrey[row][col]) {
                    isGreyVisible = true;
                    batch.draw(greyBoxTexture, boxes[row][col].x, boxes[row][col].y, boxes[row][col].width, boxes[row][col].height);
                } else {
                    isGreyVisible = false;
                    if (row == 0 && col == 0) {
                        batch.draw(redBoxTexture, boxes[row][col].x, boxes[row][col].y, boxes[row][col].width, boxes[row][col].height);
                    } else if (row == 0) {
                        batch.draw(blueBoxTexture, boxes[row][col].x, boxes[row][col].y, boxes[row][col].width, boxes[row][col].height);
                    } else if (col == 0) {
                        batch.draw(yellowBoxTexture, boxes[row][col].x, boxes[row][col].y, boxes[row][col].width, boxes[row][col].height);
                    } else {
                        batch.draw(greenBoxTexture, boxes[row][col].x, boxes[row][col].y, boxes[row][col].width, boxes[row][col].height);
                    }
                }
            }
        }

        batch.end();
    }

    private void handleGameOver() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        String value = "Game Over. Your score is: " + score;
        font.setColor(Color.WHITE);
        font.draw(batch, value, (float) Gdx.graphics.getWidth() /4, (float) Gdx.graphics.getHeight() /2);
        batch.end();
        if (Gdx.input.justTouched()) {
            gameOver = false;
            score = 0;
            timer = 0;
            gameOverTimer = 0;
            isGreyVisible = false;
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        redBoxTexture.dispose();
        blueBoxTexture.dispose();
        yellowBoxTexture.dispose();
        greenBoxTexture.dispose();
        greyBoxTexture.dispose();
        font.dispose();
    }
}









