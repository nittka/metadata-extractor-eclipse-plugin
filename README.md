# What the folderMap-Branch is for
The intention is to show where the pictures in a particular folder were taken on a map.

The basic map rendering is based on
https://wiki.openstreetmap.org/wiki/DE:OpenLayers_Marker_Example

The popup opening and closing is based on
https://stackoverflow.com/questions/8523446/openlayers-simple-mouseover-on-marker

Given a folder, all files are checked for GPS coordinates.
If present they are added to a list.
The html file then goes over that list, adds markers and popups.

![Screenshot](https://github.com/nittka/nittka.github.io/blob/master/assets/gpsImagesMap.jpg)

## Creating the runnable jar

In Eclipse, use `Export`->`Java`->`Runnable JAR file`.
Choose the `MapFromImagesGPS` launch configuration from the main project and check the `Extract required libraries into generated JAR` option.

I assume, you call the exported jar `imagesOnMap.jar`

## Running the jar

On the command line, call `java -jar imagesOnMap.jar <folder>` where folder is the folder to search for images.

In Eclipse, you can simply start the `MapFromImagesGPS` launch configuration after defining the root folder in the program arguments.

You can now open the `imageGps.html` file. Make sure Javascript is enabled (for local files as well).
