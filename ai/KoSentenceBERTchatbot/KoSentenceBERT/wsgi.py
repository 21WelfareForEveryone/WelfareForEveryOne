from main import app
from config import config

if __name__ == '__main__':
    app.run(port=config['port'], host="0.0.0.0")
