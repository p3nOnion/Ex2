import socket
import struct

modules = []
def calModules(N, M, x):
    modules.clear()
    modules.append(1 % M)
    modules.append(x % M)
    for i in range(2, N+1):
        next = (modules[i - 1] * modules[1]) % M
        modules.append(next)

def calResult(arr, M, N):
    res = 0;
    for i in range(N+1):
        res = (res + (arr[i] % M) * modules[i]) % M
    return res

HOST = '112.137.129.129'  # The server's hostname or IP address
PORT = 27002  # The port used by the server

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

message = struct.pack("ii", 0, 8) + "19020250".encode("utf-8")

s.connect((HOST, PORT))
s.sendall(message)
print("Sent.")
data = s.recv(2000)

_, _, type, len, N, M, x = struct.unpack("<7I", data[0:28])

format = "<" + str(int(len/4 - 3))  + "I"
arr = struct.unpack(format, data[28 : 28 + 4 * int(len/4 + 3)])
calModules(N, M, x)
res = calResult(arr, M, N)
message = struct.pack("<3I", 2, 4, res)
s.sendall(message)
print(type, len)
print(N, M, x)
print("Sent: ", res)

while True:
    data = s.recv(2000)
    type, len = struct.unpack("<2I", data[0:8])
    print(type, len)
    if type == 1:
        N, M, x = struct.unpack("<3I", data[8:20])
        print(N, M, x)
        format = "<" + str(int(len/4 - 3))  + "I"
        arr = struct.unpack(format, data[20 : 20 + 4 * int(len/4 + 3)])
        calModules(N, M, x)
        res = calResult(arr, M, N)
        message = struct.pack("<3I", 2, 4, res)
        s.sendall(message)
        print("Sent: ", res)
    else:
        flagLen = 8 + int(len)
        print(data[8:].decode("ascii"))
        break