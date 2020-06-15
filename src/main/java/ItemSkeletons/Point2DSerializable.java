package ItemSkeletons;

import javafx.geometry.Point2D;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by Jakob on 6/14/2020.
 */
public class Point2DSerializable implements Serializable{
    Point2D myPoint ;

    public Point2DSerializable(double x, double y) {
        myPoint = new Point2D(x,y) ;
    }
    public Point2DSerializable(Point2D point) {
        myPoint = point ;
    }
    public Point2D getPoint() {
        return myPoint ;
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeDouble(myPoint.getX());
        out.writeDouble(myPoint.getY());
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        myPoint = new Point2D(in.readDouble(), in.readDouble()) ;
    }
}
