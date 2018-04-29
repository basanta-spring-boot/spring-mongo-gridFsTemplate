package com.basant.spring.data.mongo.api.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

@RestController
public class MongoBlobController {

	@Autowired
	private GridFsOperations gridFsOperations;

	/*
	 * this variable is used to store ImageId for other actions like: findOne or
	 * delete
	 */
	private String imageFileId = "";

	@GetMapping("/save")
	public String saveFile() throws FileNotFoundException {

		// Define metaData
		DBObject metaData = new BasicDBObject();
		metaData.put("organization", "Java Techie");

		/**
		 * 1. save an image file to MongoDB
		 */

		// Get input file
		InputStream iamgeStream = new FileInputStream("C:/Users/bahota/Desktop/YouTube/logo1.png");

		metaData.put("type", "image");

		// Store file to MongoDB
		imageFileId = gridFsOperations.store(iamgeStream, "logo1.png", "image/png", metaData).getId().toString();

		System.out.println("ImageFileId = " + imageFileId);
		/**
		 * 2. save text files to MongoDB
		 */

		// change metaData
		metaData.put("type", "data");

		// Store files to MongoDb
		gridFsOperations.store(new FileInputStream("C:/Users/bahota/Desktop/YouTube/Youtube-Task.txt"),
				"Youtube-Task.txt", "text/plain", metaData);
		return "Done";
	}

	@GetMapping("/retriveImage")
	public String retriveImageFile() throws IOException {
		// read file from MongoDB
		GridFSDBFile imageFile = gridFsOperations.findOne(new Query(Criteria.where("_id").is(imageFileId)));

		// Save file back to local disk
		imageFile.writeTo("C:/Users/bahota/Desktop/Local-disk/myImage.png");

		System.out.println("Image File Name:" + imageFile.getFilename());

		return "Done";

	}

	@GetMapping("/retrieve/textfiles")
	public String retrieveTextFiles() {
		/**
		 * get all data files then save to local disk
		 */

		// Retrieve all data files
		List<GridFSDBFile> textFiles = gridFsOperations.find(new Query(Criteria.where("metadata.type").is("data")));

		// Save all back to local disk
		textFiles.forEach(file -> {

			try {
				String fileName = file.getFilename();

				file.writeTo("C:/Users/bahota/Desktop/Local-disk/" + fileName);

				System.out.println("Text File Name: " + fileName);

			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		return "Done";
	}

}
