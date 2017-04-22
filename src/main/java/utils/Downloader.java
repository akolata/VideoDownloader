package utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.*;

/**
 *  @author Aleksander Kołata
 */
public class Downloader {

    /**
     * List of videos URLs
     */
    public  List<URL>     videoURLs = new LinkedList<>();
    /**
     * Logger used
     */
    private final Logger LOG = Logger.getLogger(Downloader.class.getSimpleName());
    /**
     * Template used to generate URL.
     */
    public   String urlTemplate = "";
    /**
     * Output file template
     */
    public   String downloadedFileTemplate = "";
    /**
     * Location of output folder
     */
    public   String downloadFolderLocation = "";
    /**
     * Variable used to count,how many videos were downloaded
     */
    public  int currentlyDownloadingVideoNumber = 0;
    /**
     * How many video gonna be downloaded
     */
    public int numberOfAllVideos = 0;

    /**
     * Default constructor, with Logger configuration
     */
    public Downloader(){
        LOG.setUseParentHandlers(false);
        LOG.setLevel(Level.ALL);

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new LogFormatter());

        LOG.addHandler(consoleHandler);
        LOG.info("Application started");
    }

    /**
     * Used to add additional logger file
     * @param logFileLocation path to file with log messages
     */
    public void setFileLogHandler(String logFileLocation){
        Handler fileHandler;
        try {
            fileHandler = new FileHandler(logFileLocation,true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new LogFormatter());
            LOG.addHandler(fileHandler);
            LOG.info("FileHandler added. File with logs : " + logFileLocation);
        } catch (IOException e) {
            logException(e);
        }
    }

    /**
     * Shows info about thrown exception in formatted way
     * @param exception thrown Exception
     */
    private void logException(Exception exception){
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        String stacktrace = sw.toString();
        LOG.severe("An exception : " + exception.getClass().getSimpleName() + " occured");
        LOG.severe("Caused by : " + exception.getCause());
        LOG.severe("Exception message : " + exception.getMessage());
        LOG.severe("Stack trace : \n" + stacktrace);
    }

    /**
     * Reads all lines from given file's path - each line is video URL
     * @param path path to a file with URLs
     */
    public  void readURLsFromFile(String path){
        try {
            List<String> urlsAsStringsList = Files.readAllLines(Paths.get(path));
            videoURLs = getVideosURLsFromStringList(urlsAsStringsList);
            numberOfAllVideos = videoURLs.size();
            LOG.info(videoURLs.size() + " read from " + path + " file");
        } catch (IOException e) {
            logException(e);
        }
    }


    /**
     * Method writes all generated (from a template) URLs to a given file
     * @param path path to a file where URLs gonna be written
     */
    public  void writeURLsToFile(String path){
        File fileWithGeneratedURLs = new File(path);
        StringBuilder sb = new StringBuilder("");

        for(String url : generateVideosURLsBasedOnTheirVideoNumber()){
            sb.append(url + "\n");
        }

        try (PrintWriter pw = new PrintWriter(fileWithGeneratedURLs)) {
            pw.write(sb.toString());
        } catch (FileNotFoundException e) {
            logException(e);
        }
        LOG.info("Generated urls are now in " + path + " file");
    }

    /**
     * Method passes current video number to a template and generates video urls
     * @return list of strings with video urls
     */
    public  List<String> generateVideosURLsBasedOnTheirVideoNumber(){
        List<String> videoStrURLsList = new LinkedList<>();

        for(int i = 0; i < numberOfAllVideos +1; i++){
            String currentURL = String.format(urlTemplate,prepareNumberString(i));
            videoStrURLsList.add(currentURL);
        }

        return videoStrURLsList;
    }

    /**
     * Method generates a list of URL object based on given String-type URL
     * @param URLsAsStringList list of URLs Strings
     * @return list of URL objects based on input list
     */
    public  List<URL> getVideosURLsFromStringList(List<String> URLsAsStringList){
        List<URL> urls = new LinkedList<>();

        try{
            for(String urlStr : URLsAsStringList){
                urls.add(new URL(urlStr));
            }
        } catch (MalformedURLException e) {
            logException(e);
        }

        return urls;
    }

    /**
     * Method downloads video from given URL and saves it under location and name defined in templates
     * @param videoUrl  URL from which video gonna be downloaded
     */
    public  void downloadVideoFromURLUsingStreamUsingTemplate(URL videoUrl){
        try {
            LOG.entering(Downloader.class.getSimpleName(),"downloadVideoFromURLUsingStreamUsingTemplate",videoUrl);

            String fileName = String.format(downloadedFileTemplate,prepareNumberString(currentlyDownloadingVideoNumber));
            String filePathStr = downloadFolderLocation + "\\" + fileName ;
            currentlyDownloadingVideoNumber++;

            LOG.info("File gonna be saved in : " + filePathStr);

            File outputFile = new File(downloadFolderLocation,fileName);
            ReadableByteChannel rbc = Channels.newChannel(videoUrl.openStream());
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            LOG.info("Downloading finished");


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void downloadVideoFromURLUsingStreamAtGivenLocation(String url,String downloadedFileName,
                                                               String downloadFolderLocation){
        try {

            String filePathStr = downloadFolderLocation + "\\" + downloadedFileName ;

            LOG.info("File gonna be saved in : " + filePathStr);

            File outputFile = new File(downloadFolderLocation,downloadedFileName);
            ReadableByteChannel rbc = Channels.newChannel((new URL(url)).openStream());
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            LOG.info("Downloading finished");


        } catch (IOException e) {
            logException(e);
        }
    }



    /**
     * Method downloads video from given URL and saves it under location and name defined in templates
     * @param videoUrl  URL from which video gonna be downloaded
     */
    public  void downloadVideoFromURL(URL videoUrl){
        try {
            LOG.entering(Downloader.class.getSimpleName(),"downloadVideoFromURL",videoUrl);

            HttpURLConnection connection = (HttpURLConnection) videoUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            LOG.info("Connection established");
            String fileName = String.format(downloadedFileTemplate,prepareNumberString(currentlyDownloadingVideoNumber));
            String filePathStr = downloadFolderLocation + "\\" + fileName ;
            currentlyDownloadingVideoNumber++;
            LOG.info("File gonna be saved in : " + filePathStr);

            File outputFile = new File(downloadFolderLocation,fileName);
            FileOutputStream fos = new FileOutputStream(outputFile);

            int status = connection.getResponseCode();
            LOG.info("Response code = " + status);

            InputStream is = connection.getInputStream();
            LOG.info("Downloading started...");

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }

            fos.close();
            is.close();

            LOG.info("Downloading finished");


        } catch (Exception e) {
            logException(e);
        }


    }

    /**
     * Adds "0" if number is lower than ten
     * @param number number to be transformed
     * @return transformed form
     */
    private  String prepareNumberString(int number){
        String currentFileNumber;

        if( number < 10){
            currentFileNumber = "0" + number;
        }else{
            currentFileNumber = "" + number;
        }

        return currentFileNumber;
    }

}
