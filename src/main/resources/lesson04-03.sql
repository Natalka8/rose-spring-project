-- ### Теперь можно делать упражнения
--
-- **Основные конструкции и синтаксис**
-- Упражнение 4.1. Запустите клиент psql и настройте работу с демонстрационной базой данных (это пропускаем если база уже есть)

-- Упражнение 4.2. Выберите все модели самолетов и соответствующие им диапазоны дальности полетов.
SELECT * FROM aircrafts_data;


-- Упражнение 4.3. Найдите все самолеты c максимальной дальностью полета:
-- либо больше 10 000 км, либо меньше 4 000 км
SELECT aircraft_code, model->>'en' as model, range
FROM aircrafts_data
WHERE range > 10000 OR range < 4000
ORDER BY range DESC;

-- больше 6 000 км, а название не заканчивается на «100»
SELECT aircraft_code, model->>'en' as model, range
FROM aircrafts_data
WHERE range > 6000 AND model->>'en' NOT LIKE '%100'
ORDER BY range DESC;

-- Обратите внимание на порядок следования предложений WHERE и FROM.

-- Упражнение 4.4. Определите номера и время отправления всех рейсов, прибывших в аэропорт назначения не вовремя.
SELECT
    flight_no, actual_arrival::time
FROM flights
WHERE actual_arrival > scheduled_arrival
  AND status = 'Arrived';


-- Упражнение 4.5. Подсчитайте количество отмененных рейсов из аэропорта
-- Пулково (LED), как вылет, так и прибытие которых было назначено на четверг.

SELECT COUNT(*)
FROM flights
WHERE (departure_airport = 'LED' OR arrival_airport = 'LED')
AND status = 'Cancelled'
AND EXTRACT(DOW FROM scheduled_departure) = 4
AND EXTRACT(DOW FROM scheduled_arrival) = 4;

-- **Соединения**

-- Упражнение 4.6. Выведите имена пассажиров, купивших билеты экономкласса за сумму, превышающую 70 000 рублей.

SELECT t.passenger_name,
       b.total_amount
FROM bookings b
         JOIN bookings.tickets t
            ON b.book_ref= t.book_ref
WHERE b.total_amount>70000
  ORDER BY b.total_amount;

-- Упражнение 4.7. Напечатанный посадочный талон должен содержать фамилию и имя пассажира, коды аэропортов вылета и прилета, дату и время
-- вылета и прилета по расписанию, номер места в салоне самолета. Напишите запрос, выводящий всю необходимую информацию для полученных
-- посадочных талонов на рейсы, которые еще не вылетели.

SELECT
    t.passenger_name,
    f.departure_airport,
    f.arrival_airport,
    f.scheduled_departure,
    f.scheduled_arrival,
    bp.seat_no
FROM boarding_passes bp
         JOIN tickets t ON bp.ticket_no = t.ticket_no
         JOIN flights f ON bp.flight_id = f.flight_id
WHERE f.status IN ('Scheduled', 'On Time')
ORDER BY f.scheduled_departure;

-- Упражнение 4.8. Некоторые пассажиры, вылетающие сегодняшним рейсом
-- («сегодня» определяется функцией bookings.now), еще не прошли регистрацию, т. е. не получили посадочного талона. Выведите имена этих пассажиров и номера рейсов.

SELECT t.passenger_name, f.flight_no
FROM tickets t
         JOIN ticket_flights tf ON t.ticket_no = tf.ticket_no
         JOIN flights f ON tf.flight_id = f.flight_id
         LEFT JOIN boarding_passes bp ON t.ticket_no = bp.ticket_no AND f.flight_id = bp.flight_id
WHERE DATE(f.scheduled_departure) = DATE(bookings.now())
  AND bp.ticket_no IS NULL;

-- Упражнение 4.9. Выведите номера мест, оставшихся свободными в рейсах из Анапы (AAQ) в Шереметьево (SVO), вместе с номером рейса и его датой.

