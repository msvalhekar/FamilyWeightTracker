package com.mk.familyweighttracker.Framework;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mk.familyweighttracker.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mvalhekar on 15-04-2016.
 */
//https://developer.android.com/training/displaying-bitmaps/index.html
public class ImageUtility {
    public static final int SixHundred = 600;
    public static final int EightHundred = 800;
    public static final int SeventyFive = 75;
    public static final int OneHundred = 100;
    public static final int TwoHundred = 200;

    public static Bitmap decodeSampledBitmapFromFile(
            String imagePath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(
                bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    public static void cropImage(final Activity activity, final Uri pickedImageUri, final int requestCode, final CropDetail cropDetail) {

        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        List<ResolveInfo> list = activity.getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();

        if (size == 0) {
            Toast.makeText(activity, "Can not find image cropper app", Toast.LENGTH_SHORT).show();
            // todo: still need to save selected image
            return;
        } else {
            intent.setData(pickedImageUri);
            intent.putExtra("outputX", cropDetail.outputX);
            intent.putExtra("outputY", cropDetail.outputY);
            intent.putExtra("aspectX", cropDetail.aspectX);
            intent.putExtra("aspectY", cropDetail.aspectY);
            intent.putExtra("crop", true);
            intent.putExtra("scale", true);
            intent.putExtra("noFaceDetection", true);

            File destFile = new File(StorageUtility.getTempImagePath());
            try {
                destFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destFile));

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);
                i.setComponent(new ComponentName(res.activityInfo.packageName,  res.activityInfo.name));
                activity.startActivityForResult(i, requestCode);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title = activity.getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = activity.getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(activity, cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Choose Cropper App");
                builder.setAdapter(adapter,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                activity.startActivityForResult(cropOptions.get(item).appIntent, requestCode);
                            }
                        });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (pickedImageUri != null) {
                            activity.getContentResolver().delete(pickedImageUri, null, null);
                            //pickedImageUri = null;
                        }
                    }
                });

                builder.create().show();
            }
        }
    }

    public static String saveImage(String fileName, Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        String imagesDirectory = StorageUtility.getImagesDirectory();
        String filePath = String.format("%s/%s", imagesDirectory, fileName);
        File destination = new File(filePath);

        FileOutputStream fileOutputStream;
        try {
            destination.createNewFile();
            fileOutputStream = new FileOutputStream(destination);
            fileOutputStream.write(outputStream.toByteArray());
            fileOutputStream.flush();
            fileOutputStream.close();
            outputStream = null;
            fileOutputStream = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public static class CropOptionAdapter extends ArrayAdapter<CropOption> {
        private ArrayList<CropOption> mOptions;
        private LayoutInflater mInflater;

        public CropOptionAdapter(Context context, ArrayList<CropOption> options) {
            super(context, R.layout.crop_selector, options);

            mOptions = options;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup group) {
            if (convertView == null)
                convertView = mInflater.inflate(R.layout.crop_selector, null);

            CropOption item = mOptions.get(position);

            if (item != null) {
                ((ImageView) convertView.findViewById(R.id.crop_selector_icon))
                        .setImageDrawable(item.icon);
                ((TextView) convertView.findViewById(R.id.crop_selector_title))
                        .setText(item.title);
                return convertView;
            }
            return null;
        }
    }

    public static class CropOption {
        public CharSequence title;
        public Drawable icon;
        public Intent appIntent;
    }

    public static class CropDetail {
        public int outputX;
        public int outputY;
        public int aspectX;
        public int aspectY;

        public CropDetail(int outputX, int outputY, int aspectX, int aspectY) {
            this.outputX = outputX;
            this.outputY = outputY;
            this.aspectX = aspectX;
            this.aspectY = aspectY;
        }
    }
}
