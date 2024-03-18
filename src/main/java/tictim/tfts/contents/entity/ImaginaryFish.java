package tictim.tfts.contents.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import tictim.tfts.TFTSMod;
import tictim.tfts.angling.AnglingUtils;

/**
 * Data of "imaginary fish" - a pseudo-entity tracked by {@link TFTSHook} for making convincing looking particles
 */
public final class ImaginaryFish{
	public static final float NORMAL_SPEED = .1f;
	public static final float FAST_SPEED = .4f;

	public double x, y, z;
	private double prevX, prevY, prevZ;

	public float xRot, yRot;

	private final Vector3d v = new Vector3d();
	private final Vector3f vf = new Vector3f();

	public void initPosition(@NotNull ServerLevel level, @NotNull TFTSHook hook){
		// TODO shuffle up the fish origin a bit, it's kinda predictable rn
		//      like a random roll between all scanned blockpos N meters away, or weighted roll with sq distance as wgt
		long[] furthestBlock = new long[1];
		double[] furthestBlockDistance = new double[1];
		AnglingUtils.traverseFluid(level, hook.blockPosition(), null, pos -> {
			long l = pos.asLong();
			double dist = hook.distanceToSqr(pos.getX()+.5, pos.getY()+.5, pos.getZ()+.5);
			if(dist>furthestBlockDistance[0]){
				furthestBlock[0] = l;
				furthestBlockDistance[0] = dist;
			}
		}, 5, 5);

		if(furthestBlockDistance[0]==0){ // failsafe
			initPositionWithAngle(hook, Math.random()*(Math.PI*2));
		}else{
			this.prevX = this.x = BlockPos.getX(furthestBlock[0])+0.5;
			this.prevY = this.y = BlockPos.getY(furthestBlock[0])+0.5;
			this.prevZ = this.z = BlockPos.getZ(furthestBlock[0])+0.5;
			lookAt(hook.getX(), hook.getY(), hook.getZ());
		}

		TFTSMod.LOGGER.info("Spawned ''''fish'''' at [{} {} {}] ({}m away)", this.x, this.y, this.z, furthestBlockDistance[0]);
	}

	public void initPositionWithAngle(@NotNull TFTSHook hook, double angle){
		this.prevX = this.x = hook.getX()+Math.sin(angle)*5;
		this.prevY = this.y = hook.getY();
		this.prevZ = this.z = hook.getZ()+Math.cos(angle)*5;
		lookAt(hook.getX(), hook.getY(), hook.getZ());
	}

	public void setPosition(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public boolean moveTo(@NotNull TFTSHook hook, @NotNull ServerLevel level, double speed){
		return moveTo(hook, level, hook.getX(), hook.getY(), hook.getZ(), speed, true);
	}

	public boolean moveTo(@NotNull TFTSHook hook, @NotNull ServerLevel level,
	                      double destX, double destY, double destZ, double speed, boolean snap){
		final double deg2rad = Math.PI/180;

		// MATH SHIT
		boolean ret;

		double dist = distanceSq(destX, destY, destZ);
		if(snap&&dist<=0.125){
			ret = true;
			// snap position
			lookAt(destX, destY, destZ);
			this.x = destX;
			this.y = destY;
			this.z = destZ;
		}else{
			ret = false;
			// rotate towards target

			evalLookAt(destX, destY, destZ);
			float xRot = this.vf.x;
			float yRot = this.vf.y;
			float turnSpeed = (float)(speed*2*Math.max(1/dist, 1));

			if(Double.isFinite(xRot)){
				float diff = Mth.degreesDifference(this.xRot, xRot);
				if(diff<0) diff += 360;
				this.xRot = Mth.lerp(Mth.clamp(diff/360f*turnSpeed, 0, 1), this.xRot, xRot);
			}
			if(Double.isFinite(yRot)){
				float diff = Mth.degreesDifference(this.yRot, yRot);
				if(diff<0) diff += 360;
				this.yRot = Mth.rotLerp(Mth.clamp(diff/360f*turnSpeed, 0, 1), this.yRot, yRot);
			}

			this.yRot = Mth.wrapDegrees(this.yRot+
					(level.getGameTime()/5%2==0 ? 1 : -1)*
							(float)hook.random().triangle(0, 10));

			// move
			this.v.set(0, 0, speed).rotateX(this.xRot*deg2rad).rotateY(this.yRot*-deg2rad);
			this.x += this.v.x();
			this.y += this.v.y();
			this.z += this.v.z();
		}

		return ret;
	}

	private double iDontKnowWhatIAmWritingRightNow;

	public void updatePosition(@NotNull TFTSHook hook, @NotNull ServerLevel level){
		double dx = this.x-this.prevX;
		double dy = this.y-this.prevY;
		double dz = this.z-this.prevZ;

		double magic = 0.09;

		// TODO dynamic particle increase based on delta scale

		double delta = Math.sqrt(dx*dx+dy*dy+dz*dz);
		if(delta==0) return;

		double thing = delta/0.11;
		int particleCount = (int)thing;
		if(particleCount==0){
			this.iDontKnowWhatIAmWritingRightNow += Math.max(0.5, thing);
			if(this.iDontKnowWhatIAmWritingRightNow>=1){
				particleCount = 1;
				this.iDontKnowWhatIAmWritingRightNow -= 1;
			}
		}

		double y = Mth.floor(hook.getY())+1;
		for(int i = 0; i<particleCount; i++){
			float t = particleCount==1 ? 1 : (float)i/(particleCount-1);

			HookEffects.fish(hook, level, Mth.lerp(t, this.prevX, this.x), y, Mth.lerp(t, this.prevZ, this.z),
					(float)Mth.atan2(dx, dz), delta*10*((i+1.0)/particleCount)); // TODO speed?
		}

		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
	}

	public void lookAt(@NotNull TFTSHook hook){
		lookAt(hook.getX(), hook.getY(), hook.getZ());
	}

	public void lookAt(double x, double y, double z){
		evalLookAt(x, y, z);
		float xRot = this.vf.x;
		float yRot = this.vf.y;
		this.xRot = Float.isFinite(xRot) ? xRot : 0;
		this.yRot = Float.isFinite(yRot) ? yRot : 0;
	}

	private void evalLookAt(double x, double y, double z){
		double xd = x-this.x;
		double yd = y-this.y;
		double zd = z-this.z;
		double math = Math.sqrt(xd*xd+zd*zd);
		double deg2rad = 180/Math.PI;

		float xRot = Mth.wrapDegrees((float)-(Mth.atan2(yd, math)*deg2rad));
		float yRot = Mth.wrapDegrees((float)(Mth.atan2(zd, xd)*deg2rad)-90);
		this.vf.set(xRot, yRot, 0);
	}

	private double distanceSq(double x, double y, double z){
		double xd = x-this.x;
		double yd = y-this.y;
		double zd = z-this.z;
		return xd*xd+yd*yd+zd*zd;
	}
}
