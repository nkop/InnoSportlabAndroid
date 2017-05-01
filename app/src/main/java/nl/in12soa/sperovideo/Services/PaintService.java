package nl.in12soa.sperovideo.Services;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import arrcreations.prototype1_project.AnalyseActivity;
import nl.in12soa.sperovideo.AnalyseActivity;

/**
 * Created by Ahmad on 3/14/2017.
 */

public class PaintService {

    private AnalyseActivity mActivity;
    public PaintService(AnalyseActivity act){
        mActivity = act;
    }

    public void drawTriangle(){
        Paint paint = new Paint();
        Canvas cv = mActivity.vw2.getHolder().lockCanvas();
        paint.setStrokeWidth(4);
        paint.setColor(android.graphics.Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        Point a = new Point(0, 0);
        Point b = new Point(0, 100);
        Point c = new Point(87, 50);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.lineTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(a.x, a.y);
        path.close();


        cv.drawPath(path, paint);
        mActivity.vw2.getHolder().unlockCanvasAndPost(cv);
    }

}


