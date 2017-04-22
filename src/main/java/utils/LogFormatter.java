package utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Class used to format Logger messages
 * @author Aleksander Kołata
 */
public class LogFormatter
extends Formatter{

    @Override
    public String format(LogRecord record) {
        Level   level = record.getLevel();
        String  message = record.getMessage(),
                loggerName = record.getLoggerName(),
                outputMessage;

        long milliseconds = record.getMillis();
        Date logDate = new Date(milliseconds);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss dd-MM-YYYY");
        String dateStr = sdf.format(logDate);

        outputMessage = dateStr + "\t" + loggerName + "\n" + level + "\t" + message + "\n";

        return outputMessage;
    }
}
