package com.codemania.game.state;

import java.util.ArrayList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import com.codemania.framework.util.Painter;
import com.codemania.game.model.Block;
import com.codemania.game.model.Cloud;
import com.codemania.game.model.Player;
import com.codemania.simpleandroidgdf.Assets;
import com.codemania.simpleandroidgdf.GameMainActivity;

public class PlayState extends State {
    private Player player;
    private ArrayList<Block> blocks;
    private Cloud cloud, cloud2;
    private int playerScore = 0;
    private static final int BLOCK_HEIGHT = 50;
    private static final int BLOCK_WIDTH = 20;
    private int blockSpeed = -200;
    private static final int PLAYER_WIDTH = 66;
    private static final int PLAYER_HEIGHT = 92;

    private float recentTouchY;

    @Override
    public void init() {
        player = new Player(160, GameMainActivity.GAME_HEIGHT - 45
                - PLAYER_HEIGHT, PLAYER_WIDTH, PLAYER_HEIGHT);
        blocks = new ArrayList<Block>();
        cloud = new Cloud(100, 100);
        cloud2 = new Cloud(500, 50);
        for (int i = 0; i < 5; i++) {
            Block b = new Block(i * 200, GameMainActivity.GAME_HEIGHT - 95,
                    BLOCK_WIDTH, BLOCK_HEIGHT);
            blocks.add(b);
        }
    }

    @Override
    public void update(float delta) {
        if (!player.isAlive()) {
            setCurrentState(new GameOverState(playerScore / 100));
        }
        playerScore += 1;
        if (playerScore % 500 == 0 && blockSpeed > -280) {
            blockSpeed -= 10;
        }
        cloud.update(delta);
        cloud2.update(delta);
        Assets.runAnim.update(delta);
        player.update(delta);
        updateBlocks(delta);
    }

    private void updateBlocks(float delta) {
        for (Block b : blocks) {
            b.update(delta, blockSpeed);
            if (b.isVisible()) {
                if (player.isDucked()
                        && Rect.intersects(b.getRect(), player.getDuckRect())) {
                    b.onCollide(player);
                } else if (!player.isDucked()
                        && Rect.intersects(b.getRect(), player.getRect())) {
                    b.onCollide(player);
                }
            }
        }
    }

    @Override
    public void render(Painter g) {
        g.setColor(Color.rgb(208, 244, 247));
        g.fillRect(0, 0, GameMainActivity.GAME_WIDTH,
                GameMainActivity.GAME_HEIGHT);
        renderPlayer(g);
        renderBlocks(g);
        renderSun(g);
        renderClouds(g);
        g.drawImage(Assets.grass, 0, 405);
        renderScore(g);
    }

    private void renderScore(Painter g) {
        g.setFont(Typeface.SANS_SERIF, 25);
        g.setColor(Color.GRAY);
        g.drawString("" + playerScore / 100, 20, 30);
    }

    private void renderPlayer(Painter g) {
        if (player.isGrounded()) {
            if (player.isDucked()) {
                g.drawImage(Assets.duck, (int) player.getX(),
                        (int) player.getY());
            } else {
                Assets.runAnim.render(g, (int) player.getX(),
                        (int) player.getY(), player.getWidth(),
                        player.getHeight());
            }
        } else {
            g.drawImage(Assets.jump, (int) player.getX(), (int) player.getY(),
                    player.getWidth(), player.getHeight());
        }
    }

    private void renderBlocks(Painter g) {
        for (Block b : blocks) {
            if (b.isVisible()) {
                g.drawImage(Assets.block, (int) b.getX(), (int) b.getY(),
                        BLOCK_WIDTH, BLOCK_HEIGHT);
            }
        }
    }

    private void renderSun(Painter g) {
        g.setColor(Color.rgb(255, 165, 0));
        g.fillOval(715, -85, 170, 170);
        g.setColor(Color.YELLOW);
        g.fillOval(725, -75, 150, 150);
    }

    private void renderClouds(Painter g) {
        g.drawImage(Assets.cloud1, (int) cloud.getX(), (int) cloud.getY(), 100,
                60);
        g.drawImage(Assets.cloud2, (int) cloud2.getX(), (int) cloud2.getY(),
                100, 60);
    }

    @Override
    public boolean onTouch(MotionEvent e, int scaledX, int scaledY) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            recentTouchY = scaledY;
        } else if (e.getAction() == MotionEvent.ACTION_UP) {
            if (scaledY - recentTouchY < -50) {
                player.jump();
            } else if (scaledY - recentTouchY > 50) {
                player.duck();
            }
        }
        return true;
    }
}