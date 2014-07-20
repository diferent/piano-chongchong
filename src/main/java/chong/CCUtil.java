package chong;

import ivy.core.tool.Str;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 */
public class CCUtil {

	public static final String OUTPUTDIR = "H:/mp3/download";

	public static void save(InputStream input, String filename, String type) {
		File file = outputFile(filename, type);
		FileOutputStream output = null;
		try {
			System.out.println("正在准备下载MP3:" + file.getName());
			output = new FileOutputStream(file);
			IOUtils.copy(input, output);
			System.out.println("下载完成!");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
		}
	}

	public static File outputFile(String filename, String type) {
		File dir = null;
		if (Str.isNotEmpty(type)) {
			dir = new File(OUTPUTDIR, type);
		} else {
			dir = new File(OUTPUTDIR);
		}
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, filename);
		return file;
	}

}
