package utility;

/**
 * Управляет переменной окружения для парсинга history
 */
public class ConfigLoader {
    /**
     * Возвращает путь в зависимости от того, где запускается прога: локально или на гелиосе
     */
    public static String getJsonPath() {
        String jsonPath = System.getenv("JSON_PATH");

        if (jsonPath == null || jsonPath.isEmpty()) {
            jsonPath = "/home/studs/s465826/history.txt"; // Локальный путь по умолчанию
        }

        return jsonPath;
    }
}
