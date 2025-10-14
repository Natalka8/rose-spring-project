package org.example;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/game")
public class GameServlet extends HttpServlet {

    private static class Location {
        String title;
        String description;
        Map<String, String> actions;
        String image;

        Location(String title, String description, String image) {
            this.title = title;
            this.description = description;
            this.actions = new HashMap<>();
            this.image = image;
        }

        void addAction(String actionText, String targetLocationId) {
            this.actions.put(actionText, targetLocationId);
        }
    }

    private Map<String, Location> locations = new HashMap<>();

    @Override
    public void init() throws ServletException {
        initializeLocations();
    }

    private void initializeLocations() {
        // Начальная локация - Опушка леса
        Location forestEdge = new Location(
                "Опушка Таинственного леса",
                "Вы стоите на опушке древнего леса. Деревья такие высокие, что их вершины теряются в облаках. " +
                        "Воздух наполнен ароматом хвои и цветущих растений. Перед вами три тропинки:\n\n" +
                        "🛤️  Левая тропинка - ведет в темную чащу\n" +
                        "🛤️  Центральная тропинка - уходит вглубь леса\n" +
                        "🛤️  Правая тропинка - ведет к реке",
                "🌲"
        );
        forestEdge.addAction("пойти налево", "dark_thicket");
        forestEdge.addAction("пойти прямо", "deep_forest");
        forestEdge.addAction("пойти направо", "river");
        locations.put("start", forestEdge);

        // Темная чаща
        Location darkThicket = new Location(
                "Темная чаща",
                "Вы входите в темную чащу. Солнечный свет едва пробивается сквозь густые кроны деревьев. " +
                        "Вокруг царит полумрак, и вы слышите странные шепоты. Внезапно вы замечаете:\n\n" +
                        "🔦  Старый фонарь на дереве\n" +
                        "📜  Таинственный свиток на земле\n" +
                        "🔙  Путь назад к опушке",
                "🌳"
        );
        darkThicket.addAction("взять фонарь", "lantern");
        darkThicket.addAction("прочитать свиток", "scroll");
        darkThicket.addAction("вернуться на опушку", "start");
        locations.put("dark_thicket", darkThicket);

        // Фонарь
        Location lantern = new Location(
                "Волшебный фонарь",
                "Вы берете старый фонарь. Вдруг он загорается мягким светом. Фонарь оказывается волшебным - " +
                        "он освещает путь и отпугивает лесных духов.\n\n" +
                        "✨ Вы получили: Волшебный фонарь\n\n" +
                        "Теперь вы можете продолжить путь или вернуться:",
                "💡"
        );
        lantern.addAction("идти дальше с фонарем", "ancient_ruins");
        lantern.addAction("вернуться в чащу", "dark_thicket");
        locations.put("lantern", lantern);

        // Свиток
        Location scroll = new Location(
                "Таинственный свиток",
                "Вы разворачиваете старый свиток. На нем написано древними рунами:\n\n" +
                        "'Трижды стукни по самому старому дереву, и путь откроется тому, кто носит свет в сердце.'\n\n" +
                        "Вы запоминаете заклинание.\n\n" +
                        "✨ Вы получили: Знание древних\n\n" +
                        "Теперь можно вернуться:",
                "📜"
        );
        scroll.addAction("вернуться в чащу", "dark_thicket");
        locations.put("scroll", scroll);

        // Глубокий лес
        Location deepForest = new Location(
                "Глубокий лес",
                "Вы идете по центральной тропинке. Лес становится все гуще. Вдруг вы слышите красивое пение. " +
                        "За деревьями вы замечаете:\n\n" +
                        "🧚  Сияющее существо\n" +
                        "🌿  Странное растение с светящимися ягодами\n" +
                        "🔙  Вернуться на опушку",
                "🌿"
        );
        deepForest.addAction("подойти к существу", "fairy");
        deepForest.addAction("собрать ягоды", "berries");
        deepForest.addAction("вернуться на опушку", "start");
        locations.put("deep_forest", deepForest);

        // Фея
        Location fairy = new Location(
                "Лесная фея",
                "Перед вами лесная фея с сияющими крыльями. Она улыбается и говорит:\n\n" +
                        "'Приветствую, путник! Я охраняю этот лес. Чтобы пройти дальше, тебе нужен ключ от древнего храма. " +
                        "Найди три волшебных предмета: свет в темноте, знание древних и дар природы.'\n\n" +
                        "Фея исчезает в сиянии света.\n\n" +
                        "Теперь вы можете продолжить путь:",
                "🧚"
        );
        fairy.addAction("продолжить путь", "deep_forest");
        locations.put("fairy", fairy);

        // Ягоды
        Location berries = new Location(
                "Светящиеся ягоды",
                "Вы собираете светящиеся ягоды. Они излучают мягкий голубой свет. " +
                        "Внезапно ягоды превращаются в хрустальный амулет!\n\n" +
                        "✨ Вы получили: Амулет Лесного Света\n\n" +
                        "Этот амулет защищает вас от темных сил леса.\n\n" +
                        "Теперь можно вернуться:",
                "🍇"
        );
        berries.addAction("вернуться в лес", "deep_forest");
        locations.put("berries", berries);

        // Река
        Location river = new Location(
                "Лесная река",
                "Вы выходите к быстрой лесной реке. Вода кристально чистая и холодная. " +
                        "Через реку переброшен старый мост, а на другом берегу виднеется каменное строение.\n\n" +
                        "🌉  Перейти по мосту\n" +
                        "💧  Испить воды из реки\n" +
                        "🔙  Вернуться на опушку",
                "🌊"
        );
        river.addAction("перейти мост", "bridge");
        river.addAction("испить воды", "river_water");
        river.addAction("вернуться на опушку", "start");
        locations.put("river", river);

        // Вода реки
        Location riverWater = new Location(
                "Вода мудрости",
                "Вы пьете воду из лесной реки. Вода оказывается волшебной! " +
                        "Вы чувствуете, как к вам приходит мудрость древних. Теперь вы понимаете язык животных и растений.\n\n" +
                        "✨ Вы получили: Дар понимания природы\n\n" +
                        "Деревья шепчут вам: 'Ищи храм за мостом...'\n\n" +
                        "Теперь можно перейти мост:",
                "💧"
        );
        riverWater.addAction("перейти мост", "bridge");
        locations.put("river_water", riverWater);

        // Мост
        Location bridge = new Location(
                "Древний мост",
                "Вы стоите перед древним каменным мостом. На перилах вырезаны странные символы. " +
                        "Мост ведет к загадочному каменному строению.\n\n" +
                        "🏛️  Подойти к строению\n" +
                        "🔙  Вернуться к реке",
                "🌉"
        );
        bridge.addAction("подойти к строению", "temple");
        bridge.addAction("вернуться к реке", "river");
        locations.put("bridge", bridge);

        // Храм
        Location temple = new Location(
                "Древний храм",
                "Вы стоите перед древним храмом, покрытым мхом и плющом. Массивная каменная дверь закрыта. " +
                        "На двери три углубления необычной формы.\n\n" +
                        "🔍  Осмотреть дверь\n" +
                        "🔙  Вернуться к мосту",
                "🏛️"
        );
        temple.addAction("осмотреть дверь", "temple_door");
        temple.addAction("вернуться к мосту", "bridge");
        locations.put("temple", temple);

        // Дверь храма
        Location templeDoor = new Location(
                "Загадка храма",
                "Вы внимательно осматриваете дверь. Три углубления соответствуют:\n\n" +
                        "💡  Источнику света (фонарь)\n" +
                        "📜  Древнему знанию (свиток)\n" +
                        "✨  Природной магии (амулет)\n\n" +
                        "Чтобы открыть дверь, вам нужны все три артефакта!\n\n" +
                        "Проверьте свой инвентарь и вернитесь, когда соберете все предметы.",
                "🚪"
        );
        templeDoor.addAction("попытаться открыть дверь", "victory_check");
        templeDoor.addAction("вернуться к храму", "temple");
        locations.put("temple_door", templeDoor);

        // Проверка победы
        Location victoryCheck = new Location(
                "Попытка открыть дверь",
                "Вы пытаетесь открыть дверь храма...",
                "🔑"
        );
        victoryCheck.addAction("проверить", "victory_check");
        locations.put("victory_check", victoryCheck);

        // Древние руины
        Location ancientRuins = new Location(
                "Древние руины",
                "С фонарем в руке вы находите скрытый путь к древним руинам. Среди развалин " +
                        "вы находите статую с высеченными рунами.\n\n" +
                        "✨ Вы получили: Знание Древних\n\n" +
                        "Теперь можно изучить руны или вернуться:",
                "🏺"
        );
        ancientRuins.addAction("изучить руны", "runes");
        ancientRuins.addAction("вернуться в чащу", "dark_thicket");
        locations.put("ancient_ruins", ancientRuins);

        // Руны
        Location runes = new Location(
                "Руны Древних",
                "Вы изучаете древние руны. Они рассказывают историю хранителей леса и " +
                        "тайну волшебного артефакта, спрятанного в глубине леса.\n\n" +
                        "✨ Теперь вы обладаете мудростью древних!\n\n" +
                        "Можно вернуться к руинам:",
                "🔣"
        );
        runes.addAction("вернуться к руинам", "ancient_ruins");
        locations.put("runes", runes);

        // Победа!
        Location victory = new Location(
                "ПОБЕДА! 🏆",
                "Вы вставляете все три артефакта в углубления на двери храма. " +
                        "Дверь медленно открывается с громким скрежетом, издавая древнюю мелодию!\n\n" +
                        "Внутри вас ждет сияющий кристалл древней мудрости - источник силы всего леса!\n\n" +
                        "🌟 Кристалл излучает теплый свет и наполняет вас знанием и силой. " +
                        "Вы стали новым хранителем Таинственного леса!\n\n" +
                        "Мудрость древних теперь принадлежит вам. Лес навсегда запомнит ваше имя!",
                "🏆"
        );
        victory.addAction("начать заново", "start");
        locations.put("victory", victory);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/auth");
            return;
        }

