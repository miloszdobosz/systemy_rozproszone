import logging
import asyncio

# import threading

import grpc
import events_pb2
import events_pb2_grpc


# async def ainput(string: str) -> str:
#     await asyncio.get_event_loop().run_in_executor(
#             None, lambda s=string: sys.stdout.write(s+' '))
#     return await asyncio.get_event_loop().run_in_executor(
#             None, sys.stdin.readline)

async def run():
    async with grpc.aio.insecure_channel('0.0.0.0:50051') as channel:
        stub = events_pb2_grpc.EventsStub(channel)
        response_stream = stub.subscribe(events_pb2.Topic(id=1, name='you'))
        async for response in response_stream:
            print(response)

if __name__ == '__main__':
    logging.basicConfig()
    asyncio.run(run())
