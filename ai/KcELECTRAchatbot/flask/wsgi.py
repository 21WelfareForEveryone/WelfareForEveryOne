from main import app
from config import config

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=config['port'])
