package com.paradise_seeker.game.entity.player;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.paradise_seeker.game.entity.Character;
import com.paradise_seeker.game.entity.player.input.PlayerInputHandlerManager;
import com.paradise_seeker.game.entity.skill.*;
import com.paradise_seeker.game.map.GameMap;
import com.paradise_seeker.game.object.item.Item;
import com.paradise_seeker.game.rendering.animations.PlayerAnimationManager;
import com.paradise_seeker.game.rendering.effects.DashTrailManager;
import com.paradise_seeker.game.rendering.renderer.PlayerRendererManager;
import com.paradise_seeker.game.screen.GameScreen;

public class Player extends Character {
    public static final float MAX_HP = 1000;
    public static final float MAX_MP = 100;
    public final float dashCooldown = 0f;
    public final float dashDistance = 2f;

    public float speedMultiplier = 1f;
    public final Vector2 lastPosition = new Vector2();

    public float stateTime = 0f;
    public String direction = "down";
    public boolean isMoving = false;
    public boolean isAttacking = false;

    public float dashTimer = 0f;

    public boolean isShielding = false;
    public boolean isPaused = false;

    public PlayerInventoryManager inventoryManager;
    public DashTrailManager smokeManager = new DashTrailManager();
    public PlayerAnimationManager animationManager;
    public PlayerInputHandlerManager inputHandler;
    public PlayerRendererManager playerRenderer;
    public PlayerSkill playerSkill1;
    public PlayerSkill playerSkill2 = new PlayerSkill2();

    public boolean isDead = false;
    public boolean isHit = false;
    public boolean isShieldedHit = true;
    public boolean isInvulnerable = false;
    public float invulnerabilityTimer = 0f;
    public static final float INVULNERABILITY_DURATION = 0.7f;

    public Player(GameScreen gameScreen) {
        this.bounds = new Rectangle(0, 0, 1, 1);
        this.hp = MAX_HP;
        this.mp = MAX_MP;
        this.maxHp = MAX_HP;
        this.maxMp = MAX_MP;
        this.atk = 25;
        this.speed = 5f;
        this.x = 0;
        this.y = 0;

        this.inventoryManager = new PlayerInventoryManager();
        this.animationManager = new PlayerAnimationManager();
        this.animationManager.setAnimations();
        this.inputHandler = new PlayerInputHandlerManager();
        this.playerRenderer = new PlayerRendererManager(this.animationManager);
        this.playerSkill1 = new PlayerSkill1();
    }

    public Player(Rectangle bounds, float hp, float mp, float maxHp, float maxMp, float atk, float speed, float x, float y, PlayerSkill playerSkill1, PlayerSkill playerSkill2) {
        super(bounds, hp, mp, maxHp, maxMp, atk, speed, x, y);
        this.playerSkill1 = playerSkill1;
        this.playerSkill2 = playerSkill2;

        this.inventoryManager = new PlayerInventoryManager();
        this.animationManager = new PlayerAnimationManager();
        this.animationManager.setAnimations();
        this.inputHandler = new PlayerInputHandlerManager();
        this.playerRenderer = new PlayerRendererManager(this.animationManager);
    }

    public void regenMana(float deltaTime) {
        if (mp < MAX_MP) {
            mp += (float) 0.5 * deltaTime;
        }
        if (mp > MAX_MP) {
            mp = MAX_MP;
        }
    }

    @Override
    public void act(float deltaTime, GameMap gameMap) {
        if (isDead) return;
        Player player = gameMap.getPlayer();
        lastPosition.set(bounds.x, bounds.y);

        inputHandler.handleInput(this, deltaTime, gameMap);

        if (isInvulnerable) {
            invulnerabilityTimer -= deltaTime;
            if (invulnerabilityTimer <= 0) {
                isInvulnerable = false;
            }
        }

        regenMana(deltaTime);
        dashTimer -= deltaTime;
        speedMultiplier = 1f;

        if (isHit || isShieldedHit || isMoving || isAttacking) {
            stateTime += deltaTime;
        } else {
            stateTime = 0;
        }

        if (isAttacking) {
            Animation<TextureRegion> currentAttack = animationManager.getAttackAnimation(direction);
            if (currentAttack.isAnimationFinished(stateTime)) {
                isAttacking = false;
                stateTime = 0;
            }
        }

        smokeManager.update(deltaTime, animationManager);
        inputHandler.handleNPCInteraction(player, gameMap);
    }

    public void addSmoke(float x, float y) {
        smokeManager.addSmoke(x, y);
    }


    @Override
    public void takeHit(float damage) {
        if (isInvulnerable) return;

        if (isShielding) {
            damage /= 2;
        }

        hp = Math.max(0, hp - damage);

        if (hp == 0) {
            if (!isDead) {
                onDeath();
            }
        } else {
            isHit = true;
            stateTime = 0;
            isInvulnerable = true;
            invulnerabilityTimer = INVULNERABILITY_DURATION;
        }
    }

    public void blockMovement() {
        bounds.x = lastPosition.x;
        bounds.y = lastPosition.y;
    }

    public void addItemToInventory(Item newItem) {
        inventoryManager.addItemToInventory(newItem, this.bounds);
    }

    public boolean isDead() {
        return isDead;
    }

    public float getStateTime() {
        return stateTime;
    }


    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        this.isMoving = moving;
    }

    public PlayerSkill getPlayerSkill1() {
        return playerSkill1;
    }

    public PlayerSkill getPlayerSkill2() {
        return playerSkill2;
    }

    public float getDashTimer() {
        return dashTimer;
    }

    public void setDashTimer(float timer) {
        this.dashTimer = timer;
    }

    public float getDashDistance() {
        return dashDistance;
    }

    public float getDashCooldown() {
        return dashCooldown;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setAttacking(boolean attacking) {
        this.isAttacking = attacking;
    }

    public int[] getCollectAllFragments() {
        return inventoryManager.getCollectAllFragments();
    }

    public ArrayList<Item> getInventory() {
        return inventoryManager.getInventory();
    }

    public int getInventorySize() {
        return inventoryManager.getInventorySize();
    }

    public PlayerInventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public float getAtk() {
        return atk;
    }

    public float getMp() {
        return mp;
    }

    public void setMp(float mp) {
        this.mp = mp;
    }

    @Override
    public void onDeath() {
//        isDead = true;
//        isInvulnerable = true;
//        invulnerabilityTimer = Float.MAX_VALUE;
    	hp=MAX_HP;
    }
    public boolean isInvulnerable() {
        return isInvulnerable;
    }
}
