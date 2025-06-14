package com.paradise_seeker.game.entity.skill;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Gdx;
import com.paradise_seeker.game.entity.monster.Monster;
import com.paradise_seeker.game.screen.GameScreen;

import java.util.List;
import java.util.ArrayList;

public class PlayerSkill1 extends PlayerSkill implements PlayerProjectile {
    private static final float MIN_X = 0f;
    private static final float MAX_X = 100f;
    private static final float MIN_Y = 0f;
    private static final float MAX_Y = 100f;

    private float x, y;
    private float speed = 10f;
    private float damage;
    private boolean active = true;
    private Rectangle hitbox;
    private String direction;
    private Animation<TextureRegion> animation;
    private float stateTime = 0f;
    private String directionRaw;
    private Animation<TextureRegion> animDown;
    private List<PlayerSkill1> activeProjectiles;

    public PlayerSkill1() {
        super(10, 500); // mana, cooldown
        this.activeProjectiles = new ArrayList<>();
        loadSkillAnimations();
    }

    protected void loadSkillAnimations() {
        String[] directions = {"up", "down", "left", "right"};
        for (String dir : directions) {
            try {
                String path = "images/Entity/skills/PlayerSkills/Skill1/Skill1_" + dir + ".png";
                Texture sheet = new Texture(path);
                TextureRegion[] frames;

                if (dir.equals("left") || dir.equals("right")) {
                    TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / 4, sheet.getHeight());
                    frames = new TextureRegion[4];
                    for (int i = 0; i < 4; i++) frames[i] = tmp[0][i];
                } else {
                    TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth(), sheet.getHeight() / 4);
                    frames = new TextureRegion[4];
                    for (int i = 0; i < 4; i++) frames[i] = tmp[i][0];
                }

                skillAnimations.put(dir, new Animation<>(0.1f, frames));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void castSkill(float atk, float x, float y, String direction) {
        if (canUse(System.currentTimeMillis())) {
            // Remove any existing projectiles from this skill
            activeProjectiles.removeIf(projectile -> !projectile.isActive());

            PlayerSkill1 newProjectile = new PlayerSkill1();
            newProjectile.x = x;
            newProjectile.y = y;
            newProjectile.damage = atk * 2 * damageMultiplier;
            newProjectile.direction = direction;
            newProjectile.directionRaw = direction;
            newProjectile.hitbox = new Rectangle(x, y, 1f, 1f);
            newProjectile.animation = skillAnimations.get(direction);
            newProjectile.active = true;
            newProjectile.stateTime = 0f;
            activeProjectiles.add(newProjectile);
            setLastUsedTime(System.currentTimeMillis());
        }
    }

    @Override
    public void castSkill(float atk, Rectangle bounds, String direction) {
        float x = bounds.x + bounds.width / 2f;
        float y = bounds.y + bounds.height / 2f;
        castSkill(atk, x, y, direction);
    }

    @Override
    public void update() {
        if (!active) return;

        float delta = Gdx.graphics.getDeltaTime();
        switch (direction) {
            case "up": y += speed * delta; break;
            case "down": y -= speed * delta; break;
            case "left": x -= speed * delta; break;
            case "right": x += speed * delta; break;
        }
        hitbox.setPosition(x, y);
        stateTime += delta;

        if (x < MIN_X || x > MAX_X || y < MIN_Y || y > MAX_Y) {
            active = false;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        for (PlayerSkill1 projectile : activeProjectiles) {
            if (!projectile.active) continue;

            Animation<TextureRegion> animToDraw = projectile.animation;
            if (animToDraw == null && projectile.animDown != null) {
                animToDraw = projectile.animDown;
            }
            if (animToDraw != null) {
                TextureRegion frame = animToDraw.getKeyFrame(projectile.stateTime, false);

                float regionWidth = frame.getRegionWidth();
                float regionHeight = frame.getRegionHeight();

                float width, height, scale;
                float targetBase = 0.75f;

                if (projectile.directionRaw.equals("left") || projectile.directionRaw.equals("right")) {
                    scale = targetBase / regionHeight;
                } else {
                    scale = targetBase / regionWidth;
                }
                width = regionWidth * scale;
                height = regionHeight * scale;

                float drawX = projectile.x - width / 2f;
                float drawY = projectile.y - height / 2f;
                float originX = width / 2f;
                float originY = height / 2f;
                float rotation = 0f;

                batch.draw(frame, drawX, drawY, originX, originY, width, height, 1f, 1f, rotation);
            }
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public Rectangle getHitbox() {
        return hitbox;
    }

    @Override
    public float getDamage() {
        return damage;
    }

    @Override
    public void setInactive() {
        this.active = false;
    }

    public void setAnimDown(Animation<TextureRegion> animDown) {
        this.animDown = animDown;
    }

    @Override
    public void updateSkill(float delta, List<Monster> monsters) {
        for (PlayerSkill1 projectile : activeProjectiles) {
            projectile.update();
            for (Monster monster : monsters) {
                if (projectile.isActive() && !monster.isDead() && monster.getBounds().overlaps(projectile.getHitbox())) {
                    monster.takeHit(projectile.getDamage());
                    projectile.setInactive();
                }
            }
        }
        // Remove inactive projectiles
        activeProjectiles.removeIf(projectile -> !projectile.isActive());
    }
}
