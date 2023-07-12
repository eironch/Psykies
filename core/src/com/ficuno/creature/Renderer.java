package com.ficuno.creature;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;

public class Renderer {
    Main main;
    OrthographicCamera cam;
    SpriteBatch batch = new SpriteBatch();
    ShapeRenderer shape;
    Card card;
    Controller controller;
    FreeTypeFontGenerator fontGenerator;
    FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    BitmapFont font;
    Viewport viewport;
    GlyphLayout glyphLayout;
    float w;
    float h;
    List<Vector2> handCardPos = new ArrayList<>();
    TouchRegion touchRegion;
    Vector3 touchPos;
    Assets assets;
    Psykey playerPsykeyRef;
    Psykey playerPsykey;
    Psykey enemyPsykey;
    Psykey enemyPsykeyRef;
    List<Vector2> handCardTextPos = new ArrayList<>();
    List<Vector2> handCardTextPos2 = new ArrayList<>();
    Encounter encounter;
    Vector2 playerCardDisplayPos;
    Vector2 enemyCardDisplayPos;

    public Renderer(Main main){
        this.main = main;
        this.playerPsykey = main.currentPsykey;
        this.playerPsykeyRef = main.psykeyRef;
        this.enemyPsykey = main.enemyPsykey;
        this.enemyPsykeyRef = main.enemyPsykeyRef;
        this.controller = main.controller;
        this.encounter = main.encounter;
        this.card = main.card;
        this.assets = main.assets;
        this.touchRegion = main.touchRegion;

        playerCardDisplayPos = new Vector2();
        enemyCardDisplayPos = new Vector2();
        playerCardDisplayPos.x = ((Main.SCREEN_WIDTH/3.5f) - assets.cardBackgroundTexture.getWidth()/2f);
        playerCardDisplayPos.y = 488 - assets.cardBackgroundTexture.getHeight()/2f;
        enemyCardDisplayPos.x = (((Main.SCREEN_WIDTH) - (Main.SCREEN_WIDTH/3.5f)) - assets.cardBackgroundTexture.getWidth()/2f);
        enemyCardDisplayPos.y = 488 - assets.cardBackgroundTexture.getHeight()/2f;

        touchPos = new Vector3();
        shape = new ShapeRenderer();
        cam = new OrthographicCamera();

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 25;
        fontParameter.borderWidth = 2;
        fontParameter.borderColor = Color.BLACK;
        fontParameter.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParameter);
        glyphLayout = new GlyphLayout();

        // highlight card
        for (int x = 0; x < 6; x++){
            handCardPos.add(new Vector2(0,0));
            handCardTextPos.add(new Vector2(0,0));
            handCardTextPos2.add(new Vector2(0,0));
        }

