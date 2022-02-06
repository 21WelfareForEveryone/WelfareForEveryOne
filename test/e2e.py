from locust import HttpUser, between, task, SequentialTaskSet
from config import config
import json

headers = {'content-type': 'application/json'}

class test(SequentialTaskSet):
    wait_time = between(5, 10)
    
    @task
    def login(self):
        payload = {
            "user_id": config["user_id"],
            "user_password": config["password"]
        }

        self.client.post(config["login"], data=json.dumps(payload), headers=headers)
    
    @task
    def recommendWelfare(self):
        payload = {
            "token": config["token"]
        }
        self.client.post(config["recommend"], data=json.dumps(payload), headers=headers)
    
    @task
    def searchWelfare(self):
        payload = {
            "token" : config["token"],
            "welfare_category" : 2
        }
        self.client.post(config["search"], data=json.dumps(payload), headers=headers)
    
    @task
    def sendKsbert(self):
        payload = {
            "token": config["token"],
            "chat_message": "어린이복지"
        }
        
        self.client.post(config["kosbert"], data=json.dumps(payload), headers=headers)
    
    @task
    def sendKcelectra(self):
        payload = {
            "token": config["token"],
            "chat_message": "오늘 힘들어"
        }
        
        self.client.post(config["kcelectra"], data=json.dumps(payload), headers=headers)
    
    @task
    def done(self):
        self.interrupt()

class ApiUser(HttpUser):
    tasks = [test]
