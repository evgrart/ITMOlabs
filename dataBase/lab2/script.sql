--Сделать запрос для получения атрибутов из указанных таблиц, применив фильтры по указанным условиям:
--Таблицы: Н_ОЦЕНКИ, Н_ВЕДОМОСТИ.
--Вывести атрибуты: Н_ОЦЕНКИ.ПРИМЕЧАНИЕ, Н_ВЕДОМОСТИ.ИД.
--Фильтры (AND):
--a) Н_ОЦЕНКИ.ПРИМЕЧАНИЕ > отлично.
--b) Н_ВЕДОМОСТИ.ДАТА < 1998-01-05.
--Вид соединения: LEFT JOIN.

SELECT "Н_ВЕДОМОСТИ"."ИД", "Н_ОЦЕНКИ"."ПРИМЕЧАНИЕ"
FROM "Н_ВЕДОМОСТИ"
         LEFT JOIN "Н_ОЦЕНКИ" ON "Н_ВЕДОМОСТИ"."ОЦЕНКА" = "Н_ОЦЕНКИ"."КОД"
WHERE "Н_ОЦЕНКИ"."ПРИМЕЧАНИЕ" > 'отлично'
  AND "Н_ВЕДОМОСТИ"."ДАТА" < '1998-01-05'::date;

--Сделать запрос для получения атрибутов из указанных таблиц, применив фильтры по указанным условиям:
--Таблицы: Н_ЛЮДИ, Н_ОБУЧЕНИЯ, Н_УЧЕНИКИ.
--Вывести атрибуты: Н_ЛЮДИ.ИД, Н_ОБУЧЕНИЯ.НЗК, Н_УЧЕНИКИ.ИД.
--Фильтры: (AND)
--a) Н_ЛЮДИ.ИД = 152862.
--b) Н_ОБУЧЕНИЯ.НЗК > 999080.
--c) Н_УЧЕНИКИ.НАЧАЛО > 2009-02-09.
--Вид соединения: LEFT JOIN.

SELECT "Н_ЛЮДИ"."ИД", "Н_ОБУЧЕНИЯ"."НЗК", "Н_УЧЕНИКИ"."ИД"
FROM "Н_ЛЮДИ"
         LEFT JOIN "Н_ОБУЧЕНИЯ" ON "Н_ОБУЧЕНИЯ"."ЧЛВК_ИД" = "Н_ЛЮДИ"."ИД"
         LEFT JOIN "Н_УЧЕНИКИ" ON "Н_УЧЕНИКИ"."ЧЛВК_ИД" = "Н_ОБУЧЕНИЯ"."ЧЛВК_ИД"
WHERE "Н_ЛЮДИ"."ИД" = 152862
  AND "Н_ОБУЧЕНИЯ"."НЗК"::integer > 999080
  AND "Н_УЧЕНИКИ"."НАЧАЛО" > '2009-02-09'::date;

--Вывести число дней без учета повторений.
--При составлении запроса нельзя использовать DISTINCT.

SELECT COUNT("ДАТА")
FROM (SELECT "Н_ВЕДОМОСТИ"."ДАТА" AS "ДАТА"
      FROM "Н_ВЕДОМОСТИ"
      GROUP BY "Н_ВЕДОМОСТИ"."ДАТА") as count;

--SELECT COUNT(DISTINCT, "Н_ВЕДОМОСТИ"."ДАТА") FROM "Н_ВЕДОМОСТИ";

--Найти группы, в которых в 2011 году было более 5 обучающихся студентов на ФКТИУ.
--Для реализации использовать соединение таблиц.

SELECT "Н_УЧЕНИКИ"."ГРУППА" AS "ГРУППЫ"
FROM "Н_УЧЕНИКИ"
         INNER JOIN "Н_ПЛАНЫ" ON "Н_ПЛАНЫ"."ИД" = "Н_УЧЕНИКИ"."ПЛАН_ИД"
         INNER JOIN "Н_ОТДЕЛЫ" ON "Н_ОТДЕЛЫ"."ИД" = "Н_ПЛАНЫ"."ОТД_ИД"
WHERE "Н_ОТДЕЛЫ"."КОРОТКОЕ_ИМЯ" LIKE 'КТиУ'
  AND "Н_ПЛАНЫ"."УЧЕБНЫЙ_ГОД" LIKE '%2011%'
GROUP BY "Н_УЧЕНИКИ"."ГРУППА"
HAVING COUNT("Н_УЧЕНИКИ"."ИД") > 5;

--Выведите таблицу со средними оценками студентов группы 4100 (Номер, ФИО, Ср_оценка), у которых средняя оценка не меньше максимальной оценк(е|и) в группе 1101.

SELECT "Н_ЛЮДИ"."ИД" AS "Номер",
       "Н_ЛЮДИ"."ФАМИЛИЯ",
       "Н_ЛЮДИ"."ИМЯ",
       "Н_ЛЮДИ"."ОТЧЕСТВО",
       AVG("Н_ВЕДОМОСТИ"."ОЦЕНКА"::numeric)
