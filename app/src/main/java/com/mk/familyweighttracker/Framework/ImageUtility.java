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

import com.mk.familyweighttracker.Enums.HeightUnit;
import com.mk.familyweighttracker.Enums.WeightUnit;
import com.mk.familyweighttracker.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by mvalhekar on 15-04-2016.
 */
public class ImageUtility {
    private static final int SixHundred = 600;
    private static final int EightHundred = 800;

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
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

    public static Bitmap resizeBitmap(Bitmap image) {
        int maxWidth = EightHundred, maxHeight = SixHundred;
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        double imageAspect = (double) imageWidth / imageHeight;
        double canvasAspect = (double) maxWidth / maxHeight;
        double scaleFactor;

        if (imageAspect < canvasAspect)
            scaleFactor = (double) maxHeight / imageHeight;
        else
            scaleFactor = (double) maxWidth / imageWidth;

        float scaleWidth = ((float) scaleFactor) * imageWidth;
        float scaleHeight = ((float) scaleFactor) * imageHeight;

        return Bitmap.createScaledBitmap(image, (int) scaleWidth, (int) scaleHeight, true);
    }

    public static Bitmap compressImage(String filePath) {
        //http://voidcanvas.com/whatsapp-like-image-compression-in-android/

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

        // by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        // you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        // max Height and width values of the compressed image is taken as 816x612
        float maxHeight = 150.0f;
        float maxWidth = 150.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        // width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        // setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        // inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

        // this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            // load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        // check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(Constants.LogTag.App, e.getMessage());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            // write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(Constants.LogTag.App, e.getMessage());
        }

        return scaledBitmap;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
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
