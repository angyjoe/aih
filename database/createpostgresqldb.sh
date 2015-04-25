dropdb -e -U postgres inference
createdb -O postgres -e -U postgres inference
psql -e -f inferencepostgresqlmysql.sql -d inference -U postgres