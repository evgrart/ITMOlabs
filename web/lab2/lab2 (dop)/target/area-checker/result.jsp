<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Результат проверки</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <video class="video" autoplay muted loop width="100%" height="100%">
        <source src="images/nurgle-warhammer.mp4" type="video/mp4">
        Your browser does not support the video tag.
    </video>  

    <div class="res">
        <h1>Результат проверки попадания</h1>
        
        <% 
            // ИЗМЕНЕНИЕ: Берем результат из сессии, а не из запроса
            Map<String, Object> result = (Map<String, Object>) session.getAttribute("result");
            if (result != null) {
        %>
            <table border="1" style="width: 100%; margin: 20px 0;">
                <tr>
                    <th>Параметр</th>
                    <th>Значение</th>
                </tr>
                <tr>
                    <td>X</td>
                    <td><%= result.get("x") %></td>
                </tr>
                <tr>
                    <td>Y</td>
                    <td><%= result.get("y") %></td>
                </tr>
                <tr>
                    <td>R</td>
                    <td><%= result.get("r") %></td>
                </tr>
                <tr>
                    <td>Результат</td>
                    <td style="color: <%= (Boolean)result.get("hit") ? "green" : "red" %>;">
                        <%= (Boolean)result.get("hit") ? "Попадание" : "Промах" %>
                    </td>
                </tr>
                 <!-- Опционально: можно добавить отображение кластера и здесь -->
                <tr>
                    <td>Кластер</td>
                    <td><%= result.get("cluster") %></td>
                </tr>
                <tr>
                    <td>Время</td>
                    <td><%= result.get("currentTime") %></td>
                </tr>
            </table>
        <% 
            // Очищаем атрибут сессии, чтобы при обновлении страницы не показывать старый результат
            session.removeAttribute("result");
           } 
        %>
        
        <a href="game.jsp" style="display: inline-block; padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px;">
            Вернуться к форме
        </a>
    </div>
</body>
</html>