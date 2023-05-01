# Dispatcher Bot 

Додаток для взаємодії мессенджера та сервісів компанії КОНЕКС

- Пінгування та сворення запитів на стороні сервіси за допомогою scheduler
- Відправка повідомлення користувачу в мессенджер через бота
- Список доступних ресурсів в REST API [swagger](http://10.10.1.14:8082/docs/)
 

## Запуск проєкта

1. Завантажте данний [проєкт](https://github.com/konexit/tbot) 
2. Змінити domain and port в application.properties та в інших файлах: `application.properties`,`swagger/swagger.html`, `swagger/swagger.json`, `swagger/swagger.json`, `config.json`
3. Змінити `build.dir` та `project.basedir` у файлі `pom.xml` для продового бiлда
4. При необхідності внести змінити у файл `config.json`