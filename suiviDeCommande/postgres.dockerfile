FROM postgres
COPY ./dump/init.sql /docker-entrypoint-initdb.d/