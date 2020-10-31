package com.kushal.kotlinuniversalvideoplayer.helpers;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Video.Media;

import com.kushal.kotlinuniversalvideoplayer.model.VideoFolderModel;
import com.kushal.kotlinuniversalvideoplayer.model.VideosModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideosAndFoldersUtility {

    private String[] VIDEO_COLUMNS = {"_id", "_display_name", "title", "date_added", "duration", "resolution", "_size", "_data", "mime_type"};
    private Context context;
    private List<VideosModel> videos = new ArrayList();

    public VideosAndFoldersUtility(Context context2) {
        this.context = context2.getApplicationContext();
    }

    private List<VideosModel> getVideosFromCursor(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        while (cursor.moveToNext()) {
            try {
                VideosModel videoModel = new VideosModel();
                videoModel.set_ID(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow("_id"))));
                videoModel.setName(cursor.getString(cursor.getColumnIndexOrThrow("_display_name")));
                videoModel.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                videoModel.setDateAdded(TheUtility.humanReadableDate(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow("date_added"))) * 1000));
                videoModel.setDuration(TheUtility.parseTimeFromMilliseconds(cursor.getString(cursor.getColumnIndex("duration"))));
                videoModel.setResolution(cursor.getString(cursor.getColumnIndexOrThrow("resolution")));
                videoModel.setSize(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow("_size"))));
                videoModel.setSizeReadable(TheUtility.humanReadableByteCount(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow("_size"))), false));
                videoModel.setData(cursor.getString(cursor.getColumnIndexOrThrow("_data")));
                videoModel.setTime(cursor.getString(cursor.getColumnIndexOrThrow("mime_type")));
                arrayList.add(videoModel);
            } catch (Exception unused) {
            }
        }
        return arrayList;
    }

    public void deleteFolders(List<String> list) {
        for (String deleteVideosByFolder : list) {
            deleteVideosByFolder(deleteVideosByFolder);
        }
    }

    public void deleteVideos(List<Long> list) {
        for (Long longValue : list) {
            context.getContentResolver().delete(ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, longValue.longValue()), null, null);
        }
    }

    public boolean deleteVideosByFolder(String str) {
        File file = new File(str);
        if (file.exists() && file.isDirectory()) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("%");
            sb.toString();
            Cursor query = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "_data"}, "_data Like ?", new String[]{str}, "date_added DESC");
            while (query.moveToNext()) {
                if (new File(query.getString(query.getColumnIndexOrThrow("_data"))).getParent().equalsIgnoreCase(str)) {
                    context.getContentResolver().delete(ContentUris.withAppendedId(Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(query.getString(query.getColumnIndexOrThrow("_id")))), null, null);
                }
            }
        }
        return file.delete();
    }

    public List<VideoFolderModel> fetchAllFolders() {

        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        String str = "klsdf";
        VideoFolderModel folderModel = null;
        int i = 0;
        for (VideosModel videoModel : this.videos) {
            String parent = new File(videoModel.getData()).getParent();
            String name = new File(parent).getName();
            VideoFolderModel folderModel1 = new VideoFolderModel();
            folderModel1.setName(name);
            folderModel1.setPath(parent);
            folderModel1.videosPP();
            folderModel1.sizePP(videoModel.getSize());
            String name2 = folderModel1.getName();
            if (i == 0) {
                arrayList.add(folderModel1);
                str = folderModel1.getName();
                arrayList2.add(str);
                folderModel = folderModel1;
            } else if (i != 0 && !str.equals(name2) && !arrayList2.contains(name2)) {
                arrayList.add(folderModel1);
                str = folderModel1.getName();
                arrayList2.add(str);
                folderModel = folderModel1;
            } else if (i != 0) {
                folderModel.videosPP();
            }
            i++;
        }
        return arrayList;
    }

    public List<VideosModel> fetchAllVideos() {
        Cursor query = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, VIDEO_COLUMNS, null, null, "date_added DESC");
        if (query != null) {
            videos = getVideosFromCursor(query);
            query.close();
        }
        return this.videos;
    }

    @SuppressLint({"Recycle"})
    public List<VideosModel> fetchVideosByFolder(String str) {
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("%");
        Cursor query = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, VIDEO_COLUMNS, "_data Like ?", new String[]{sb.toString()}, "date_added DESC");
        while (query.moveToNext()) {
            if (new File(query.getString(query.getColumnIndexOrThrow("_data"))).getParent().equalsIgnoreCase(str)) {
                VideosModel model = new VideosModel();
                model.set_ID(Long.parseLong(query.getString(query.getColumnIndexOrThrow("_id"))));
                model.setName(query.getString(query.getColumnIndexOrThrow("_display_name")));
                model.setTitle(query.getString(query.getColumnIndexOrThrow("title")));
                model.setDateAdded(TheUtility.humanReadableDate(Long.parseLong(query.getString(query.getColumnIndexOrThrow("date_added"))) * 1000));
                model.setDuration(TheUtility.parseTimeFromMilliseconds(query.getString(query.getColumnIndexOrThrow("duration"))));
                model.setResolution(query.getString(query.getColumnIndexOrThrow("resolution")));
                model.setSize(Long.parseLong(query.getString(query.getColumnIndexOrThrow("_size"))));
                model.setSizeReadable(TheUtility.humanReadableByteCount(Long.parseLong(query.getString(query.getColumnIndexOrThrow("_size"))), false));
                model.setData(query.getString(query.getColumnIndexOrThrow("_data")));
                model.setTime(query.getString(query.getColumnIndexOrThrow("mime_type")));
                arrayList.add(model);
            }
        }
        return arrayList;
    }

    public void deleteVideo(Uri uri) {
        File file = new File(uri.getPath());
        file.delete();
        context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file)));
    }
}
