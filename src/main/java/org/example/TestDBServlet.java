package org.example;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/db-test")
public class TestDBServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("    <title>Проверка подключения к БД</title>");
        html.append("    <style>");
        html.append("        body { font-family: Arial, sans-serif; margin: 40px; background: #f5f5f5; }");
        html.append("        .container { max-width: 800px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        html.append("        .success { color: green; padding: 10px; background: #e8f5e8; border-radius: 5px; margin: 10px 0; }");
        html.append("        .error { color: red; padding: 10px; background: #ffebee; border-radius: 5px; margin: 10px 0; }");
        html.append("        .info { color: #2196f3; padding: 10px; background: #e3f2fd; border-radius: 5px; margin: 10px 0; }");
        html.append("        table { width: 100%; border-collapse: collapse; margin: 20px 0; }");
        html.append("        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        html.append("        th { background: #f2f2f2; }");
        html.append("        .nav { margin: 20px 0; text-align: center; }");
        html.append("        .nav a { display: inline-block; margin: 5px; padding: 10px 15px; background: #764ba2; color: white; text-decoration: none; border-radius: 5px; }");
        html.append("    </style>");
        html.append("</head>");
        html.append("<body>");
        html.append("    <div class='container'>");
        html.append("        <h1>🔍 Проверка подключения к Базе Данных</h1>");

        // Проверка подключения
        html.append("        <h2>1. Проверка подключения</h2>");
        boolean connectionSuccess = testConnection(html);

        if (connectionSuccess) {
            // Проверка таблиц
            html.append("        <h2>2. Проверка таблиц</h2>");
            checkTables(html);

            // Проверка данных
            html.append("        <h2>3. Проверка данных</h2>");
            checkData(html);
        }

        html.append("        <div class='nav'>");
        html.append("            <a href='/rose/welcome'>🏠 На главную</a>");
        html.append("            <a href='/rose/game'>🎮 Играть</a>");
        html.append("            <a href='/rose/stats'>📊 Статистика</a>");
        html.append("        </div>");
        html.append("    </div>");
        html.append("</body>");
        html.append("</html>");

        response.getWriter().print(html.toString());
    }

    private boolean testConnection(StringBuilder html) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            html.append("<div class='success'>");
            html.append("✅ <strong>Подключение успешно!</strong><br>");
            html.append("📊 База данных: ").append(meta.getDatabaseProductName()).append(" ").append(meta.getDatabaseProductVersion()).append("<br>");
            html.append("🔗 URL: ").append(meta.getURL()).append("<br>");
            html.append("👤 Пользователь: ").append(meta.getUserName()).append("<br>");
            html.append("🚀 Драйвер: ").append(meta.getDriverName()).append(" ").append(meta.getDriverVersion());
            html.append("</div>");
            return true;
        } catch (SQLException e) {
            html.append("<div class='error'>");
            html.append("❌ <strong>Ошибка подключения!</strong><br>");
            html.append("Сообщение: ").append(e.getMessage()).append("<br>");
            html.append("<br><strong>Возможные причины:</strong><br>");
            html.append("- База данных не запущена<br>");
            html.append("- Неправильные настройки в database.properties<br>");
            html.append("- Порт 3306 занят<br>");
            html.append("- Проблемы с сетью Docker");
            html.append("</div>");
            return false;
        }
    }

    private void checkTables(StringBuilder html) {
        List<String> requiredTables = List.of("users", "game_saves", "game_stats", "player_achievements");
        List<String> foundTables = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables("rose_quest", null, "%", new String[]{"TABLE"});

            html.append("<table>");
            html.append("<tr><th>Таблица</th><th>Статус</th><th>Колонки</th></tr>");

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                foundTables.add(tableName.toLowerCase());

                // Получаем информацию о колонках
                List<String> columns = new ArrayList<>();
                ResultSet columnsRs = meta.getColumns("rose_quest", null, tableName, null);
                while (columnsRs.next()) {
                    columns.add(columnsRs.getString("COLUMN_NAME"));
                }

                html.append("<tr>");
                html.append("<td>").append(tableName).append("</td>");
                html.append("<td>✅ Найдена</td>");
                html.append("<td>").append(String.join(", ", columns)).append("</td>");
                html.append("</tr>");
            }

            html.append("</table>");

            // Проверяем отсутствующие таблицы
            for (String requiredTable : requiredTables) {
                if (!foundTables.contains(requiredTable)) {
                    html.append("<div class='error'>");
                    html.append("❌ Таблица '").append(requiredTable).append("' не найдена!");
                    html.append("</div>");
                }
            }

        } catch (SQLException e) {
            html.append("<div class='error'>Ошибка при проверке таблиц: ").append(e.getMessage()).append("</div>");
        }
    }

    private void checkData(StringBuilder html) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            // Проверяем пользователей
            html.append("<h3>👥 Пользователи:</h3>");
            checkTableData(conn, "users", html);

            // Проверяем сохранения игр
            html.append("<h3>💾 Сохранения игр:</h3>");
            checkTableData(conn, "game_saves", html);

            // Проверяем статистику
            html.append("<h3>📈 Статистика игр:</h3>");
            checkTableData(conn, "game_stats", html);

            // Проверяем достижения
            html.append("<h3>🏆 Достижения:</h3>");
            checkTableData(conn, "player_achievements", html);

        } catch (SQLException e) {
            html.append("<div class='error'>Ошибка при проверке данных: ").append(e.getMessage()).append("</div>");
        }
    }

    private void checkTableData(Connection conn, String tableName, StringBuilder html) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM " + tableName;
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                int count = rs.getInt("count");
                html.append("<div class='info'>");
                html.append("Записей в таблице '").append(tableName).append("': <strong>").append(count).append("</strong>");
                html.append("</div>");

                // Показываем несколько записей если есть
                if (count > 0) {
                    showSampleData(conn, tableName, html);
                }
            }
        }
    }

    private void showSampleData(Connection conn, String tableName, StringBuilder html) throws SQLException {
        String sql = "SELECT * FROM " + tableName + " LIMIT 3";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            html.append("<table>");
            html.append("<tr>");
            for (int i = 1; i <= columnCount; i++) {
                html.append("<th>").append(meta.getColumnName(i)).append("</th>");
            }
            html.append("</tr>");

            while (rs.next()) {
                html.append("<tr>");
                for (int i = 1; i <= columnCount; i++) {
                    html.append("<td>").append(rs.getString(i)).append("</td>");
                }
                html.append("</tr>");
            }
            html.append("</table>");
        }
    }
}