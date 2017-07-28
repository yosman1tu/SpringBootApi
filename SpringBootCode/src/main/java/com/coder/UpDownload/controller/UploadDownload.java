package com.coder.UpDownload.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.coder.UpDownload.storage.StorageService;

@Controller
public class UploadDownload {
	
	Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	StorageService storageService;

	List<String> files = new ArrayList<String>();

	@GetMapping("/")
	public String listUploadedFiles(Model model) {
		return "uploadForm";
	}

	@PostMapping("/")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
		try {
			storageService.store(file);
			model.addAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");
			files.add(file.getOriginalFilename());
		} catch (Exception e) {
			model.addAttribute("message", "FAIL to upload " + file.getOriginalFilename() + "!");
		}
		return "uploadForm";
	}

	@GetMapping("/gellallfiles")
	public String getListFiles(Model model) {
		model.addAttribute("files",
				files.stream()
						.map(fileName -> MvcUriComponentsBuilder
								.fromMethodName(UploadDownload.class, "getFile", fileName).build().toString())
						.collect(Collectors.toList()));
		model.addAttribute("totalFiles", "TotalFiles: " + files.size());
		return "listFiles";
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> getFile(@PathVariable String filename) {
		Resource file = storageService.loadFile(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}
	
	@GetMapping("/download")
	@PostMapping("/")
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public void getFile(HttpServletRequest request, HttpServletResponse response){
		//File file = new File("Downloads/myPDFfile.pdf");
		

		
		
		ServletContext context = request.getServletContext();
		String fileToDownload = ("/Users/yahyaosman/Pictures/Candy.png");
		File file = new File(fileToDownload);
		try
		{
		InputStream inputStream = new FileInputStream(file);
		response.setContentType(context.getMimeType(fileToDownload));
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", fileToDownload));
		response.setHeader("Content-Length", String.valueOf(file.length()));
		IOUtils.copy(inputStream, response.getOutputStream());
		response.flushBuffer();
		//response.setContentLength((int) file.length()); 
		
		}catch(FileNotFoundException ex)
		{
			log.info("file not found");
		}catch (IOException ex)
		{
			log.info("No response");
		}
	   }
	
}