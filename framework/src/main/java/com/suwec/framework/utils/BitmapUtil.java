package com.suwec.framework.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Description 位图处理工具(包含将原图转换为圆角图，与base64之间的相互转换，位图质量压缩，图片的截取，图片的保存)
 * @date 2015年6月6日 上午9:55:28
 */
public class BitmapUtil {
	private static final String TAG = "BitmapUtil";
	/***************** 单例模式 *****************/
	/**
	 * @Description BitmapUtil 本类静态对象，防止被创建
	 */
	private static BitmapUtil instance;

	/**
	 * @Description 空构造函数
	 * @author Mayouwei mayouwei@outlook.com
	 * @date 2015年6月6日 上午10:08:02
	 */
	private BitmapUtil() {
	}

	/**
	 * @Description 对外暴露一个方法来获取本类对象
	 * @return BitmapUtil 返回该工具类对象
	 * @author Mayouwei mayouwei@outlook.com
	 * @date 2015年6月6日 上午10:08:11
	 */
	public static BitmapUtil getInstance() {
		if (instance == null) {// 如果本类对象不存在
			instance = new BitmapUtil();// 直接创建一个新的本类对象
		}
		return instance;// 返回本类对象
	}

	/***************** 单例模式 *****************/

	/**
	 * @Description 将图片转换为圆角图片
	 * @param bitmap
	 *            需要被转换为圆角图片的原位图资源
	 * @param radius
	 *            该圆角图片的半径。圆角直径和图片边长的比例。radius=1表示圆形
	 * @return Bitmap 返回转换后的圆角图片。
	 * @date 2015年6月6日 上午10:09:26
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float radius) {
		// 通过接收到位图来创建一个新的位图(接收的位图的尺寸，和属性)
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);// 通过新创建的位图来创建一个画布
		final int color = 0xff424242;// 定义一个颜色值
		final Paint paint = new Paint();// 定义一个画笔
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());// 创建一个矩形
		final RectF rectF = new RectF(rect);// 创建精度更高的矩形
		final float roundPx = bitmap.getWidth() / 2;// 图像的宽度除以二当做圆角图形的半径

		paint.setAntiAlias(true);// 设置抗锯齿
		canvas.drawARGB(0, 0, 0, 0);// 相当于清屏
		paint.setColor(color);// 设置画笔颜色
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 绘制圆角矩形(矩形对象,X轴的半径,Y轴的半径,画笔)
		// canvas原有的图片可以理解为背景，就是dst；
		// 新画上去的图片可以理解为前景，就是src。
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式
		canvas.drawBitmap(bitmap, rect, rect, paint);// 绘制图片(将要绘制出来的位图,前景图可为空,背景图,画笔)
		return output;// 返回最终的位图(画布上的图)
	}

	/**
	 * @Description 将bitmap转为base64
	 * @param bitmap
	 *            所需要转换的bitmap
	 * @return String 返回一个转换后的base64的字符串
	 * @date 2015年6月6日 下午2:34:40
	 */
	public static String bitmapToBase64(Bitmap bitmap) {

		String result = null;// 定义一个结果字符串(用于存储最后的结果)
		ByteArrayOutputStream baos = null;// 定义一个字节数组输出流
		try {
			if (bitmap != null) {// 如果接收到的图片存在
				baos = new ByteArrayOutputStream();// 实例化字节数组输出流对象
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 将图片以JPEG格式，不进行任何压缩，将其读到流中

				baos.flush();// 刷新流

				byte[] bitmapBytes = baos.toByteArray();// 将存有数据的字节数组流转换为字节数组
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT).replace("\n", "");// 将字节数组以字符串的形式存储起来
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {// 如果流存在
					baos.flush();// 刷新流
					baos.close();// 关闭流
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;// 将转换后的base64字符串返回
	}

	/**
	 * 创建纯色bitmap
	 * swc@yitong.com.cn 2017-11-14 下午2:06:12
	 * @param width
	 * @param height
	 * @param color
	 * @return
	 */
	public static Bitmap createBitmapByColor(int width,int height,int color){
		int[] colors = new int[width*height];
		for (int i = 0; i < colors.length; i++) {
			colors[i] = color;
		}
		Bitmap resource = Bitmap.createBitmap(colors, width, height, Config.ARGB_8888);
		return resource;
	}

	public static String bitmapTranBase64(Bitmap bitmap,int quality){
		// 要返回的字符串
		String reslut = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
				baos.flush();
				baos.close();
				// 转换为字节数组
				byte[] byteArray = baos.toByteArray();
				// 转换为字符串
				reslut = new String(new org.apache.commons.codec.binary.Base64().encode(byteArray));
				return reslut;
			} else {
				return null;
			}
		} catch (IOException e) {
			return null;
		} finally {
			try {
				if (baos != null) {
					baos.close();
				}
			} catch (IOException e) {
			}
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle(); // 回收图片所占的内存
				bitmap = null;
				System.gc(); // 提醒系统及时回收
			}
		}
	}

	public static String bitmapTranBase64(Bitmap bitmap){
		return bitmapTranBase64(bitmap,60);
	}

	/**
	 * @Description 将base64形式的字符串转为bitmap
	 * @param base64Data
	 *            所需要被转换的base64字符串
	 * @return Bitmap 返回转换好的bitmap
	 * @author Mayouwei mayouwei@outlook.com
	 * @date 2015年6月6日 下午2:36:08
	 */
	public static Bitmap base64ToBitmap(String base64Data) {
		byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);// 将接收的字符串以base64的形式解码为字节数组
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);// 将字节数组转换为位图，然后返回该位图
	}

	/**
	 * @Description 图片的裁剪
	 * @param activity
	 *            调用者的上下文
	 * @param uri
	 *            图片的地址
	 * @param outputX
	 *            图片裁剪后的宽
	 * @param outputY
	 *            图片裁剪后的高
	 * @param requestCode
	 *            请求码
	 * @author Mayouwei mayouwei@outlook.com
	 * @date 2015年6月6日 下午3:29:52
	 */
	public static void cropImage(Activity activity, Uri uri, int outputX, int outputY, int requestCode) {
		try {
			Intent intent = new Intent("com.android.camera.action.CROP");
			// 实现对图片的裁剪,必须要设置图片的属性和大小
			// 设置属性，表示获取任意类型的图片
			intent.setDataAndType(uri, "image/*");
			// 设置可以滑动选选择区域的属性,注意这里是字符串"true"
			intent.putExtra("crop", "true");
			// 设置剪切框1:1比例的效果
			// 这个是裁剪时候的 裁剪框的 X方向的比例
			intent.putExtra("aspectX", 1);// 宽高比
			// 同上Y方向的比例. (注意： aspectX, aspectY ，两个值都需要为 整数，如果有一个为浮点数，就会导致比例失效)
			intent.putExtra("aspectY", 1);
			// 返回数据的时候的 X像素大小
			intent.putExtra("outputX", outputX);// 宽高
			// 返回的时候 Y的像素大小
			intent.putExtra("outputY", outputY);
			// 图片输出格式
			intent.putExtra("outputFormat", "JPEG");
			intent.putExtra("scale", true);// 黑边
			intent.putExtra("scaleUpIfNeeded", true);// 黑边
			// 是否去除面部检测， 如果你需要特定的比例去裁剪图片，那么这个一定要去掉，因为它会破坏掉特定的比例
			intent.putExtra("noFaceDetection", true);
			// 是否要返回值
			intent.putExtra("return-data", true);
			activity.startActivityForResult(intent, requestCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description 将图片存储到文件
	 * @param path
	 *            存储的目的路径
	 * @param image
	 *            需要存储的图片
	 * @return boolean 表示是否存储成功
	 * @author Mayouwei mayouwei@outlook.com
	 * @date 2015年6月6日 下午3:31:27
	 */
	public static boolean saveBitmapToFile(String path, Bitmap image) {
		try {
			File file = new File(path);// 通过接收的路径创建一个文件
			if (!file.exists() || file.isDirectory()) {// 如果该文件不存在，并且是一个目录
				file.createNewFile();// 创建一个新文件
			}
			FileOutputStream fos = new FileOutputStream(file);// 创建文件输出流
			image.compress(Bitmap.CompressFormat.JPEG, 100, fos);// 将图片文件转换为流
			fos.flush();// 刷新流
			fos.close();// 关闭流
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @Description 比例压缩方式
	 * @param srcPath
	 *            需要被压缩的图片的地址
	 * @return Bitmap 返回被压缩后的图片
	 * @author Mayouwei mayouwei@outlook.com
	 * @date 2015年6月6日 下午4:39:14
	 */
	public static Bitmap compressImage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();// 创建位图属性
		newOpts.inJustDecodeBounds = true;// 读内容，如果为false，那么就是只读边(为了获取尺寸)不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 从接收的文件全路径和设定的属性来解析出位图
		// options.inSampleSize是以2的指数的倒数被进行放缩。这样，我们可以依靠inSampleSize的值的设定将图片放缩载入，
		// 这样一般情况也就不会出现上述的OOM问题了。现在问题是怎么确定inSampleSize的值？每张图片的放缩大小的比例应该是不一样的！
		// 这样的话就要运行时动态确定。在BitmapFactory.Options中提供了另一个成员inJustDecodeBounds。
		// 设置inJustDecodeBounds为true后，decodeFile并不分配空间，但可计算出原始图片的长度和宽度，即opts.width和opts.height。
		// 有了这两个参数，再通过一定的算法，即可得到一个恰当的inSampleSize。
		newOpts.inSampleSize = computeSampleSize(newOpts, -1, 720 * 480);
		// 这里一定要将其设置回false，因为之前我们将其设置成了true
		newOpts.inJustDecodeBounds = false;

		// int w = newOpts.outWidth;// 获取属性的宽
		// int h = newOpts.outHeight;// 获取属性的高
		// // float hh = 720f;// 自定义高
		// float hh = 480f;// 自定义高
		// float ww = 320f;// 自定义宽
		// int be = 1;// 定义默认采样率
		// if (w > h && w > ww) {
		// be = (int) (newOpts.outWidth / ww);
		// } else if (w < h && h > hh) {
		// be = (int) (newOpts.outHeight / hh);
		// }
		// if (be <= 0)
		// be = 1;
		// newOpts.inSampleSize = be;// 设置采样率

		newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return bitmap;
	}

	/**
	 * @description BitmapUtil compressPicture 质量压缩图片
	 * @param srcPath
	 *            需要被压缩的图片的地址
	 * @return Bitmap 返回被压缩后的图片
	 * @author 马有为 email: mayouwei@outlook.com
	 * @date 2015年10月12日 下午4:08:42
	 */
	public static Bitmap compressPicture(String srcPath) {
		return compressPicture(BitmapFactory.decodeFile(srcPath));
	}

	public static Bitmap compressPicture(Bitmap bitmaps){
		Bitmap image = setBitmapSize(bitmaps,1024);
		if (image == null)
			return null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 >= 100 && options > 0) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			options -= 10;// 每次都减少10
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	public static Bitmap setBitmapSize(Bitmap bm,int newWidth){
		if (bm == null)
			return null;
		int width = bm.getWidth();
		int height = bm.getHeight();
		// 设置想要的大小
//    int newWidth = 800;
//    int newHeight = 1200;
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
//		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleWidth);
		Bitmap mbitmap = Bitmap.createBitmap(bm,0,0,width,height,matrix,true);
		return mbitmap;
	}

	public static Bitmap getViewBitmap(View view){
		view.setDrawingCacheEnabled(true);
		//measure()实际测量 自己显示在屏幕上的宽高 2个参数，int widthMeasureSpec 和 int heightMeasureSpec表示具体的测量规则。
		view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		//确定View的大小和位置的,然后将其绘制出来
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		//调用getDrawingCache方法就可 以获得view的cache图片
		Bitmap bb = Bitmap.createBitmap(view.getDrawingCache());
		//系统把原来的cache销毁
		view.setDrawingCacheEnabled(false);
		return bb;
	}

	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128
				: (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * @description BitmapUtil getPath 根据URI获取图片路经
	 * @param context
	 *            调用者的上下文
	 * @param uri
	 *            图片的URI
	 * @return String 类型的图片路经
	 * @author 马有为 email: mayouwei@outlook.com
	 * @date 2015年7月12日 下午8:11:34
	 */
	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;// 版本信息是否高于Android4.4
		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) { // ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
			} else if (isDownloadsDocument(uri)) {// DownloadsProvider
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(uri)) {// MediaProvider
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };
				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		} else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore
																	// (and
																	// general)
			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();
			return getDataColumn(context, uri, null, null);
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
			return uri.getPath();
		}
		return null;
	}

	/**
	 * * Get the value of the data column for this Uri. This is useful for *
	 * MediaStore Uris, and other file-based ContentProviders. * @param context
	 * The context. * @param uri The Uri to query. * @param selection (Optional)
	 * Filter used in the query. * @param selectionArgs (Optional) Selection
	 * arguments used in the query. * @return The value of the _data column,
	 * which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	public static String selectImage(Context context, Intent data) {
		Uri selectedImage = data.getData();
		// Log.e(TAG, selectedImage.toString());
		if (selectedImage != null) {
			String uriStr = selectedImage.toString();
			String path = uriStr.substring(10, uriStr.length());
			if (path.startsWith("com.sec.android.gallery3d")) {
				Logs.e(TAG, "It's auto backup pic path:" + selectedImage.toString());
				return null;
			}
		}
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String picturePath = cursor.getString(columnIndex);
		cursor.close();
		return picturePath;
	}

	/**
	 * 图片旋转
	 * */
	public static Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
		Matrix m = new Matrix();
		m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
		float targetX, targetY;
		if (orientationDegree == 90) {
			targetX = bm.getHeight();
			targetY = 0;
		} else {
			targetX = bm.getHeight();
			targetY = bm.getWidth();
		}

		final float[] values = new float[9];
		m.getValues(values);

		float x1 = values[Matrix.MTRANS_X];
		float y1 = values[Matrix.MTRANS_Y];

		m.postTranslate(targetX - x1, targetY - y1);

		Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Config.ARGB_8888);

		Paint paint = new Paint();
		Canvas canvas = new Canvas(bm1);
		canvas.drawBitmap(bm, m, paint);
		return bm1;
	}

	/**
	 * * @param uri The Uri to check. * @return Whether the Uri authority is
	 * ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * * @param uri The Uri to check. * @return Whether the Uri authority is
	 * DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * * @param uri The Uri to check. * @return Whether the Uri authority is
	 * MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * * @param uri The Uri to check. * @return Whether the Uri authority is
	 * Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
}
