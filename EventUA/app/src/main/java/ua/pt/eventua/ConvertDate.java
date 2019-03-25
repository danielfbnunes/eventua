package ua.pt.eventua;

public class ConvertDate {

    public static String dateToString_days(String date)
    {
        String out="";

        String day = date.trim().split("\\s+")[0];
        out += day.split("-")[2] + " ";
        switch (Integer.parseInt(day.split("-")[1]))
        {
            case 1:
                out += "January, ";
                break;
            case 2:
                out += "February, ";
                break;
            case 3:
                out += "March, ";
                break;
            case 4:
                out += "April, ";
                break;
            case 5:
                out += "May, ";
                break;
            case 6:
                out += "June, ";
                break;
            case 7:
                out += "July, ";
                break;
            case 8:
                out += "August, ";
                break;
            case 9:
                out += "September, ";
                break;
            case 10:
                out += "October, ";
                break;
            case 11:
                out += "November, ";
                break;
            case 12:
                out += "December, ";
                break;
        }
        out += day.split("-")[0] + " ";
        return out;
    }

    public static String dateToString_hours(String date)
    {
        String day = date.trim().split("\\s+")[1];
        return day;
    }


}
