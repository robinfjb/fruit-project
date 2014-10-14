package com.robin.fruitseller.media;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

public class AlbumController {
	private Context context;
	public AlbumController(Context context) {
		super();
		this.context = context;
	}

	public ArrayList<String> getPhotos(String album_dir) {
        ArrayList<String> photos = new ArrayList<String>();
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[] { MediaStore.Images.Media.DATA };
        Cursor cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        cursor.moveToFirst();
        int fileNum = cursor.getCount();

        for (int counter = 0; counter < fileNum; counter++) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            //获取路径中文件的目录
            String file_dir = getDir(path);
            if(file_dir.equals(album_dir))
                photos.add(path);
            cursor.moveToNext();
        }
        cursor.close();
        return photos;
    }
	
	public ArrayList<AlbumBean> getAlbums() {
        ArrayList<AlbumBean> albums = new ArrayList<AlbumBean>();
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[] { MediaStore.Images.Media.DATA };
        Cursor cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        cursor.moveToFirst();
        int fileNum = cursor.getCount();

        for (int counter = 0; counter < fileNum; counter++) {
            Log.w("tag", "---file is:" + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            String file_dir = getDir(path);
            boolean in_albums = false;//默认不存在于albums中
            for (AlbumBean temp_album : albums) {
                if(temp_album.mName.equals(file_dir)) {
                    in_albums = true;
                    break;
                }
            }

            if(!in_albums)
            {
            	AlbumBean album = new AlbumBean();
                album.mName = getDir(path);
                album.mNum = "(" + getPicNum(album.mName) + ")";
                album.mCoverUrl = path;
                albums.add(album);
            }
            cursor.moveToNext();
        }
        cursor.close();

        return albums;
    }
	
	
	public int getPicNum(String album_file_dir) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[] { MediaStore.Images.Media.DATA };
        Cursor cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        cursor.moveToFirst();
        int fileNum = cursor.getCount();

        int photo_num = 0;
        for (int counter = 0; counter < fileNum; counter++) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            //获取路径中文件的目录
            String file_dir = getDir(path);

            if(album_file_dir.equals(file_dir))
                photo_num++;
            cursor.moveToNext();
        }
        cursor.close();
        return photo_num;
    }
	    
	public String getDir(String path) {
	    String subString = path.substring(0, path.lastIndexOf('/'));
	    return subString.substring(subString.lastIndexOf('/') + 1, subString.length());
	}
}
