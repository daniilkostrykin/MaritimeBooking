FROM postgres:latest

ENV POSTGRES_USER=daniil
ENV POSTGRES_PASSWORD=daniil
ENV POSTGRES_DB=sea_cruises

EXPOSE 5433

VOLUME /var/lib/postgresql/data

CMD ["postgres", "-p", "5433"] 