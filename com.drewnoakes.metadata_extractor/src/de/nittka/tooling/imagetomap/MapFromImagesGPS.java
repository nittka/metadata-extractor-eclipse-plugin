package de.nittka.tooling.imagetomap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;

public class MapFromImagesGPS {

	public static void main(String[] args) {
		File folder = getFolderFromArgs(args);
		if(!folder.exists() || !folder.isDirectory()){
			System.err.println("no folder "+folder);
			return;
		}
		try {
			System.out.println("processing "+folder.getAbsolutePath());
			GpsJsonCreator visitor = new GpsJsonCreator();
			Files.walkFileTree(folder.toPath(), visitor);
			writeGpsDataFiles(visitor);
			writeMainHtml();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static File getFolderFromArgs(String[] args){
		if(args.length==1){
			return new File(args[0]);
		}
		return new File(".");
	}

	private static void writeGpsDataFiles(GpsJsonCreator jsonCreator) throws IOException{
		List<String> data = jsonCreator.getJsonData();
		StringBuilder b=new StringBuilder("var gpsMarkers=[\n  ");
		b.append(String.join(",\n  ", data));
		b.append("\n];");
		File markerFile=new File("markers.js");
		Files.write(markerFile.toPath(), b.toString().getBytes());
		if(data.isEmpty()){
			System.err.println("no files with gps data found");
		}
	}

	private static void writeMainHtml() throws IOException{
		File htmlFile=new File("imageGps.html");
		if(htmlFile.exists()){
			return;
		}
		InputStream stream = MapFromImagesGPS.class.getClassLoader().getResourceAsStream("ol2Marker.html");
		byte[] content=new byte[stream.available()];
		stream.read(content);
		Files.write(htmlFile.toPath(), content);
	}

	private static class GpsJsonCreator implements FileVisitor<Path>{

		int imageCount=0;
		private List<String> jsonData=new ArrayList<>();
		public List<String> getJsonData() {
			return jsonData;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			File f=file.toFile();
			extractGps(f);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		private void extractGps(File f){
			try {
				Metadata md=ImageMetadataReader.readMetadata(f);
				Iterator<Directory> iterator = md.getDirectories().iterator();
				while (iterator.hasNext()) {
					Directory dir = (Directory) iterator.next();
					if(dir instanceof GpsDirectory){
						GpsDirectory gpsDir = (GpsDirectory)dir;
						if(gpsDir.getGeoLocation()!=null){
							jsonData.add(getJsonString(gpsDir.getGeoLocation(), f));
						}
					}
				}
			} catch (ImageProcessingException | IOException e) {
			}
		}

		private String getJsonString(GeoLocation loc, File f){
			int id=++imageCount;
			return String.format("{\"id\":\"id%s\", \"lon\":\"%s\", \"lat\":\"%s\", \"title\":\"%s\", \"imgLoc\":\"%s\"}", id, loc.getLongitude(), loc.getLatitude(), f.getName(), f.toPath().toUri());
		}
	}
}
