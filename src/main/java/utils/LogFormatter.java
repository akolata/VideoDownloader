package utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Class used to format a Loggers messages
 * @author Aleksander Kołata
 */
public class LogFormatter
extends Formatter{

    @Override
    public String format(LogRecord record) {
        Level   level = record.getLevel();
        String  message = record.getMessage(),
                loggerName = record.getLoggerName(),
                outputMessage = "";
        long miliseconds = record.getMillis();
        Date logDate = new Date(miliseconds);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss dd-MM-YYYY");
        String dateStr = sdf.format(logDate);

        outputMessage = dateStr + "\t" + loggerName + "\n" + level + "\t" + message;

        return outputMessage;
    }
}