        User user = (User) session.getAttribute("user");
        String contextPath = request.getContextPath();

        // Обработка перезапуска игры
        if ("restart".equals(request.getParameter("action"))) {
            session.removeAttribute("currentLocation");
            session.removeAttribute("inventory");
            session.setAttribute("currentLocation", "start");

            Map<String, Boolean> newInventory = new HashMap<>();
            newInventory.put("lantern", false);
            newInventory.put("scroll_knowledge", false);
            newInventory.put("amulet", false);
            session.setAttribute("inventory", newInventory);

            response.sendRedirect(contextPath + "/game");
            return;
        }

        // Получаем или устанавливаем текущую локацию игрока
        String currentLocationId = (String) session.getAttribute("currentLocation");
        if (currentLocationId == null) {
            currentLocationId = "start";
            session.setAttribute("currentLocation", currentLocationId);
        }

        // Получаем инвентарь игрока
        Map<String, Boolean> inventory = (Map<String, Boolean>) session.getAttribute("inventory");
        if (inventory == null) {
            inventory = new HashMap<>();
            inventory.put("lantern", false);
            inventory.put("scroll_knowledge", false);
            inventory.put("amulet", false);
            session.setAttribute("inventory", inventory);
        }

        // Обработка действий игрока
        String actionParam = request.getParameter("action");
        System.out.println("Action received: " + actionParam);
        System.out.println("Current location: " + currentLocationId);
        System.out.println("Inventory: " + inventory);

