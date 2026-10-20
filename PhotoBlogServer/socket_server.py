import socket

server = socket.socket()
server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
server.bind(("0.0.0.0", 8001))
server.listen(1)

print("Socket Server listening on port 8001...")

client, address = server.accept()
request = client.recv(16384).decode("utf-8", errors="replace")

with open("socket_request.txt", "w") as file:
    file.write(request)

print("Request received and saved!")

body = b"[]"
client.sendall(
    b"HTTP/1.1 200 OK\r\n"
    b"Content-Type: application/json\r\n"
    b"Content-Length: 2\r\n"
    b"Connection: close\r\n\r\n" + body
)

client.close()
server.close()
