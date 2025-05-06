//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package general.main_classes;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private double x;
    private double y;

    public Coordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "(" + this.x + " ; " + this.y + ")";
    }

    public Double getX() {
        return this.x;
    }

    public Double getY() {
        return this.y;
    }

    public static CoordinatesBuilder builder() {
        return new CoordinatesBuilder();
    }

    public static class CoordinatesBuilder {
        private double x;
        private double y;

        public CoordinatesBuilder() {
        }

        public CoordinatesBuilder x(double x) {
            this.x = x;
            return this;
        }

        public CoordinatesBuilder y(double y) {
            this.y = y;
            return this;
        }

        public Coordinates build() {
            return new Coordinates(this.x, this.y);
        }
    }
}