        if (actionParam != null && locations.containsKey(currentLocationId)) {
            Location currentLocation = locations.get(currentLocationId);

            // Ищем действие по тексту
            for (Map.Entry<String, String> actionEntry : currentLocation.actions.entrySet()) {
                if (actionEntry.getKey().equals(actionParam)) {
                    String newLocationId = actionEntry.getValue();
                    System.out.println("Found action, new location: " + newLocationId);

                    // Обновляем инвентарь при определенных действиях
                    if ("lantern".equals(newLocationId)) {
                        inventory.put("lantern", true);
                        System.out.println("Got lantern!");
                    }
                    if ("scroll".equals(newLocationId)) {
                        inventory.put("scroll_knowledge", true);
                        System.out.println("Got scroll knowledge!");
                    }
                    if ("berries".equals(newLocationId)) {
                        inventory.put("amulet", true);
                        System.out.println("Got amulet!");
                    }
                    if ("runes".equals(newLocationId)) {
                        inventory.put("scroll_knowledge", true);
                        System.out.println("Got runes knowledge!");
                    }

                    // Проверка победы при попытке открыть дверь
                    if ("victory_check".equals(newLocationId)) {
                        boolean hasAllArtifacts = inventory.get("lantern") &&
                                inventory.get("scroll_knowledge") &&
                                inventory.get("amulet");
                        System.out.println("Victory check: " + hasAllArtifacts);
                        System.out.println("Lantern: " + inventory.get("lantern"));
                        System.out.println("Scroll: " + inventory.get("scroll_knowledge"));
                        System.out.println("Amulet: " + inventory.get("amulet"));

                        if (hasAllArtifacts) {
                            newLocationId = "victory";
                            System.out.println("VICTORY ACHIEVED!");
                        } else {
                            newLocationId = "temple_door";
                            System.out.println("Not all artifacts collected");
                        }
                    }

                    session.setAttribute("currentLocation", newLocationId);
                    session.setAttribute("inventory", inventory);
                    currentLocationId = newLocationId;
                    break;
                }
            }
        }

