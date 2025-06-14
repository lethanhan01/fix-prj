package com.paradise_seeker.game.entity.skill;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.paradise_seeker.game.entity.monster.Monster;
import com.paradise_seeker.game.entity.player.*;
import java.util.List;

public class PlayerSkill2 extends PlayerSkill {
	private float currentX, currentY;
	private float offsetX = 1f;
	private float offsetY = 1f;
	private float stateTime = 0f;
	private boolean isCasting = false;
	private String currentDirection;
	private Rectangle hitbox;
	private boolean hasDealtDamage = false;
	private float scale = 0.02f;
	private float currentAtk = 0f;

	public PlayerSkill2() {
		super(20, 1000); // mana, cooldown
		loadSkillAnimations();
	}

	@Override
	protected void loadSkillAnimations() {
		try {
			// Animation cho hướng lên
			TextureRegion[] upFrames = new TextureRegion[4];
			upFrames[0] = new TextureRegion(new Texture(Gdx.files.internal("images/Entity/skills/PlayerSkills/Skill2/len/lightning_skill1_frame1.png")));
			upFrames[1] = new TextureRegion(new Texture(Gdx.files.internal("images/Entity/skills/PlayerSkills/Skill2/len/lightning_skill1_frame2.png")));
			upFrames[2] = new TextureRegion(new Texture(Gdx.files.internal("images/Entity/skills/PlayerSkills/Skill2/len/lightning_skill1_frame3.png")));
			upFrames[3] = new TextureRegion(new Texture(Gdx.files.internal("images/Entity/skills/PlayerSkills/Skill2/len/lightning_skill1_frame4.png")));
			skillAnimations.put("up", new Animation<>(0.07f, upFrames));

			// Animation cho hướng xuống
			TextureRegion[] downFrames = new TextureRegion[4];
			downFrames[0] = new TextureRegion(new Texture(Gdx.files.internal("images/Entity/skills/PlayerSkills/Skill2/xuong/lightning_skill1_frame1.png")));
			downFrames[1] = new TextureRegion(new Texture(Gdx.files.internal("images/Entity/skills/PlayerSkills/Skill2/xuong/lightning_skill1_frame2.png")));
			downFrames[2] = new TextureRegion(new Texture(Gdx.files.internal("images/Entity/skills/PlayerSkills/Skill2/xuong/lightning_skill1_frame3.png")));
			downFrames[3] = new TextureRegion(new Texture(Gdx.files.internal("images/Entity/skills/PlayerSkills/Skill2/xuong/lightning_skill1_frame4.png")));
			skillAnimations.put("down", new Animation<>(0.07f, downFrames));

			// Animation cho hướng trái
			TextureRegion[] leftFrames = new TextureRegion[4];
			leftFrames[0] = new TextureRegion(new Texture(Gdx.files.internal("images/Entity/skills/PlayerSkills/Skill2/trai/lightning_skill1_frame1.png")));
			leftFrames[1] = new TextureRegion(new Texture(Gdx.files.internal("images/Entity/skills/PlayerSkills/Skill2/trai/lightning_skill1_frame2.png")));
			leftFrames[2] = new TextureRegion(new Texture(Gdx.files.internal("images/Entity/skills/PlayerSkills/Skill2/trai/lightning_skill1_frame3.png")));
			leftFrames[3] = new TextureRegion(new Texture(Gdx.files.internal("images/Entity/skills/PlayerSkills/Skill2/trai/lightning_skill1_frame4.png")));
			skillAnimations.put("left", new Animation<>(0.07f, leftFrames));

			// Animation cho hướng phải
			TextureRegion[] rightFrames = new TextureRegion[4];
			rightFrames[0] = new TextureRegion(new Texture(Gdx.files.internal("images/Entity/skills/PlayerSkills/Skill2/phai/lightning_skill1_frame1.png")));
			rightFrames[1] = new TextureRegion(new Texture(Gdx.files.internal("images/Entity/skills/PlayerSkills/Skill2/phai/lightning_skill1_frame2.png")));
			rightFrames[2] = new TextureRegion(new Texture(Gdx.files.internal("images/Entity/skills/PlayerSkills/Skill2/phai/lightning_skill1_frame3.png")));
			rightFrames[3] = new TextureRegion(new Texture(Gdx.files.internal("images/Entity/skills/PlayerSkills/Skill2/phai/lightning_skill1_frame4.png")));
			skillAnimations.put("right", new Animation<>(0.07f, rightFrames));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void castSkill(float atk, float x, float y, String direction) {
		// Không sử dụng hàm này
	}

	@Override
	public void castSkill(float atk, Rectangle bounds, String direction) {
		if (canUse(System.currentTimeMillis())) {
			this.currentAtk = atk;
			float centerX = bounds.x + bounds.width / 2f;
			float centerY = bounds.y + bounds.height / 2f;
			float offset = 2.0f;  // Khoảng cách offset chiêu so với trung tâm nhân vật

			// Xác định offset dựa trên hướng
			switch (direction) {
				case "up":
					this.offsetY = offset+1f;
					this.offsetX = 0.5f;
					break;
				case "down":
					this.offsetY = -offset;
					this.offsetX = 0.5f;
					break;
				case "left":
					this.offsetX = -offset;
					this.offsetY = 0.5f;
					break;
				case "right":
					this.offsetX = offset+1f;
					this.offsetY = 0.5f;
					break;
				default:
					System.err.println("Unknown direction: " + direction);
					return;
			}

			centerX += offsetX;
			centerY += offsetY;

			// Bắt đầu cast skill
			this.currentX = centerX;
			this.currentY = centerY;
			this.currentDirection = direction;
			this.stateTime = 0f;
			this.hasDealtDamage = false;
			this.isCasting = true;

			// Tạo hitbox dựa trên frame đầu tiên của animation
			Animation<TextureRegion> anim = skillAnimations.get(direction);
			if (anim != null) {
				TextureRegion frame = anim.getKeyFrame(0f);
				float realWidth = frame.getRegionWidth() * scale;
				float realHeight = frame.getRegionHeight() * scale;
				this.hitbox = new Rectangle(
					centerX - realWidth / 2f,
					centerY - realHeight / 2f,
					realWidth,
					realHeight
				);
			}

			setLastUsedTime(System.currentTimeMillis());
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		if (!isCasting) return;

		Animation<TextureRegion> anim = skillAnimations.get(currentDirection);
		if (anim != null) {
			TextureRegion frame = anim.getKeyFrame(stateTime, false);
			float realWidth = frame.getRegionWidth() * scale;
			float realHeight = frame.getRegionHeight() * scale;
			float drawX = currentX - realWidth / 2f;
			float drawY = currentY - realHeight / 2f;

			batch.draw(frame, drawX, drawY, realWidth, realHeight);
		}
	}

	@Override
	public void updateSkill(float delta, List<Monster> monsters) {
		if (!isCasting) return;

		stateTime += Gdx.graphics.getDeltaTime();
		Animation<TextureRegion> anim = skillAnimations.get(currentDirection);
		
		if (anim != null && anim.isAnimationFinished(stateTime)) {
			isCasting = false;
			return;
		}

		// Cập nhật hitbox
		if (hitbox != null) {
			hitbox.setPosition(currentX - hitbox.getWidth() / 2f, currentY - hitbox.getHeight() / 2f);
		}

		// Kiểm tra va chạm
		if (!hasDealtDamage) {
			for (Monster monster : monsters) {
				if (!monster.isDead() && hitbox.overlaps(monster.getBounds())) {
					monster.takeHit(currentAtk * 2 * damageMultiplier);
					hasDealtDamage = true;
					break;
				}
			}
		}
	}

	@Override
	public void updatePosition(Player player) {
		if (isCasting) {
			this.currentX = player.lastPosition.x + offsetX;
			this.currentY = player.lastPosition.y + offsetY;
		}
	}
}



