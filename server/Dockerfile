FROM node:16

WORKDIR /server

COPY package.json .

RUN npm install

COPY . ./

EXPOSE 3000

CMD [ "npm", "start" ]