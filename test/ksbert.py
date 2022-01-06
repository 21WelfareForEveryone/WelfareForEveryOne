from locust import HttpUser, between, task
from config import config
import json

class test(HttpUser):
    wait_time = between(5, 10)
    
    @task
    def connect(self):
        payload = {
            "token": config.token,
            "chat_message": "어린이복지"
        }
        
        headers = {'content-type': 'application/json'}
        
        self.client.post(config.chatbot, data=json.dumps(payload), headers=headers)