package com.pingan.eauthsdk.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Environment;

public class BitmapUtil {

	public static String getSystemProperty(String propName) {
		String line;
		BufferedReader input = null;
		try {
			Process p = Runtime.getRuntime().exec("getprop " + propName);
			input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
			line = input.readLine();
			input.close();
		} catch (IOException ex) {
			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					// Log.e(TAG, "Exception while closing InputStream", e);
				}
			}
		}
		return line;
	}

	public static Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
		Matrix m = new Matrix();
		m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);

		try {

			Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);

			return bm1;
		} catch (OutOfMemoryError ex) {
		}

		return null;
	}

	public static void upsideDownYUV240SP(byte[] src, byte[] des, int width, int height) {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				des[i * width + j] = src[(height - i) * width + j];
			}
		}
	}

	public static void rotateYUV240SP(byte[] src, byte[] des, int width, int height) {

		int wh = width * height;
		// 旋转Y
		int k = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				des[k] = src[width * j + i];
				k++;
			}
		}

		for (int i = 0; i < width; i += 2) {
			for (int j = 0; j < height / 2; j++) {
				des[k] = src[wh + width * j + i];
				des[k + 1] = src[wh + width * j + i + 1];
				k += 2;
			}
		}

	}

	private static void yuv420spToRgb(int[] rgb, byte[] yuv420sp, int width, int height) {
		final int frameSize = width * height;
		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0)
					y = 0;
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}
				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);
				if (r < 0)
					r = 0;
				else if (r > 262143)
					r = 262143;
				if (g < 0)
					g = 0;
				else if (g > 262143)
					g = 262143;
				if (b < 0)
					b = 0;
				else if (b > 262143)
					b = 262143;
				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}
	}

	public static void yuv2gray(float[] gray, byte[] yuv420sp, int width, int height) {
		// 0217修改，暂时保留
		int index;
		for (int j = 0, yp = 0; j < width; j++) {

			for (int i = 0; i < height; i++, yp++) {
				index = (height - 1 - i) * width + j;
				gray[index] = (float) (0xff & ((int) yuv420sp[yp]));
				if (gray[index] < 0)
					gray[index] = 0;
			}
		}
	}

	public static Bitmap createBitmapFromYUVByte(byte[] data, int w, int h, boolean isFront, int displayOrientation) {
		Bitmap _bitmapBitmap = null;
		Bitmap _resultBitmap = null;
		int[] rgb = new int[w * h];
		yuv420spToRgb(rgb, data, w, h);
		data = null;

		try {
			_bitmapBitmap = Bitmap.createBitmap(rgb, w, h, Bitmap.Config.RGB_565);
			// return _bitmapBitmap;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		}

		Matrix _matrix = new Matrix();
		prepareMatrix(_matrix, isFront, displayOrientation);
		try {
			_resultBitmap = Bitmap.createBitmap(_bitmapBitmap, 0, 0, _bitmapBitmap.getWidth(),
					_bitmapBitmap.getHeight(), _matrix, true);
			return _resultBitmap;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		} finally {
			if (_bitmapBitmap != null && !_bitmapBitmap.isRecycled() && (_bitmapBitmap.equals(_resultBitmap))) {
				_bitmapBitmap.recycle();
				_bitmapBitmap = null;
			}
			_matrix = null;
		}
	}

	public static byte[] bitmapToBytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}

	private static void prepareMatrix(Matrix matrix, boolean isFront, int displayOrientation) {
		// Need mirror for front camera.
		matrix.setScale(isFront ? -1 : 1, 1);
		matrix.postRotate(displayOrientation);
	}

	public static Bitmap cropBitmap_second(RectF rect, Bitmap bitmap) {

		float left = rect.left;
		float top = rect.top;
		float width = rect.width() + left;
		float height = rect.height() + top;

		if (left < 0) {
			left = 0;
		}

		if (top < 0) {
			top = 0;
		}

		if (left + width > bitmap.getWidth() - 1) {
			width = bitmap.getWidth() - 1 - left;
		}

		if (top + height > bitmap.getHeight() - 1) {
			height = bitmap.getHeight() - 1 - top;
		}

		return Bitmap.createBitmap(bitmap, (int) left, (int) top, (int) width, (int) height);
	}

	public static Bitmap cropBitmap(RectF rect, Bitmap bitmap, int nHeight, int nWidth, float ratio) {
		int nw, nh, xn, yn, x0, y0;
		int x = (int) rect.left;
		int y = (int) rect.top;
		int w = (int) rect.width() + x;
		int h = (int) rect.height() + y;

		x = 480 - x - w;

		x0 = (int) (x - w * (ratio - 1) * 0.5);
		xn = (int) (x0 + w * ratio);
		if (x0 < 0) {
			x0 = 0;
		}
		if (xn > nWidth - 1) {
			xn = nWidth - 1;
		}
		nw = xn - x0 + 1;

		y0 = (int) (y - h * (ratio - 1));
		yn = (int) (y0 + h * (1 + (ratio - 1) * 1.5));
		if (y0 < 0) {
			y0 = 0;
		}
		if (yn > nHeight - 1) {
			yn = nHeight - 1;
		}
		nh = yn - y0 + 1;
		return Bitmap.createBitmap(bitmap, (int) x0, (int) y0, (int) nw, (int) nh);
	}

	 
	static String cur_name = "";
	static String pf = ".jpg";
	static String fileName = "";

	public static void saveImageToGallery(Bitmap bmp) {
		File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
		if (!appDir.exists()) {
			appDir.mkdir();
		}
		cur_name = String.valueOf(System.currentTimeMillis());
		fileName = cur_name + pf; // 以倒计时命名
		File file = new File(appDir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(CompressFormat.JPEG, 100, fos); // .JPEG格式
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    
    //压缩bitmap图片
    public static Bitmap compressLimitDecodeCompress(Bitmap image,int maxSize,float targetWidth, float targetHeight,int targetKbSize) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 80, baos);
        if( (baos.toByteArray().length / 50)>maxSize) {//判断如果图片大于50kb,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设为了true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int startW = newOpts.outWidth;
        int startH = newOpts.outHeight;

        float targetW = targetWidth;
        float targetH = targetHeight;
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (startW > startH && startW > targetH) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / targetH);
        } else if (startW < startH && startH > targetW) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / targetW);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;//降低图片从ARGB888到RGB565
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmap;
    }
	
    public static float dip2px(Activity context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (dpValue * scale + 0.5f);  
    }

	
  //是否有相机权限
    public static boolean cameraIsCanUse() {  
        boolean isCanUse = true;  
        Camera mCamera = null;  
        try {  
            mCamera = Camera.open();  
            Camera.Parameters mParameters = mCamera.getParameters(); //针对魅族手机  
            mCamera.setParameters(mParameters);  
        } catch (Exception e) {  
            isCanUse = false;  
        }  
        if (mCamera != null) {  
            try {  
                mCamera.release();  
            } catch (Exception e) {  
                e.printStackTrace();  
                return isCanUse;  
            }  
        }  
        return isCanUse;  
    }  
  
}
