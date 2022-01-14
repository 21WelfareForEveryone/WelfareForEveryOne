FROM kmg2933/welfare-for-everyone:0916

LABEL "title"="KcELECTRA"

WORKDIR /workspace/KcELECTRA

COPY . .

RUN conda install -c conda-forge uwsgi

ENTRYPOINT ["uwsgi", "--ini", "uwsgi.ini"]