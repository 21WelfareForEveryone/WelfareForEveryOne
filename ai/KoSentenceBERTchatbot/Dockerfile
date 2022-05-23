FROM bookbug/kosbert_image

LABEL "title"="KoSBERT"

WORKDIR /workspace/KoSentenceBERT

COPY KoSentenceBERT/main.py .
COPY KoSentenceBERT/utils.py .
COPY KoSentenceBERT/config.py .
COPY KoSentenceBERT/wsgi.py .
COPY KoSentenceBERT/corpus_embedding.csv .
# COPY KoSentenceBERT/device/serialization.py /torch/ #GPU

RUN cp -r /workspace/KoSentenceBERT_SKTBERT/output /workspace/KoSentenceBERT/output

RUN pip install gunicorn && pip install gevent

ENTRYPOINT ["gunicorn","--bind", "0.0.0.0:8080","wsgi:app", "-k", "gevent", "--timeout", "6000"]