SELECT
    f.flight_no,
    f.scheduled_departure::date as flight_date,
    s.seat_no
FROM flights f
         CROSS JOIN seats s
WHERE f.departure_airport = 'AAQ'
  AND f.arrival_airport = 'SVO'
  AND f.status IN ('Scheduled', 'On Time')
  AND s.aircraft_code = f.aircraft_code
  AND NOT EXISTS (
    SELECT 1 FROM boarding_passes bp
    WHERE bp.flight_id = f.flight_id
      AND bp.seat_no = s.seat_no
)
ORDER BY f.scheduled_departure, s.seat_no;
-- **Агрегирование и группировка**

-- Упражнение 4.10. Напишите запрос, возвращающий среднюю стоимость авиабилета из Воронежа (VOZ) в Санкт-Петербург (LED). Поэкспериментируйте с другими агрегирующими функциями (sum, max). Какие еще агрегирующие функции бывают?
SELECT
    AVG(tf.amount) as avg_price,
    SUM(tf.amount) as total_sum,
    MAX(tf.amount) as max_price,
    MIN(tf.amount) as min_price,
    COUNT(*) as tickets_count
FROM ticket_flights tf
         JOIN flights f ON tf.flight_id = f.flight_id
WHERE f.departure_airport = 'VOZ'
  AND f.arrival_airport = 'LED';

-- Упражнение 4.11. Напишите запрос, возвращающий среднюю стоимость авиабилета в каждом из классов перевозки. Модифицируйте его таким образом, чтобы было видно, какому классу какое значение соответствует.
SELECT
    fare_conditions as class,
    AVG(amount) as avg_price,
    COUNT(*) as tickets_count
FROM ticket_flights
GROUP BY fare_conditions
ORDER BY avg_price DESC;

-- Упражнение 4.12. Выведите все модели самолетов вместе с общим количеством мест в салоне.

SELECT
    ad.aircraft_code,
    ad.model->>'en' as aircraft_model,
    COUNT(s.seat_no) as total_seats
FROM aircrafts_data ad
         JOIN seats s ON ad.aircraft_code = s.aircraft_code
GROUP BY ad.aircraft_code, ad.model
ORDER BY total_seats DESC;

-- Упражнение 4.13. Напишите запрос, возвращающий список аэропортов, в которых было принято более 500 рейсов.
SELECT
    ad.airport_code,
    ad.airport_name->>'en' as airport_name,
    ad.city->>'en' as city,
    COUNT(f.flight_id) as arrival_flights_count
FROM airports_data ad
         JOIN flights f ON ad.airport_code = f.arrival_airport
WHERE f.status IN ('Arrived', 'Departed')
GROUP BY ad.airport_code, ad.airport_name, ad.city
HAVING COUNT(f.flight_id) > 500
ORDER BY arrival_flights_count DESC;

-- **Модификация данных**
-- Упражнение 4.14. Авиакомпания провела модернизацию салонов всех имеющихся самолетов «Сессна» (код CN1), в результате которой был добавлен седьмой ряд кресел. Измените соответствующую таблицу, чтобы отразить этот факт.
-- Добавляем седьмой ряд (места 7A, 7B, 7C, 7D и т.д. в зависимости от конфигурации)

SELECT seat_no, fare_conditions
FROM seats
WHERE aircraft_code = 'CN1'
  AND seat_no LIKE '7%'
ORDER BY seat_no;

-- Упражнение 4.15. В результате еще одной модернизации в самолетах «Аэробус A319» (код 319) ряды кресел с шестого по восьмой были переведены в разряд бизнес-класса. Измените таблицу одним запросом и получите измененные данные с помощью предложения RETURNING.
-- Обновляем класс обслуживания для рядов 6-8
UPDATE seats
SET fare_conditions = 'Business'
WHERE aircraft_code = '319'
  AND SUBSTRING(seat_no FROM '^(\d+)')::integer BETWEEN 6 AND 8
