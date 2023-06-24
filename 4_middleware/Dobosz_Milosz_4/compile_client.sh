python -m grpc_tools.protoc -I. --python_out=./client/gen --pyi_out=./client/gen --grpc_python_out=./client/gen events.proto
