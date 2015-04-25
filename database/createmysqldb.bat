mysqladmin -u root -p drop inference
mysqladmin -u root -p create inference
mysql -u root -p -D inference -f < inferencepostgresqlmysql.sql