FROM "Н_ЛЮДИ"
         INNER JOIN "Н_УЧЕНИКИ" ON "Н_УЧЕНИКИ"."ЧЛВК_ИД" = "Н_ЛЮДИ"."ИД"
         INNER JOIN "Н_ВЕДОМОСТИ" ON "Н_ВЕДОМОСТИ"."ЧЛВК_ИД" = "Н_ЛЮДИ"."ИД"
    AND "Н_ВЕДОМОСТИ"."ОЦЕНКА" NOT IN ('осв', 'неявка', 'незач', 'зачет')
WHERE "Н_УЧЕНИКИ"."ГРУППА" = '4100'
GROUP BY "Н_ЛЮДИ"."ИД", "Н_ЛЮДИ"."ФАМИЛИЯ", "Н_ЛЮДИ"."ИМЯ", "Н_ЛЮДИ"."ОТЧЕСТВО"
HAVING AVG("Н_ВЕДОМОСТИ"."ОЦЕНКА"::integer) >= (SELECT MAX("Н_ВЕДОМОСТИ"."ОЦЕНКА"::integer)
                                                FROM "Н_УЧЕНИКИ"
                                                         INNER JOIN public."Н_ВЕДОМОСТИ"
                                                                    on "Н_УЧЕНИКИ"."ИД" = "Н_ВЕДОМОСТИ"."ИД"
                                                                        AND "Н_УЧЕНИКИ"."ГРУППА" = '1101'
                                                                        AND "Н_ВЕДОМОСТИ"."ОЦЕНКА" NOT IN
                                                                            ('осв', 'неявка', 'незач', 'зачет'));

--Получить список студентов, отчисленных до первого сентября 2012 года с заочной формы обучения (специальность: 230101). В результат включить:
--номер группы;
--номер, фамилию, имя и отчество студента;
--номер пункта приказа;
--Для реализации использовать подзапрос с EXISTS.

SELECT "СТУДЕНТЫ"."ГРУППА",
       "СТУДЕНТЫ"."ИД",
       "Н_ЛЮДИ"."ФАМИЛИЯ",
       "Н_ЛЮДИ"."ИМЯ",
       "Н_ЛЮДИ"."ОТЧЕСТВО",
       "СТУДЕНТЫ"."П_ПРКОК_ИД"
FROM "Н_УЧЕНИКИ" "СТУДЕНТЫ"
         JOIN "Н_ЛЮДИ" ON "Н_ЛЮДИ"."ИД" = "СТУДЕНТЫ"."ЧЛВК_ИД"
         JOIN "Н_ПЛАНЫ" ON "СТУДЕНТЫ"."ПЛАН_ИД" = "Н_ПЛАНЫ"."ИД"
         JOIN "Н_ФОРМЫ_ОБУЧЕНИЯ" ON "Н_ПЛАНЫ"."ФО_ИД" = "Н_ФОРМЫ_ОБУЧЕНИЯ"."ИД"
    AND ("Н_ФОРМЫ_ОБУЧЕНИЯ"."НАИМЕНОВАНИЕ" = 'Заочная')
         JOIN "Н_НАПРАВЛЕНИЯ_СПЕЦИАЛ" ON "Н_ПЛАНЫ"."НАПС_ИД" = "Н_НАПРАВЛЕНИЯ_СПЕЦИАЛ"."ИД"
         JOIN "Н_НАПР_СПЕЦ" ON "Н_НАПР_СПЕЦ"."ИД" = "Н_НАПРАВЛЕНИЯ_СПЕЦИАЛ"."НС_ИД"
    AND "Н_НАПР_СПЕЦ"."КОД_НАПРСПЕЦ" = '230101'
WHERE EXISTS (SELECT 'GOYDA'
              FROM "Н_УЧЕНИКИ" "ВНУТР_УЧЕНИКИ"
              WHERE "ВНУТР_УЧЕНИКИ"."ПРИЗНАК" = 'отчисл'
                AND "ВНУТР_УЧЕНИКИ"."СОСТОЯНИЕ" = 'утвержден'
                AND "ВНУТР_УЧЕНИКИ"."ИД" = "СТУДЕНТЫ"."ИД"
                AND DATE("ВНУТР_УЧЕНИКИ"."КОНЕЦ") < '2012-09-01'::DATE);

--Вывести список людей, не являющихся или не являвшихся студентами ФКТИУ (данные, о которых отсутствуют в таблице Н_УЧЕНИКИ). В запросе нельзя использовать DISTINCT.

SELECT "Н_ЛЮДИ"."ИД",
       "Н_ЛЮДИ"."ФАМИЛИЯ",
       "Н_ЛЮДИ"."ИМЯ",
       "Н_ЛЮДИ"."ОТЧЕСТВО"
FROM "Н_ЛЮДИ"
WHERE NOT EXISTS (SELECT 1
                  FROM "Н_УЧЕНИКИ"
                           JOIN "Н_ПЛАНЫ" ON "Н_УЧЕНИКИ"."ПЛАН_ИД" = "Н_ПЛАНЫ"."ИД"
                           JOIN "Н_ОТДЕЛЫ" ON "Н_ПЛАНЫ"."ОТД_ИД" = "Н_ОТДЕЛЫ"."ИД"
                      AND "Н_ОТДЕЛЫ"."КОРОТКОЕ_ИМЯ" = 'КТиУ'
                  WHERE "Н_УЧЕНИКИ"."ЧЛВК_ИД" = "Н_ЛЮДИ"."ИД");



