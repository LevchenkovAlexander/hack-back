# Капи Mini App
Это mini app на платформе Max, который поможет расставить приоритеты Ваших задач.

## Возможности нашего mini app
 - Добавление задачи
 - Трекинг прогресса задачи
 - Генерация оптимального порядка выполнения задач, опирающееся на Ваше свободное время, дедлайн задачи и ее сложность
### Ознакомиться с остальными деталями нашего проекта можно в презентации.
# Инструкция по запуску

## 1. Установка и настройка Localtonet

- Скачайте клиент с официального сайта: [https://localtonet.com/download](https://localtonet.com/download)
- Перейдите в папку с файлом клиента в терминале
- Выполните команду:

```bash
localtonet --authtoken gHyDh0KlEZrX7AwBGW3VJqCk4i1afSPY && ssh -p 223 zGt91FJ5EMxzLtHavwNOAbonzB9SUvJnYKmJDB2-S4d5oSqApdSy2y_newJ-YcAUdml_9z5DhzKmZumlm-LsfA@d5axrvjae.localto.net -R5911:127.0.0.1:8080
```
## 2. Запуск проекта

- Перейдите в папку с проектом `./backend`
- Убедитесь, что на системе установлен и работает Docker Engine
- Выполните команду сборки:

```bash
docker compose up --build
```
