package com.viettel.dms.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;

public class FileUtils {
	public static void openFile(Context context, File url) throws Exception {
		// Create URI
        Uri uri = Uri.fromFile(url);

		Intent intent = new Intent(Intent.ACTION_VIEW);
		// Check what kind of file you are trying to open, by comparing the url
		// with extensions.
		// When the if condition is matched, plugin sets the correct intent
		// (mime) type,
		// so Android knew what application to use to open the file
		if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
			// Word document
			intent.setDataAndType(uri, "application/msword");
		} else if (url.toString().contains(".pdf")) {
			// PDF file
			intent.setDataAndType(uri, "application/pdf");
		} else if (url.toString().contains(".ppt")
				|| url.toString().contains(".pptx")) {
			// Powerpoint file
			intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		} else if (url.toString().contains(".xls")
				|| url.toString().contains(".xlsx")) {
			// Excel file
			intent.setDataAndType(uri, "application/vnd.ms-excel");
		} else if (url.toString().contains(".zip")
				|| url.toString().contains(".rar")) {
			 // ZIP Files
            intent.setDataAndType(uri, "application/zip");
		} else if (url.toString().contains(".rtf")) {
			// RTF file
			intent.setDataAndType(uri, "application/rtf");
		} else if (url.toString().contains(".wav")
				|| url.toString().contains(".mp3")) {
			// WAV audio file
			intent.setDataAndType(uri, "audio/x-wav");
		} else if (url.toString().contains(".gif")) {
			// GIF file
			intent.setDataAndType(uri, "image/gif");
		} else if (url.toString().contains(".jpg")
				|| url.toString().contains(".jpeg")
				|| url.toString().contains(".png")) {
			// JPG file
			intent.setDataAndType(uri, "image/jpeg");
		} else if (url.toString().contains(".txt")) {
			// Text file
			intent.setDataAndType(uri, "text/plain");
		} else if (url.toString().contains(".3gp")
				|| url.toString().contains(".mpg")
				|| url.toString().contains(".mpeg")
				|| url.toString().contains(".mpe")
				|| url.toString().contains(".mp4")
				|| url.toString().contains(".avi")) {
			// Video files
			intent.setDataAndType(uri, "video/*");
		} else {
			// if you want you can also define the intent type for any other
			// file

			// additionally use else clause below, to manage other unknown
			// extensions
			// in this case, Android will show all applications installed on the
			// device
			// so you can choose which application to use
			intent.setDataAndType(uri, "*/*");
		}

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	public static String getStringSizeLengthFile(long size) {

	    DecimalFormat df = new DecimalFormat("#.##");

	    float sizeKb = 1024.0f;
	    float sizeMb = sizeKb * sizeKb;
	    float sizeGb = sizeMb * sizeKb;
	    float sizeTerra = sizeGb * sizeKb; 

	    if(size < sizeMb)
	        return df.format(size / sizeKb)+ " Kb";
	    else if(size < sizeGb)
	        return df.format(size / sizeMb) + " Mb";
	    else if(size < sizeTerra)
	        return df.format(size / sizeGb) + " Gb";

	    return "";
	}

    public static void saveObject(Context context, String fileName, Object data) {
        FileOutputStream fos = null;
        ObjectOutputStream os = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.writeObject(data);
        } catch (IOException e) {
            Log.e("FileUtils", "Cannot write to file", e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void deleteFileObject(Context context, String fileName) {
        context.deleteFile(fileName);
    }

    public static <T> T loadObject(Context context, String fileName, Class<T> clazz) {
        T result;
        FileInputStream inStream = null;
        FileInputStream fis = null;
        ObjectInputStream is = null;
        try {
            fis = context.openFileInput(fileName);
            is = new ObjectInputStream(fis);
            result = clazz.cast(is.readObject());
            return result;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
