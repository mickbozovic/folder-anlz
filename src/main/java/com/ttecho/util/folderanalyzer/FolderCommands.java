package com.ttecho.util.folderanalyzer;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

@Component
public class FolderCommands implements CommandMarker {

	/**
	 * spring shell command that will be executed when called from the
	 * shell
	 * 
	 * @param folderPath
	 * @param dateFilter
	 * @return
	 */
	@CliCommand(value = "anlz", help = "Analyze Folders")
	public String analyze(
			@CliOption(key = { "",
			"folder path" }, mandatory = true, help = "The folder path to analyze") final String folderPath,
			@CliOption(key = { "date" }, mandatory = false, help = "Date to filter results --date yyyy-mm-dd") final String dateFilter) {

		return directoryWalk(folderPath, dateFilter);

	}

	/**
	 * Does the directory/folder walk, counts number of files in each
	 * sub-directory and counts the total number of files in the given directory
	 * accepts folder name and date filter as optional.
	 * 
	 * @param folderPath
	 * @param dateFilter
	 * @return name of the file that analasis is saved to
	 */
	private String directoryWalk(String folderPath, final String dateFilter) {
		// System.out.println("directoryWalk");
		final Properties dirProperties = new Properties();
		Set folders;
		String folder;
		
		// output file name will have a time stamp 
		LocalDateTime ldt = LocalDateTime.now(); 
		String outputFileName = "Output-"+ldt+".csv";
		final Path outputFile = Paths.get(outputFileName);
		
		Path start = Paths.get(folderPath);
		// Path start =
		// Paths.get("/home/ubuntu/2techo/dev/th/folder-analyzer/folders");
		try {
			// should never happen with milisecond timestamp, but just in case
			Files.deleteIfExists(outputFile);
			Files.createFile(outputFile);
			//Files.write(outputFile, "File List \n".getBytes(), StandardOpenOption.CREATE);
			
			Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

				int fileCount = 0;

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					// System.out.println("File visited: " + file);
					
					// get the first 10 characters of the file creation time yyyy-mm-dd
					String fileCreationTime = attrs.creationTime().toString().substring(0, 10);
					//System.out.println("file creation time: "+ fileCreationTime);
					String fileAndTime = file.toString()+","+fileCreationTime;
					//writeToOutputFile(outputFile, file.toString());
					
					//writeToOutputFile(outputFile, fileAndTime);
					
					// System.out.println("File creation time: " +
					// attrs.creationTime());
					// BasicFileAttributes attrs = Files.readAttributes(file,
					// BasicFileAttributes.class);
					
					//System.out.println("date filter: "+ dateFilter);
					
					// if date filter is provided only write the file and 
					// increment the file count if there is a match
					if(dateFilter != null && dateFilter.equals(fileCreationTime))
					{
						//System.out.println("dateFilter match: "+file);
						writeToOutputFile(outputFile, fileAndTime);
						fileCount++;
					}
					
					// if date filter not provided write and increment for every 
					// file in the directory
					else if(dateFilter == null)
					{
						//System.out.println("dateFilter null: "+file);
						writeToOutputFile(outputFile, fileAndTime);
						fileCount++;
					}

					return FileVisitResult.CONTINUE;
				}

				/**
				 * After every directory visit store directory name and file count 
				 * for that directory in the hashmap
				 */
				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
					if (e == null) {

						dirProperties.put(dir.toString(), String.valueOf(fileCount));
						fileCount = 0;
						return FileVisitResult.CONTINUE;
					} else {
						// directory iteration failed
						throw e;
					}
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return e.toString();
		}

		// Show all folders and file count in hashtable.

		StringBuffer buf = new StringBuffer();
		buf.append("=======================================").append(OsUtils.LINE_SEPARATOR);
		buf.append("*          Folder Analysis            *").append(OsUtils.LINE_SEPARATOR);
		buf.append("=======================================").append(OsUtils.LINE_SEPARATOR);
		// buf.append("Folder # of Files").append(OsUtils.LINE_SEPARATOR);

		
		/*
		 * Iteratre through all the stored folders and write the count per folder
		 * into the output file. Also count total of all the folders
		 */
		folders = dirProperties.keySet(); // get set-view of keys
		Iterator itr = folders.iterator();

		writeToOutputFile(outputFile, "SUMMARY");
		
		int totalFiles = 0;
		while (itr.hasNext()) {
			folder = (String) itr.next();
			// System.out.println("The folder " + str + " has " +
			// dirProperties.getProperty(str) + " files");

			String filesCount = dirProperties.getProperty(folder);
			totalFiles = totalFiles + Integer.valueOf(filesCount);
			
			String fileCountToWrite = "Folder " + folder + " has " + filesCount + " files";
			//buf.append("Folder " + folder + " has " + filesCount + " files").append(OsUtils.LINE_SEPARATOR);
			buf.append(fileCountToWrite).append(OsUtils.LINE_SEPARATOR);
			
			
			writeToOutputFile(outputFile, fileCountToWrite);
		}
		
		String totalToWrite = "Total # of files: " + totalFiles;
		//buf.append("Total # of files: " + totalFiles).append(OsUtils.LINE_SEPARATOR);
		buf.append(totalToWrite).append(OsUtils.LINE_SEPARATOR);
		
		writeToOutputFile(outputFile, totalToWrite);
		//return buf.toString();
		
		// return to the user via shell
		return "Output saved into: "+ outputFile.toString();
	}

	/**
	 * Does the actual write to the output file
	 * @param outputFile
	 * @param toWrite
	 */
	private void writeToOutputFile(Path outputFile, String toWrite) {
		String toWriteLocal = toWrite +"\n";
		byte[] strToBytes = toWriteLocal.getBytes();

		try {
			Files.write(outputFile, strToBytes, StandardOpenOption.APPEND);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
