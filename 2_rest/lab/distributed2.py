from fastapi import FastAPI
from enum import Enum

app = FastAPI()

polls = {}


@app.get("/poll")
async def get_polls():
    return polls


@app.get("/poll/{poll_id}")
async def get_poll(poll_id):
    return polls[poll_id]


@app.get("/poll/{poll_id}/vote")
async def get_votes(poll_id):
    return polls[poll_id]["votes"]


@app.get("/poll/{poll_id}/vote/{vote_id}")
async def get_vote(poll_id, vote_id):
    return polls[poll_id]["votes"][vote_id]


@app.put("/poll/{poll_id}")
async def put_poll(poll_id, poll):
    polls[poll_id] = poll


@app.put("/poll/{poll_id}/vote/{vote_id}")
async def put_vote(poll_id, vote_id, vote):
    polls[poll_id]["votes"][vote_id] = vote


from pydantic import BaseModel


class Poll(BaseModel):
    polls: dict


@app.post("/poll")
async def post_polls(poll: Poll):
    global polls
    polls = poll.polls
    print(type(polls))


@app.post("/poll/{poll_id}/vote")
async def post_votes(poll_id, votes):
    polls[poll_id]["votes"] = votes


@app.delete("/poll/{poll_id}")
async def delete_poll(poll_id):
    polls.pop(poll_id)


@app.delete("/poll/{poll_id}/vote/{vote_id}")
async def delete_vote(poll_id, vote_id):
    polls[poll_id]["votes"].pop(vote_id)
