package com.poly.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.poly.entity.PathSaveFile;
import com.poly.service.UploadService;

@Service
public class UploadServiceImpl implements UploadService {
	@Autowired
	HttpServletRequest request;

	@Override
	public File save(MultipartFile file, String path, String fileName) throws IOException {
		if (!file.isEmpty()) {
			String path1 = PathSaveFile.PATH;
			File directory = new File(path1 + path);
			if (!directory.exists()) {
				directory.mkdirs();
			}
			File f = new File(directory, fileName);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(f));
			byte[] data = file.getBytes();
			bufferedOutputStream.write(data);
			bufferedOutputStream.close();

			File dir = new File(request.getServletContext().getRealPath(path));
			if (!dir.exists()) {
				dir.mkdirs();
			}
			try {
				File savedFile = new File(dir, fileName);
				file.transferTo(savedFile);
				return savedFile;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

}
