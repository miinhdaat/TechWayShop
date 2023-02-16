package com.poly.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.poly.service.DeleteJunkImages;

@Service
public class DeleteJunkImagesImpl implements DeleteJunkImages{

	@Override
	public void DeleteI(String dirr, String[] db) {
		File dir = new File(dirr);
		String[] a = dir.list();
		String[] b = db;
		List<String> b1 = new ArrayList<>(Arrays.asList(b));

		for (int i = 0; i < a.length; i++) {
			if((b1.contains(a[i]))==false) {
				Path path = Paths.get(dir+"/"+a[i]);
				try {
					Files.delete(path);
					System.out.println("Xoa thanh cong file: " + path);
				} catch (Exception e) {
					System.out.println("Xoa that bai file: " + path);
				}
			}
		}
	}

}
