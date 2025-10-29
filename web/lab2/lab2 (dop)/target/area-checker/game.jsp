<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Лаба веб-1</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <video id="background" class="background" autoplay muted loop width="100%" height="100%">
        <source src="images/background.mp4" type="video/mp4">
        Your browser does not support the video tag.
    </video>
    <img id="cursor-arrow" src="images/red-arrow.png" alt="Стрелка курсора">

    <a href="index.jsp" class="backbutton">
        НАЗАД
    </a>

    <button id="sound" class="soundbutton">
        ВКЛ ЗВУК 
    </button>

    <form class="oreshnik" method="POST" action="controller">
        <h2>ОРЕШНИК</h2>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message" style="color: red; text-align: center; margin: 10px;">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <div class="form-group">
            <label><p>X:</p></label>
            <input type="hidden" id="xvalue" name="x" value="0">
            <div class="radio-group">
                <div><label><input type="radio" name="x-choice" value="-2" onclick="document.getElementById('xvalue').value = this.value;"> -2</label></div>
                <div><label><input type="radio" name="x-choice" value="-1.5" onclick="document.getElementById('xvalue').value = this.value;"> -1.5</label></div>
                <div><label><input type="radio" name="x-choice" value="-1" onclick="document.getElementById('xvalue').value = this.value;"> -1</label></div>
                <div><label><input type="radio" name="x-choice" value="-0.5" onclick="document.getElementById('xvalue').value = this.value;"> -0.5</label></div>
                <div><label><input type="radio" name="x-choice" value="0" onclick="document.getElementById('xvalue').value = this.value;" checked> 0</label></div>
                <div><label><input type="radio" name="x-choice" value="0.5" onclick="document.getElementById('xvalue').value = this.value;"> 0.5</label></div>
                <div><label><input type="radio" name="x-choice" value="1" onclick="document.getElementById('xvalue').value = this.value;"> 1</label></div>
                <div><label><input type="radio" name="x-choice" value="1.5" onclick="document.getElementById('xvalue').value = this.value;"> 1.5</label></div>
                <div><label><input type="radio" name="x-choice" value="2" onclick="document.getElementById('xvalue').value = this.value;"> 2</label></div>
            </div>
        </div>

        <div class="form-group">
            <label for="yvalue"><p>Y:</p></label>
            <input type="text" id="yvalue" name="y" class="input">
        </div>
        
        <div class="form-group">
            <label for="rvalue"><p>R:</p></label>
            <select id="rvalue" name="r" class="input">
                <option value="1">1</option>
                <option value="1.5">1.5</option>
                <option value="2">2</option>
                <option value="2.5">2.5</option>
                <option value="3">3</option>
            </select>                  
        </div>
        
        <button type="submit" id="bam" class="exterminatus">ЭКСТЕРМИНАТУС</button>
    </form>

    <table class="errors">
        <thead>
            <tr>
                <th>Ошибка</th>
                <th>Время</th>
            </tr>
        </thead>
        <tbody id="errors-body">
        </tbody>
    </table>

    <div class="results-container">
        <table class="results">
            <thead>
                <tr>
                    <th class="X">X</th>
                    <th class="Y">Y</th>
                    <th class="R">R</th>
                    <th class="res">Результат</th>
                    <th class="time">Время</th>
                </tr>
            </thead>
            <tbody id="results-body">
                <%
                    List<Map<String, Object>> results = 
                        (List<Map<String, Object>>) application.getAttribute("results");
                    if (results != null) {
                        for (Map<String, Object> result : results) {
                %>
                    <tr>
                        <td><%= result.get("x") %></td>
                        <td><%= result.get("y") %></td>
                        <td><%= result.get("r") %></td>
                        <td><%= (Boolean)result.get("hit") ? "Попадание" : "Промах" %></td>
                        <td><%= result.get("currentTime") %></td>
                    </tr>
                <%
                        }
                    }
                %>
            </tbody>
        </table>
    </div>

    <canvas id="canvas" class="canvas" width="500" height="500"></canvas>

    <script>
        const serverResults = [
            <%
                if (results != null) {
                    for (int i = 0; i < results.size(); i++) {
                        Map<String, Object> result = results.get(i);
            %>
                {
                    x: <%= result.get("x") %>,
                    y: <%= result.get("y") %>,
                    r: <%= result.get("r") %>,
                    hit: <%= result.get("hit") %>,  
                    cluster: <%= result.get("cluster") != null ? result.get("cluster") : -1 %>
                }<%= (i < results.size() - 1) ? "," : "" %>
            <%
                    }
                }
            %>
        ];
    </script>

    <script src="scripts.js"></script>

</body>
</html>