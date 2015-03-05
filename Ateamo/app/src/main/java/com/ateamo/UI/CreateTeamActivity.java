package com.ateamo.UI;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ateamo.ateamo.R;

import yuku.ambilwarna.AmbilWarnaDialog;

public class CreateTeamActivity extends Activity {

    private ImageView leftBadgeImageView;
    private Bitmap leftMask;
    private ImageView rightBadgeImageView;
    private Bitmap rightMask;

    private int colorLeft = Color.DKGRAY;
    private GradientDrawable gradientDrawableLeft;
    private int colorRight = Color.LTGRAY;
    private GradientDrawable gradientDrawableRight;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);
        initBadge();
        gradientDrawableLeft = getGradientDrawable(true);
        final Button badgeLeftButton = (Button) findViewById(R.id.badgeLeftButton);
        badgeLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPickerDialog(badgeLeftButton, true, colorLeft);
            }
        });
        badgeLeftButton.setBackground(gradientDrawableLeft);
        final Button badgeRightButton = (Button) findViewById(R.id.badgeRightButton);
        badgeRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPickerDialog(badgeRightButton, false, colorRight);
            }
        });
        gradientDrawableRight = getGradientDrawable(false);
        badgeRightButton.setBackground(gradientDrawableRight);
    }



    private void initBadge() {
        leftBadgeImageView = (ImageView) findViewById(R.id.leftBadgeImageView);
        leftMask = BitmapFactory.decodeResource(getResources(), R.drawable.badge_left_mask);
        colorizeBadgePart(leftBadgeImageView, colorLeft, leftMask);
        rightBadgeImageView = (ImageView) findViewById(R.id.rightBadgeImageView);
        rightMask = BitmapFactory.decodeResource(getResources(), R.drawable.badge_right_mask);
        colorizeBadgePart(rightBadgeImageView, colorRight, rightMask);
    }



    private void colorizeBadgePart(ImageView badgeImageView, int color, Bitmap mask) {
        Rect rect = new Rect(0, 0, 1, 1);
        Bitmap image = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(rect, paint);


        Bitmap original = image;
        original = Bitmap.createScaledBitmap(original, mask.getWidth(), mask.getHeight(), true);

        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(result);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mCanvas.drawBitmap(original, 0, 0, null);
        mCanvas.drawBitmap(mask, 0, 0, paint);
        paint.setXfermode(null);
        badgeImageView.setImageBitmap(result);
    }


    private GradientDrawable getGradientDrawable(boolean left) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(left ? colorLeft : colorRight);
        gradientDrawable.setCornerRadius(10);
        return gradientDrawable;
    }



    void openColorPickerDialog(final Button button, final boolean left, int color) {
        new AmbilWarnaDialog(CreateTeamActivity.this, color, false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                if (left) {
                    colorLeft = color;
                    colorizeBadgePart(leftBadgeImageView, colorLeft, leftMask);
                } else {
                    colorRight = color;
                    colorizeBadgePart(rightBadgeImageView, colorRight, rightMask);
                }
//                Drawable drawable0 = button.getBackground();
//                StateListDrawable drawable1 = (StateListDrawable) button.getBackground();
                GradientDrawable drawable = (GradientDrawable) button.getBackground();
                drawable.setColor(color);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }

        }).show();
    }
}