        configureCam();
    }

    public void render(float deltaTime){
        ScreenUtils.clear(0.796f, 0.859f, 0.988f, 1);
        Gdx.gl.glClearColor(132, 126, 135, 255);

        if (main.showTurnDisplay){
            main.overlayTimer += deltaTime;

            if (main.overlayTimer >= 2.5){
                main.showTurnDisplay = false;
                main.overlayTimer = 0;
            }
        }

        cam.update();
        shape.setProjectionMatrix(cam.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(172/255f, 50/255f, 50/255f, 255/255f);
        batch.begin();
        batch.setProjectionMatrix(cam.combined);

        renderBackground();
        renderCards();
        renderUI();
        renderOtherUI();
        renderPsykies();
        renderText();
        renderOverlay(deltaTime);

        batch.end();
        shape.end();

        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.CYAN);

        renderDebugUI();

        shape.end();
    }

    public void renderOverlay(float deltaTime){
        batch.draw(assets.statsDisplayTexture, (Main.SCREEN_WIDTH/2f + 160) - assets.statsDisplayTexture.getWidth()/2f, 500);

        batch.draw(assets.statsCounterIconsMirror[enemyPsykey.idDefenseValue], Main.SCREEN_WIDTH/2f - 96,636);
        batch.draw(assets.statsCounterIconsMirror[enemyPsykey.egoDefenseValue], Main.SCREEN_WIDTH/2f - 96,584);
        batch.draw(assets.statsCounterIconsMirror[enemyPsykey.superegoDefenseValue], Main.SCREEN_WIDTH/2f - 96,532);

        batch.draw(assets.statsCounterIcons[enemyPsykey.idProwessValue], Main.SCREEN_WIDTH/2f + 336,636);
        batch.draw(assets.statsCounterIcons[enemyPsykey.egoProwessValue], Main.SCREEN_WIDTH/2f + 336,584);
        batch.draw(assets.statsCounterIcons[enemyPsykey.superegoProwessValue], Main.SCREEN_WIDTH/2f + 336,532);

        batch.draw(assets.statsDisplayTexture, (Main.SCREEN_WIDTH/2f - 160) - assets.statsDisplayTexture.getWidth()/2f, 300);

        batch.draw(assets.statsCounterIconsMirror[playerPsykey.idDefenseValue], Main.SCREEN_WIDTH/2f - 416,436);
        batch.draw(assets.statsCounterIconsMirror[playerPsykey.idDefenseValue], Main.SCREEN_WIDTH/2f - 416,384);
        batch.draw(assets.statsCounterIconsMirror[playerPsykey.idDefenseValue], Main.SCREEN_WIDTH/2f - 416,332);

        batch.draw(assets.statsCounterIcons[enemyPsykey.idProwessValue], Main.SCREEN_WIDTH/2f + 16,436);
        batch.draw(assets.statsCounterIcons[enemyPsykey.egoProwessValue], Main.SCREEN_WIDTH/2f + 16,384);
        batch.draw(assets.statsCounterIcons[enemyPsykey.superegoProwessValue], Main.SCREEN_WIDTH/2f + 16,332);

        if (main.showTurnDisplay){
            font.getData().setScale(2);

            if (main.currentTurn == Main.ENEMY_TURN){
                font.setColor(new Color(172/255f, 50/255f, 50/255f, 255/255f));

                setGlyphLayout("Enemy Turn");
                font.draw(batch, glyphLayout, (Main.SCREEN_WIDTH/2f) - w/2, 509);
            } else if (main.currentTurn == Main.PLAYER_TURN){
                font.setColor(new Color(99/255f, 155/255f, 255/255f, 255/255f));

                setGlyphLayout("Player Turn");
                font.draw(batch, glyphLayout, (Main.SCREEN_WIDTH/2f) - w/2, 509);
            }

            font.setColor(Color.WHITE);
            font.getData().setScale(1);

            drawCardDisplay();
        }
    }

    public void renderText(){
        //font.draw(batch, "Deal 15 damage to the enemy and inflict stun.", 100,100);

        setGlyphLayout(card.playerDrawPileCardTypesNames.size());
        font.draw(batch, glyphLayout, (Main.SCREEN_WIDTH - (104 + assets.drawPileIcon.getRegionWidth())) - w/2, 208 + h);

        setGlyphLayout(card.playerDiscardPileCardTypesNames.size());
        font.draw(batch, glyphLayout, (100 + assets.drawPileIcon.getRegionWidth()) - w/2, 208 + h);

        setGlyphLayout(playerPsykey.name);
        font.draw(batch, glyphLayout, ((Main.SCREEN_WIDTH / 2f) - (Main.SCREEN_WIDTH / 8f)) - 240 - (w/2), 440);

        setGlyphLayout(enemyPsykey.name);
        font.draw(batch, glyphLayout, ((Main.SCREEN_WIDTH / 2f) + (Main.SCREEN_WIDTH / 8f)) + 232 - (w/2), 556);

        if (playerPsykey.block > 0){
            setGlyphLayout(playerPsykey.healthPoints + "/" + playerPsykeyRef.healthPoints);
        } else {
            setGlyphLayout(playerPsykey.healthPoints + "/" + playerPsykeyRef.healthPoints);
        }
        font.draw(batch, glyphLayout, ((Main.SCREEN_WIDTH / 2f) - (Main.SCREEN_WIDTH / 8f)) + 204 - (w/2), 361);

        if (enemyPsykey.block > 0){
            setGlyphLayout(enemyPsykey.healthPoints + "/" + enemyPsykeyRef.healthPoints);
        } else {
            setGlyphLayout(enemyPsykey.healthPoints + "/" + enemyPsykeyRef.healthPoints);
        }
        font.draw(batch, glyphLayout, ((Main.SCREEN_WIDTH / 2f) + (Main.SCREEN_WIDTH / 8f)) - 200 - (w/2), 632);
    }

    public void renderPsykies(){
        batch.draw(playerPsykey.textureMirror, ((Main.SCREEN_WIDTH / 2f) - (Main.SCREEN_WIDTH / 8f)) - 80,312);
        touchRegion.playerPsykeyPoly.setPosition(((Main.SCREEN_WIDTH / 2f) - (Main.SCREEN_WIDTH / 8f)) - 80,312);

        batch.draw(enemyPsykey.texture, ((Main.SCREEN_WIDTH / 2f) + (Main.SCREEN_WIDTH / 8f)) - 80, 512);
        touchRegion.enemyPsykeyPoly.setPosition(((Main.SCREEN_WIDTH / 2f) + (Main.SCREEN_WIDTH / 8f)) - 80, 512);
    }

    public void renderOtherUI(){
        batch.draw(assets.helpIcon, 0, Creature.HEIGHT - (assets.helpIcon.getRegionHeight() + 10));
        batch.draw(assets.menuIcon, Main.SCREEN_WIDTH - (assets.menuIcon.getRegionWidth() + 10),
                Creature.HEIGHT - (assets.menuIcon.getRegionHeight() + 10));
        batch.draw(assets.bagIcon, 10, 10);
        batch.draw(main.actionsMenuState, Main.SCREEN_WIDTH - (main.actionsMenuState.getRegionWidth() + 10), 10);


        touchRegion.uiTouchRegionPolys.get(0).setPosition(0, Creature.HEIGHT - (assets.helpIcon.getRegionHeight() + 10));
        touchRegion.uiTouchRegionPolys.get(1).setPosition(Main.SCREEN_WIDTH - (assets.menuIcon.getRegionWidth() + 10),
                Creature.HEIGHT - (assets.menuIcon.getRegionHeight() + 10));
        touchRegion.uiTouchRegionPolys.get(2).setPosition(10, 10);
        touchRegion.uiTouchRegionPolys.get(3).setPosition(Main.SCREEN_WIDTH - (assets.actionsIconClose.getRegionWidth() + 10), 10);

        batch.draw(assets.discardPileIcon, 104, 140);
        batch.draw(assets.drawPileIcon, Main.SCREEN_WIDTH - (104 + assets.drawPileIcon.getRegionWidth()), 140);
    }

    public void renderUI(){
        if (main.showStatsPlayer){
            shape.rect(((Main.SCREEN_WIDTH / 2f) - (Main.SCREEN_WIDTH / 8f)) + 292,344,
                    376 * ((float) playerPsykey.healthPoints / (float) playerPsykeyRef.healthPoints),16);
            batch.draw(assets.playerUIBar, ((Main.SCREEN_WIDTH / 2f) - (Main.SCREEN_WIDTH / 8f)) - 368,312);

            if (main.playerPlayIcon != null){
                batch.draw(main.playerPlayIcon,
                        ((Main.SCREEN_WIDTH / 2f) - (Main.SCREEN_WIDTH / 8f)) + 96, 408);
            }

        } else if (main.showStatsEnemy){
            shape.rect(((Main.SCREEN_WIDTH / 2f) + (Main.SCREEN_WIDTH / 8f)) - 292 - (376 * ((float) enemyPsykey.healthPoints / (float) enemyPsykeyRef.healthPoints)),616,
                    376 * ((float) enemyPsykey.healthPoints / (float) enemyPsykeyRef.healthPoints),16);
            batch.draw(assets.enemyUIBar, ((Main.SCREEN_WIDTH / 2f) + (Main.SCREEN_WIDTH / 8f)) - 688,504);

            if (main.enemyPlayIcon != null){
                batch.draw(main.enemyPlayIcon,
                        ((Main.SCREEN_WIDTH / 2f) - (Main.SCREEN_WIDTH / 8f)) + 144, 500);
            }
        }
    }

    public void renderCards(){
        if (card.playerHandPileCardTypesNames == null){
            return;
        }

        for (int x = 0; x < 6; x++){
            handCardPos.get(x).set(Main.SCREEN_WIDTH / 2f -
                            findStartPos(card.playerHandPileCardTypesNames.size()) - getXPosition(x + 1),
                    40 + main.handCardSelected.get(x));

            handCardTextPos.get(x).set(Main.SCREEN_WIDTH / 2f -
                            findStartPos(card.playerHandPileCardTypesNames.size()) - getXPosition(x + 1),
                    222 + main.handCardSelected.get(x));

            handCardTextPos2.get(x).set(Main.SCREEN_WIDTH / 2f -
                            findStartPos(card.playerHandPileCardTypesNames.size()) - getXPosition(x + 1),
                    67 + main.handCardSelected.get(x));
        }

        for (int x = 0; x < touchRegion.cardTouchRegionPolys.size(); x++){
            touchRegion.cardTouchRegionPolys.get(x).setPosition(handCardPos.get(x).x, handCardPos.get(x).y);
        }

        if (card.playerHandPileCardTypesNames.size() >= 1){
            batch.draw(assets.cardBackgroundTexture, handCardPos.get(0).x + 16, handCardPos.get(0).y - 16);
            batch.draw(getTexture(0), handCardPos.get(0).x, handCardPos.get(0).y);

            if ((card.playerHandPileCardTypesNames.get(0)).length == 3){
                setGlyphLayout(card.playerHandPileCardTypesNames.get(0)[2]);
                font.draw(batch, glyphLayout, handCardTextPos.get(0).x + 22 - w/2, handCardTextPos.get(0).y);

                setGlyphLayout(card.playerHandPileCardTypesNames.get(0)[2]);
                font.draw(batch, glyphLayout, handCardTextPos2.get(0).x + 90 - w/2, handCardTextPos2.get(0).y);
            }
        }

        if (card.playerHandPileCardTypesNames.size() >= 2){
            batch.draw(assets.cardBackgroundTexture, handCardPos.get(1).x + 16, handCardPos.get(1).y - 16);
            batch.draw(getTexture(1), handCardPos.get(1).x, handCardPos.get(1).y);

            if ((card.playerHandPileCardTypesNames.get(1)).length == 3){
                setGlyphLayout(card.playerHandPileCardTypesNames.get(1)[2]);
                font.draw(batch, glyphLayout, handCardTextPos.get(1).x + 22 - w/2, handCardTextPos.get(1).y);

                setGlyphLayout(card.playerHandPileCardTypesNames.get(1)[2]);
                font.draw(batch, glyphLayout, handCardTextPos2.get(1).x + 90 - w/2, handCardTextPos2.get(1).y);
            }
        }

        if (card.playerHandPileCardTypesNames.size() >= 3){
            batch.draw(assets.cardBackgroundTexture, handCardPos.get(2).x + 16, handCardPos.get(2).y - 16);
            batch.draw(getTexture(2), handCardPos.get(2).x, handCardPos.get(2).y);

            if ((card.playerHandPileCardTypesNames.get(2)).length == 3){
                setGlyphLayout(card.playerHandPileCardTypesNames.get(2)[2]);
                font.draw(batch, glyphLayout, handCardTextPos.get(2).x + 22 - w/2, handCardTextPos.get(2).y);

                setGlyphLayout(card.playerHandPileCardTypesNames.get(2)[2]);
                font.draw(batch, glyphLayout, handCardTextPos2.get(2).x + 90 - w/2, handCardTextPos2.get(2).y);
            }
        }

        if (card.playerHandPileCardTypesNames.size() >= 4){
            batch.draw(assets.cardBackgroundTexture, handCardPos.get(3).x + 16, handCardPos.get(3).y - 16);
            batch.draw(getTexture(3), handCardPos.get(3).x, handCardPos.get(3).y);

            if ((card.playerHandPileCardTypesNames.get(3)).length == 3){
                setGlyphLayout(card.playerHandPileCardTypesNames.get(3)[2]);
                font.draw(batch, glyphLayout, handCardTextPos.get(3).x + 22 - w/2, handCardTextPos.get(3).y);

                setGlyphLayout(card.playerHandPileCardTypesNames.get(3)[2]);
                font.draw(batch, glyphLayout, handCardTextPos2.get(3).x + 90 - w/2, handCardTextPos2.get(3).y);
            }
        }

        if (card.playerHandPileCardTypesNames.size() >= 5){
            batch.draw(assets.cardBackgroundTexture, handCardPos.get(4).x + 16, handCardPos.get(4).y - 16);
            batch.draw(getTexture(4), handCardPos.get(4).x, handCardPos.get(4).y);

            if ((card.playerHandPileCardTypesNames.get(4)).length == 3){
                setGlyphLayout(card.playerHandPileCardTypesNames.get(4)[2]);
                font.draw(batch, glyphLayout, handCardTextPos.get(4).x + 22 - w/2, handCardTextPos.get(4).y);

                setGlyphLayout(card.playerHandPileCardTypesNames.get(4)[2]);
                font.draw(batch, glyphLayout, handCardTextPos2.get(4).x + 90 - w/2, handCardTextPos2.get(4).y);
            }
        }

        if (card.playerHandPileCardTypesNames.size() >= 6){
            batch.draw(assets.cardBackgroundTexture, handCardPos.get(5).x + 16, handCardPos.get(5).y - 16);
            batch.draw(getTexture(5), handCardPos.get(5).x, handCardPos.get(5).y);

            if ((card.playerHandPileCardTypesNames.get(5)).length == 3){
                setGlyphLayout(card.playerHandPileCardTypesNames.get(5)[2]);
                font.draw(batch, glyphLayout, handCardTextPos.get(5).x + 22 - w/2, handCardTextPos.get(5).y);

                setGlyphLayout(card.playerHandPileCardTypesNames.get(5)[2]);
                font.draw(batch, glyphLayout, handCardTextPos2.get(5).x + 90 - w/2, handCardTextPos2.get(5).y);
            }
        }
    }

    public void drawCardDisplay(){
        if (encounter.playerCardPlayedPrev != null && encounter.enemyCardPlayedPrev != null){
            batch.draw(assets.cardBackgroundTexture, playerCardDisplayPos.x + 16, playerCardDisplayPos.y - 16);

            batch.draw(assets.getTexture(encounter.playerCardPlayedPrev[0], encounter.playerCardPlayedPrev[1]),
                    playerCardDisplayPos.x, playerCardDisplayPos.y);

            setGlyphLayout(encounter.playerCardPlayedPrev[2]);
            font.draw(batch, glyphLayout, playerCardDisplayPos.x + 23 - w/2, playerCardDisplayPos.y + 182);

            setGlyphLayout(encounter.playerCardPlayedPrev[2]);
            font.draw(batch, glyphLayout, playerCardDisplayPos.x + 91 - w/2, playerCardDisplayPos.y + 27);

            batch.draw(assets.cardBackgroundTexture, enemyCardDisplayPos.x,enemyCardDisplayPos.y - 16);

            batch.draw(assets.getTexture(encounter.enemyCardPlayedPrev[0], encounter.enemyCardPlayedPrev[1]),
                    enemyCardDisplayPos.x - 16, enemyCardDisplayPos.y);

            setGlyphLayout(encounter.enemyCardPlayedPrev[2]);
            font.draw(batch, glyphLayout, enemyCardDisplayPos.x + 7 - w/2, enemyCardDisplayPos.y + 182);

            setGlyphLayout(encounter.enemyCardPlayedPrev[2]);
            font.draw(batch, glyphLayout, enemyCardDisplayPos.x + 75 - w/2, enemyCardDisplayPos.y + 27);
        }
    }
    public void renderBackground(){
        batch.draw(assets.encounterBgTextures[0], -668,-216);
        batch.draw(assets.spotlightTexture, ((Main.SCREEN_WIDTH / 2f) - (Main.SCREEN_WIDTH / 8f)) - 108,272);
        batch.draw(assets.spotlightTexture, ((Main.SCREEN_WIDTH / 2f) + (Main.SCREEN_WIDTH / 8f)) - 108,472);

    }
    public void  renderDebugUI(){
//        for (int x = 0; x < touchRegion.cardTouchRegionPolys.size(); x++){
//            shape.polygon(touchRegion.cardTouchRegionPolys.get(x).getTransformedVertices());
//        }
//
//        for (Polygon uiTouchRegion : touchRegion.uiTouchRegionPolys){
//            shape.polygon(uiTouchRegion.getTransformedVertices());
//        }
        shape.polygon(touchRegion.playerPsykeyPoly.getTransformedVertices());
        shape.polygon(touchRegion.enemyPsykeyPoly.getTransformedVertices());

        if (Gdx.input.justTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(),0);
            cam.unproject(touchPos);
        }

        //shape.circle(touchPos.x, touchPos.y, 20);
    }

    public  int getXPosition(int num){
        return 120 * (Math.round(((card.playerHandPileCardTypesNames.size())/ 2f)) - num);
    }
    public TextureRegion getTexture(int index){
        return assets.getTexture(card.playerHandPileCardTypesNames.get(index)[0], card.playerHandPileCardTypesNames.get(index)[1]);
    }

    public int findStartPos(int cardCount){
        if (cardCount % 2 ==0){
            return 124;
        }

        return 56;
    }

    public void setGlyphLayout(String text){
        glyphLayout.setText(font, text);
        w = glyphLayout.width;
        h = glyphLayout.height;
    }

    public void setGlyphLayout(int text){
        glyphLayout.setText(font, Integer.toString(text));
        w = glyphLayout.width;
        h = glyphLayout.height;
    }

    public void configureCam(){
        if (Main.SCREEN_HEIGHT >= 800){
            cam.translate(Main.SCREEN_WIDTH / 2f, 720 / 2f);
            cam.zoom = 15.2f;
            viewport = new FillViewport(MainMenuScreen.GAME_WORLD_WIDTH * Main.aspectRatio, MainMenuScreen.GAME_WORLD_HEIGHT, cam);

        } else {
            cam.translate(Main.SCREEN_WIDTH / 2f, 720 / 2f);
            viewport = new ExtendViewport(MainMenuScreen.GAME_WORLD_WIDTH * Main.aspectRatio, MainMenuScreen.GAME_WORLD_HEIGHT, cam);
            cam.zoom = 7.2f;
        }
    }
}