        Location currentLocation = locations.get(currentLocationId);
        if (currentLocation == null) {
            currentLocation = locations.get("start");
        }

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("    <title>Квест: Таинственный лес - Rose Project</title>");
        html.append("    <meta charset='UTF-8'>");
        html.append("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("    <style>");
        html.append("        body { ");
        html.append("            font-family: 'Georgia', serif; ");
        html.append("            margin: 0; ");
        html.append("            padding: 20px; ");
        html.append("            background: linear-gradient(135deg, #1a2f1a 0%, #0d1f0d 100%); ");
        html.append("            color: #e8f5e8; ");
        html.append("            min-height: 100vh;");
        html.append("            line-height: 1.6;");
        html.append("        }");
        html.append("        .container { ");
        html.append("            max-width: 800px; ");
        html.append("            margin: 0 auto; ");
        html.append("            background: rgba(40, 60, 40, 0.9); ");
        html.append("            padding: 30px; ");
        html.append("            border-radius: 15px; ");
        html.append("            border: 2px solid #5d8c5d; ");
        html.append("            box-shadow: 0 8px 32px rgba(0,0,0,0.3); ");
        html.append("        }");
        html.append("        .nav { ");
        html.append("            margin: 20px 0; ");
        html.append("            text-align: center;");
        html.append("        }");
        html.append("        .nav a { ");
        html.append("            display: inline-block; ");
        html.append("            margin: 5px; ");
        html.append("            padding: 12px 20px; ");
        html.append("            background: rgba(93, 140, 93, 0.3); ");
        html.append("            color: #c8e6c8; ");
        html.append("            text-decoration: none; ");
        html.append("            border-radius: 8px; ");
        html.append("            border: 1px solid #5d8c5d; ");
        html.append("            transition: all 0.3s;");
        html.append("        }");
        html.append("        .nav a:hover { ");
        html.append("            background: rgba(93, 140, 93, 0.6); ");
        html.append("            transform: translateY(-2px);");
        html.append("        }");
        html.append("        .location-icon { ");
        html.append("            font-size: 4em; ");
        html.append("            text-align: center; ");
        html.append("            margin: 20px 0; ");
        html.append("            text-shadow: 0 0 20px rgba(200, 230, 200, 0.5);");
        html.append("        }");
        html.append("        .location-title { ");
        html.append("            color: #a5d6a7; ");
        html.append("            text-align: center; ");
        html.append("            margin-bottom: 30px; ");
        html.append("            font-size: 1.8em; ");
        html.append("            border-bottom: 2px solid #5d8c5d; ");
        html.append("            padding-bottom: 10px;");
        html.append("        }");
        html.append("        .location-description { ");
        html.append("            background: rgba(30, 45, 30, 0.7); ");
        html.append("            padding: 25px; ");
        html.append("            border-radius: 10px; ");
        html.append("            border-left: 4px solid #a5d6a7; ");
        html.append("            margin-bottom: 30px; ");
        html.append("            white-space: pre-line;");
        html.append("        }");
        html.append("        .actions { ");
        html.append("            display: grid; ");
        html.append("            gap: 15px; ");
        html.append("            margin: 30px 0; ");
        html.append("        }");
        html.append("        .action-btn { ");
        html.append("            background: linear-gradient(135deg, #4caf50 0%, #388e3c 100%); ");
        html.append("            color: white; ");
        html.append("            padding: 15px 25px; ");
        html.append("            border: none; ");
        html.append("            border-radius: 10px; ");
        html.append("            font-size: 16px; ");
        html.append("            cursor: pointer; ");
        html.append("            text-decoration: none; ");
        html.append("            text-align: center; ");
        html.append("            transition: all 0.3s; ");
        html.append("            font-family: 'Georgia', serif;");
        html.append("            display: block;");
        html.append("        }");
        html.append("        .action-btn:hover { ");
        html.append("            background: linear-gradient(135deg, #66bb6a 0%, #4caf50 100%); ");
        html.append("            transform: translateY(-2px); ");
        html.append("            box-shadow: 0 4px 15px rgba(76, 175, 80, 0.3);");
        html.append("        }");
        html.append("        .inventory { ");
        html.append("            background: rgba(30, 45, 30, 0.7); ");
        html.append("            padding: 20px; ");
        html.append("            border-radius: 10px; ");
        html.append("            margin: 20px 0; ");
        html.append("            border: 1px solid #5d8c5d;");
        html.append("        }");
        html.append("        .inventory h3 { ");
        html.append("            color: #a5d6a7; ");
        html.append("            margin-top: 0; ");
        html.append("        }");
        html.append("        .artifact { ");
        html.append("            display: inline-block; ");
        html.append("            margin: 5px 10px; ");
        html.append("            padding: 8px 15px; ");
        html.append("            background: rgba(93, 140, 93, 0.3); ");
        html.append("            border-radius: 5px; ");
        html.append("            border: 1px solid #5d8c5d; ");
        html.append("        }");
        html.append("        .artifact.owned { ");
        html.append("            background: rgba(76, 175, 80, 0.3); ");
        html.append("            border-color: #4caf50; ");
        html.append("            color: #c8e6c8; ");
        html.append("        }");
        html.append("        .victory { ");
        html.append("            text-align: center; ");
        html.append("            background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%); ");
        html.append("            padding: 40px; ");
        html.append("            border-radius: 15px; ");
        html.append("            margin: 20px 0; ");
        html.append("            border: 3px solid gold;");
        html.append("        }");
        html.append("        .victory h2 { ");
        html.append("            color: gold; ");
        html.append("            font-size: 2.5em; ");
        html.append("            margin-bottom: 20px; ");
        html.append("            text-shadow: 0 0 10px rgba(255,215,0,0.5);");
        html.append("        }");
        html.append("        .not-all-artifacts { ");
        html.append("            background: rgba(244, 67, 54, 0.2); ");
        html.append("            padding: 15px; ");
        html.append("            border-radius: 8px; ");
        html.append("            border-left: 4px solid #f44336; ");
        html.append("            margin: 15px 0; ");
        html.append("        }");
        html.append("    </style>");
        html.append("</head>");
        html.append("<body>");
        html.append("    <div class='container'>");
        html.append("        <div class='nav'>");
        html.append("            <a href='" + contextPath + "/welcome'>🏠 Главная</a>");
        html.append("            <a href='" + contextPath + "/game?action=restart'>🔄 Начать заново</a>");
        html.append("            <a href='" + contextPath + "/logout'>🚪 Выход</a>");
        html.append("        </div>");
        html.append("        ");
        html.append("        <h1 style='text-align: center; color: #a5d6a7; margin-bottom: 10px;'>🎮 Квест: Таинственный лес</h1>");
        html.append("        <p style='text-align: center; color: #c8e6c8; margin-bottom: 30px;'>Игрок: " + user.getUsername() + " 👤</p>");

        if ("victory".equals(currentLocationId)) {
            // Экран победы
            html.append("        <div class='victory'>");
            html.append("            <div style='font-size: 6em; margin-bottom: 20px;'>🏆</div>");
            html.append("            <h2>" + currentLocation.title + "</h2>");
            html.append("            <div class='location-description' style='background: rgba(255,255,255,0.1);'>");
            html.append("                " + currentLocation.description);
            html.append("            </div>");
            html.append("            <div style='margin-top: 30px;'>");
            html.append("                <a href='" + contextPath + "/game?action=restart' class='action-btn' style='background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%);'>🎮 Начать новую игру</a>");
            html.append("            </div>");
            html.append("        </div>");
        } else {
            // Обычная игровая локация
            html.append("        <div class='location-icon'>" + currentLocation.image + "</div>");
            html.append("        <div class='location-title'>" + currentLocation.title + "</div>");
            html.append("        <div class='location-description'>" + currentLocation.description + "</div>");

            // Действия
            html.append("        <div class='actions'>");
            for (Map.Entry<String, String> actionEntry : currentLocation.actions.entrySet()) {
                String actionText = actionEntry.getKey();
                String actionUrl = contextPath + "/game?action=" + actionText;
                html.append("            <a href='" + actionUrl + "' class='action-btn'>" + actionText + "</a>");
            }
            html.append("        </div>");

            // Инвентарь
            html.append("        <div class='inventory'>");
            html.append("            <h3>🎒 Инвентарь:</h3>");
            html.append("            <div class='artifact " + (inventory.get("lantern") ? "owned" : "") + "'>💡 Волшебный фонарь " + (inventory.get("lantern") ? "✓" : "✗") + "</div>");
            html.append("            <div class='artifact " + (inventory.get("scroll_knowledge") ? "owned" : "") + "'>📜 Знание древних " + (inventory.get("scroll_knowledge") ? "✓" : "✗") + "</div>");
            html.append("            <div class='artifact " + (inventory.get("amulet") ? "owned" : "") + "'>✨ Амулет света " + (inventory.get("amulet") ? "✓" : "✗") + "</div>");
            html.append("        </div>");

            // Сообщение, если не все артефакты собраны при попытке открыть дверь
            if ("temple_door".equals(currentLocationId)) {
                boolean hasAllArtifacts = inventory.get("lantern") && inventory.get("scroll_knowledge") && inventory.get("amulet");
                if (!hasAllArtifacts) {
                    html.append("        <div class='not-all-artifacts'>");
                    html.append("            <strong>⚠️ Внимание!</strong> У вас нет всех необходимых артефактов для открытия двери храма.");
                    html.append("        </div>");
                }
            }
        }

        html.append("    </div>");
        html.append("</body>");
        html.append("</html>");

        response.getWriter().print(html.toString());
    }
}