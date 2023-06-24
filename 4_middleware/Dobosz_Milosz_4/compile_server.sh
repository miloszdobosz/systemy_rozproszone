export PATH=$PATH:/home/mi/.mix/escripts/
protoc --elixir_out=plugins=grpc:./server/gen events.proto
