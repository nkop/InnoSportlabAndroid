package nl.in12soa.sperovideo.Models;

/**
 * Created by Wouter on 18-5-2017.
 */

public class Video {

    public String _id;
    public String filePath;
    public String sporter;
    public String date;

    public Video(String _id, String filePath, String sporter, String date)
    {
        this._id = _id;
        this.filePath = filePath;
        this.sporter = sporter;
        this.date = date;
    }
}
