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
     * List of correct URLs to a videos
     */
    public  List<URL>     videoURLs = new LinkedList<>();
    /**
     * Logger used
     */
    private  final Logger LOG = Logger.getLogger(Downloader.class.getSimpleName());
    /**
     * Template used to generate URL.
     */
    private  final String URL_TEMPLATE = "";
    /**
     * Output file template
     */
    private  final String FILENAME_TEMPLATE = "";
    /**
     * Location of output folder
     */
    private  final String DOWNLOAD_FOLDER_LOCATION = "";
    /**
     * Variable used to count,how many videos were downloaded
     */
    private  int          VIDEO_NUMBER = 0;
    /**
     * How many video gonna be downloaded
     */
    private  int          VIDEO_COUNT = 102;

    /**
     * Reads all lines from given file's path - each line is video URL
     * @param path path to a file with URLs
     */
    public  void readURLsFromFile(String path){
        try {
            List<String> urlsAsStringsList = Files.readAllLines(Paths.get(path));
            videoURLs = getVideosURLsFromStringList(urlsAsStringsList);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public Downloader(){
        LOG.setUseParentHandlers(false);
        LOG.setLevel(Level.ALL);

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new LogFormatter());

        LOG.addHandler(consoleHandler);
    }

    /**
     * Used to specify where to log Logger messages
     * @param logFilePath path to file with log messages
     */
    public void setFileLogHandler(String logFilePath){
        Handler fileHandler = null;
        try {
            fileHandler = new FileHandler("",true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new LogFormatter());
            LOG.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Method writes all generated (from a template) URLs to a given file
     * @param path path to a file where URLs gonna be written
     */
    public  void writeURLsToFile(String path){
        File outFile = new File(path);
        StringBuilder sb = new StringBuilder("");

        for(String url : getVideoURLsBasedOnVideoNumber()){
            sb.append(url + "\n");
        }

        try (PrintWriter pw = new PrintWriter(outFile)) {
            pw.write(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method passes current video number to a template and generates video urls
     * @return list of strings with video urls
     */
    public  List<String> getVideoURLsBasedOnVideoNumber(){
        List<String> videoStrURLsList = new LinkedList<>();

        for(int i = 0 ; i < VIDEO_COUNT; i++){
            String currentURL = String.format(URL_TEMPLATE,prepareNumberString(i));
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
            e.printStackTrace();
        }

        return urls;
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
            String fileName = String.format(FILENAME_TEMPLATE,prepareNumberString(VIDEO_NUMBER));
            String filePathStr = DOWNLOAD_FOLDER_LOCATION + "\\" + fileName ;
            VIDEO_NUMBER++;
            LOG.info("File gonna be saved in : " + filePathStr);

            File outputFile = new File(DOWNLOAD_FOLDER_LOCATION,fileName);
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


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Method downloads video from given URL and saves it under location and name defined in templates
     * @param videoUrl  URL from which video gonna be downloaded
     */
    public  void downloadVideoFromURLUsingStream(URL videoUrl){
        try {
            LOG.entering(Downloader.class.getSimpleName(),"downloadVideoFromURLUsingStream",videoUrl);
            String fileName = String.format(FILENAME_TEMPLATE,prepareNumberString(VIDEO_NUMBER));
            String filePathStr = DOWNLOAD_FOLDER_LOCATION + "\\" + fileName ;
            VIDEO_NUMBER++;
            LOG.info("File gonna be saved in : " + filePathStr);

            File outputFile = new File(DOWNLOAD_FOLDER_LOCATION,fileName);
            ReadableByteChannel rbc = Channels.newChannel(videoUrl.openStream());
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            LOG.info("Downloading finished");


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Method downloads video from given URL and saves it under location and name defined in templates
     * @param videoUrl  URL from which video gonna be downloaded
     * @param videoNumber  downloaded video filename number
     */
    public  void downloadVideoFromURLUsingStream(URL videoUrl,int videoNumber){
        try {
            LOG.entering(Downloader.class.getSimpleName(),"downloadVideoFromURLUsingStream",videoUrl);
            String fileName = String.format(FILENAME_TEMPLATE,prepareNumberString(videoNumber));
            String filePathStr = DOWNLOAD_FOLDER_LOCATION + "\\" + fileName ;
            VIDEO_NUMBER++;
            LOG.info("File gonna be saved in : " + filePathStr);

            File outputFile = new File(DOWNLOAD_FOLDER_LOCATION,fileName);
            ReadableByteChannel rbc = Channels.newChannel(videoUrl.openStream());
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            LOG.info("Downloading finished");


        } catch (IOException e) {
            e.printStackTrace();
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
