package zgame;

public class StaticMathTools {

	public static Quat multQuat(Quat a, Quat b) {
		Quat dst = new Quat();
		float t[] = new float[8];

		t[0] = (a.w + a.x) * (b.w + b.x);
		t[1] = (a.z - a.y) * (b.y - b.z);
		t[2] = (a.x - a.w) * (b.y + b.z);
		t[3] = (a.y + a.z) * (b.x - b.w);
		t[4] = (a.x + a.z) * (b.x + b.y);
		t[5] = (a.x - a.z) * (b.x - b.y);
		t[6] = (a.w + a.y) * (b.w - b.z);
		t[7] = (a.w - a.y) * (b.w + b.z);

		dst.w = (float) (t[1] + ((-t[4] - t[5] + t[6] + t[7]) * 0.5));
		dst.x = (float) (t[0] - ((t[4] + t[5] + t[6] + t[7]) * 0.5));
		dst.y = (float) (-t[2] + ((t[4] - t[5] + t[6] - t[7]) * 0.5));
		dst.z = (float) (-t[3] + ((t[4] - t[5] - t[6] + t[7]) * 0.5));

		return dst;
	}

	public static Quat normaliseQuat(Quat src) {
		Quat dst = new Quat();
		float d = src.x * src.x + src.y * src.y + src.z * src.z + src.w * src.w;

		d = (float) ((d > 0) ? (1 / Math.sqrt(d)) : 1);

		dst.x = src.x * d;
		dst.y = src.y * d;
		dst.z = src.z * d;
		dst.w = src.w * d;

		return dst;
	}

	public static Quat angleAxisToQuat(float angle, float x, float y, float z) {
		Quat q = new Quat();
		float temp_angle = (float) (angle * 0.01745 / 2.0);
		q.w = (float) Math.cos(temp_angle);
		q.x = (float) Math.sin(temp_angle) * x;
		q.y = (float) Math.sin(temp_angle) * y;
		q.z = (float) Math.sin(temp_angle) * z;

		return q;
	}

	public static Quat rotQuat(Quat dst, float angle, float x, float y, float z) {
		Quat q;
		q = angleAxisToQuat(angle, x, y, z);
		Quat r = new Quat();
		r.x = dst.x;
		r.y = dst.y;
		r.z = dst.z;
		r.w = dst.w;
		dst = multQuat(r, q);
		dst = normaliseQuat(dst);
		return dst;
	}

	public static void quatToMatrix(Quat q, float matrix[]) {
		float two_xx = q.x * (q.x + q.x);
		float two_xy = q.x * (q.y + q.y);
		float two_xz = q.x * (q.z + q.z);

		float two_wx = q.w * (q.x + q.x);
		float two_wy = q.w * (q.y + q.y);
		float two_wz = q.w * (q.z + q.z);

		float two_yy = q.y * (q.y + q.y);
		float two_yz = q.y * (q.z + q.z);

		float two_zz = q.z * (q.z + q.z);

		matrix[0] = 1 - (two_yy + two_zz);
		matrix[1] = two_xy - two_wz;
		matrix[2] = two_xz + two_wy;
		matrix[3] = 0;
		matrix[4] = two_xy + two_wz;
		matrix[5] = 1 - (two_xx + two_zz);
		matrix[6] = two_yz - two_wx;
		matrix[7] = 0;
		matrix[8] = two_xz - two_wy;
		matrix[9] = two_yz + two_wx;
		matrix[10] = 1 - (two_xx + two_yy);
		matrix[11] = 0;
		matrix[12] = 0;
		matrix[13] = 0;
		matrix[14] = 0;
		matrix[15] = 1;
	}

}
