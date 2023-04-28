# Dispatcher Bot

Сервіс для переадресування запитів із мессенджера на сервіси

- Пінгування та сворення запитів на стороні сервіси за допомогою scheduler
- Відправка повідомлення користувачу в мессенджер через бота
- Список доступних ресурсів в REST API [swagger](http://10.10.5.173:8082/docs/)
 

## Етапи початку розборки

1. Завантажте данний [проеєкт](https://github.com/konexit/tbot) 
2. Змінити domain and port в application.properties в інших файлах (`application.properties`,`swagger/swagger.html`, `swagger/swagger.json`, `swagger/swagger.json`, `config.json`)
3. Змінити basedir в `pom.xml` для билда swaggerResources
4. Внести змінити у файл `config.json`