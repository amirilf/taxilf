sudo service postgresql stop
sudo service redis-server stop
sudo docker rm taxilf-postgres
sudo docker-compose up -d
mvn spring-boot:run
sudo docker-compose down