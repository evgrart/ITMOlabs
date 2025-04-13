package main_classes;

/**
 * Координаты - одно из полей {@link StudyGroup}
 */
public class Coordinates {
    private double x;
    private double y;

    /**
     * Констуктор, принимает значения х и у
     */
    public Coordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return Возвращает строку в формате (x ; y)
     */
    @Override
    public String toString() {
        return "(" + x + " ; " + y + ")";
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    /**
     * Создает билдер, дабы инициализировать Coordinates
     */
    public static CoordinatesBuilder builder() {
        return new CoordinatesBuilder();
    }

    /**
     * Билдер, чтобы выпендриваться мол прочёл хабр
     */
    public static class CoordinatesBuilder {
        private double x;
        private double y;

        public CoordinatesBuilder x(double x) {
            this.x = x;
            return this;
        }

        public CoordinatesBuilder y(double y) {
            this.y = y;
            return this;
        }

        /**
         * Собираем из билдера объект Coordinates
         */
        public Coordinates build() {
            return new Coordinates(x, y);
        }
    }
}