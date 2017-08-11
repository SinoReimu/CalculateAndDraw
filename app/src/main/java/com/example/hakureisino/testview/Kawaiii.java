package com.example.hakureisino.testview;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.ref.WeakReference;

import static android.R.attr.type;

/**
 * Created by HakureiSino on 2017/8/11.
 */

public class Kawaiii extends View {

    public Kawaiii(Context context) {
        super(context);
    }
    public Kawaiii(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }
    public Kawaiii(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Context mContext;
    private Paint paint;
    private Bitmap maskbitmap;
    private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private WeakReference<Bitmap> mWeakBitmap;

    private void init () {
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap = null;
        if(null!=mWeakBitmap) bitmap = mWeakBitmap.get();
        if(null==bitmap||bitmap.isRecycled()) {
            Log.i("hakurei", "genereate");
            Drawable drawable = getResources().getDrawable(R.drawable.back);
            if (drawable != null) {
                Log.i("hakurei2", "enterMask");
                bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                Canvas mCanvas = new Canvas(bitmap);
                drawable.setBounds(0, 0,getWidth(),
                        getHeight());
                drawable.draw(mCanvas);
                if (maskbitmap == null || maskbitmap.isRecycled()) maskbitmap = getMask();
                else         Log.i("hakurei2", "enterMask");
                paint.reset();
                paint.setFilterBitmap(false);
                paint.setXfermode(mXfermode);
                //绘制形状
                mCanvas.drawBitmap(maskbitmap, 0, 0, paint);
                paint.setXfermode(null);
                drawObject(canvas, bitmap);
                drawText(canvas);
            }
        } else {
            Log.i("hakurei2", "enterMask");
            paint.setXfermode(null);
            drawObject(canvas, bitmap);
            drawText(canvas);
        }
    }

    private void drawObject(Canvas canvas, Bitmap bitmap) {
        paint.reset();
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
    }

    private void drawText(Canvas canvas) {
        paint.reset();
        paint.setAntiAlias(true);
        paint.setTextSize(34);
        paint.setColor(Color.GREEN);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("赏", getWidth()/2, getHeight()/1.8f, paint);
    }

    public Bitmap getMask()
    {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);

        // 计算切割线
        // 三角高度为100dp 宽度为屏幕宽度 先换算出角度
        // 该控件距离右边40dp 高度宽度28dp
        // 所以右边截取宽度为  dp2px(40)/width*dp2px(100)
        // 左边为 dp2px(68)/width*dp2px(100)

        DisplayMetrics metric = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(metric);
        float width = 1.0f*metric.widthPixels;

        float r = DensityUtil.dip2px(getContext(), 41)/width*DensityUtil.dip2px(getContext(), 100);
        float l = DensityUtil.dip2px(getContext(), 68)/width*DensityUtil.dip2px(getContext(), 100);
        float z = l+r;
        Log.w("waaa", "r:"+DensityUtil.dip2px(getContext(), 41)+" l:"+" width:"+DensityUtil.dip2px(getContext(), 100));
        Log.w("waaa", "r:"+r+" l:"+l+" width:"+width);
        Path mPath=new Path();
        mPath.moveTo(0, l);
        mPath.lineTo(getWidth(), r);
        mPath.lineTo(getWidth(), getHeight());
        mPath.lineTo(0, getHeight());
        mPath.close();

        canvas.drawPath(mPath, paint);

        return bitmap;
    }

}
