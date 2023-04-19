import socket;

serverIP = "127.0.0.1"
serverPort = 9008
msg = "żółta gęś!"

print('PYTHON UDP CLIENT')
client = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
# client.sendto(bytes(msg, 'utf'), (serverIP, serverPort))
msg_bytes = (300).to_bytes(4, byteorder='little')
client.sendto(msg_bytes, (serverIP, serverPort))

while True:
    buff, address = client.recvfrom(1024)
    print("received msg: " + str(buff, 'utf'))


