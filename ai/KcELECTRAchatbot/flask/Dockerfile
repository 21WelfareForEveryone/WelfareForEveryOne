FROM kmg2933/welfare-for-everyone:0916

LABEL "title"="KcELECTRA"

WORKDIR /workspace/KcELECTRA

COPY . .

RUN pip install gunicorn && pip install gevent

ENTRYPOINT ["gunicorn", "--bind", "0.0.0.0:8000", "wsgi:app", "-k", "gevent", "--timeout", "6000"]
