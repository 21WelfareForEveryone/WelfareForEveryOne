from locust import HttpUser, between, task
from config import config
import json

class test(HttpUser):

    @task
    def connect(self):
        payload = {
            "user_id": config["user_id"],
            "user_password": config["password"]
        }

        headers = {'content-type': 'application/json'}

        self.client.post(config["login"], data=json.dumps(payload), headers=headers)