RETURNING aircraft_code, seat_no, fare_conditions;

-- Упражнение 4.16. Создайте новое бронирование текущей датой. В качестве номера бронирования можно взять любую последовательность из шести символов, начинающуюся на символ подчеркивания. Общая сумма должна составлять 30 000 рублей.
-- Создайте электронный билет, связанный с бронированием, на ваше имя.
-- Назначьте электронному билету два рейса: один из Москвы (VKO) во Владивосток (VVO) через неделю, другой — обратно через две недели. Оба рейса выполняются эконом-классом, стоимость каждого должна составлять 15 000 рублей.
-- Создаем бронирование
INSERT INTO bookings (book_ref, book_date, total_amount)
VALUES ('_ROSE1', NOW(), 30000.00);

-- Создаем билет
INSERT INTO tickets (ticket_no, book_ref, passenger_id, passenger_name, contact_data)
VALUES ('1234567890123', '_ROSE1', 'ROSE123', 'Иван Иванов', '{"phone": "+79161234567", "email": "ivan@mail.ru"}');

-- Находим подходящие рейсы
WITH selected_flights AS (
    SELECT flight_id FROM flights
    WHERE departure_airport = 'VKO' AND arrival_airport = 'VVO'
      AND scheduled_departure > NOW() + INTERVAL '7 days'
    LIMIT 1
),
     return_flights AS (
         SELECT flight_id FROM flights
         WHERE departure_airport = 'VVO' AND arrival_airport = 'VKO'
           AND scheduled_departure > NOW() + INTERVAL '14 days'
         LIMIT 1
     )
-- Добавляем рейсы к билету
INSERT INTO ticket_flights (ticket_no, flight_id, fare_conditions, amount)
SELECT '1234567890123', flight_id, 'Economy', 15000.00
FROM selected_flights
UNION ALL
SELECT '1234567890123', flight_id, 'Economy', 15000.00
FROM return_flights;
-- **Описание данных: отношения**

-- Упражнение 4.17. Авиакомпания хочет предоставить пассажирам возможность повышения класса обслуживания уже после покупки билета при регистрации на рейс. За это взимается отдельная плата. Добавьте в демонстрационную базу данных возможность хранения таких операций.


-- Упражнение 4.18. Авиакомпания начинает выдавать пассажирам карточки постоянных клиентов. Вместо того чтобы каждый раз вводить имя, номер документа и контактную информацию, постоянный клиент может указать номер своей карты, к которой привязана вся необходимая информация. При этом клиенту может предоставляться скидка.
-- Измените существующую схему данных так, чтобы иметь возможность хранить информацию о постоянных клиентах.

-- Упражнение 4.19. Постоянные клиенты могут бесплатно провозить с собой животных. Добавьте в ранее созданную таблицу постоянных клиентов информацию о перевозке домашних животных.

-- **Вложенные подзапросы**

-- Упражнение 4.20. Найдите модели самолетов «дальнего следования», максимальная продолжительность рейсов которых составила более 6 часов.


-- Упражнение 4.21. Подсчитайте количество рейсов, которые хотя бы раз были задержаны более чем на 4 часа.


-- Упражнение 4.22. Для составления рейтинга аэропортов учитывается суточная пропускная способность, т. е. среднее количество вылетевших из него и прилетевших в него за сутки пассажиров. Выведите 10 аэропортов с наибольшей суточной пропускной  способностью, упорядоченных по убыванию данной величины.

-- **Псевдонимы для таблиц**

-- Упражнение 4.23. С целью оценки интенсивности работы обслуживающего персонала аэропорта Шереметьево (SVO) вычислите, сколько раз вылеты следовали друг за другом с перерывом менее пяти минут.

-- **Представления**

-- Упражнение 4.24. Количество рейсов, принятых конкретным аэропортом за каждый день, — довольно востребованный запрос. Напишите представление данного запроса для аэропорта города Барнаул (BAX).
