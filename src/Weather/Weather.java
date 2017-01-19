package Weather;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Weather {
    public final String dayOfWeek;
    public final String currentTemp;
    public final String minTemp;
    public final String maxTemp;
    public final String humidity;
    public final String description;
    public final String iconURL;
    public final String pressure;
    public String date;

    //Constructor
    public Weather(long timeStamp, double minTemp, double maxTemp,
                   double humidity, String description, String iconName, double pressure, double currentTemp){

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);

        this.dayOfWeek = convertTimeStampToday(timeStamp);
        this.minTemp = numberFormat.format(minTemp) + "\u00B0F";
        this.maxTemp = numberFormat.format(maxTemp) + "\u00B0F";
        this.humidity =
                NumberFormat.getPercentInstance().format(humidity / 100.0);
        this.description = description;
        this.iconURL = "http://openweathermap.org/img/w/" + iconName + ".png";
        this.pressure = 
        		NumberFormat.getPercentInstance().format(pressure / 100.0);
        this.currentTemp = numberFormat.format(currentTemp) + "\u00b0F";
        date = convertTimeStampDate(timeStamp);
    }

    public static String convertTimeStampToday(long timeStamp)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp * 1000);

        TimeZone tz = TimeZone.getDefault();

        calendar.add(Calendar.MILLISECOND,
                tz.getOffset(calendar.getTimeInMillis()));

        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE");
        return dateFormatter.format(calendar.getTime());
    }
    
    public static String convertTimeStampDate(long timeStamp)
    {
    	Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp * 1000);

        TimeZone tz = TimeZone.getDefault();

        calendar.add(Calendar.MILLISECOND,
                tz.getOffset(calendar.getTimeInMillis()));

        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, MMM d, ''yy");
        return dateFormatter.format(calendar.getTime());
    }
}
