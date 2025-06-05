# MaritimeBooking

**Настольное приложение для управления и анализа бронирований морских круизов.** Работает поверх PostgreSQL-базы `sea_cruises` (схема `maritime_booking`) и объединяет CRUD-операции над портами, судами, клиентами, рейсами и билетами с набором готовых аналитических отчётов в одном JavaFX-окне. Рассчитано на администраторов продаж и аналитиков, которым нужен локальный GUI-инструмент без веб-развёртывания и внешнего API.

![Java](https://img.shields.io/badge/Java-22-ED8B00?logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-17.0.2-1B82D3?logo=java&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-6.2-59666C?logo=hibernate&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16%2B-316192?logo=postgresql&logoColor=white)

## Как это устроено

Одна база — два взаимозаменяемых рантайма доступа к данным. Пользовательский интерфейс и реляционная схема общие; отличается только слой доступа:

```
        ┌───────────────────────────────────────────┐
        │                JavaFX UI                   │
        │   15 вкладок-операций + произвольный SQL   │
        └───────────────────┬───────────────────────┘
                            │  статические вызовы
              ┌─────────────┴─────────────┐
              ▼                           ▼
     ┌──────────────────┐       ┌────────────────────────┐
     │   JDBCManager    │       │   HibernateManager     │
     │  PreparedStmt    │       │  HQL / Session / ORM   │
     └────────┬─────────┘       └───────────┬────────────┘
              └──────────────┬──────────────┘
                             ▼
     PostgreSQL · jdbc:postgresql://localhost:5433/sea_cruises
     schema: maritime_booking
```

- **JDBC-рантайм** — `MaritimeBookingApp` + `JDBCManager`: ручные `PreparedStatement`/`ResultSet`, полный контроль над SQL.
- **Hibernate-рантайм** — `MaritimeBookingAppHibernate` + `HibernateManager`: те же операции через `Session`, HQL и entity-маппинг.

Оба рантайма предоставляют **одинаковый набор из 15 операций** (см. «Публичные интерфейсы») и могут запускаться по очереди на одной и той же базе.

## Быстрый обзор

**Сценарий: покупка билета.** Вкладка «Покупка билета» — типовой путь администратора:

1. Выбрать активный рейс и судно (список строится только из рейсов со статусом `active`).
2. Выбрать кабину выбранного судна (категория: `standard` / `deluxe` / `suite`).
3. Указать цену, способ оплаты (`card` / `cash`), тип питания, наличие страховки и вес багажа.
4. Указать дату покупки не позднее текущей.

Валидация собрана в `Validator` и выполняется до обращения к БД; ошибки выводятся списком, проблемные поля подсвечиваются.

**Отчёт: аналитика продаж.** Вкладка «Продажи билетов» агрегирует билеты по месяцам и странам:

```
Месяц │ Отпр.  │ Приб.    │ Билетов │ Выручка  │ Средний чек │ Страховка, %
──────┼────────┼──────────┼─────────┼──────────┼─────────────┼─────────────
  1   │ Norway │ Germany  │     128 │  512 000 │       4 000 │          62
  3   │ Italy  │ Greece   │      95 │  380 000 │       4 000 │          55
  7   │ Spain  │ Portugal │     210 │  945 000 │       4 500 │          71
```

Отчёт строится одним SQL-запросом: `GROUP BY` по месяцам, `SUM` для выручки, `AVG` для среднего чека и доля застрахованных билетов для конверсии страховок. Группировка по месяцам и странам даёт сводку, а отсечение малозначимых групп выполняется условием по агрегату (`HAVING`).

## Background

- **Два слоя доступа к данным над одной схемой.** Проект намеренно демонстрирует оба подхода к работе с SQL: ручной JDBC (`JDBCManager`) и ORM-абстракцию Hibernate (`HibernateManager` + JPA-сущности). Логика интерфейса не зависит от выбранного слоя.
- **Доменные стандарты.** Суда идентифицируются по [IMO](https://www.imo.org/), морские порты — по [UN/LOCODE](https://unece.org/trade/cefact/unlocode-code-list-country-and-territory).
- **Нормализация схемы.** Реляционная схема спроектирована методом декомпозиции по алгоритму Фэджина и приведена к третьей и четвёртой нормальным формам (3NF/4NF), что исключает аномалии вставки, модификации и удаления при работе с составными и многозначными зависимостями.
- **Доменные перечисления:**
  - `VoyageStatus`: `active`, `delayed`, `completed`, `cancelled`, `postponed`, `in_progress`
  - `CabinCategory`: `standard`, `deluxe`, `suite`
  - `PaymentMethod`: `card`, `cash`
  - `MealType`: `no_meals`, `breakfast`, `half_board`, `full_board`, `all_inclusive`, `ultra_all_inclusive`

## Публичные интерфейсы

### Вкладки приложения

| № | Вкладка | Назначение | Входные данные |
|---|---------|-----------|----------------|
| 1 | Таблицы | Метаданные и произвольный SQL | список таблиц схемы, свободный запрос |
| 2 | Покупка билета | Оформить билет существующему клиенту | email, рейс, судно, кабина, цена, оплата, питание, страховка, багаж, дата |
| 3 | Добавление клиента | Зарегистрировать нового клиента | email, ФИО, паспорт (10 цифр), дата рождения |
| 4 | Билеты за год с ценой меньше | Билеты за год дешевле порога | год, максимальная цена |
| 5 | Билеты клиента | Билеты выбранного клиента | email клиента |
| 6 | Клиенты по питанию | Клиенты с заданным типом питания | тип питания |
| 7 | Рейсы по статусу и питанию | Рейсы по статусу и питанию | статус, тип питания |
| 8 | Билеты со страховкой по стране | Застрахованные билеты по стране | страна отправления |
| 9 | Добавить клиента и билет | Атомарная вставка клиента и билета | параметры клиента и билета |
| 10 | Удалить рейс и всё связанное | Каскадное удаление рейса и связанных записей | рейс, судно |
| 11 | Корректировка цены багажа | Массовый пересчёт цен по весу багажа | минимальный вес, дата |
| 12 | Маршрут рейса | Последовательность этапов рейса | рейс |
| 13 | Продажи билетов | Аналитика продаж по месяцам и странам | год |
| 14 | Средний чек по клиентам | Средний чек на клиента | — |
| 15 | Выручка по маршрутам | Выручка по парам портов | — |

### Слой доступа к данным

`JDBCManager` и `HibernateManager` предоставляют одинаковую функциональность. Ниже — ключевые группы (JDBC-сигнатуры; ORM-версии принимают `Session`):

| Назначение | Метод | Возврат |
|-----------|-------|---------|
| Метаданные схемы | `getTablesMetadata(Connection)` | `ResultSet` |
| Произвольный запрос | `executeQuery(Connection, String sql)` | `ResultSet` |
| Покупка билета | `buyTicket(Connection, String email, int voyageId, String vesselId, int cabinId, double price, String paymentMethod, String mealType, boolean hasInsurance, int luggageWeight, String purchaseDateStr)` | `void` |
| Добавить клиента | `addClient(Connection, String lastName, String firstName, String middleName, long passportSeries, String birthDateStr, String email)` | `void` |
| Клиент + билет (в транзакции) | `addClientAndTicket(Connection, ...)` | `int ticketId` |
| Проверка клиента | `checkCustomerExists(Connection, String email)` | `boolean` |
| Каскадное удаление рейса | `deleteVoyage(Connection, int voyageId, String vesselId)` | `void` |
| Пересчёт цены багажа | `updateLuggagePrice(Connection, double minWeight, String date)` | `void` |
| Аналитика продаж | `getTicketSales(Connection, String year)` | `ResultSet` |
| Средний чек | `getAverageCheck(Connection)` | `ResultSet` |
| Выручка по маршрутам | `getTotalRevenue(Connection)` | `ResultSet` |

Полный перечень — в исходниках: [`JDBCManager`](src/main/java/org/example/Practice/JDBCManager.java) и [`HibernateManager`](src/main/java/org/example/Practice/HibernateManager.java).

### Схема базы данных

Все объекты находятся в схеме `maritime_booking`:

| Таблица | Первичный ключ | Назначение |
|---------|----------------|-----------|
| `customers` | `email` | Клиенты: ФИО, паспорт, дата рождения |
| `vessels` | `imo` | Суда: название, владелец, год постройки, страна регистрации |
| `ports` | `un_locode` | Порты: название, город, страна, глубина гавани |
| `voyages` | `id`, `vessel_id` | Рейсы и их статус |
| `voyage_stages` | `stop_number`, `voyage_id`, `vessel_id` | Этапы рейса: порты отправления/прибытия, даты |
| `cabins` | `id`, `vessel_id` | Кабины: категория, вместимость, вид из окна |
| `tickets` | `id`, `voyage_id`, `vessel_id`, `cabin_id` | Билеты: email, цена, оплата, питание, страховка, багаж, дата |

## Ограничения

- **Реквизиты подключения захардкожены:** база `sea_cruises`, пользователь/пароль `daniil`/`daniil`, порт `5433`. Переменные окружения и `.env` не используются; для другого стенда правьте `src/main/resources/hibernate.properties`, `src/main/resources/hibernate.cfg.xml` и константы в `JDBCManager`/`MaritimeBookingApp*`.
- **Автосоздание схемы:** в `hibernate.cfg.xml` включён `hibernate.hbm2ddl.auto=update` — Hibernate досоздаёт и обновляет таблицы, но не наполняет их данными.

## Установка и запуск

**Требования:** JDK 22, Maven 3.9+, Docker (для PostgreSQL).

1. Клонировать репозиторий:

```bash
git clone https://github.com/daniilkostrykin/MaritimeBooking.git
cd MaritimeBooking
```

2. Поднять PostgreSQL в Docker на порту `5433`:

```bash
docker build -t sea_cruises .
docker run -d --name sea_cruises -p 5433:5433 sea_cruises
```

3. Наполнить базу `sea_cruises` данными (схема `maritime_booking`), например восстановив резервную копию:

```bash
psql -h localhost -p 5433 -U daniil -d sea_cruises -f backup.sql
```

4. Запустить приложение (Hibernate-рантайм, как задано в `pom.xml`):

```bash
mvn clean javafx:run
```

Либо полностью автоматизированно — скрипт поднимает Docker Desktop, стартует контейнер, запускает приложение и по завершении останавливает контейнер:

```bash
run_with_docker.bat
```

5. Чтобы запустить JDBC-рантайм вместо Hibernate, переключите главную точку входа в `pom.xml` (плагин `javafx-maven-plugin`) и запустите снова:

```xml
<mainClass>org.example.Practice.MaritimeBookingApp</mainClass>
```

```bash
mvn clean javafx:run
```

Оба рантайма работают с одной базой `sea_cruises`.
