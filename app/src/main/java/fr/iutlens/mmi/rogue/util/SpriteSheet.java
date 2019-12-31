package fr.iutlens.mmi.rogue.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.HashMap;
import java.util.Map;

public class SpriteSheet {
	private Bitmap bitmap;
	private Bitmap[] sprite;
	public final int n,m;
	public int w,h;
	private RectF dst;
	private Rect src;
	
	private static Map<Integer,SpriteSheet> map;
	private static Paint paint;
	
	static {
		map = new HashMap();
		paint = new Paint();
		paint.setAntiAlias(true);
	}

	public static void register(int id, int n, int m, Context context){
		map.put(id,new SpriteSheet(n,m));
		if (context != null){
			get(context, id);
		}
	}

    public static void register(int id1, int id2, int n, int m, Context context){
        map.put(id1,new SpriteSheet(n,m));
        if (context != null){
            get(context, id1,id2);
        }
    }
	
	
	public SpriteSheet(int n, int m){
		this.n = n;
		this.m = m;
		
		src = new Rect();
		dst = new RectF();
		
		bitmap = null;
		sprite = null;
	}
	
	public SpriteSheet(Context context, int id, int n, int m){
		this(n,m);
		load(context,id);
		
	}

	private void load(Context context, int id) {
		bitmap = Utils.loadImage(context, id);
		w = bitmap.getWidth() / n;
		h = bitmap.getHeight() / m;

    }

    private void load(Context context, int id1, int id2) {
        bitmap = Utils.loadImages(context, id2, id1);
        w = bitmap.getWidth() / n;
        h = bitmap.getHeight() / m;

    }


    public void paint(Canvas canvas, int ndx, float x, float y){
	/*	int i = ndx%n;
		int j = ndx/n;
		src.set(i*w, j*h, (i+1)*w-1, (j+1)*h-1);
		dst.set(x,y,x+w,y+h);
		canvas.drawBitmap(bitmap, src, dst, paint); */
		canvas.drawBitmap(getBitmap(ndx), x,y, paint);
	}

    public void paint(Canvas canvas, int ndx, float x, float y, Paint p){
        canvas.drawBitmap(getBitmap(ndx), x,y, p);
    }

	public void paint(Canvas canvas, int ndx, float x, float y, int color) {
		canvas.drawBitmap(getBitmap(ndx), x,y, Utils.getColorFilterPaint(color));
	}

	public Bitmap getBitmap(int ndx){
		if (sprite == null) sprite = new Bitmap[n*m];
		if (sprite[ndx] == null){
			int i = ndx%n;
			int j = ndx/n;
			sprite[ndx] = createCroppedBitmap(bitmap, i*w, j*h, w, h);
		}
		return sprite[ndx];
	}
	
	public static Bitmap createCroppedBitmap(Bitmap src, int left, int top, int width, int height) {
		  /*
		    bug: returns incorrect region, so must do it manually
		    return Bitmap.createBitmap(src, left, top,width, height);
		  */
		  int stride = width; // ints per row, if want padding at end of row, make stride larger
		  int offset = 0;
		  int []pixels = new int[width*height];
		  src.getPixels(pixels, offset, stride, left, top, width, height);
		  return Bitmap.createBitmap(pixels, width, height, src.getConfig());
		}
	

	public static SpriteSheet get(Context context, int id) {
		SpriteSheet result = map.get(id);
		if (result.bitmap == null) result.load(context, id);
		return result;
	}

    public static SpriteSheet get(Context context, int id1, int id2) {
        SpriteSheet result = map.get(id1);
        if (result.bitmap == null) result.load(context, id1, id2);
        return result;
    }

	public static SpriteSheet get(int id) {
		SpriteSheet result = map.get(id);
		return result;
	}

}
