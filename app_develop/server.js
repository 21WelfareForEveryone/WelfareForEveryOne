var http = require("http");

http
  .createServer(function (request, response) {
    response.writeHead(200, {
      "Content-Type": "test/plain",
    });

    response.end("hello");
  })
  .listen(3000, console.log("server port : 3000"));
