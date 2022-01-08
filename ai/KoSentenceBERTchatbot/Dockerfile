FROM bookbug/kosbert_image

LABEL "title"="KoSBERT"

WORKDIR /workspace

COPY KoSentenceBERT ./KoSentenceBERT
# COPY KoSentenceBERT/device/serialization.py /torch/ #GPU

WORKDIR /workspace/KoSentenceBERT

RUN conda install -y -c conda-forge uwsgi

ENTRYPOINT [ "uwsgi", "--ini", "uwsgi.ini" ]
